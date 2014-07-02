package patient;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;

public class Patient extends Applet {

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		// GP-compliant JavaCard applet registration
		new Patient().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
		
		blacklist = new DrugList((byte) 4);
		whitelist = new DrugList((byte) 28);
		PatientID = new byte[8];
		Bloodtype = (byte) 0xFF;
	}

	static DrugList blacklist;

	static DrugList whitelist;

	static byte Bloodtype;

	static byte[] PatientID;

	public void process(APDU apdu) {
		// Good practice: Return 9000 on SELECT
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
			sendWhitelist(buf, apdu);
			break;
		case (byte) 0x04:
			addToWhitelist(buf, apdu);
			break;
		case (byte) 0x05:
			sendBloodType(buf, apdu);
			break;
		case (byte) 0x06:
			sendPatientId(buf, apdu);
			break;
		default:
			// good practice: If you don't know the INStruction, say so:
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

		for (short i = 0; i <= blacklist.size() && !(i < 0); i++) {
			byte buffer[] = blacklist.getElement(i);
			for (byte s = 0; s < blacklist.datalength; s++) {
				buf[s] = buffer[s];
			}
			sendEncryptedAPDU(apdu, (short) blacklist.datalength);
		}
	}

	private void addToBlacklist(byte[] buf, APDU apdu) {

		byte[] buffer = new byte[blacklist.datalength];

		for (byte i = 0; i < blacklist.datalength; i++)
			buffer[i] = buf[ISO7816.OFFSET_CDATA + i];

		if (blacklist.add(buffer)) { // Success
			buf[0] = 0x01;
			sendEncryptedAPDU(apdu, (short) 1);
		} else {
			buf[0] = 0x00;
			sendEncryptedAPDU(apdu, (short) 1);
		}

	}

	private void sendWhitelist(byte[] buf, APDU apdu) {
		for (short i = 0; i <= whitelist.size() && !(i < 0); i++) {
			byte buffer[] = whitelist.getElement(i);
			for (byte s = 0; s < whitelist.datalength; s++) {
				buf[s] = buffer[s];
			}
			sendEncryptedAPDU(apdu, (short) whitelist.datalength);
		}
	}

	private void addToWhitelist(byte[] buf, APDU apdu) {
		byte[] buffer = new byte[whitelist.datalength];

		for (byte i = 0; i < whitelist.datalength; i++)
			buffer[i] = buf[ISO7816.OFFSET_CDATA + i];

		if (whitelist.add(buffer)) { // Success
			buf[0] = 0x01;
			sendEncryptedAPDU(apdu, (short) 1);
		} else {
			buf[0] = 0x00;
			sendEncryptedAPDU(apdu, (short) 1);
		}
	}

	private void sendBloodType(byte[] buf, APDU apdu) {
		buf[0] = Bloodtype;
		sendEncryptedAPDU(apdu, (short) 1);
	}

	private void sendPatientId(byte[] buf, APDU apdu) {
		for (byte i = 0; i < PatientID.length; i++) {
			buf[i] = PatientID[i];
		}
		sendEncryptedAPDU(apdu, (short) PatientID.length);
	}

	private void sendEncryptedAPDU(APDU apdu, short len) {
		// Encrypt APDU
		apdu.setOutgoingAndSend((short) 0, len);
	}

}

