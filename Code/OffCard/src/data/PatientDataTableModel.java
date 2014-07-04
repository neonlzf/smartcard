package data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class PatientDataTableModel extends DefaultTableModel {

	private int patientId;
	private short bloodtype;

	public PatientDataTableModel() {
	}

	public void parseData() {
		this.patientId = 12345;
		this.bloodtype = 0;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return null;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			switch (rowIndex) {
			case 0:
				return "Patient-ID";
			case 1:
				return "Blood type";
			default:
				return "";
			}
		case 1:
			switch (rowIndex) {
			case 0:
				return new Integer(this.patientId).toString();
			case 1:
				return new Integer(this.bloodtype).toString();
			default:
				return "";
			}
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

	}

}
