package com.andorid.security;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.DecoderException;

public class CryptoAES {
	private static int iterationCount = 65536;
	private static int keyLength = 256;
	private static SecureRandom secRandom;
	private static IvParameterSpec iv;
	private static SecretKey secretKey;
	
//	public CryptoAES(String password) {	}

	public static Key createPublicKey() throws DecoderException, NoSuchAlgorithmException, InvalidKeySpecException {
//		SecureRandom rnd = new SecureRandom(); //da sostituire con un'altra classe più sicura
//		byte[] salt = new byte[8];
//		rnd.nextBytes(salt);
//		//System.out.println ("** Crypt ** generated salt :" + convertToHex(mSalt));
//		
//		/* Derive the key, given password and salt. */
//		SecretKeyFactory factory;
//		SecretKey tmp = null;
//		KeySpec spec = new PBEKeySpec("password".toCharArray(), salt, iterationCount, keyLength);
//		try {
//			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//			tmp = factory.generateSecret(spec);
//		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(1);
//		} 
//		String keyStr = "6a6b663472346c38736873346569727538346234333534376635333962353666";
//         
//        // decode the key string into bytes (using Apache Commons)
//        byte[] keyBytes = Hex.decodeHex(keyStr.toCharArray());
//
//        // create a representation of the key
//        SecretKey spec = new SecretKeySpec(keyBytes, "AES");
//
//        // turn the key spec into a usable key
//        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("AES");
//        SecretKey key = keyFactory.generateSecret((KeySpec) spec);
		
//        SecureRandom secRandom = SecureRandom.getInstance("SHA1PRNG");
		secRandom = SecureRandom.getInstance("SHA1PRNG");
		KeyGenerator kg = KeyGenerator.getInstance("AES");
 	    kg.init(256, secRandom);
 	    secretKey = kg.generateKey();
         
		return secretKey;
	}
	
	public static SecretKey getSecretKey() {
		return secretKey;
	}

	public static byte[] encrypt(Key publicKey, byte[] audioData) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
			InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, DecoderException {
		/* Encrypt the message. */
//		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
	    Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    AESCipher.init(Cipher.ENCRYPT_MODE, createPublicKey(), secRandom);
		
		byte[] ciphertext = AESCipher.doFinal(audioData);

		iv = new IvParameterSpec(AESCipher.getIV());
//		System.out.println("Dati criptati:");
//		System.out.println(ciphertext.toString());
		
		return ciphertext;
	}

	public static byte[] decrypt(Key privateKey, byte[] ciphertext) 
			throws NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidParameterSpecException, InvalidKeyException, 
			InvalidAlgorithmParameterException, IllegalBlockSizeException, 
			BadPaddingException {
		/* Decrypt the message, given derived key and initialization vector. */
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		AlgorithmParameters params = cipher.getParameters();
//		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
//		cipher.init(Cipher.DECRYPT_MODE, privateKey, new IvParameterSpec(iv));
		cipher.init(Cipher.DECRYPT_MODE, privateKey, iv, secRandom);
		byte[] plaintext = cipher.doFinal(ciphertext);

//		System.out.println("Dati decriptati:");
//		System.out.println(plaintext);

		return plaintext;
	}
}
