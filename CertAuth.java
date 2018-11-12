import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;
import java.nio.file.Paths;

public class CertAuth {
	String currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"\\src\\senders\\";
	public CertAuth() throws IOException {
	}
	public int newSender(String senderName) throws IOException {
		File sendersFile = new File(currAbsPath+senderName+".txt");
		if(!sendersFile.exists()) {
			sendersFile.getParentFile().mkdirs();
			sendersFile.createNewFile();
		}
		return 0;
	}
	public int newReceiver(String receiverName, String senderName) throws IOException {
		File sendersFile = new File(currAbsPath+senderName+".txt");
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
        sc.close();
        // NEED TO DO: need to generate su/xu and send su too server and send xu to receiver
		return 0;
	}
}
