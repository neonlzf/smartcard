package patient;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class Patient extends Applet {

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration
		new Patient().register(bArray, (short) (bOffset + 1), bArray[bOffset]);

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

		switch (buf[ISO7816.OFFSET_INS]) {
		case (byte) 0x00:
			imHere(buf, apdu);
			break;
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
		sendEncryptedAPDU(apdu, (short) 7);
	}

	private void sendBlacklist(byte[] buf, APDU apdu) {

		short element = Util.getShort(buf, (short) (ISO7816.OFFSET_P1 & 0x00FF));
		byte buffer[] = blacklist.getElement(element);
		Util.arrayCopy(buffer, (short) 0, buf, (short) 0, blacklist.datalength);
		sendEncryptedAPDU(apdu, blacklist.datalength);
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
		sendEncryptedAPDU(apdu, whitelist.datalength);
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
		sendEncryptedAPDU(apdu, (short) 1);
	}

	private void sendPatientId(byte[] buf, APDU apdu) {

		for (i = 0; i < PatientID.length; i++) {
			buf[i] = PatientID[i];
		}
		sendEncryptedAPDU(apdu, (short) PatientID.length);
	}

	private void sendEncryptedAPDU(APDU apdu, short len) {
		// Encrypt APDU
		apdu.setOutgoing();
		apdu.setOutgoingLength(len);
		apdu.sendBytes((short) 0, len);
		ISOException.throwIt(ISO7816.SW_NO_ERROR);
	}
}