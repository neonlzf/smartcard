package data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class MedListModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Med> meds;
	private boolean withDosage;
	private MedDataManagement mdm;

	public MedListModel(boolean withDosage) {
		this.meds = new ArrayList<Med>();
		this.withDosage = withDosage;
		this.mdm = MedDataManagement.getInstance();
	}

	public void parseData(ArrayList<ByteBuffer> medsRaw) {
		for (Iterator iterator = medsRaw.iterator(); iterator.hasNext();) {
			ByteBuffer byteBuffer = (ByteBuffer) iterator.next();
			Med tempMed = new Med();
			tempMed.setId(byteBuffer.getInt());

			// search Med name
			tempMed.setName(mdm.getMedName(tempMed.getId()));

			// parsing dosage list
			if (this.withDosage) {
				ArrayList<Dosage> dosages = new ArrayList<Dosage>();
				for (int i = 0; i < 24; i++) {
					byte temp = byteBuffer.get();
					if ((byte) (temp & 0x1F) == (byte) 0x18) {
						continue;
					} else {
						int time = (temp & 0x1F);
						int amount = (temp >>> 5) & 0x07;
						Dosage tempDos = new Dosage(time, amount);
						dosages.add(tempDos);
					}
				}
				tempMed.setDosage(dosages);
			}
			this.meds.add(tempMed);

		}

		this.fireTableDataChanged();
	}

	public ArrayList<ByteBuffer> serializeData() {
		System.out.println(this.getClass() + ".serializeData() called");
		ArrayList<ByteBuffer> ret = new ArrayList<ByteBuffer>();
		int bufferSize = this.withDosage ? 28 : 4;
		System.out.println("Buffersize: " + bufferSize);

		for (Iterator iterator = meds.iterator(); iterator.hasNext();) {
			Med med = (Med) iterator.next();
			ByteBuffer tempBuf = ByteBuffer.allocate(bufferSize);

			tempBuf.putInt(med.getId());
			System.out.println("Med-ID: " + med.getId());

			if (this.withDosage) {
				List<Dosage> tempDosages = med.getDosage();

				for (int i = 0; i < 24; i++) {
					byte dosageByte = 0;
					try {
						Dosage tempDosage = tempDosages.get(i);
						int time = tempDosage.getTime();
						int amount = tempDosage.getAmount();
						dosageByte = (byte) ((byte) (amount << 5) | (byte) time);
					} catch (IndexOutOfBoundsException e) {
						dosageByte = (byte) 0x18;
					}

					tempBuf.put(dosageByte);
				}
			}

			ret.add(tempBuf);
		}

		return ret;
	}

	public void addElement(Med newMed) {
		this.meds.add(newMed);
		this.fireTableDataChanged();
	}

	public void removeElement(int index) {
		meds.remove(index);
		this.fireTableDataChanged();
	}

	public Med getElement(int index) {
		try {
			return this.meds.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		try {
			return this.meds.size();
		} catch (NullPointerException e) {
			return 0;
		}

	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "ID";
		case 1:
			return "Name";

		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return meds.get(rowIndex).getId();
		case 1:
			return meds.get(rowIndex).getName();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// do nothing
	}

}
