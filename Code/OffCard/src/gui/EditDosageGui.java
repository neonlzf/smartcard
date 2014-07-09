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

import javax.swing.JComboBox;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

import data.Dosage;
import data.DosagesModel;

public class EditDosageGui extends JFrame {

	private JPanel contentPane;
	private JComboBox comboBoxTime;
	private JComboBox comboBoxAmount;

	private DosagesModel dtm;
	private int selected = -2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EditDosageGui frame = new EditDosageGui(null);
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
	public EditDosageGui(DosagesModel dtm) {
		setAlwaysOnTop(true);
		this.dtm = dtm;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 275, 128);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblTimeinHours = new JLabel("Time (in hours)");
		GridBagConstraints gbc_lblTimeinHours = new GridBagConstraints();
		gbc_lblTimeinHours.anchor = GridBagConstraints.EAST;
		gbc_lblTimeinHours.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimeinHours.gridx = 0;
		gbc_lblTimeinHours.gridy = 0;
		contentPane.add(lblTimeinHours, gbc_lblTimeinHours);

		comboBoxTime = new JComboBox();
		comboBoxTime.setModel(new DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
		GridBagConstraints gbc_comboBoxTime = new GridBagConstraints();
		gbc_comboBoxTime.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxTime.gridx = 1;
		gbc_comboBoxTime.gridy = 0;
		contentPane.add(comboBoxTime, gbc_comboBoxTime);

		JLabel lblAmount = new JLabel("Amount");
		GridBagConstraints gbc_lblAmount = new GridBagConstraints();
		gbc_lblAmount.anchor = GridBagConstraints.EAST;
		gbc_lblAmount.insets = new Insets(0, 0, 5, 5);
		gbc_lblAmount.gridx = 0;
		gbc_lblAmount.gridy = 1;
		contentPane.add(lblAmount, gbc_lblAmount);

		comboBoxAmount = new JComboBox();
		comboBoxAmount.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }));
		GridBagConstraints gbc_comboBoxAmount = new GridBagConstraints();
		gbc_comboBoxAmount.insets = new Insets(0, 0, 5, 0);
		gbc_comboBoxAmount.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBoxAmount.gridx = 1;
		gbc_comboBoxAmount.gridy = 1;
		contentPane.add(comboBoxAmount, gbc_comboBoxAmount);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		contentPane.add(panel, gbc_panel);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new OkAction());
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new CancelAction());
		panel.add(btnCancel);
	}

	public EditDosageGui(DosagesModel dtm, int selectedIndex) {
		this(dtm);
		this.selected = selectedIndex;
		Dosage d = this.dtm.getElement(this.selected);
		this.comboBoxTime.setSelectedIndex(d.getTime());
		this.comboBoxAmount.setSelectedIndex(d.getAmount() - 1);
	}

	private class OkAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (EditDosageGui.this.selected == -2) {
				Dosage temp = new Dosage(EditDosageGui.this.comboBoxTime.getSelectedIndex(),
						EditDosageGui.this.comboBoxAmount.getSelectedIndex() + 1);
				EditDosageGui.this.dtm.addElement(temp);
			} else {
				Dosage d = EditDosageGui.this.dtm.getElement(EditDosageGui.this.selected);
				d.setTime(EditDosageGui.this.comboBoxTime.getSelectedIndex());
				d.setAmount(EditDosageGui.this.comboBoxAmount.getSelectedIndex() + 1);
				EditDosageGui.this.dtm.fireTableDataChanged();
			}
			EditDosageGui.this.dispose();
		}
	}

	private class CancelAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			EditDosageGui.this.dispose();
		}

	}
}
