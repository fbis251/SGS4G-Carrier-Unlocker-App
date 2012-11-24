package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class HexUtils {

	public HexUtils() {
		// TODO Auto-generated constructor stub
	}

	public static String bytesToHexString(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		int currentByte;

		if (bytes == null) {
			/* No hex string if the byte array is null */
			return "";
		}

		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			currentByte = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[currentByte >>> 4];
			hexChars[j * 2 + 1] = hexArray[currentByte & 0x0F];
		}

		return new String(hexChars);
	}

	// Method from the Example Depot
	// http://www.exampledepot.com/egs/java.io/file2bytearray.html
	public static byte[] getBytesFromFile(File file) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.e("HexUtils",
					"Could not find the file to write to: " + file.getName());
			return new byte[0];
		}
		long length = file.length();

		// Check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large, return empty byte array
			try {
				inputStream.close();
			} catch (IOException e) {
				Log.e("HexUtils", "Could not find the file to write to: "
						+ file.getName());
				return new byte[0];
			}
			return new byte[0];
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		try {
			while (offset < bytes.length
					&& (numRead = inputStream.read(bytes, offset, bytes.length
							- offset)) >= 0) {
				offset += numRead;
			}
			inputStream.close();
		} catch (IOException e) {
			Log.e("HexUtils", "Error while reading: " + file.getName());
			return new byte[0];
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			/* Error occurred, return an empty array */
			return new byte[0];
		}

		return bytes;
	}

	public static byte[] hexStringToBytes(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
