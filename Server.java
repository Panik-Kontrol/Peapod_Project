import java.security.Security;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.spec.DHParameterSpec;


public class Server {
//	String currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"\\src\\server\\";
//	String currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/src/server/";
	private static String OS = System.getProperty("os.name").toLowerCase();
    private static String currAbsPath;		
	BigInteger[] serverParams;
	//AttributeTuple attrs = new AttributeTuple();
	BigInteger[] serverAttrs = new BigInteger[5];
	public Server() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		if(OS.indexOf("win") >= 0) {
			currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"\\src\\server\\";
		}
		else {
			currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/src/server/";
		}
		serverParams = new BigInteger[4];
		File serverParamFile = new File(currAbsPath+"server_params.txt");
		if(serverParamFile.exists()) {
			// Server parameter settings already exists. Read and set params
			Scanner sc = new Scanner(serverParamFile);
			int idx = 0;
			while (sc.hasNextLine()) {
				serverParams[idx] = sc.nextBigInteger();
				idx++;
		    }
			sc.close();
		}
		else {
			// Generate parameters and create the file
			Random r = new Random();

			BigInteger p = BigInteger.probablePrime(1024,r);
	        BigInteger g = getGcdOneRandom(p.subtract(BigInteger.ONE), r);
	        BigInteger prKey = getRandomNum(p.subtract(BigInteger.valueOf(2)), r);
	        BigInteger pubKey = g.modPow(prKey, p);
	        
			serverParamFile.getParentFile().mkdirs();
			serverParamFile.createNewFile();
			FileWriter fileWriter = new FileWriter(serverParamFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.println(p);
			printWriter.println(g);
			printWriter.println(prKey);
			printWriter.print(pubKey);
			printWriter.close();
	        serverParams[0] = p;
	        serverParams[1] = g;
	        serverParams[2] = prKey;
	        serverParams[3] = pubKey;
		}
		/*for(int i=0; i<serverParams.length; ++i) {
			System.out.println("Server parameter " + i + " is "+serverParams[i]);
		}*/
	}
	public void connect(String username) {
		Security.addProvider(new BouncyCastleProvider());
		if(Security.getProvider("BC")==null) {
			System.out.println("Bouncy Castle Provider is NOT available");
		}
		else {
			System.out.println("Bouncy Castle provider is available");
		}
	}
	public BigInteger getP() {
		return serverParams[0];
	}
	public BigInteger getG() {
		return serverParams[1];
	}
	public BigInteger getPubKey() {
		return serverParams[3];
	}
	private BigInteger getPrivKey() {
		return serverParams[2];
	}
	public void storeKeyForReceiver(String receiverName, BigInteger key) {
		
	}
/*	public BigInteger getRandomNum(BigInteger upper_limit, Random r) {
		BigInteger result = new BigInteger(upper_limit.bitLength(), r);
		if(result.compareTo(upper_limit) >= 0 || result.compareTo(BigInteger.ONE) <= 0) {
			result = new BigInteger(upper_limit.bitLength(), r);
		}
		return result;
	}*/
	public BigInteger getRandomNum(BigInteger upper_limit, Random r) {
		BigInteger result = new BigInteger(upper_limit.bitLength(), r);
		while(result.compareTo(upper_limit) >= 0 || result.compareTo(BigInteger.ONE) <= 0) {
			result = new BigInteger(upper_limit.bitLength(), r);
		}
		return result;
	}
	public BigInteger getGcdOneRandom(BigInteger mod, Random r) {
		BigInteger result = new BigInteger(mod.bitLength(), r);
		while(result.compareTo(mod) >= 0 || result.compareTo(BigInteger.ONE) <= 0 || !result.gcd(mod).equals(BigInteger.ONE)) {
			result = new BigInteger(mod.bitLength(), r);
		}
		return result;
	}
	public int getAttrSize() {
		return serverAttrs.length;
	}
	public BigInteger sam(BigInteger base, BigInteger exponent, BigInteger mod) {
		String exponentBits = exponent.toString(2);
		BigInteger res = new BigInteger(base.toString());

		for (int i = 1; i < exponentBits.length(); i++) {
			res = res.multiply(res).mod(mod) ;
			if(exponentBits.charAt(i) == '1' ) {
				res = res.multiply(base).mod(mod);
			}
		
		}
		return res;
	}
	public void storeMessage(BigInteger m, BigInteger gr, BigInteger[] attr_messages, BigInteger[] attr_keys, String senderName, CertAuth ca) throws IOException {
		/*BigInteger kM = kE.modPow(serverParams[2],  serverParams[0]);//sam(kE, serverParams[2], serverParams[0]);
		System.out.println("kM: " + kM);
		BigInteger kM_inverse = kM.modInverse(serverParams[0]);
		BigInteger message = m.multiply(kM_inverse).mod(serverParams[0]);
		System.out.println("a(" + getG() +")^d(" + getPrivKey() + ") = \n" + sam(serverParams[1],serverParams[2],serverParams[0]));
		System.out.println(new String(message.toByteArray()));
		System.out.println(message.toString());*/ // Tested basic elgamal here and it worked fine
		BigInteger s = ca.serverLogin(senderName);
		File sendersFile = new File(currAbsPath+senderName+".txt");
		if(!sendersFile.exists()) {
			sendersFile.getParentFile().mkdirs();
			sendersFile.createNewFile();
			FileWriter fileWriter = new FileWriter(sendersFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(senderName);
			printWriter.print(" "+m.modPow(s, getP()));
			printWriter.print(" "+gr.modPow(s, getP()));
			for(int i = 0; i < attr_messages.length; i++) {
				printWriter.print(" "+attr_messages[i].modPow(s, getP()));
			}
			for(int i = 0; i < attr_messages.length; i++) {
				printWriter.print(" "+attr_keys[i].modPow(s, getP()));
			}
			printWriter.close();
		}
	}
	public BigInteger[] getMessage(String senderName, BigInteger[] creds, CertAuth ca) throws IOException {
		Random r = new Random();
		BigInteger result[] = new BigInteger[12];
		BigInteger s = ca.serverLogin(senderName);
		BigInteger s_inverse = s.modInverse(serverParams[0]);
		BigInteger blind_total = BigInteger.ONE;
		int count = 0;
		// Need to generate blinding factors first
		for(int i = 0; i < creds.length; ++i) {
			if(creds[i].equals(BigInteger.ONE)) {
				count++;
			}
		}
		for(int i = creds.length; i < creds.length; ++i) {
			if(creds[i].equals(BigInteger.ONE)) {
				count--;
				if(count <= 0) {
					creds[i] = getRandomNum(getP(), r);
					blind_total = blind_total.multiply(creds[i]).mod(getP());
				}
				else {
					creds[i] = blind_total.modInverse(getP());
				}
			}
		}
		
		File senderFile = new File(currAbsPath+senderName+".txt");
		if(!senderFile.exists()) {
			System.out.println("The sender " + senderName + " is not registered. Cannot retrieve your private key");
			result[0] = BigInteger.ZERO.subtract(BigInteger.ONE);
			return result;
		}
		Scanner sc = new Scanner(senderFile);
		while (sc.hasNextLine()) {
			if(sc.next(senderName) != null) {
				result[0] = new BigInteger(sc.next()).multiply(s_inverse).mod(getP()); // m
				result[1] = new BigInteger(sc.next()).multiply(s_inverse).mod(getP()); // gr
				for(int i = 0; i < creds.length; ++i) {
					if(creds[i].equals(BigInteger.ZERO)) {
						result[i+2] = BigInteger.ZERO.subtract(BigInteger.ONE);
					}
					else {
						result[i+2] = new BigInteger(sc.next()).multiply(s_inverse).mod(getP());					
					}
				}
				for(int i = creds.length; i < creds.length*2; ++i) {
					if(creds[i-creds.length].equals(BigInteger.ZERO)) {
						result[i+2] = BigInteger.ZERO.subtract(BigInteger.ONE);
					}
					else {
						result[i+2] = new BigInteger(sc.next()).multiply(s_inverse).mod(getP());
						result[i+2] = result[i+2].multiply(creds[i-creds.length]).mod(getP());
					}
				}
	    		sc.close();
	    		return result;
	    	}
			else {
				sc.close();
				result[0] = BigInteger.ZERO.subtract(BigInteger.ONE);
				return result;
			}
	    }
		result[0] = BigInteger.ZERO.subtract(BigInteger.ONE);
		return result;
	}
	public void testElgamal(BigInteger m, BigInteger kE) throws IOException {
		BigInteger kM = kE.modPow(getPrivKey(), getP());
		BigInteger kM_inverse = kM.modInverse(getP());
		BigInteger message = m.multiply(kM_inverse).mod(getP());
		System.out.println("Testing El Gamal parameters, message is: " + new String(message.toByteArray()));
		//System.out.println(message.toString()); // Tested basic elgamal here and it worked fine
	}
}
