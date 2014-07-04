package Cipher;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.DESKey;
import javacard.security.KeyBuilder;
import javacardx.crypto.Cipher;

public class AESApp extends Applet {

	/**
	 * Ins_Encrypt = EO
	 */
	private static final byte INS_ENCRYPT = (byte) 0xE0;

	/**
	 * Ins_Decrypt = DO
	 */
	private static final byte INS_DECRYPT = (byte) 0xD0;

	/** Temporary buffer in RAM. */
	byte[] tmp;

	/** Key for encryption. */
	DESKey key;

	/**
	 * The Key Code =
	 * {0x0000,0x0001,0x0002,0x0003,0x0004,0x0005,0x0006,0x0007,0x0008} by
	 * Default
	 */
	private static byte[] keyCode = { 0x0000, 0x0001, 0x0002, 0x0003, 0x0004, 0x0005, 0x0006, 0x0007,0x0008,0x0000 };

	/** Cipher for encryption and decryption. */
	Cipher cipher;

	public AESApp() {
		tmp = JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
		cipher = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M1, false);
		key = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
		key.setKey(keyCode, (short) 0);
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		new AESApp().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
	}

	public void process(APDU apdu) {
		byte[] buf = apdu.getBuffer();
		byte ins = buf[ISO7816.OFFSET_INS];
		short lc = (short) (buf[ISO7816.OFFSET_LC] & 0x00FF);
		short outLength;
		if (selectingApplet()) {
			return;
		}
		switch (ins) {
		case INS_ENCRYPT:
			readBuffer(apdu, tmp, (short) 0, lc);
			cipher.init(key, Cipher.MODE_ENCRYPT);
			outLength = cipher.doFinal(tmp, (short) 0, lc, buf, (short) 0);
			apdu.setOutgoingAndSend((short) 0, outLength);
			break;
		case INS_DECRYPT:
			readBuffer(apdu, tmp, (short) 0, lc);
			cipher.init(key, Cipher.MODE_DECRYPT);
			outLength = cipher.doFinal(tmp, (short) 0, lc, buf, (short) 0);
			apdu.setOutgoingAndSend((short) 0, outLength);
			break;
		default:
			ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
		}
	}

	private void readBuffer(APDU apdu, byte[] dest, short offset, short length) {
		byte[] buf = apdu.getBuffer();
		short readCount = apdu.setIncomingAndReceive();
		short i = 0;
		Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, dest, offset, readCount);
		while ((short) (i + readCount) < length) {
			i = (short) (i + (readCount));
			offset = (short) (offset + readCount);
			readCount = (short) apdu.receiveBytes(ISO7816.OFFSET_CDATA);
			Util.arrayCopy(buf, ISO7816.OFFSET_CDATA, dest, offset, readCount);
		}
	}
}
