package patient;

public class DrugList {

	private static short listLength = 128;

	public byte datalength;

	public byte[] list;

	private short firstfree;

	private short capLeft;

	public DrugList(byte dataLength) {
		this.datalength = dataLength;
		list = new byte[(short) (listLength * datalength)];
		capLeft = listLength;
		firstfree = 0;
	}

	public short size() {

		return ((short) (listLength - capLeft));
	}

	public byte[] getElement(short n) {

		if (n < size() - 1) {
			byte buf[] = new byte[datalength];
			for (byte i = 0; i < datalength; i++)
				buf[i] = list[n * datalength + i];
			return buf;
		} else
			return new byte[] { 0, 0, 0, 0 };
	}

	public boolean add(byte[] ID) {

		if (capLeft > 0) {
			for (byte i = 0; i < datalength; i++) {
				list[firstfree * datalength + i] = ID[i];
			}
			firstfree++;
			capLeft--;
			return true;
		} else
			return false;
	}

	public boolean remove(byte[] ID) {

		for (short i = 0; i < listLength * datalength; i = (short) (i + datalength)) {
			if (list[i] == ID[0] && list[i + 1] == ID[1]
					&& list[i + 2] == ID[2] && list[i + 3] == ID[3]) {
				for (short s = i; s < listLength * datalength; s++)
					list[s] = list[s + datalength];
				firstfree--;
				capLeft++;
				return true;
			}
		}
		return false;
	}
}

