package com.fernandobarillas.SGS4GCarrierUnlocker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HexUtils {

	public HexUtils() {
		// TODO Auto-generated constructor stub
	}

	public static String bytesToHexString(byte[] bytes) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		int currentByte;

		for (int j = 0; j < bytes.length; j++) {
			currentByte = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[currentByte >>> 4];
			hexChars[j * 2 + 1] = hexArray[currentByte & 0x0F];
		}

		return new String(hexChars);
	}

	// Method from the Example Depot
	// http://www.exampledepot.com/egs/java.io/file2bytearray.html
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream inputStream = new FileInputStream(file);
		long length = file.length();

		// Check to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large, return empty byte array
			inputStream.close();
			return new byte[0];
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = inputStream.read(bytes, offset, bytes.length
						- offset)) >= 0) {
			offset += numRead;
		}
		inputStream.close();

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
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
