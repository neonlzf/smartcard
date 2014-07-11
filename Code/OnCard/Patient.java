package patient;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.DESKey;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateCrtKey;
import javacard.security.RSAPublicKey;
import javacardx.crypto.Cipher;

public class Patient extends Applet {

	/**
	 * EXPORT_RSA_PUBLIC_Key = F0
	 */
	private final static byte EXPORT_RSA_PUB_MOD = (byte) 0xF0;

	/**
	 * EXPORT_RSA_PUBLIC_Key = F2
	 */
	private final static byte EXPORT_RSA_PUB_EXP = (byte) 0xF2;

	/**
	 * encode/decode with public exponent (own/other party) = D0
	 */
	private final static byte RSA_ENCODE = (byte) 0xD0;

	/**
	 * =D2
	 */
	private final static byte RSA_DECODE = (byte) 0xD2;

	/**
	 * =D4
	 */
	private final static byte DES_DECODE = (byte) 0xD4;

	private static final byte DES_CODE = (byte) 0xD6;

	/** Temporary buffer in RAM. */
	byte[] tmp;

	// own key pair
	private KeyPair keyPair;

	private RSAPrivateCrtKey rsa_privateKey;

	private RSAPublicKey rsa_publicKey;

	private Cipher rsaCipher = null;

	private Cipher desCipher = null;

	private DESKey desKey = null;

	private byte[] exponent = { 0x00, 0x00, 0x0003 };

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration
		Patient pat = new Patient();
		pat.register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		pat.tmp = JCSystem.makeTransientByteArray((short) 256, JCSystem.CLEAR_ON_RESET);

		byte li = bArray[bOffset];
		byte lc = bArray[bOffset + li + 1];
		byte la = bArray[bOffset + li + lc + 2];
		blacklist = new DrugList((byte) 4);
		whitelist = new DrugList((byte) 28);
		PatientID = new byte[8];

		// bLength must be 9
		for (byte i = 0; i < 8; i++)
			PatientID[i] = bArray[bOffset + li + lc + 3 + i];
		Bloodtype = (byte) bArray[bOffset + li + lc + la + 2];
		buffer = new byte[whitelist.datalength];

