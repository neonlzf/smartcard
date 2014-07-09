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
		Install ocf = new Install();
		SmartCard.start();
		EventGenerator.getGenerator().addCTListener(ocf);
		ocf.cardInserted(null);
		// 00 A4 04 00 06 52 53 41 41 70 70 00
		byte[] selApplet = { 0x00, (byte) 0xA4, 0x04, 0x00, 0x06, 0x52, 0x53, 0x41, 0x41, 0x70, 0x70, 0x00 };
		
		System.out.print("Select Applet: ");
		ocf.sendInstruction(selApplet);
		byte[] insGetExp = { 0x00, (byte) 0x00F2 };
		byte[] insGetMod = { 0x00, (byte) 0x00F0 };
		
		System.out.print("Get Exponent: ");
		byte[] pubModulusCache = ocf.sendInstruction(insGetExp);
		byte[] pubModulus = new byte[pubModulusCache.length - 2];
		
		System.out.print("Get Modulus: ");
		byte[] pubExpCache = ocf.sendInstruction(insGetMod);
		byte[] pubExp = new byte[pubExpCache.length - 1];
		System.arraycopy(pubModulusCache, 0, pubModulus, 0, pubModulusCache.length - 2);
		System.arraycopy(pubExpCache, 0, pubExp, 1, pubExpCache.length - 2);
		// pubExp[0] = (byte) (pubExp[0] & 0x7F);
		RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(pubExp), new BigInteger(pubModulus));
		KeyFactory factory = KeyFactory.getInstance("RSA");
		PublicKey pub = factory.generatePublic(spec);
//		System.out.println(pub);

		
		SecretKey secretKey = generateDESKey();
		Cipher desCipher = Cipher.getInstance("DES/ECB/NoPadding");
		desCipher.init(Cipher.ENCRYPT_MODE, secretKey);

		System.out.print("Secret Key:");
		for (Byte b : secretKey.getEncoded())
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();

		byte[] cipherText = encodeDesKey(secretKey.getEncoded(),pub);
		byte[] answer = { 0x00, (byte) 0xD2, 0x00, 0x00, (byte) cipherText.length };
		
		System.out.print("Send Secret Key: ");
		for (Byte b : ocf.sendData(answer, cipherText))
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();
		
		byte[] hallowelt = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i','j','k','m','n','o','p','q','b','l','u','b','b' };
		byte[] cipherHallo = cipherwithPadding(desCipher, hallowelt);		
		byte[] ins = { 0x00, (byte) 0xD4, 0x00, 0x00, (byte) cipherHallo.length };

		System.out.print("Send Hallo Welt: ");
		for (Byte b : ocf.sendData(ins, cipherHallo))
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();
		
		System.out.println("Hallo Welt: ");
		for (Byte b : hallowelt)
			System.out.print(Integer.toHexString(b & 0x00ff) + ",");
		System.out.println();

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
				System.out.print("0" + Integer.toHexString(b & 0x00ff));
			} else
				System.out.print(Integer.toHexString(b & 0x00ff));
		}
		System.out.println();
		ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);
		byte[] ret = new byte[responseAPDU1.getBuffer().length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = (byte) (responseAPDU1.getBuffer()[i] & 0x00ff);
		}
		return ret;

	}

	private static SecretKey generateDESKey() throws NoSuchAlgorithmException{
		KeyGenerator keyGen = KeyGenerator.getInstance("DES");
		keyGen.init(56);
		return keyGen.generateKey();
	}
	
	private static byte[] encodeDesKey(byte[] secretKey,PublicKey pub) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		final Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pub);
		return cipher.doFinal(secretKey);
	}
	
	private static byte[] cipherwithPadding(Cipher desCipher, byte[] text) throws IllegalBlockSizeException, BadPaddingException{
		if(text.length % 8 != 0){
			byte[] newText = new byte[text.length + (8-(text.length % 8))];
			System.out.println("Textlenght"+newText.length);
			System.arraycopy(text, 0, newText, 0, text.length);
			return desCipher.doFinal(newText);
		}
		return desCipher.doFinal(text);
	}
}