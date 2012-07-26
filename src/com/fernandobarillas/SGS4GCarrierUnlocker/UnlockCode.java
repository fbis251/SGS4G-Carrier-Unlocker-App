package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.xdatv.xdasdk.Shell;

public class UnlockCode {
	static String storagePathRoot = Environment.getExternalStorageDirectory()
			.getPath();
	static String nvDataFile;
	static String outputFile = storagePathRoot + "/unlockcode.txt";
	static String nvDataTempFile = storagePathRoot + "/nv_data.bin";

	public UnlockCode() {
		Log.i("myid", "UnlockCode instantiated");
		// We're going to need to hard-code the variables if they're not passed
		// in, so we'll use the default path for the SGS4G'S nv_data.bin
		nvDataFile = "/efs/root/afs/settings/nv_data.bin";
	}

	public UnlockCode(String nvDataFile) {
		this();
		UnlockCode.nvDataFile = nvDataFile;
	}

	public UnlockCode(String nvDataFile, String outputFile) {
		this(nvDataFile);
		// Make sure that the file will get saved to the sdcard
		if (!outputFile.startsWith(storagePathRoot)) {
			outputFile = storagePathRoot + outputFile;
		}
		UnlockCode.outputFile = outputFile;
	}

	public UnlockCode(String nvDataFile, String outputFile,
			String nvDataTempFile) {
		this(nvDataFile, outputFile);
		// Make sure that the file will get saved to the sdcard
		if (!nvDataTempFile.startsWith(storagePathRoot)) {
			nvDataTempFile = storagePathRoot + nvDataTempFile;
		}
		UnlockCode.nvDataTempFile = nvDataTempFile;
	}

	public String getUnlockCode() {
		Log.i("myid", "UnlockCode: Getting Unlock Code");
		String unlockCode = "";
		byte[] byteArray;
		Shell shell = new Shell();

		try {
			// Copy over the nv_data.bin file from the default location to a
			// temporary location
			Log.i("myid", "UnlockCode: Getting SU permissions");
			shell.sendShellCommand(new String[] { "su", "-c",
					"cat " + nvDataFile + " > " + nvDataTempFile });

			// Now we can convert it to a hex string
			byteArray = getBytesFromFile(new File(nvDataTempFile));
			String hexString = bytesToHexString(byteArray);

			try {
				Log.i("myid", "UnlockCode: Regex search");
				Pattern regex = Pattern
						.compile("ff0[01]00000000([0-9a-f]{16})ff");
				Matcher regexMatcher = regex.matcher(hexString);
				while (regexMatcher.find()) {
					// If the regex successfully matched, the code will be in
					// capturing group 1
					unlockCode = extractUnlockCode(regexMatcher.group(1));
					if (unlockCode != "") {
						// We found a good code! end the loop
						Log.i("myid", "UnlockCode: Code successfully found!");
						break;
					}
				}
			} catch (PatternSyntaxException e) {
				// Syntax error in the regular expression
				Log.e("myid", "UnlockCode: Regex error");
				e.printStackTrace();
			}

			// Now we delete the temporary file
			shell.sendShellCommand(new String[] { "su", "-c",
					"rm " + nvDataTempFile });

		} catch (IOException e) {
			Log.e("myid", "UnlockCode: Error opening temp file, I probably don't have SU permission");
			e.printStackTrace();
		}
		return unlockCode;
	}

	private String extractUnlockCode(String hexString) {
		Log.i("myid", "UnlockCode: Extracting Unlock Code");
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
				if (number < 0 || number > 9) {
					// It has to be between 0-9 or it's a bad result
					break;
				}
				result += number;
				hexByte = "";
			}
		}

		return result;
	}

	public boolean saveUnlockCodeToSDCard(String unlockCode) {
		Log.i("myid", "UnlockCode: Saving unlock code to: " + outputFile);
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
			// all we need to know is we can neither read nor write
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
			Log.e("myid", "UnlockCode: Save to sd card failed!");
			e.printStackTrace();
			returnStatus = false;
		}

		return returnStatus;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
		Formatter formatter = new Formatter(stringBuilder);

		for (byte currentByte : bytes) {
			// Output each byte as a hex string character
			formatter.format("%02x", currentByte);
		}

		// Close the formatter
		formatter.close();
		return stringBuilder.toString();
	}

	// Method from the Example Depot
	// http://www.exampledepot.com/egs/java.io/file2bytearray.html
	private byte[] getBytesFromFile(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// Check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large, return empty byte array
			// Close the input stream and return bytes
			inputStream.close();
			return new byte[0];
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = inputStream.read(bytes, offset, bytes.length
						- offset)) >= 0) {
			offset += numRead;
		}

		// Close the input stream since we no longer need it
		inputStream.close();

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		return bytes;
	}
}
