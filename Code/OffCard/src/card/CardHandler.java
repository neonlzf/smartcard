package card;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.xml.internal.ws.api.message.Message;

import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.service.CardServiceException;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminalException;
import opencard.core.terminal.CommandAPDU;
import opencard.core.terminal.ResponseAPDU;
import opencard.core.util.OpenCardPropertyLoadingException;
import opencard.opt.util.PassThruCardService;

public class CardHandler implements CTListener {
	private static CardHandler reference = null;

	private SmartCard card = null;

	private CardHandler() {
		try {
			// start the SmartCard
			SmartCard.start();

			// install this object as a CardTerminalListener
			EventGenerator.getGenerator().addCTListener(this);

			// Test, if a SmartCard is inserted
			this.cardInserted(null);
		} catch (OpenCardPropertyLoadingException plfe) {
			System.out.println("OpenCardPropertyLoadingException: ");
			System.out.println(plfe.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println("ClassNotFoundException: ");
			System.out.println(cnfe.getMessage());
		} catch (CardServiceException cse) {
			System.out.println("CardServiceException: ");
			System.out.println(cse.getMessage());
		} catch (CardTerminalException cte) {
			System.out.println("CardTerminalException: ");
			System.out.println(cte.getMessage());
		} catch (Exception e) {
			System.out.println("Exception: ");
			System.out.println(e.getMessage());
		}
	}

	public static CardHandler getInstance() {
		if (CardHandler.reference == null)
			CardHandler.reference = new CardHandler();
		return reference;
	}

	public void cardInserted(CardTerminalEvent arg0) throws CardTerminalException {
		System.out.println("card inserted");
		try {
			CardRequest cardRequest = new CardRequest(CardRequest.ANYCARD, null, PassThruCardService.class);
			cardRequest.setTimeout(1);
			card = SmartCard.waitForCard(cardRequest);

			if (card != null) {
				System.out.println("SmartCard gefunden");
			} else {
				System.out.println("Bitte SmartCard einfügen");
				JOptionPane.showMessageDialog(null, "Bitte SmartCard einfügen!", "SmartCard no Found",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			System.out.println("Exception: ");
			System.out.println(e.getMessage());
		}
	}

	public void cardRemoved(CardTerminalEvent arg0) throws CardTerminalException {
		// TODO Automatisch erstellter Methoden-Stub

	}

	public byte[] sendData(byte[] instruction, byte[] Data) throws ClassNotFoundException, IOException {
		CommandAPDU commandAPDU = new CommandAPDU(instruction.length + Data.length);
		commandAPDU.setLength(instruction.length + Data.length);
		System.arraycopy(instruction, 0, commandAPDU.getBuffer(), 0, instruction.length);
		System.arraycopy(Data, 0, commandAPDU.getBuffer(), instruction.length, Data.length);
		return send(commandAPDU);
	}

	public byte[] sendInstruction(byte[] instruction) throws ClassNotFoundException, IOException {
		CommandAPDU commandAPDU = new CommandAPDU(instruction.length);
		commandAPDU.setLength(instruction.length);
		System.arraycopy(instruction, 0, commandAPDU.getBuffer(), 0, instruction.length);
		return send(commandAPDU);
	}

	private byte[] send(CommandAPDU commandAPDU) throws IOException, ClassNotFoundException {
		PassThruCardService passThru = (PassThruCardService) card.getCardService(PassThruCardService.class, true);
		for (Byte b : commandAPDU.getBuffer()) {
			if (b.intValue() <= 15 && b.intValue() >= 0) {
				System.err.print("0" + Integer.toHexString(b & 0x00ff));
			} else
				System.err.print(Integer.toHexString(b & 0x00ff));
		}
		System.out.println();
		ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);
		byte[] ret = new byte[responseAPDU1.getBuffer().length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (responseAPDU1.getBuffer()[i] & 0x00ff);
		}
		return ret;

	}
}