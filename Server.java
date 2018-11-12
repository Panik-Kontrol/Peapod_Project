import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Server {
	String currAbsPath = Paths.get(".").toAbsolutePath().normalize().toString()+"\\src\\server\\";
	BigInteger[] serverParams;
	//AttributeTuple attrs = new AttributeTuple();
	BigInteger[] serverAttrs = new BigInteger[5];
	public Server() throws IOException {
		serverParams = new BigInteger[4];
		File serverParamFile = new File(currAbsPath+"server_params.txt");
		if(serverParamFile.exists()) {
			Scanner sc = new Scanner(serverParamFile);
			int idx = 0;
			while (sc.hasNextLine()) {
				serverParams[idx] = sc.nextBigInteger();
				idx++;
		    }
			sc.close();
		}
		else {
			// Generate parameters and create the file. Using an already generated one for now
		}
		for(int i=0; i<serverParams.length; ++i) {
			System.out.println("Server parameter " + i + " is "+serverParams[i]);
		}
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
	public void storeKeyForReceiver(String receiverName, BigInteger key) {
		
	}
	public BigInteger getRandomNum(BigInteger upper_limit, Random r) {
		BigInteger result = new BigInteger(upper_limit.bitLength(), r);
		if(result.compareTo(upper_limit) >= 0 || result.compareTo(BigInteger.ONE) <= 0) {
			result = new BigInteger(upper_limit.bitLength(), r);
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
}
