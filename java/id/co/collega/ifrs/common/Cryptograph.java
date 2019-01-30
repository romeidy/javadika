package id.co.collega.ifrs.common;

import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Class ini berisi static method untuk melakukan enkripsi/dekripsi teks menggunakan algoritma DES
 *
 * @author Bustanil Arifin
 *
 */
public class Cryptograph {

	private Cipher cipherFile;
	private Cipher cipherMemory;
	private SecretKey secretKeyFile = null;
	private SecretKey secretKeyMemory = null;
	private static Cryptograph cg = null;
	
	@SuppressWarnings("unused")
	private Cryptograph() {
		try {
			ObjectInputStream in = new ObjectInputStream(Cryptograph.class.getResourceAsStream("RunKey.key"));
			if(in == null){
				throw new SecurityException("File RunKey.key tidak ditemukan");
			}
			DESKeySpec desKeySpec = new DESKeySpec("rahasiaX".getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			secretKeyFile = (SecretKey) in.readObject();
			secretKeyMemory = keyFactory.generateSecret(desKeySpec);
			cipherFile = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipherMemory = Cipher.getInstance("DES/ECB/PKCS5Padding");

			in.close();
		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}

	}

	/**
	 * mengambil instance dari class ini. Instance dari class ini merupakan static object.
	 *
	 * @return instance static dari class ini
	 */
	public static Cryptograph getInstance() {
		if (cg == null) {
			cg = new Cryptograph();
		}
		return cg;
	}

	/**
	 * Melakukan enkripsi string
	 *
	 * @param plainText
	 *            teks yang akan dienkripsi
	 * @return teks hasil enkripsi
	 */
	public String encryptText(String plainText) {
		if (plainText == null) {
			return null;
		}
		String encrypted = null;
		try {
			cipherFile.init(Cipher.ENCRYPT_MODE, secretKeyFile);
			cipherMemory.init(Cipher.ENCRYPT_MODE, secretKeyMemory);
			byte[] utf8 = plainText.getBytes("UTF8");
			byte[] encrypt = cipherFile.doFinal(utf8);
			encrypted=new Base64().encodeAsString(cipherMemory.doFinal(encrypt));
		} catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}
		return encrypted;
	}

	/**
	 * Melakukan dekripsi teks
	 *
	 * @param chiperText
	 *            teks yang akan didekripsi
	 * @return teks hasil depkripsi
	 */
	public String decryptText(String chiperText) {
		if (chiperText == null) {
			return null;
		}
		String decrypted = null;
		try {
			cipherFile.init(Cipher.DECRYPT_MODE, secretKeyFile);
			cipherMemory.init(Cipher.DECRYPT_MODE, secretKeyMemory);
			byte[] decrypt = new Base64().decode(chiperText.trim());
			// log.info("decrypt size = " + decrypt.length);
			byte[] utf8 = cipherMemory.doFinal(decrypt);
			decrypted = new String(cipherFile.doFinal(utf8), "UTF8");
		} catch (Exception e){
			throw new RuntimeException("Gagal decrypt teks '" + chiperText.trim() + "' : " + e.getMessage());
		}
		return decrypted;
	}
	
	public static String MD5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] md5hash = new byte[32];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		md5hash = md.digest();
		return convertToHex(md5hash);
    }
	
	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		System.out.println(buf.toString());
		return buf.toString();
	}
	
	static public void main(String[] args){
		Cryptograph.getInstance().encryptText("password");
		System.out.println(Cryptograph.getInstance().decryptText("otnK0os8ZbI5Dy5WT59ENQLBllA325pJ"));
	}
}
