import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;


public class Receiver {
	public static void main(String[] args) throws IOException {
		CertAuth ca = new CertAuth();
		ca.newReceiver("Bob","Alice");
		Server server = ca.connectToServer();
		BigInteger attribute_key = ca.senderLogin("Alice");
		BigInteger ak_inv = attribute_key.modInverse(server.getP());
		
		BigInteger[] creds = new BigInteger[5];
		creds = generateCredentialTuple(); // Creating tuple for credentials
		
		// Get the message from the server
		BigInteger[] message_data = server.getMessage("Alice", creds, ca);

		// Storing off the message, the g^r key,etc
		BigInteger m = message_data[0].multiply(ak_inv);
		BigInteger gr = message_data[1].multiply(ak_inv);
		
		// Trying to generate the k from sender
		BigInteger sender_key = BigInteger.ONE;
		for(int i = 0; i < creds.length; ++i) {
			if(message_data[i+2].equals(BigInteger.ZERO.subtract(BigInteger.ONE))) {
				
			}
			else {
				sender_key.multiply(message_data[i+2].multiply(ak_inv)).mod(server.getP());
			}
		}
		// Find the inverse of k
		BigInteger sender_key_inv = sender_key.modInverse(server.getP());
		// Multiple the message by the inverse of k
		BigInteger answer = m.multiply(sender_key_inv).mod(server.getP());
		System.out.println("message is: " + new String(answer.toByteArray()));
	}
	
	private static BigInteger[] generateCredentialTuple() {
		BigInteger attrs[] = new BigInteger[5];
		attrs[0] = BigInteger.ONE; // Valid attribute
		attrs[1] = BigInteger.ZERO;
		attrs[2] = BigInteger.ONE; // Valid attribute
		attrs[3] = BigInteger.ZERO; // Invalid attribute, generating random for it
		attrs[4] = BigInteger.ONE; // Valid attribute
		return attrs;
	}
}
