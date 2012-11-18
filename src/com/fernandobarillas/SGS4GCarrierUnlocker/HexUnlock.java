package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.xdatv.xdasdk.Shell;

import android.os.Environment;
import android.util.Log;

public class HexUnlock {
	static String STORAGE_PATH_ROOT = Environment.getExternalStorageDirectory()
			.getPath();
	static String NV_DATA_FILE = "/efs/root/afs/settings/nv_data.bin";
	static String NV_DATA_FILE_MD5 = NV_DATA_FILE + ".md5";
	static String NV_DATA_TEMP_FILE = STORAGE_PATH_ROOT + "/nv_data.bin";
	static String NV_DATA_NEW_FILE = STORAGE_PATH_ROOT + "/nv_data_new.bin";
	static String HEX_STRING;
	static Shell shell = new Shell();

	public HexUnlock() {

	}

	public static String doHexUnlock() {
		Log.i("HexUnlock", "Getting Unlock Code");
		String unlockCode = "";
		byte[] byteArray;

		try {
			// Copy over the nv_data.bin file from the default location to a
			// temporary location
			Log.i("HexUnlock", "Getting SU permissions");
			shell.sendShellCommand(new String[] { "su", "-c",
					"cat " + NV_DATA_FILE + " > " + NV_DATA_TEMP_FILE });

			// Now we can convert it to a hex string
			byteArray = HexUtils.getBytesFromFile(new File(NV_DATA_TEMP_FILE));
			String hexString = HexUtils.bytesToHexString(byteArray);

			try {
				Log.i("HexUnlock", "Regex search");
				Pattern regex = Pattern
						.compile("FF0[01]00000000([0-9A-F]{16})FF");
				Matcher regexMatcher = regex.matcher(hexString);
				while (regexMatcher.find()) {
					Log.i("HexUnlock", "Regex matched");

					// We found a good code! We're sure this is the byte we want
					// to switch
					// hexString = regexMatcher.group(0);
					try {
						Log.i("HexUnlock", "Lock Status Before: "
								+ checkLockStatus(hexString));
						// Lock
						// String newHexString = hexString.replaceAll(
						// "FF00(00000000[0-9A-F]{16}FF)", "FF01$1");
						// Unlock
						String newHexString = hexString.replaceAll(
								"FF01(00000000[0-9A-F]{16}FF)", "FF00$1");
						if (newHexString.equals(hexString)) {
							Log.i("HexUnlock", "The hex strings match");
						} else {
							Log.i("HexUnlock",
									"The hex strings no longer match ");

							char[] oldChar = hexString.toCharArray();
							char[] newChar = newHexString.toCharArray();
							for (int i = 0; i < oldChar.length; i++) {
								if (oldChar[i] != newChar[i]) {
									if (i < 10)
										System.out.print("0");
									System.out.print(i + ": ");
									System.out.print(oldChar[i] + " ");
									System.out.print(newChar[i] + " ");
									System.out.print(" MISMATCH ");
								}
								System.out.println();
								System.out.println();
							}

							byte[] outputBytes = HexUtils
									.hexStringToBytes(newHexString);
							// TODO: Write to file here
							if (writeFile(outputBytes)) {

								Log.i("HexUnlock", "Replacing nv_data.bin");
								shell.sendShellCommand(new String[] {
										"su",
										"-c",
										"cat " + NV_DATA_NEW_FILE + " > "
												+ NV_DATA_FILE });
							} else {
								Log.e("HexUnlock",
										"Could not output new nv_data.bin file!");
							}

						}
						Log.i("HexUnlock", "Lock Status After: "
								+ checkLockStatus(hexString));
					} catch (Exception e) {

					}

					break;
				}
			} catch (PatternSyntaxException e) {
				// Syntax error in the regular expression
				Log.e("HexUnlock", "Regex error");
				e.printStackTrace();
			}

			// Now we delete the temporary file
			Log.i("HexUnlock", "Deleting temp file");
			shell.sendShellCommand(new String[] { "su", "-c",
					"rm " + NV_DATA_TEMP_FILE + " " + NV_DATA_FILE_MD5 });

		} catch (IOException e) {
			Log.e("HexUnlock",
					"Error opening temp file, I probably don't have SU permission");
			e.printStackTrace();
		}
		return unlockCode;
	}

	private static boolean writeFile(byte[] byteArray) {
		try {
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/");
			dir.mkdirs();
			File file = new File(NV_DATA_NEW_FILE);

			FileOutputStream f = new FileOutputStream(file);

			f.write(byteArray);
			f.close();

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	/* Returns true if unlocked, false if locked */
	public static String checkLockStatus(String hexString) throws IOException {
		byte[] byteArray;
		shell.sendShellCommand(new String[] { "su", "-c",
				"cat " + NV_DATA_FILE + " > " + NV_DATA_TEMP_FILE });
		byteArray = HexUtils.getBytesFromFile(new File(NV_DATA_TEMP_FILE));
		hexString = HexUtils.bytesToHexString(byteArray);

		Pattern regexLocked = Pattern.compile("FF0100000000([0-9A-F]{16})FF");
		Matcher regexMatcherLocked = regexLocked.matcher(hexString);

		Pattern regexUnlocked = Pattern.compile("FF0000000000([0-9A-F]{16})FF");
		Matcher regexMatcherUnlocked = regexUnlocked.matcher(hexString);

		if (regexMatcherLocked.find()) {
			return "locked";
		}

		if (regexMatcherUnlocked.find()) {
			return "unlocked";
		}

		return null;
	}

}
