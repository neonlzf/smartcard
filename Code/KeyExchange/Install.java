package rsa;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminalException;
import opencard.core.terminal.CommandAPDU;
import opencard.core.terminal.ResponseAPDU;
import opencard.opt.util.PassThruCardService;

public class Install implements CTListener {

	private SmartCard card = null;

	static byte littleA = 0x0005;

	public static void main(String[] args) throws NoSuchAlgorithmException, ClassNotFoundException, InterruptedException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		Install blubb = new Install();
		SmartCard.start();
		EventGenerator.getGenerator().addCTListener(blubb);
		blubb.cardInserted(null);
		byte[] selApplet = { 0x00, (byte) 0xA4, 0x04, 0x00, 0x06, 0x52, 0x53, 0x41, 0x41, 0x70, 0x70, 0x00 };
		blubb.sendInstruction(selApplet);
		byte[] insGetExp = { 0x00, (byte) 0x00F2 };
		byte[] insGetMod = { 0x00, (byte) 0x00F0 };
		byte[] pubModulusCache = blubb.sendInstruction(insGetExp);
		byte[] pubModulus = new byte[pubModulusCache.length - 2];
		byte[] pubExpCache = blubb.sendInstruction(insGetMod);
		byte[] pubExp = new byte[pubExpCache.length-1];
		System.out.println(pubModulusCache.length);
		System.arraycopy(pubModulusCache, 0, pubModulus, 0, pubModulusCache.length - 2);
		System.arraycopy(pubExpCache, 0, pubExp, 1, pubExpCache.length - 2);
//		pubExp[0] = (byte) (pubExp[0] & 0x7F);
		RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(pubExp), new BigInteger(pubModulus));
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pub = factory.generatePublic(spec);
		System.out.println(pub);

		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		keyGen.init(56);
		SecretKey secretKey = keyGen.generateKey();
		for (Byte b : secretKey.getEncoded())
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pub);
		byte[] cipherText = cipher.doFinal(secretKey.getEncoded());
		byte[] insDecodeKey = { 0x00, (byte) 0xD2, 0x00, 0x00,(byte) cipherText.length };
		for (Byte b : blubb.sendData(insDecodeKey, cipherText))
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
	}

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
