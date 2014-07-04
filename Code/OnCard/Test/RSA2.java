package CipherApp;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.security.ECPrivateKey;
import javacard.security.ECPublicKey;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateCrtKey;
import javacard.security.RSAPublicKey;
import javacard.security.Signature;
import javacard.security.KeyBuilder;
import javacardx.crypto.Cipher;

public class RSA2 extends Applet {

//	class byte
	private final static byte CLA = (byte) 0x80;
	//	export own modulus and public exponent
	private final static byte EXPORT_RSA_PUBLIC_MOD = (byte) 0xF0;
	private final static byte EXPORT_RSA_PUBLIC_EXP = (byte) 0xF2;
	//	import other party's modulus and public exponent
	private final static byte IMPORT_RSA_PUBLIC_MOD = (byte) 0xE0;
	private final static byte IMPORT_RSA_PUBLIC_EXP = (byte) 0xE2;
	//	encode/decode with public exponent (own/other party)
	private final static byte RSA_ENCODE = (byte) 0xD0;
	private final static byte RSA_DECODE = (byte) 0xD2;
	//	own key pair
	private KeyPair keyPair;
	private RSAPrivateCrtKey rsa_privateKey;
	private RSAPublicKey rsa_publicKey;
	
	ECPrivateKey ecprikey;
	ECPublicKey ecpubkey;
	//	other party's public key
	private RSAPublicKey otherPartyPublicKey;

	private Cipher rsaCipher = null;

	private Signature signature = null;

	private final static short BYTE_SIZE = 8;
	private final static short ARRAY_SIZE = 128;
	private final static short ARRAY_SIZE_BITS = BYTE_SIZE*ARRAY_SIZE;

	private RSA2(byte[] bArray, short bOffset, byte bLength) {
		/** this party's key: create RSA-key-pair object (in CRT-form) */
		keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, (short) ARRAY_SIZE_BITS);
		/** generate "unique" RSA-key-pair (secure random involved) */
		keyPair.genKeyPair();
		/** get private key (in CRT-form) */
		rsa_privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
		/** get public key */
		rsa_publicKey = (RSAPublicKey) keyPair.getPublic();
		/** create an uninitialized cryptographic RSA-key
		 *  for the other partie's key */
		otherPartyPublicKey =
			(RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,
					                           KeyBuilder.LENGTH_RSA_1024,
					                           false);
		
		/** Instance of the Signature algorithm ALG_RSA_MD5_PKCS1 :
		 *  generates a 16-byte MD5 digest, pads the digest according 
		 *  to the PKCS#1 (v1.5) scheme, and encrypts it using RSA.
		 *  false means: no external access, i.e. no sharing with other applets */
		signature = Signature.getInstance(Signature.ALG_RSA_MD5_PKCS1, false);
		/** initialize the signature object with the appropriate Key for signing
		 */
		signature.init(rsa_privateKey, Signature.MODE_SIGN);
	}
	public static void install(byte[] bArray, short bOffset, byte bLength) {
		(new RSA2(bArray, bOffset, bLength)).register();
	} 
	public boolean select() {
		return true;
	} 
	public void deselect() {
	} 
	public void process(APDU apdu) {
		byte[] buf = apdu.getBuffer();
		short lc = (short) (buf[ISO7816.OFFSET_LC] & (short) 0x00FF) ;
		switch (buf[ISO7816.OFFSET_CLA]) {
		case CLA :
			switch (buf[ISO7816.OFFSET_INS]) {
			// export own modulus and public exponent
			case EXPORT_RSA_PUBLIC_MOD :
				exportPublicModulus(apdu);
				break;
			case EXPORT_RSA_PUBLIC_EXP :
				exportPublicExponent(apdu);
				break;
			// export other parties modulus and public exponent
			case IMPORT_RSA_PUBLIC_MOD :
				importPublicModulus(apdu,lc);
				break;
			case IMPORT_RSA_PUBLIC_EXP :
				importPublicExponent(apdu,lc);
				break;
			// create cipher text from input
			case RSA_ENCODE :
				rsa_encode(apdu);
				break;
			// decode cipher text from input
			case RSA_DECODE :
				rsa_decode(apdu);
				break;
			case ISO7816.CLA_ISO7816 :
				if (selectingApplet()) {
					ISOException.throwIt(ISO7816.SW_NO_ERROR);
				}
				break;
			default : 
				ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
			}
		}
	}
	private void exportPublicModulus(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		// get the modulus and store it in the apdu buffer
		short modLen = rsa_publicKey.getModulus(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) modLen);
	}
	private void exportPublicExponent(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		// get the public exponent and store it in the apdu buffer
		short expLen = rsa_publicKey.getExponent(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) expLen);
	}
	private void importPublicModulus(APDU apdu, short lc) {
		byte[] buffer = apdu.getBuffer();
		// set the modulus with the data from the apdu buffer
		otherPartyPublicKey.setModulus(buffer, ISO7816.OFFSET_CDATA, lc);
	}
	private void importPublicExponent(APDU apdu, short lc) {
		byte[] buffer = apdu.getBuffer();
		// set the modulus with the data from the apdu buffer
		otherPartyPublicKey.setExponent(buffer, ISO7816.OFFSET_CDATA, lc);
	}
	private void rsa_encode(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		short byteRead = (short) (apdu.setIncomingAndReceive());
		// initialize the cipher for encryption
		rsaCipher.init(otherPartyPublicKey, Cipher.MODE_ENCRYPT);
		short ret =
			rsaCipher.doFinal(
					buffer,
					(short) ISO7816.OFFSET_CDATA,
					byteRead,
					buffer,
					(short) 0);
		apdu.setOutgoingAndSend((short) 0, ret);
	}
	private void rsa_decode(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		short byteRead = (short) (apdu.setIncomingAndReceive());
		// initialize the cipher for encryption
		rsaCipher.init(rsa_privateKey, Cipher.MODE_DECRYPT);
		short ret =
			rsaCipher.doFinal(
					buffer,
					(short) ISO7816.OFFSET_CDATA,
					byteRead,
					buffer,
					(short) 0);
		apdu.setOutgoingAndSend((short) 0, ret);
	}

}
