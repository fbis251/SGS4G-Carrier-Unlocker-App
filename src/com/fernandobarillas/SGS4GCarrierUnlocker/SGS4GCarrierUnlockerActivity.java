package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.fernandobarillas.SGS4GCarrierUnlocker.Shell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SGS4GCarrierUnlockerActivity extends Activity implements Runnable {
	String pleaseWait;
	String outputFile;
	String nvDataFile;
	String nvDataTempFile;
	String unlockCodeFound;
	String unlockCodeNotFound;
	String unlockCodeSaved;
	String unlockCodeNotSaved;
	String unlockCode;
	String resultText;
	ProgressDialog pd;
	TextView tv;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize variables
		pleaseWait = getString(R.string.pleaseWait);
		outputFile = getString(R.string.outputFile);
		nvDataFile = getString(R.string.nvDataFile);
		nvDataTempFile = getString(R.string.nvDataTempFile);
		unlockCodeFound = getString(R.string.unlockCodeFound);
		unlockCodeNotFound = getString(R.string.unlockCodeNotFound);
		unlockCodeSaved = getString(R.string.unlockCodeSaved);
		unlockCodeNotSaved = getString(R.string.unlockCodeNotSaved);

		// Set the resultText view for use by other methods
		tv = (TextView) this.findViewById(R.id.resultText);

		// This is our main button! Basically only here so the user will need to
		// do something before the superuser request
		final Button unlockButton = (Button) findViewById(R.id.unlockButton);
		unlockButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// We don't want users repeatedly tapping the button
				unlockButton.setEnabled(false);

				// Tell the user to chill while we do our thing
				pd = new ProgressDialog(SGS4GCarrierUnlockerActivity.this);
				pd.setMessage(pleaseWait);
				pd.setCancelable(false);
				pd.show();

				// We start the unlocking process in another thread so the UI
				// can update while it waits for the result
				Thread thread = new Thread(SGS4GCarrierUnlockerActivity.this);
				thread.start();
			}
		});
	}

	// Process Dialog
	// run and handler modified from
	// http://www.helloandroid.com/tutorials/using-threads-and-progressdialog
	public void run() {
		unlockCode = getUnlockCode();

		if (unlockCode != "") {
			resultText = unlockCodeFound + "\n" + unlockCode;
			// Try to write the unlock code to the SD card
			if (saveUnlockCodeToSD(unlockCode)) {
				resultText += "\n\n" + unlockCodeSaved + "\n" + outputFile;
			} else {
				resultText += "\n\n" + unlockCodeNotSaved;
			}
		} else {
			resultText = unlockCodeNotFound;
		}

		// Tell the thread handler that we're done
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// We don't need the dialog anymore
			pd.dismiss();
			tv.setText(resultText);
		}
	};

	public String getUnlockCode() {
		String unlockCode = "";
		byte[] byteArray;
		Shell shell = new Shell();

		try {
			// Copy over the nv_data.bin file from the default location to a
			// temporary location
			shell.sendShellCommand(new String[] { "su", "-c",
					"cat " + nvDataFile + " >" + nvDataTempFile });

			// Now we can convert it to a hex string
			byteArray = getBytesFromFile(new File(nvDataTempFile));
			String hexString = bytesToHexString(byteArray);

			try {
				Pattern regex = Pattern
						.compile("ff0[01]00000000([0-9a-f]{16})ff");
				Matcher regexMatcher = regex.matcher(hexString);
				while (regexMatcher.find()) {
					// If the regex successfully matched, the code will be in
					// capturing group 1
					unlockCode = extractUnlockCode(regexMatcher.group(1));
					if (unlockCode != "") {
						// We found a good code! end the loop
						break;
					}
				}
			} catch (PatternSyntaxException e) {
				// Syntax error in the regular expression
				e.printStackTrace();
			}

			// Now we delete the temporary file
			shell.sendShellCommand(new String[] { "su", "-c",
					"rm " + nvDataTempFile });

		} catch (IOException e) {
			e.printStackTrace();
		}
		return unlockCode;
	}

	private boolean saveUnlockCodeToSD(String unlockCode) {
		// From android "Checking media availability" example:
		// http://developer.android.com/guide/topics/data/data-storage.html#filesExternal

		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		boolean returnStatus;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		try {
			if (mExternalStorageAvailable && mExternalStorageWriteable) {
				PrintWriter out = new PrintWriter(outputFile);
				out.println(unlockCode);
				out.close();
				returnStatus = true;
			} else {
				returnStatus = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			returnStatus = false;
		}

		return returnStatus;
	}

	private String extractUnlockCode(String hexString) {
		String hexByte = "";
		String result = "";
		for (int i = 0; i < hexString.length(); i++) {
			// We want to iterate through each character in the string
			// They should all be encoded as ASCII hex
			char c = hexString.charAt(i);
			hexByte = hexByte + c;
			if ((i % 2) == 1) {
				// We want to look at 2-character hex codes
				Integer number = Integer.decode("0x" + hexByte);

				// The ASCII character 0 is integer 48, so we subtract it from
				// our result
				number -= 48;

				// Now we have the real decimal stored
				if (number < 0 || number > 9)
					// It has to be between 0-9 or it's a bad result
					break;
				result += number;
				hexByte = "";
			}
		}

		return result;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		Formatter formatter = new Formatter(sb);

		for (byte b : bytes) {
			// Output each byte as a hex string character
			formatter.format("%02x", b);
		}

		return sb.toString();
	}

	// Method from the Example Depot
	// http://www.exampledepot.com/egs/java.io/file2bytearray.html
	private byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large, return empty byte array
			return new byte[0];
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

}
