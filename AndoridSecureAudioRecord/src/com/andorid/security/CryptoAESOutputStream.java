package com.andorid.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class CryptoAESOutputStream {
	private CipherOutputStream cos;

	public CryptoAESOutputStream(String filePath, CryptoKeyManager ckm) throws GeneralSecurityException, IOException, ClassNotFoundException {
		ckm.saveKey();
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		aesCipher.init(Cipher.ENCRYPT_MODE, ckm.loadKey());
		cos = new CipherOutputStream(new FileOutputStream(filePath), aesCipher);
	}
	
	public CipherOutputStream getCipherOutputStream() {
		return cos;
	}
	
	public void write(byte[] buf) throws IOException {
		cos.write(buf);
	}
	
	public void close() throws IOException {
		cos.close();
	}
}
