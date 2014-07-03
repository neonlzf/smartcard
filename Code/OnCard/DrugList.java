package patient;

import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class DrugList {

	private static short listLength = 128;

	public byte datalength;

	public byte[] list;

	private short firstfree;

	private short capLeft;

	private byte buffer[];
	
	short i;

	public DrugList(byte dataLength) {
		this.datalength = dataLength;
		buffer = new byte[dataLength];
		list = new byte[(short) (listLength * datalength)];
		capLeft = listLength;
		firstfree = 0;
	}

	public short size() {

		return ((short) (listLength - capLeft));
	}

	public byte[] getElement(short n) {

		if (n < size())
			Util.arrayCopy(list, (short) (n * datalength), buffer, (short) 0,
					datalength);
		else
			ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
		return buffer;
	}

	public void add(byte[] ID) {

		if (capLeft > 0) {
			Util.arrayCopy(ID, (short) 0, list,
					(short) (firstfree * datalength), datalength);
			firstfree++;
			capLeft--;
			ISOException.throwIt(ISO7816.SW_NO_ERROR);
		} else
			ISOException.throwIt(ISO7816.SW_FILE_FULL);
	}

	public void remove(byte[] ID) {

		for (i = 0; i < listLength * datalength; i = (short) (i + datalength)) {
			if (Util.arrayCompare(list, i, ID, (short) 0, (short) 4) == 0) {
				Util.arrayCopy(list, (short) (i + datalength), list, i,
						(short) (size() * datalength - i));
				firstfree--;
				capLeft++;
				ISOException.throwIt(ISO7816.SW_NO_ERROR);
			}
		}
		ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	}
}

