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
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SGS4GCarrierUnlockerActivity extends Activity {
	String outputFile;
	String nvDataFile;
	String nvDataTempFile;
	String unlockCodeFound;
	String unlockCodeNotFound;
	String unlockCodeSaved;
	String unlockCodeNotSaved;
	String unlockCode;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Initialize variables
		outputFile = getString(R.string.outputFile);
		nvDataFile = getString(R.string.nvDataFile);
		nvDataTempFile = getString(R.string.nvDataTempFile);
		unlockCodeFound = getString(R.string.unlockCodeFound);
		unlockCodeNotFound = getString(R.string.unlockCodeNotFound);
		unlockCodeSaved = getString(R.string.unlockCodeSaved);
		unlockCodeNotSaved = getString(R.string.unlockCodeNotSaved);

		final Button unlockButton = (Button) findViewById(R.id.unlockButton);
		unlockButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				unlockButton.setEnabled(false);
				unlockCode = getUnlockCode();
				String text = "";

				if (unlockCode != "") {
					text = unlockCodeFound + "\n" + unlockCode;
					// Since we found the unlockCode, we try to write it to
					// the SD card
					if (saveUnlockCodeToSD(unlockCode)) {
						text += "\n\n" + unlockCodeSaved + "\n" + outputFile;
					} else {
						text += "\n\n" + unlockCodeNotSaved;
					}
				} else {
					text = unlockCodeNotFound;
				}

				setNewTextInTextView(text);
			}
		});
	}

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
					// We're going to use regexMatcher's capturing group 1
					// from the regex pattern above.
					unlockCode = extractUnlockCode(regexMatcher.group(1));
					if (unlockCode != "")
						// We found a good code! end the loop
						break;
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

	public boolean saveUnlockCodeToSD(String unlockCode) {
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

	public static String extractUnlockCode(String hexString) {
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

	public static String bytesToHexString(byte[] bytes) {
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
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
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

	public void setNewTextInTextView(String text) {
		TextView tv = new TextView(this);
		tv = (TextView) findViewById(R.id.resultText);
		tv.setText(text);
	}
}