		pat.generateRSAKeyPair();
	}

	static byte buffer[];

	static DrugList blacklist;

	static DrugList whitelist;

	static byte Bloodtype;

	static byte[] PatientID;

	byte i;

	public void process(APDU apdu) {
		if (selectingApplet()) {
			return;
		}

		// Decrypt APDU Data --> resolves in decrypted APDU
		// ************************************************

		byte[] buf = apdu.getBuffer();
		short outLength;
		short lc = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);

		switch (buf[ISO7816.OFFSET_INS]) {
		// case (byte) 0x00:
		// imHere(buf, apdu);
		// break;
		case (byte) 0x01:
			sendBlacklist(buf, apdu);
			break;
		case (byte) 0x02:
			addToBlacklist(buf, apdu);
			break;
		case (byte) 0x03:
			emptyBlacklist();
			break;
		case (byte) 0x04:
			sendWhitelist(buf, apdu);
			break;
		case (byte) 0x05:
			addToWhitelist(buf, apdu);
			break;
		case (byte) 0x06:
			emptyWhitelist();
			break;
		case (byte) 0x07:
			sendBloodType(buf, apdu);
			break;
		case (byte) 0x08:
			sendPatientId(buf, apdu);
			break;
		// krypto related
		case EXPORT_RSA_PUB_EXP:
			exportPublicExponent(apdu);
			break;
		case EXPORT_RSA_PUB_MOD:
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
			DecodeAndSetDESKey(apdu, lc);
			break;
		case DES_DECODE:
			Decode(apdu, lc);
		case ISO7816.CLA_ISO7816:
			if (selectingApplet()) {
				ISOException.throwIt(ISO7816.SW_NO_ERROR);
			}
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	private void imHere(byte[] buf, APDU apdu) {
		buf[0] = 'I';
		buf[1] = 'M';
		buf[2] = '_';
		buf[3] = 'H';
		buf[4] = 'E';
		buf[5] = 'R';
		buf[6] = 'E';
		sendAPDU(apdu, (short) 7);
	}

	private void sendBlacklist(byte[] buf, APDU apdu) {

		short element = Util.getShort(buf, (short) (ISO7816.OFFSET_P1 & 0x00FF));
		byte buffer[] = blacklist.getElement(element);
		Util.arrayCopy(buffer, (short) 0, buf, (short) 0, blacklist.datalength);
		sendAPDU(apdu, blacklist.datalength);
	}

	private void addToBlacklist(byte[] buf, APDU apdu) {

		Util.arrayCopy(buf, (short) (ISO7816.OFFSET_CDATA & 0x00FF), buffer, (short) 0, blacklist.datalength);
		blacklist.add(buffer);
	}

	private void emptyBlacklist() {
		blacklist.empty();
	}

	private void sendWhitelist(byte[] buf, APDU apdu) {
		short element = Util.getShort(buf, (short) (ISO7816.OFFSET_P1 & 0x00FF));
		byte buffer[] = whitelist.getElement(element);
		Util.arrayCopy(buffer, (short) 0, buf, (short) 0, whitelist.datalength);
		sendAPDU(apdu, whitelist.datalength);
	}

	private void addToWhitelist(byte[] buf, APDU apdu) {

		Util.arrayCopy(buf, (short) (ISO7816.OFFSET_CDATA & 0x00FF), buffer, (short) 0, whitelist.datalength);
		whitelist.add(buffer);
	}

	private void emptyWhitelist() {
		whitelist.empty();
	}

	private void sendBloodType(byte[] buf, APDU apdu) {

		buf[0] = Bloodtype;
		sendAPDU(apdu, (short) 1);
	}

	private void sendPatientId(byte[] buf, APDU apdu) {

		for (i = 0; i < PatientID.length; i++) {
			buf[i] = PatientID[i];
		}
		sendAPDU(apdu, (short) PatientID.length);
	}

	private void sendAPDU(APDU apdu, short len) {
		// Encrypt APDU		
		apdu.setOutgoing();
		apdu.setOutgoingLength(len);
		apdu.sendBytes((short) 0, len);
		ISOException.throwIt(ISO7816.SW_NO_ERROR);
	}

	// Krypto related methods
	private void exportPublicModulus(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		short expLen = rsa_publicKey.getModulus(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) expLen);
	}

	private void exportPublicExponent(APDU apdu) {
		byte buffer[] = apdu.getBuffer();
		short expLen = rsa_publicKey.getExponent(buffer, (short) 0);
		apdu.setOutgoingAndSend((short) 0, (short) expLen);
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

	private void generateRSAKeyPair() {
		rsaCipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
		keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
		rsa_privateKey = (RSAPrivateCrtKey) keyPair.getPrivate();
		rsa_publicKey = (RSAPublicKey) keyPair.getPublic();
		rsa_publicKey.setExponent(exponent, (short) 0, (short) 3);
		keyPair.genKeyPair();
		desCipher = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
		desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
	}

	private void DecodeAndSetDESKey(APDU apdu, short lc) {
		readBuffer(apdu, tmp, (short) 0, lc);
		apdu.setOutgoing();
		rsaCipher.init(rsa_privateKey, Cipher.MODE_DECRYPT);
		short outLength = rsaCipher.doFinal(tmp, (short) 0, lc, apdu.getBuffer(), (short) 0);
		desKey.setKey(apdu.getBuffer(), (short) 0);
		apdu.setOutgoingLength(outLength);
		apdu.sendBytes((short) 0, (short) 0); // den empfangenen Schl�ssel
		// nicht zur�ck senden
	}

	private void Decode(APDU apdu, short lc) {
		readBuffer(apdu, tmp, (short) 0, lc);
		desCipher.init(desKey, Cipher.MODE_DECRYPT);
		apdu.setOutgoing();
		short outLength = desCipher.doFinal(tmp, (short) 0, lc, apdu.getBuffer(), (short) 0);
		apdu.setOutgoingLength(outLength);
		apdu.sendBytes((short) 0, (short) 0);
	}
	
	private void Encode(APDU apdu, byte[] in){
		//TODO
	}
}