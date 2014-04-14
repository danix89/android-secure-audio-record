package com.andorid.security;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.os.Environment;

public class CryptoKeyManager {
	public static final int AES_Key_Size = 256;

	private static final String AUDIO_RECORDER_FOLDER = 
			Environment.getExternalStorageDirectory().getPath() + "/AudioRecorder";
	private static final String KEY_FILE = AUDIO_RECORDER_FOLDER + "/keyFile.key";
	private static final String PUBLIC_KEY_FILE = AUDIO_RECORDER_FOLDER + "/public.key";
	private static final String PRIVATE_KEY_FILE = AUDIO_RECORDER_FOLDER + "/private.key";

	private Cipher pkCipher;

	private File keyFile;

	public CryptoKeyManager() throws GeneralSecurityException, IOException {
		pkCipher = Cipher.getInstance("RSA");
		
		keyFile = new File(KEY_FILE);
		keyFile.createNewFile();
		
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048);
		KeyPair kp = kpg.genKeyPair();
		
//		privateKeyFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/privateKeyFile.key");
//		FileOutputStream os = new FileOutputStream(privateKeyFile);
//		os.write(kp.getPrivate().getEncoded());
//		os.close();
		
//		publicKeyFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/publicKeyFile.key");
//		os = new FileOutputStream(publicKeyFile);
//		os.write(kp.getPublic().getEncoded());
//		os.close();
		
		KeyFactory fact = KeyFactory.getInstance("RSA");
		RSAPublicKeySpec pub = fact.getKeySpec(kp.getPublic(), RSAPublicKeySpec.class);
		RSAPrivateKeySpec priv = fact.getKeySpec(kp.getPrivate(), RSAPrivateKeySpec.class);

		saveToFile(PUBLIC_KEY_FILE, pub.getModulus(), pub.getPublicExponent());
		saveToFile(PRIVATE_KEY_FILE, priv.getModulus(), priv.getPrivateExponent());
	}

	private void saveToFile(String fileName, BigInteger mod, BigInteger exp) 
			throws IOException {
			ObjectOutputStream oout = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(fileName)));
			try {
				oout.writeObject(mod);
				oout.writeObject(exp);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oout.close();
			}
	}
	
	private PrivateKey readPrivateKeyFromFile() throws IOException {
		FileInputStream in = new FileInputStream(PRIVATE_KEY_FILE);
		ObjectInputStream oin =
				new ObjectInputStream(new BufferedInputStream(in));
		try {
			BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PrivateKey priKey = fact.generatePrivate(keySpec);
		    return priKey;
		  } catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
			oin.close();
		}
	}

	private PublicKey readPublicKeyFromFile() throws IOException {
		FileInputStream in = new FileInputStream(PUBLIC_KEY_FILE);
		ObjectInputStream oin =
				new ObjectInputStream(new BufferedInputStream(in));
		try {
			BigInteger m = (BigInteger) oin.readObject();
			BigInteger e = (BigInteger) oin.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey pubKey = fact.generatePublic(keySpec);
			return pubKey;
		} catch (Exception e) {
			throw new RuntimeException("Spurious serialisation error", e);
		} finally {
			oin.close();
		}
	}
	
	/**
	 * Creates a new AES key
	 * @return 
	 */
	public SecretKeySpec makeKey() throws NoSuchAlgorithmException {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
	    kgen.init(AES_Key_Size);
	    SecretKey key = kgen.generateKey();
	    byte[] aesKey = key.getEncoded();
	    return new SecretKeySpec(aesKey, "AES");
	}

	/**
	 * Decrypts an AES key from a file using an RSA private key
	 */
	public SecretKeySpec loadKey() throws GeneralSecurityException, IOException, ClassNotFoundException {
//		// read private key to be used to decrypt the AES key
//		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
//		byte[] encodedKey = (byte[]) ois.readObject();
//		ois.close();
//		
//		// create private key
//		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		PrivateKey pk = kf.generatePrivate(privateKeySpec);
		
		// read AES key
		pkCipher.init(Cipher.DECRYPT_MODE, readPrivateKeyFromFile());
		byte[] aesKey = new byte[AES_Key_Size/8];
		
		CipherInputStream is = new CipherInputStream(new FileInputStream(keyFile), pkCipher);
		is.read(aesKey);
		return new SecretKeySpec(aesKey, "AES");
	}
	
	/**
	 * Encrypts the AES key to a file using an RSA public key
	 * @throws ClassNotFoundException 
	 */
	public void saveKey() throws IOException, GeneralSecurityException, ClassNotFoundException {
//		// read public key to be used to encrypt the AES key
//		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
//		byte[] encodedKey = (byte[]) ois.readObject();
//		ois.close();
//		
//		// create public key
//		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
//		KeyFactory kf = KeyFactory.getInstance("RSA");
//		PublicKey pk = kf.generatePublic(publicKeySpec);
		
		// write AES key
		pkCipher.init(Cipher.ENCRYPT_MODE, readPublicKeyFromFile());
		CipherOutputStream os = new CipherOutputStream(new FileOutputStream(keyFile), pkCipher);
		os.write(makeKey().getEncoded());
		os.close();
	}
}
