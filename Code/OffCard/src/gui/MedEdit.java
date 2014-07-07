package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import data.Dosage;
import data.DosageTableModel;
import data.Med;
import data.MedTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.util.ArrayList;

public class MedEdit extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldMedId;
	private JTextField textFieldMedName;
	private JTable dosageTable;

	private MedTableModel mtm;
	private Med toEdit;
	private DosageTableModel dtm;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MedEdit frame = new MedEdit(null, false);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @wbp.parser.constructor
	 */
	public MedEdit(MedTableModel mtm, boolean withDosage) {
		setAlwaysOnTop(true);
		setTitle("Edit List Entry");
		final MedEdit tempFrame = this;
		this.mtm = mtm;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblMedid = new JLabel("Med-ID");
		GridBagConstraints gbc_lblMedid = new GridBagConstraints();
		gbc_lblMedid.anchor = GridBagConstraints.EAST;
		gbc_lblMedid.insets = new Insets(0, 0, 5, 5);
		gbc_lblMedid.gridx = 0;
		gbc_lblMedid.gridy = 0;
		contentPane.add(lblMedid, gbc_lblMedid);

		textFieldMedId = new JTextField();
		GridBagConstraints gbc_textFieldMedId = new GridBagConstraints();
		gbc_textFieldMedId.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldMedId.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMedId.gridx = 1;
		gbc_textFieldMedId.gridy = 0;
		contentPane.add(textFieldMedId, gbc_textFieldMedId);
		textFieldMedId.setColumns(10);

		JLabel lblMedname = new JLabel("Med-Name");
		GridBagConstraints gbc_lblMedname = new GridBagConstraints();
		gbc_lblMedname.anchor = GridBagConstraints.EAST;
		gbc_lblMedname.insets = new Insets(0, 0, 5, 5);
		gbc_lblMedname.gridx = 0;
		gbc_lblMedname.gridy = 1;
		contentPane.add(lblMedname, gbc_lblMedname);

		textFieldMedName = new JTextField();
		GridBagConstraints gbc_textFieldMedName = new GridBagConstraints();
		gbc_textFieldMedName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldMedName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMedName.gridx = 1;
		gbc_textFieldMedName.gridy = 1;
		contentPane.add(textFieldMedName, gbc_textFieldMedName);
		textFieldMedName.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		contentPane.add(scrollPane, gbc_scrollPane);

		dosageTable = new JTable();
		this.dtm = new DosageTableModel(new ArrayList<Dosage>());
		dosageTable.setModel(this.dtm);
		scrollPane.setViewportView(dosageTable);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		contentPane.add(panel_1, gbc_panel_1);

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new AddDosageAction());
		panel_1.add(btnAdd);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new DeleteDosageAction());

		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new EditDosageAction());
		panel_1.add(btnEdit);
		panel_1.add(btnDelete);

		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 4;
		contentPane.add(panel, gbc_panel);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new OkAction());
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new CancelAction());
		panel.add(btnCancel);

		this.dosageTable.setEnabled(withDosage);
		btnAdd.setEnabled(withDosage);
		btnDelete.setEnabled(withDosage);
		btnEdit.setEnabled(withDosage);

	}

	public MedEdit(MedTableModel mtm, int index, boolean withDosage) {
		this(mtm, withDosage);

		this.toEdit = mtm.getElement(index);

		this.textFieldMedId.setText(new Integer(this.toEdit.getId()).toString());
		this.textFieldMedName.setText(this.toEdit.getName());
		this.dtm = new DosageTableModel(toEdit.getDosage());
		this.dosageTable.setModel(this.dtm);
		try {
			((DefaultTableModel) this.dosageTable.getModel()).fireTableDataChanged();
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	private void addElement() {
		Med newMed = new Med();
		try {
			newMed.setId(Integer.parseInt(this.textFieldMedId.getText()));
		} catch (NumberFormatException e) {
		}
		newMed.setName(this.textFieldMedName.getText());
		newMed.setDosage(this.dtm.getDosage());
		this.mtm.addElement(newMed);
		this.dispose();
	}

	private void editElemtent() {
		try {
			toEdit.setId(Integer.parseInt(this.textFieldMedId.getText()));
		} catch (NumberFormatException e) {
		}
		toEdit.setName(this.textFieldMedName.getText());
		toEdit.setDosage(this.dtm.getDosage());
		mtm.fireTableDataChanged();
		this.dispose();
	}

	private class OkAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (MedEdit.this.toEdit == null) {
				MedEdit.this.addElement();
			} else {
				MedEdit.this.editElemtent();
			}
		}
	}

	private class CancelAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			MedEdit.this.dispose();
		}

	}

	private class AddDosageAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EditDosageGui ed = new EditDosageGui(MedEdit.this.dtm);
			ed.setLocationRelativeTo(MedEdit.this);
			ed.setVisible(true);

		}

	}

	private class EditDosageAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selectedRow = MedEdit.this.dosageTable.getSelectedRow();
			if (selectedRow > -1) {
				EditDosageGui ed = new EditDosageGui(MedEdit.this.dtm, selectedRow);
				ed.setLocationRelativeTo(MedEdit.this);
				ed.setVisible(true);
			}
		}
	}

	private class DeleteDosageAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selectedRow = MedEdit.this.dosageTable.getSelectedRow();
			if (selectedRow > -1) {
				MedEdit.this.dtm.removeElement(selectedRow);
			}
		}

	}
}
