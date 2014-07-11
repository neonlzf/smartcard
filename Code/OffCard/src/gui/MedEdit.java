package gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import data.Dosage;
import data.DosagesModel;
import data.Med;
import data.MedDataManagement;
import data.MedEntry;
import data.MedListModel;

public class MedEdit extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldMedId;
	private JTextField textFieldMedName;
	private JTable dosageTable;

	private MedListModel mtm;
	private Med toEdit;
	private DosagesModel dtm;
	private MedDataManagement mdm;

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
	public MedEdit(MedListModel mtm, boolean withDosage) {
		setAlwaysOnTop(true);
		setTitle("Edit List Entry");
		final MedEdit tempFrame = this;
		this.mtm = mtm;
		this.mdm = MedDataManagement.getInstance();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 383, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
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
		gbc_textFieldMedId.gridwidth = 2;
		gbc_textFieldMedId.insets = new Insets(0, 0, 5, 5);
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
		gbc_textFieldMedName.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldMedName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMedName.gridx = 1;
		gbc_textFieldMedName.gridy = 1;
		contentPane.add(textFieldMedName, gbc_textFieldMedName);
		textFieldMedName.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new SearchAction());
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 5, 0);
		gbc_btnSearch.gridx = 2;
		gbc_btnSearch.gridy = 1;
		contentPane.add(btnSearch, gbc_btnSearch);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		contentPane.add(scrollPane, gbc_scrollPane);

		dosageTable = new JTable();
		this.dtm = new DosagesModel(new ArrayList<Dosage>());
		dosageTable.setModel(this.dtm);
		scrollPane.setViewportView(dosageTable);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 3;
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
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridwidth = 3;
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

	public MedEdit(MedListModel mtm, int index, boolean withDosage) {
		this(mtm, withDosage);

		this.toEdit = mtm.getElement(index);

		this.textFieldMedId.setText(new Integer(this.toEdit.getId()).toString());
		this.textFieldMedName.setText(this.toEdit.getName());
		this.dtm = new DosagesModel(toEdit.getDosage());
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

	private class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			List<MedEntry> tempList = MedEdit.this.mdm.search(MedEdit.this.textFieldMedName.getText());
			if (tempList.size() == 1) {
				MedEntry me = tempList.get(0);
				MedEdit.this.textFieldMedId.setText(new Integer(me.getId()).toString());
				MedEdit.this.textFieldMedName.setText(me.getName());
			}else{
				MedEdit.this.textFieldMedId.setText("?");
			}
		}

	}
}
