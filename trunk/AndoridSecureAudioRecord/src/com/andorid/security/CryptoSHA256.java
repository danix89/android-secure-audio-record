package com.andorid.security;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoSHA256 {
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA256(File fileName) {
    	byte[] sha1hash = null;
    	try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String fs = fileName.toString();
			md.update(fs.getBytes(), 0, fs.length());
			sha1hash = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        return convertToHex(sha1hash);
    }
}