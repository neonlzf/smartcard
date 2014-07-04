package rsa;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

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

public class Install implements CTListener {

	private SmartCard card = null;

//	byte[] selApplet = { 0x00, (byte) 0x00A4, 0x0004, 0x0000, 0x0009, 0x0072, 0x0073, 0x0061, 0x0041, 0x0070, 0x0070, 0x006C, 0x0065, 0x0074, 0x0000 };
	byte[] selApplet = {0x00, (byte) 0xA4, 0x04, 0x00, 0x06, 0x61, 0x65, 0x73, 0x61, 0x70, 0x70, 0x00};
	static byte littleA = 0x0005;

	// byte[] key = { 0x000, (byte) 0x00b3, (byte) 0x00b9, 0x007, 0x0022, (byte)
	// 0x00c6, 0x006e, 0x0051, 0x002c, (byte) 0x00d5, 0x0019, (byte) 0x00d3,
	// (byte) 0x00ea, (byte) 0x00b9, 0x003c, 0x0039, 0x0030, (byte) 0x00eb,
	// (byte) 0x00c9, (byte) 0x00de, (byte) 0x00c3, (byte) 0x0090, 0x0026,
	// (byte) 0x00ae, (byte) 0x00db, (byte) 0x00f0, 0x0030, 0x0065, 0x0062,
	// 0x0045, (byte) 0x00d8, (byte) 0x00af, 0x0070, 0x0028, (byte) 0x00c5,
	// 0x0019, 0x003c, 0x0020, 0x0033, (byte) 0x0083, (byte) 0x00c1, 0x003c,
	// (byte) 0x00d1, (byte) 0x00e0, 0x0020, 0x004b, 0x0051, (byte) 0x00c7,
	// (byte) 0x0083, (byte) 0x00cc, (byte) 0x00f3, 0x001b, (byte) 0x00e3,
	// (byte) 0x00fa, 0x0063, (byte) 0x0085, 0x001, 0x004c, (byte) 0x00b9,
	// 0x005, 0x0019, (byte) 0x00f0, (byte) 0x00b0, 0x0057, (byte) 0x00d3 };

	public static void main(String[] args) throws NoSuchAlgorithmException, ClassNotFoundException, InterruptedException, IOException {
		Install blubb = new Install();
		SmartCard.start();
		EventGenerator.getGenerator().addCTListener(blubb);
		blubb.cardInserted(null);
		// KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		// generator.initialize(512);
		// KeyPair kp = generator.generateKeyPair();
		// RSAPublicKey PubKey = (RSAPublicKey) kp.getPublic();
		// RSAPrivateKey PriKey = (RSAPrivateKey) kp.getPrivate();
		// byte[] pubModulus = PubKey.getModulus().toByteArray();
		// byte[] pubExp = PubKey.getPublicExponent().toByteArray();
		// byte[] priModulus = PriKey.getModulus().toByteArray();
		// byte[] priExponent = PriKey.getPrivateExponent().toByteArray();

		byte[] g = { 0x0002 };
		byte[] p = { 0x000D };
		byte[] bigA = { (byte) ((((int) Math.pow((double) g[0], (double) littleA)) % ((int) p[0])) & 0x00ff) };
		System.out.println(bigA[0]);
		System.out.println(g[0]);
		System.out.println(p[0]);
		blubb.selectApplet();
		while (System.in.read() != 'x')
			;
		blubb.sendA(bigA);
		while (System.in.read() != 'x')
			;
		blubb.sendG(g);
		while (System.in.read() != 'x')
			;
		blubb.sendP(p);
		 while(System.in.read() != 'x');
		 blubb.getB();

	}



