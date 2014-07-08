package data;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableModel;

public class PatientDataTableModel extends DefaultTableModel {

	private long patientId;
	private short bloodType;
	private String[] bloodTypes = { "A -", "A +", "B -", "B +", "AB -", "AB +", "0 -", "0 +" };

	public PatientDataTableModel() {
	}

	public void parseData(byte[] patientIdRaw, byte[] bloodTypeRaw) {
		ByteBuffer patBuffer = ByteBuffer.allocate(10);
		patBuffer.put(patientIdRaw);
		patBuffer.rewind();
		this.patientId = patBuffer.getLong();

		ByteBuffer btBuffer = ByteBuffer.allocate(3);
		btBuffer.put(bloodTypeRaw);
		btBuffer.rewind();
		this.bloodType = (short) btBuffer.get(0);
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
				return new Long(this.patientId).toString();
			case 1:
				return bloodTypes[this.bloodType];
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
