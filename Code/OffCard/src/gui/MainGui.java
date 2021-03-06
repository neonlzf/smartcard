package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import opencard.core.terminal.CardTerminalException;
import card.CardHandler;
import data.MedListModel;
import data.PatientDataModel;

public class MainGui {

	private JFrame frmMediplaner;
	private JLabel lblBlacklist;
	private JLabel lblWhitelist;
	private JScrollPane whitelistScrollPane;
	private JTable whitelistTable;
	private JScrollPane blacklistScrollPane;
	private JTable blacklistTable;
	private JLabel lblPatientendaten;
	private JTable patientDataTable;
	private JButton btnReadSmartcard;
	private JButton btnWriteSmartcard;
	private JPanel blacklistPanel;
	private JButton btnAddBlacklist;
	private JPanel whitelistPanel;
	private JButton btnAddWhitelist;
	private JButton btnEditBlacklist;
	private JButton btnDeleteBlacklist;
	private JButton btnEditWhitelist;
	private JButton btnDeleteWhitelist;

	private PatientDataModel patientData;
	private MedListModel wlData;
	private MedListModel blData;

	private CardHandler ch;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.setOperatorLevel(0);
					window.frmMediplaner.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws CardTerminalException
	 */
	public MainGui() throws CardTerminalException {
		this.ch = CardHandler.getInstance();
		initialize();
	}

	public void setOperatorLevel(int opLevel) {
		switch (opLevel) {
		case 1:
			this.whitelistTable.setEnabled(true);
			this.btnAddWhitelist.setEnabled(true);
			this.btnEditWhitelist.setEnabled(true);
			this.btnDeleteWhitelist.setEnabled(true);

			this.blacklistTable.setEnabled(true);
			this.btnAddBlacklist.setEnabled(true);
			this.btnEditBlacklist.setEnabled(true);
			this.btnDeleteBlacklist.setEnabled(true);

			this.patientDataTable.setEnabled(true);

			this.btnWriteSmartcard.setEnabled(true);
			this.btnReadSmartcard.setEnabled(true);
			break;

		default:
			this.whitelistTable.setEnabled(false);
			this.btnAddWhitelist.setEnabled(false);
			this.btnEditWhitelist.setEnabled(false);
			this.btnDeleteWhitelist.setEnabled(false);

			this.blacklistTable.setEnabled(false);
			this.btnAddBlacklist.setEnabled(false);
			this.btnEditBlacklist.setEnabled(false);
			this.btnDeleteBlacklist.setEnabled(false);

			this.patientDataTable.setEnabled(false);

			this.btnWriteSmartcard.setEnabled(false);
			this.btnReadSmartcard.setEnabled(true);
			break;
		}

	}