	private void selectApplet() throws ClassNotFoundException, InterruptedException, IOException {
		PassThruCardService passThru = (PassThruCardService) card.getCardService(PassThruCardService.class, true);
		CommandAPDU commandAPDU = new CommandAPDU(selApplet.length);
		commandAPDU.setLength(selApplet.length);
		System.arraycopy(selApplet, 0, commandAPDU.getBuffer(), 0, selApplet.length);
		for (Byte b : commandAPDU.getBuffer()) {
			if (b.intValue() <= 15 && b.intValue() >= 0) {
				System.err.print("0" + Integer.toHexString(b & 0x00ff));
			} else
				System.err.print(Integer.toHexString(b & 0x00ff));
		}
		System.out.println();
		while (System.in.read() != 'x')
			;
		ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);
		byte[] ret = responseAPDU1.getBuffer();
		for (Byte b : ret)
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
	}

	private void sendA(byte[] Data) throws ClassNotFoundException, InterruptedException, IOException {
		byte[] instruction = { 0x0000, 0x0002, 0x00, 0x00, (byte) (Data.length) };
		sendData(instruction, Data);
	}

	private void sendP(byte[] Data) throws ClassNotFoundException, InterruptedException, IOException {
		byte[] instruction = { 0x0000, 0x0022, 0x00, 0x00, (byte) Data.length };
		sendData(instruction, Data);

	}

	private void sendG(byte[] Data) throws ClassNotFoundException, InterruptedException, IOException {
		byte[] instruction = { 0x0000, 0x0012, 0x00, 0x00, (byte) (Data.length) };
		sendData(instruction, Data);

	}
	
	private void getB() throws ClassNotFoundException, IOException {
		byte[] instruction = { 0x0000, 0x0032, 0x00, 0x00, (byte) 0 };
		sendData(instruction);
	}

	// private void sendPriExponent(byte[] Data) throws ClassNotFoundException,
	// InterruptedException, IOException {
	// byte[] instruction = { 0x0000, 0x0022, 0x00, 0x00, (byte) Data.length };
	// sendData(instruction, Data);
	//
	// }

	@Override
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
			}
		} catch (Exception e) {
			System.out.println("Exception: ");
			System.out.println(e.getMessage());
		}

	}

	@Override
	public void cardRemoved(CardTerminalEvent arg0) throws CardTerminalException {
		// TODO Auto-generated method stub

	}

	public void sendData(byte[] instruction, byte[] Data) throws ClassNotFoundException, IOException {
		PassThruCardService passThru = (PassThruCardService) card.getCardService(PassThruCardService.class, true);
		CommandAPDU commandAPDU = new CommandAPDU(instruction.length + Data.length);
		commandAPDU.setLength(instruction.length + Data.length);
		System.arraycopy(instruction, 0, commandAPDU.getBuffer(), 0, instruction.length);
		System.arraycopy(Data, 0, commandAPDU.getBuffer(), instruction.length , Data.length);
		byte[] ret = commandAPDU.getBuffer();
		for (Byte b : commandAPDU.getBuffer()) {
			if (b.intValue() <= 15 && b.intValue() >= 0) {
				System.err.print("0" + Integer.toHexString(b & 0x00ff));
			} else
				System.err.print(Integer.toHexString(b & 0x00ff));
		}
		System.out.println();
		while (System.in.read() != 'x')
			;
		ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);
		ret = responseAPDU1.getBuffer();
		for (Byte b : ret)
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();
	}
	
	public void sendData(byte[] instruction) throws ClassNotFoundException, IOException {
		PassThruCardService passThru = (PassThruCardService) card.getCardService(PassThruCardService.class, true);
		CommandAPDU commandAPDU = new CommandAPDU(instruction.length);
		commandAPDU.setLength(instruction.length);
		System.arraycopy(instruction, 0, commandAPDU.getBuffer(), 0, instruction.length);
		byte[] ret = commandAPDU.getBuffer();
		for (Byte b : commandAPDU.getBuffer()) {
			if (b.intValue() <= 15 && b.intValue() >= 0) {
				System.err.print("0" + Integer.toHexString(b & 0x00ff));
			} else
				System.err.print(Integer.toHexString(b & 0x00ff));
		}
		System.out.println();
		while (System.in.read() != 'x')
			;
		ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);
		ret = responseAPDU1.getBuffer();
		for (Byte b : ret)
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();
	}

}
