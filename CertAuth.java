import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class CertAuth {
	private static String OS = System.getProperty("os.name").toLowerCase();
    private static String currAbsPath;		
    Server server;
	public CertAuth() throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		if(OS.indexOf("win") >= 0) {
			currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"\\src\\senders\\";
		}
		else {
			currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"/src/senders/";
		}
		server = new Server();
	}
	public int newSender(String senderName) throws IOException {
		File sendersFile = new File(currAbsPath+senderName+".txt");
		if(!sendersFile.exists()) {
			sendersFile.getParentFile().mkdirs();
			sendersFile.createNewFile();
			BigInteger[] keys = new BigInteger[3];
			keys = generateKeys(server);
			FileWriter fileWriter = new FileWriter(sendersFile);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(senderName);
			printWriter.print(" "+keys[0]);
			printWriter.print(" "+keys[1]);
			printWriter.print(" "+keys[2]);
			printWriter.close();
		}
		return 1;
	}
	public int newReceiver(String receiverName, String senderName) throws IOException {
		/*File sendersFile = new File(currAbsPath+senderName+".txt");
		if(!sendersFile.exists()) {
			System.out.println("The sender " + senderName + " is not registered. Cannot add you to their list.");
			return -1;
		}
		Scanner sc = new Scanner(sendersFile);
		while (sc.hasNextLine()) {
			if(sc.next(receiverName) != null) {
				sc.close();
	    		return 0;
	    	}
	    }
		sc.close();
		sc = new Scanner(sendersFile);
		FileWriter fw = new FileWriter(sendersFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        fw.append(receiverName);
        bw.close();
        sc.close();*/
        // NEED TO DO: need to generate su/xu and send su too server and send xu to receiver
		return 0;
	}
	private BigInteger[] generateKeys(Server server) {
		Random r = new Random();
		BigInteger[] result = new BigInteger[3];
		result[0] = server.getRandomNum(server.getP(), r); // party key
		result[1] = server.getRandomNum(server.getP(), r); // server key
		result[2] = result[0].multiply(result[1]).mod(server.getP()); // K used for both sender and receiver's decode
		return result;
	}
	public BigInteger senderLogin(String senderName) throws FileNotFoundException {
		File senderFile = new File(currAbsPath+senderName+".txt");
		if(!senderFile.exists()) {
			System.out.println("The sender " + senderName + " is not registered. Cannot retrieve your private key");
			return BigInteger.ZERO.subtract(BigInteger.ONE);
		}
		Scanner sc = new Scanner(senderFile);
		while (sc.hasNextLine()) {
			if(sc.next(senderName) != null) {
	    		BigInteger key = new BigInteger(sc.next());
	    		sc.close();
	    		return key;
	    	}
	    }
		sc.close();
		return BigInteger.ZERO.subtract(BigInteger.ONE);
	}
	public BigInteger serverLogin(String senderName) throws FileNotFoundException {
		File senderFile = new File(currAbsPath+senderName+".txt");
		if(!senderFile.exists()) {
			System.out.println("The sender " + senderName + " is not registered. Cannot retrieve your private key");
			return BigInteger.ZERO.subtract(BigInteger.ONE);
		}
		Scanner sc = new Scanner(senderFile);
		while (sc.hasNextLine()) {
			if(sc.next(senderName) != null) {
				sc.next();
	    		BigInteger key = new BigInteger(sc.next());
	    		sc.close();
	    		return key;
	    	}
	    }
		sc.close();
		return BigInteger.ZERO.subtract(BigInteger.ONE);
	}
	public Server connectToServer() {
		return server;
	}
}
