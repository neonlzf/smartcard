package data;

import java.util.List;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class DosageTableModel extends DefaultTableModel {
	private List<Dosage> dosage;

	public DosageTableModel(List<Dosage> dosageData) {
		this.dosage = dosageData;
	}

	public List<Dosage> getDosage() {
		return dosage;
	}

	public void addElement(Dosage d) {
		this.dosage.add(d);
		this.fireTableDataChanged();
	}

	public void removeElement(int index) {
		try {
			this.dosage.remove(index);
			this.fireTableDataChanged();
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public Dosage getElement(int index) {
		try {
			return this.dosage.get(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Time";
		case 1:
			return "Amount";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		try {
			return dosage.size();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			Dosage temp = this.dosage.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return temp.getTime();
			case 1:
				return temp.getAmount();
			default:
				return null;
			}
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

}
