package CipherApp;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateCrtKey;
import javacard.security.RSAPublicKey;
import javacardx.crypto.Cipher;

public class RSA extends Applet {

	/**
	 * EXPORT_RSA_PUBLIC_Key = F0
	 */
	private final static byte EXPORT_RSA_MOD_EXP = (byte) 0xF0;

	/**
	 * EXPORT_RSA_PUBLIC_Key = F2
	 */
	private final static byte EXPORT_RSA_PUBLIC_EXP = (byte) 0xF2;

	/**
	 * encode/decode with public exponent (own/other party) = D0
	 */
	private final static byte RSA_ENCODE = (byte) 0xD0;

	/**
	 * =D2
	 */
	private final static byte RSA_DECODE = (byte) 0xD2;

	/** Temporary buffer in RAM. */
	byte[] tmp;

	// own key pair
	private KeyPair keyPair;

	private RSAPrivateCrtKey rsa_privateKey;

	private RSAPublicKey rsa_publicKey;

	private Cipher rsaCipher = null;

	private byte[] exponent = { 0x0003,0x00, 0x00, 0x0003 };

	public RSA() {
		tmp = JCSystem.makeTransientByteArray((short) 256, JCSystem.CLEAR_ON_RESET);
		rsaCipher.getInstance(Cipher.ALG_RSA_NOPAD, false);
		keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
		rsa_privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
		rsa_publicKey = (RSAPublicKey) keyPair.getPublic();
		rsa_publicKey.setExponent(exponent, (short) 0, (short) 3);
		keyPair.genKeyPair();

	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration

		new RSA().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu) {
		// Good practice: Return 9000 on SELECT
		if (selectingApplet()) {
			return;
		}

		byte[] buf = apdu.getBuffer();
		short lc = (short)(buf[ISO7816.OFFSET_LC] & 0x00FF);
		switch (buf[ISO7816.OFFSET_INS]) {
		case EXPORT_RSA_PUBLIC_EXP:
			exportPublicExponent(apdu);
			break;
		case EXPORT_RSA_MOD_EXP:
			exportPublicModulus(apdu);
			break;
		// export other parties modulus and public exponent
		case RSA_ENCODE:
			rsa_encode(apdu);
//			readBuffer(apdu,tmp,(short)0,lc);
//			apdu.setOutgoing();
//            rsaCipher.init(rsa_publicKey,Cipher.MODE_ENCRYPT);
//            short outLength = rsaCipher.doFinal(tmp,(short)0,lc,buf,(short)0);
//            apdu.setOutgoingLength(outLength);
//            apdu.sendBytes((short)0,outLength);
			break;
		// decode cipher text from input
		case RSA_DECODE:
			rsa_decode(apdu);
			break;
		case ISO7816.CLA_ISO7816:
			if (selectingApplet()) {
				ISOException.throwIt(ISO7816.SW_NO_ERROR);
			}
			break;
		default:
			ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
		}

	}

	private void exportPublicModulus(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		// get the public exponent and store it in the apdu buffer
		short expLen = rsa_publicKey.getModulus(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) expLen);
	}

	private void exportPublicExponent(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		// get the public exponent and store it in the apdu buffer
		short expLen = rsa_publicKey.getExponent(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) expLen);
	}

	private void rsa_encode(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		byte bufferEnc[] = new byte[128];
		rsaCipher.init(rsa_publicKey, Cipher.MODE_ENCRYPT);
//		rsaCipher.doFinal(buffer, (short) ISO7816.OFFSET_CDATA, (short) 3, bufferEnc, (short) 0);
		javacard.framework.Util.arrayCopy(bufferEnc, (short) 0, buffer, (short) 0, (short) 128);
		apdu.setOutgoingAndSend((short) 0, (short) 128);
	}

	private void rsa_decode(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		short byteRead = (short) (apdu.setIncomingAndReceive());
		// initialize the cipher for encryption
		rsaCipher.init(rsa_privateKey, Cipher.MODE_DECRYPT);
		short ret = rsaCipher.doFinal(buffer, (short) ISO7816.OFFSET_CDATA, byteRead, buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, ret);
	}

	private void readBuffer(APDU apdu, byte[] dest, short offset, short length) {
		byte[] buf = apdu.getBuffer();
		short readCount = apdu.setIncomingAndReceive();
		short i = 0;
		Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, dest, offset, readCount);
		while ((short) (i + readCount) < length) {
			i += readCount;
			offset += readCount;
			readCount = (short) apdu.receiveBytes(ISO7816.OFFSET_CDATA);
			Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, dest, offset, readCount);
		}
	}
}
