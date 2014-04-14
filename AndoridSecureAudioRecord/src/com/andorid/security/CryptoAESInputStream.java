package com.andorid.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoAESInputStream  {
	private CipherInputStream cis;

	public CryptoAESInputStream(InputStream in, CryptoKeyManager ckm) throws GeneralSecurityException, IOException {
		Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		try {
//			aesCipher.init(Cipher.DECRYPT_MODE, ckm.loadKey(), new IvParameterSpec(ckm.loadKey().getEncoded()));
			SecretKeySpec key = ckm.loadKey();
			aesCipher.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cis = new CipherInputStream(in, aesCipher);
	}

	public int read(byte[] buf) throws IOException {
		return cis.read(buf, 0, buf.length);
	}

	public void close() throws IOException {
		cis.close();
	}
}
