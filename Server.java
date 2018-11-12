package Peapod_Project;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class Server {
	int booger;
	public Server() {
		this.booger = 16;
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
	public void createUser() {
		
	}
}
