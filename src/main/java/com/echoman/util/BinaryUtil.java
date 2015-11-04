package com.echoman.util;


public class BinaryUtil {
	
	private static String hexStr = "0123456789ABCDEF";
	private static String[] binaryArray = { "0000", "0001", "0010", "0011",
			"0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
			"1100", "1101", "1110", "1111" };
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String bytes2BinaryStr(byte[] bArray) {

		String outStr = "";
		int pos = 0;
		for (byte b : bArray) {
			pos = (b & 0xF0) >> 4;
			outStr += binaryArray[pos];
			pos = b & 0x0F;
			outStr += binaryArray[pos];
		}
		return outStr;

	}

	/**
	 * 
	 * @param bytes
	 * @return 
	 */
	public static String BinaryToHexString(byte[] bytes) {
		return BinaryToHexString(bytes, " ");
	}
	/**
	 * 
	 * @param bytes
	 * @return 
	 */
	public static String BinaryToHexString_(byte[] bytes) {
		return BinaryToHexString(bytes, "_");
	}
	
	private static String BinaryToHexString(byte[] bytes, String split) {

		String result = "";
		String hex = "";
		for (int i = 0; i < bytes.length; i++) {
			
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			if(i == bytes.length - 1)
				result += hex;
			else
				result += hex + split;
		}
		return result;
	}

	/**
	 * 
	 * @param hexString
	 * @return 
	 */
	public static byte[] HexStringToBinary(String hexString) {
		
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;
		byte low = 0;

		for (int i = 0; i < len; i++) {
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte) (high | low);
		}
		return bytes;
	}
}
