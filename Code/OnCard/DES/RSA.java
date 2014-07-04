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
/**
 * 
 * @author rkupferschmied
 * @see usage: 
 * cm>  /select |RSAApp
 *  => 00 A4 04 00 06 52 53 41 41 70 70 00                .....RSAApp.
 *  (381956 nsec)
 *  <= 90 00                                              ..
 * Status: No Error
 * cm>  /send 00D000000701020304050607
 *  => 00 D0 00 00 07 01 02 03 04 05 06 07                ............
 *  (1217 usec)
 *  <= 02 7C E5 76 07 45 BC B3 D0 8B 03 FB E6 1E 7C 79    .|.v.E........|y
 *     5C C8 BE 97 9D 74 94 B6 C4 6A 86 48 23 17 F4 5D    \....t...j.H#..]
 *     DE 4B 19 00 03 F1 5A C4 A0 7E 0B 17 B6 61 0B A6    .K....Z..~...a..
 *     6A A2 C1 AA 21 32 CE 97 A6 CB 23 34 39 98 7B A0    j...!2....#49.{.
 *     90 00                                              ..
 * Status: No Error
 * cm>  /send 00D2000040027CE5760745BCB3D08B03FBE61E7C795CC8BE979D7494B6C46A86482317F45DDE4B190003F15AC4A07E0B17B6610BA66AA2C1AA2132CE97A6CB233439987BA0
 *  => 00 D2 00 00 40 02 7C E5 76 07 45 BC B3 D0 8B 03    ....@.|.v.E.....
 *    FB E6 1E 7C 79 5C C8 BE 97 9D 74 94 B6 C4 6A 86    ...|y\....t...j.
 *    48 23 17 F4 5D DE 4B 19 00 03 F1 5A C4 A0 7E 0B    H#..].K....Z..~.
 *    17 B6 61 0B A6 6A A2 C1 AA 21 32 CE 97 A6 CB 23    ..a..j...!2....#
 *    34 39 98 7B A0                                     49.{.
 *  (3893 usec)
 *  <= 01 02 03 04 05 06 07 90 00                         .........
 * Status: No Error
 * 
 */
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

	/**
	 * ja man muss den Exponenten von Hand setzten
	 */
	private byte[] exponent = { 0x00, 0x00, 0x0003 };

	public RSA() {
		tmp = JCSystem.makeTransientByteArray((short) 256, JCSystem.CLEAR_ON_RESET);
		rsaCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
		keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512);
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
		short outLength;
		byte[] buf = apdu.getBuffer();
		short lc = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
		switch (buf[ISO7816.OFFSET_INS]) {
		case EXPORT_RSA_PUBLIC_EXP:
			exportPublicExponent(apdu);
			break;
		case EXPORT_RSA_MOD_EXP:
			exportPublicModulus(apdu);
			break;
		// export other parties modulus and public exponent
		case RSA_ENCODE:
			// rsa_encode(apdu);
			readBuffer(apdu, tmp, (short) 0, lc);
			apdu.setOutgoing();
			rsaCipher.init(rsa_publicKey, Cipher.MODE_ENCRYPT);
			outLength = rsaCipher.doFinal(tmp, (short) 0, lc, buf, (short) 0);
			apdu.setOutgoingLength(outLength);
			apdu.sendBytes((short) 0, outLength);
			break;
		// decode cipher text from input
		case RSA_DECODE:
			readBuffer(apdu, tmp, (short) 0, lc);
			apdu.setOutgoing();
			rsaCipher.init(rsa_privateKey, Cipher.MODE_DECRYPT);
			outLength = rsaCipher.doFinal(tmp, (short) 0, lc, buf, (short) 0);
			apdu.setOutgoingLength(outLength);
			apdu.sendBytes((short) 0, outLength);
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
		// rsaCipher.doFinal(buffer, (short) ISO7816.OFFSET_CDATA, (short) 3,
		// bufferEnc, (short) 0);
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
