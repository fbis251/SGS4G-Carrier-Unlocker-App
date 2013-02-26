package com.fernandobarillas.SGS4GCarrierUnlocker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UnlockCode {
	static String STORAGE_PATH_ROOT = Environment.getExternalStorageDirectory()
			.getPath();
	static String NV_DATA_FILE;
	static String OUTPUT_FILE = STORAGE_PATH_ROOT + "/unlockcode.txt";
	static String NV_DATA_TEMP_FILE = STORAGE_PATH_ROOT + "/nv_data.bin";
	Shell SHELL = new Shell();

	public UnlockCode() {
		Log.i("UnlockCode", "UnlockCode instantiated");
		// We're going to need to hard-code the variables if they're not passed
		// in, so we'll use the default path for the SGS4G'S nv_data.bin
		NV_DATA_FILE = "/efs/root/afs/settings/nv_data.bin";
	}

	public UnlockCode(String nvDataFile) {
		this();
		UnlockCode.NV_DATA_FILE = nvDataFile;
	}

	public UnlockCode(String nvDataFile, String outputFile) {
		this(nvDataFile);
		// Make sure that the file will get saved to the sdcard
		if (!outputFile.startsWith(STORAGE_PATH_ROOT)) {
			outputFile = STORAGE_PATH_ROOT + outputFile;
		}
		UnlockCode.OUTPUT_FILE = outputFile;
	}

	public UnlockCode(String nvDataFile, String outputFile,
			String nvDataTempFile) {
		this(nvDataFile, outputFile);
		// Make sure that the file will get saved to the sdcard
		if (!nvDataTempFile.startsWith(STORAGE_PATH_ROOT)) {
			nvDataTempFile = STORAGE_PATH_ROOT + nvDataTempFile;
		}
		UnlockCode.NV_DATA_TEMP_FILE = nvDataTempFile;
	}

	public String getUnlockCode() {
		Log.i("UnlockCode", "Getting Unlock Code");
		String unlockCode = "";
		byte[] byteArray;

		// Copy over the nv_data.bin file from the default location to a
		// temporary location
		Log.i("UnlockCode", "Getting SU permissions");
		SHELL.sendCommand("cat " + NV_DATA_FILE + " > " + NV_DATA_TEMP_FILE);

		// Now we can convert it to a hex string
		byteArray = HexUtils.getBytesFromFile(new File(NV_DATA_TEMP_FILE));
		String hexString = HexUtils.bytesToHexString(byteArray);

		try {
			Log.i("UnlockCode", "Regex search");
			Pattern regex = Pattern.compile("FF0[01]00000000([0-9A-F]{16})FF");
			Matcher regexMatcher = regex.matcher(hexString);
			while (regexMatcher.find()) {
				// If the regex successfully matched, the code will be in
				// capturing group 1
				unlockCode = extractUnlockCode(regexMatcher.group(1));
				if (unlockCode != "") {
					// We found a good code! end the loop
					Log.i("UnlockCode", "Code successfully found!");
					break;
				}
			}
		} catch (PatternSyntaxException e) {
			// Syntax error in the regular expression
			Log.e("UnlockCode", "Regex error");
			e.printStackTrace();
		}

		// Now we delete the temporary file
		SHELL.sendCommand("rm " + NV_DATA_TEMP_FILE);
		return unlockCode;
	}

	public static String extractUnlockCode(String hexString) {
		Log.i("UnlockCode", "Extracting Unlock Code");
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
		Log.i("UnlockCode", "Saving unlock code to: " + OUTPUT_FILE);
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
				PrintWriter out = new PrintWriter(OUTPUT_FILE);
				out.println(unlockCode);
				out.close();
				returnStatus = true;
			} else {
				returnStatus = false;
			}
		} catch (IOException e) {
			Log.e("UnlockCode", "Save to sd card failed!");
			e.printStackTrace();
			returnStatus = false;
		}

		return returnStatus;
	}
}