	public void loadData(PatientDataModel patientData, MedListModel blData, MedListModel wlData) {
		this.patientData = patientData;
		this.blData = blData;
		this.wlData = wlData;

		this.patientDataTable.setModel(this.patientData);
		this.blacklistTable.setModel(this.blData);
		this.whitelistTable.setModel(this.wlData);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMediplaner = new JFrame();
		frmMediplaner.setTitle("MediPlaner");
		frmMediplaner.setBounds(100, 100, 550, 400);
		frmMediplaner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMediplaner.getContentPane().setLayout(new BorderLayout(0, 0));
		final MainGui tempRef = this;
		final JFrame tempJF = frmMediplaner;

		JPanel selectionPanel = new JPanel();
		FlowLayout fl_selectionPanel = (FlowLayout) selectionPanel.getLayout();
		fl_selectionPanel.setAlignment(FlowLayout.LEFT);
		selectionPanel.setMinimumSize(new Dimension(0, 100));
		frmMediplaner.getContentPane().add(selectionPanel, BorderLayout.NORTH);

		JLabel lblRolle = new JLabel("Rolle:");
		selectionPanel.add(lblRolle);

		JComboBox comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox source = (JComboBox) e.getSource();
				int selection = source.getSelectedIndex();
				System.out.println("ComboBox Selection: " + selection);
				tempRef.setOperatorLevel(selection);
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] { "", "Hausarzt", "Notarzt", "Pfleger", "Automat" }));
		selectionPanel.add(comboBox);

		btnReadSmartcard = new JButton("Load");
		btnReadSmartcard.addActionListener(new LoadAction());
		selectionPanel.add(btnReadSmartcard);

		btnWriteSmartcard = new JButton("Save");
		btnWriteSmartcard.addActionListener(new SaveAction());
		selectionPanel.add(btnWriteSmartcard);

		JPanel placeholderPanel = new JPanel();
		frmMediplaner.getContentPane().add(placeholderPanel, BorderLayout.CENTER);
		GridBagLayout gbl_placeholderPanel = new GridBagLayout();
		gbl_placeholderPanel.columnWidths = new int[] { 0, 0, 0 };
		gbl_placeholderPanel.rowHeights = new int[] { 10, 0, 0, 0, 0 };
		gbl_placeholderPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_placeholderPanel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0 };
		placeholderPanel.setLayout(gbl_placeholderPanel);

		lblPatientendaten = new JLabel("Patientendaten");
		GridBagConstraints gbc_lblPatientendaten = new GridBagConstraints();
		gbc_lblPatientendaten.gridwidth = 2;
		gbc_lblPatientendaten.insets = new Insets(0, 0, 5, 0);
		gbc_lblPatientendaten.gridx = 0;
		gbc_lblPatientendaten.gridy = 0;
		placeholderPanel.add(lblPatientendaten, gbc_lblPatientendaten);

		patientDataTable = new JTable();
		patientDataTable.setModel(new DefaultTableModel(new Object[][] { { "Patient-ID", null },
				{ "Blood type", null }, }, new String[] { "New column", "New column" }) {
			Class[] columnTypes = new Class[] { String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});
		GridBagConstraints gbc_patientDataTable = new GridBagConstraints();
		gbc_patientDataTable.gridwidth = 2;
		gbc_patientDataTable.insets = new Insets(0, 0, 5, 0);
		gbc_patientDataTable.fill = GridBagConstraints.BOTH;
		gbc_patientDataTable.gridx = 0;
		gbc_patientDataTable.gridy = 1;
		placeholderPanel.add(patientDataTable, gbc_patientDataTable);

		lblBlacklist = new JLabel("Blacklist");
		GridBagConstraints gbc_lblBlacklist = new GridBagConstraints();
		gbc_lblBlacklist.insets = new Insets(0, 0, 5, 5);
		gbc_lblBlacklist.gridx = 0;
		gbc_lblBlacklist.gridy = 2;
		placeholderPanel.add(lblBlacklist, gbc_lblBlacklist);

		lblWhitelist = new JLabel("Whitelist");
		GridBagConstraints gbc_lblWhitelist = new GridBagConstraints();
		gbc_lblWhitelist.insets = new Insets(0, 0, 5, 0);
		gbc_lblWhitelist.gridx = 1;
		gbc_lblWhitelist.gridy = 2;
		placeholderPanel.add(lblWhitelist, gbc_lblWhitelist);

		blacklistScrollPane = new JScrollPane();
		GridBagConstraints gbc_blacklistScrollPane = new GridBagConstraints();
		gbc_blacklistScrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_blacklistScrollPane.fill = GridBagConstraints.BOTH;
		gbc_blacklistScrollPane.gridx = 0;
		gbc_blacklistScrollPane.gridy = 3;
		placeholderPanel.add(blacklistScrollPane, gbc_blacklistScrollPane);

		blacklistTable = new JTable();
		blacklistTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "ID", "Name" }));
		blacklistScrollPane.setViewportView(blacklistTable);

		whitelistScrollPane = new JScrollPane();
		GridBagConstraints gbc_whitelistScrollPane = new GridBagConstraints();
		gbc_whitelistScrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_whitelistScrollPane.fill = GridBagConstraints.BOTH;
		gbc_whitelistScrollPane.gridx = 1;
		gbc_whitelistScrollPane.gridy = 3;
		placeholderPanel.add(whitelistScrollPane, gbc_whitelistScrollPane);

		whitelistTable = new JTable();
		whitelistTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "ID", "Name" }));
		whitelistScrollPane.setViewportView(whitelistTable);

		blacklistPanel = new JPanel();
		GridBagConstraints gbc_blacklistPanel = new GridBagConstraints();
		gbc_blacklistPanel.insets = new Insets(0, 0, 0, 5);
		gbc_blacklistPanel.fill = GridBagConstraints.BOTH;
		gbc_blacklistPanel.gridx = 0;
		gbc_blacklistPanel.gridy = 4;
		placeholderPanel.add(blacklistPanel, gbc_blacklistPanel);

		btnAddBlacklist = new JButton("Add");
		btnAddBlacklist.addActionListener(new BlackListAddAction());
		blacklistPanel.add(btnAddBlacklist);

		btnEditBlacklist = new JButton("Edit");
		btnEditBlacklist.addActionListener(new BlackListEditAction());
		blacklistPanel.add(btnEditBlacklist);

		btnDeleteBlacklist = new JButton("Delete");
		btnDeleteBlacklist.addActionListener(new BlackListDeleteAction());
		blacklistPanel.add(btnDeleteBlacklist);

		whitelistPanel = new JPanel();
		GridBagConstraints gbc_whitelistPanel = new GridBagConstraints();
		gbc_whitelistPanel.fill = GridBagConstraints.BOTH;
		gbc_whitelistPanel.gridx = 1;
		gbc_whitelistPanel.gridy = 4;
		placeholderPanel.add(whitelistPanel, gbc_whitelistPanel);

		btnAddWhitelist = new JButton("Add");
		btnAddWhitelist.addActionListener(new WhiteListAddAction());
		whitelistPanel.add(btnAddWhitelist);

		btnEditWhitelist = new JButton("Edit");
		btnEditWhitelist.addActionListener(new WhiteListEditAction());
		whitelistPanel.add(btnEditWhitelist);

		btnDeleteWhitelist = new JButton("Delete");
		btnDeleteWhitelist.addActionListener(new WhiteListDeleteAction());
		whitelistPanel.add(btnDeleteWhitelist);
	}

	private class LoadAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// load Data from SmartCard
			new Runnable() {

				@Override
				public void run() {
					// get patient id
					byte[] inst1 = { 0x00, 0x08, 0x00, 0x00 };
					byte[] patientIdRaw = null;
					try {
						patientIdRaw = ch.decodeDES(ch.sendInstruction(inst1));
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// get bloodtype
					byte[] inst2 = { 0x00, 0x07, 0x00, 0x00 };
					byte[] bloodTypeRaw = null;
					try {
						bloodTypeRaw = ch.decodeDES(ch.sendInstruction(inst2));
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					System.out.println("Patient-ID: " + CardHandler.bytesToHex(patientIdRaw));
					System.out.println("Bloodtype: " + CardHandler.bytesToHex(bloodTypeRaw));

					PatientDataModel patientData = new PatientDataModel();
					patientData.parseData(patientIdRaw, bloodTypeRaw);

					// read blacklist
					ArrayList<ByteBuffer> blRaw = new ArrayList<ByteBuffer>();
					for (int i = 0; i < 128; i++) {
						byte[] instBl = { 0x00, 0x01, 0x00, (byte) i };
						byte[] blItem = null;
						try {
							blItem = ch.decodeDES(ch.sendInstruction(instBl));
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						System.out.println("readBlacklistItem(" + i + ") returned: " + CardHandler.bytesToHex(blItem));

						if (blItem.length == 0) {
							System.out.println("Blacklist - Items read: " + i);
							break;
						}

						ByteBuffer tempBuffer = ByteBuffer.allocate(blItem.length);
						tempBuffer.put(blItem);
						tempBuffer.rewind();
						blRaw.add(tempBuffer);
					}

					MedListModel blData = new MedListModel(false);
					blData.parseData(blRaw);

					// read whitelist
					ArrayList<ByteBuffer> wlRaw = new ArrayList<ByteBuffer>();
					for (int i = 0; i < 128; i++) {
						byte[] instWl = { 0x00, 0x04, 0x00, (byte) i };
						byte[] wlItem = null;
						try {
							wlItem = ch.decodeDES(ch.sendInstruction(instWl));
						} catch (ClassNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						System.out.println("readWhitelistItem(" + i + ") returned: " + CardHandler.bytesToHex(wlItem));

						if (wlItem.length == 0) {
							System.out.println("Whitelist - Items read: " + i);
							break;
						}

						ByteBuffer tempBuffer = ByteBuffer.allocate(wlItem.length);
						tempBuffer.put(wlItem);
						tempBuffer.rewind();
						wlRaw.add(tempBuffer);
					}

					MedListModel wlData = new MedListModel(true);
					wlData.parseData(wlRaw);

					final PatientDataModel fpd = patientData;
					final MedListModel fbl = blData;
					final MedListModel fwl = wlData;

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							MainGui.this.loadData(fpd, fbl, fwl);

						}
					});

				}
			}.run();
		}
	}

	private class SaveAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			new Runnable() {

				@Override
				public void run() {
					// emptying med lists on card
					byte[] inst1 = { 0x00, 0x03, 0x00, 0x00 };
					try {
						ch.sendInstruction(inst1);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					byte[] inst2 = { 0x00, 0x06, 0x00, 0x00 };
					try {
						ch.sendInstruction(inst2);
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					// write blacklist to card
					List<ByteBuffer> bufBl = MainGui.this.blData.serializeData();
					System.out.println("blacklist buffer length: " + bufBl.size());
					byte[] inst3 = { 0x00, 0x02, 0x00, 0x00, 0x04 };

					for (Iterator iterator = bufBl.iterator(); iterator.hasNext();) {
						ByteBuffer byteBuffer = (ByteBuffer) iterator.next();
						byte data[] = byteBuffer.array();
						System.out.println("Data: " + data.length + " " + ch.bytesToHex(data));
						try {
							byte cryptedData[] = ch.cipherwithPadding(data);
							System.out
									.println("Crypted Data: " + cryptedData.length + " " + ch.bytesToHex(cryptedData));
							inst3[4] = (byte) cryptedData.length;
							ch.sendData(inst3, cryptedData);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					// write whitelist to card
					List<ByteBuffer> bufWl = MainGui.this.wlData.serializeData();
					byte[] inst4 = { 0x00, 0x05, 0x00, 0x00, 0x1C };

					for (Iterator iterator = bufWl.iterator(); iterator.hasNext();) {
						ByteBuffer byteBuffer = (ByteBuffer) iterator.next();
						byte data[] = byteBuffer.array();

						try {
							byte cryptedData[] = ch.cipherwithPadding(data);
							inst4[4] = (byte) cryptedData.length;
							ch.sendData(inst4, cryptedData);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}.run();
		}

	}

	private class BlackListAddAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			MedEdit me = new MedEdit(MainGui.this.blData, false);
			me.setLocationRelativeTo(MainGui.this.frmMediplaner);
			me.setVisible(true);
		}

	}

	private class BlackListEditAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selection = MainGui.this.blacklistTable.getSelectedRow();
			if (selection > -1) {
				MedEdit me = new MedEdit(MainGui.this.blData, selection, false);
				me.setLocationRelativeTo(MainGui.this.frmMediplaner);
				me.setVisible(true);
			}
		}

	}

	private class BlackListDeleteAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selectedRow = MainGui.this.blacklistTable.getSelectedRow();
			if (selectedRow > -1)
				MainGui.this.blData.removeElement(selectedRow);
		}

	}

	private class WhiteListAddAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			MedEdit me = new MedEdit(MainGui.this.wlData, true);
			me.setLocationRelativeTo(MainGui.this.frmMediplaner);
			me.setVisible(true);
		}

	}

	private class WhiteListEditAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selection = MainGui.this.whitelistTable.getSelectedRow();
			if (selection > -1) {
				MedEdit me = new MedEdit(MainGui.this.wlData, selection, true);
				me.setLocationRelativeTo(MainGui.this.frmMediplaner);
				me.setVisible(true);
			}
		}

	}

	private class WhiteListDeleteAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int selectedRow = MainGui.this.whitelistTable.getSelectedRow();
			if (selectedRow > -1)
				MainGui.this.wlData.removeElement(selectedRow);
		}

	}
}
