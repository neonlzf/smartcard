package data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class MedTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Med> meds;

	public MedTableModel() {
		this.meds = new ArrayList<Med>();
	}

	public void parseData(ArrayList<ByteBuffer> medsRaw) {
		for (Iterator iterator = medsRaw.iterator(); iterator.hasNext();) {
			ByteBuffer byteBuffer = (ByteBuffer) iterator.next();
			Med tempMed = new Med();
			tempMed.setId(byteBuffer.getInt());
			this.meds.add(tempMed);
		}

		this.fireTableDataChanged();
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
