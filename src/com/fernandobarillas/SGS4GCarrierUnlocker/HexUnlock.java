package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;
import android.util.Log;

public class HexUnlock {
	static String STORAGE_PATH_ROOT = Environment.getExternalStorageDirectory()
			.getPath();
	static String NV_DATA_FILE = "/efs/root/afs/settings/nv_data.bin";
	static String NV_DATA_FILE_MD5 = NV_DATA_FILE + ".md5";
	static String NV_DATA_TEMP_FILE = STORAGE_PATH_ROOT + "/nv_data.bin";
	static String HEX_STRING;
	static String LOCK_STATUS;
	static Shell SHELL = new Shell();

	public HexUnlock() {
		updateHexString();
		System.out.println("Lock status: " + LOCK_STATUS);
	}

	public boolean doHexEdit(String state) {
		updateHexString();

		boolean returnStatus = false;
		Pattern regex = Pattern.compile("(FF0[01]00000000[0-9A-F]{16}FF)");
		Matcher regexMatcher = regex.matcher(HEX_STRING);
		String newHexString = "";
		while (regexMatcher.find()) {
			Log.i("HexUnlock", "Found correct hex string for lock/unlock");

			// We found a good code! Make sure this is the correct byte to flip
			String unlockHexString = HEX_STRING.replaceAll(
					"FF01(00000000[0-9A-F]{16}FF)", "FF00$1");
			String lockHexString = HEX_STRING.replaceAll(
					"FF00(00000000[0-9A-F]{16}FF)", "FF01$1");

			if (state == "unlocked") {
				newHexString = unlockHexString;
				Log.i("HexUnlock", "Performing hex unlock");
			} else if (state == "locked") {
				newHexString = lockHexString;
				Log.i("HexUnlock", "Performing hex lock");
			}

			if (newHexString.equals(HEX_STRING)) {
				Log.i("HexUnlock",
						"Hex Strings match, no replacement performed.");
			} else {
				Log.i("HexUnlock",
						"Hex strings don't match, doing replacement.");

				if (writeNvFile(HexUtils.hexStringToBytes(newHexString))) {
					Log.i("HexUnlock", "Replacing nv_data.bin");
					returnStatus = true;
				} else {
					Log.e("HexUnlock", "Could not output new nv_data.bin file!");
				}
			}

			break;
		}

		updateHexString();
		return returnStatus;
	}

	private boolean writeNvFile(byte[] byteArray) {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/");
			dir.mkdirs();
			File file = new File(NV_DATA_TEMP_FILE);

			FileOutputStream f = new FileOutputStream(file);

			f.write(byteArray);
			f.close();
			updateOriginalNvFile();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	private String getHexString() {
		// Copy over the nv_data.bin file from the default location to a
		// temporary location
		Log.i("HexUnlock", "Getting hex string from nv_data.bin");
		updateNvTempFile();

		return HexUtils.bytesToHexString(HexUtils.getBytesFromFile(new File(
				NV_DATA_TEMP_FILE)));
	}

	/* Returns true if unlocked, false if locked */
	public String getLockStatus() {
		String returnString = "";
		Pattern regexLocked = Pattern.compile("FF0100000000([0-9A-F]{16})FF");
		Matcher regexMatcherLocked = regexLocked.matcher(HEX_STRING);
		while (regexMatcherLocked.find()) {
			if (UnlockCode.extractUnlockCode(regexMatcherLocked.group(1)) != "") {
				// We have a find if we can get an unlock code
				returnString = "locked";
			}
		}

		Pattern regexUnlocked = Pattern.compile("FF0000000000([0-9A-F]{16})FF");
		Matcher regexMatcherUnlocked = regexUnlocked.matcher(HEX_STRING);
		while (regexMatcherUnlocked.find()) {
			if (UnlockCode.extractUnlockCode(regexMatcherUnlocked.group(1)) != "") {
				// We have a find if we can get an unlock code
				returnString = "unlocked";
			}
		}

		cleanNvTempFile();

		Log.i("HexUnlock", "Lock Status: " + returnString);
		return returnString;
	}

	private void updateHexString() {
		Log.i("HexUnlock", "Updating hex string");
		HEX_STRING = getHexString();
		LOCK_STATUS = getLockStatus();
	}

	private void cleanNvTempFile() {
		Log.i("HexUnlock", "Deleting temp file");
		SHELL.sendCommand("rm -f " + NV_DATA_TEMP_FILE + " " + NV_DATA_FILE_MD5);
	}

	private void updateNvTempFile() {
		Log.i("HexUnlock", "Updating nv_data temp file");
		SHELL.sendCommand("cat " + NV_DATA_FILE + " > " + NV_DATA_TEMP_FILE);
	}

	private void updateOriginalNvFile() {
		Log.i("HexUnlock", "Updating nv_data /efs file");
		SHELL.sendCommand("cat " + NV_DATA_TEMP_FILE + " > " + NV_DATA_FILE);
	}
}
