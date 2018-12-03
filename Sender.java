import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class Sender {
	public static void main(String[] args) throws IOException {
		CertAuth ca = new CertAuth();
		ca.newSender("Alice");
		Server server = ca.connectToServer();
		BigInteger attribute_key = ca.senderLogin("Alice");
		
		// Get the paramters we need to work with from the server
		BigInteger p = server.getP();
		BigInteger g = server.getG();
		BigInteger B = server.getPubKey();
		BigInteger[] attrs = new BigInteger[5];
		//AttributeTuple attrTuple = new AttributeTuple();
		attrs = generatePolicyTuple(p, g, server);
		
		BigInteger k = attrs[0].multiply(attrs[2]).multiply(attrs[4]).mod(p);
		String m_tmp = "This actually works!";
		System.out.println("Alice is attempting to send: " + m_tmp);
		BigInteger m_bytes = new BigInteger(m_tmp.getBytes());
		testElgamal(m_bytes, k, p, g, B, server);
		
		
		BigInteger y = g.modPow(attribute_key, p);
		BigInteger yk = y.modPow(k, p);
		BigInteger m = m_bytes.multiply(yk).mod(p); // encrypt message with ephemeral key
		BigInteger gk = g.modPow(k, p);
	
		// Now need to create the messages for attributes
		BigInteger[] attribute_messages = new BigInteger[5];
		for(int i=0; i<attrs.length; ++i) {
			//BigInteger y = g.modPow(attribute_key, p); // Just using 1 key for now to prove it out, this should be a separate yi per attribute
			attribute_messages[i] = y.modPow(attrs[i], p); 
		}
		// And the g^r keys
		BigInteger[] attribute_keys = new BigInteger[5];
		for(int i=0; i<attrs.length; ++i) {
			attribute_keys[i] = g.modPow(attrs[i], p);
		}
		
		// Send it to the server for storage.
		server.storeMessage(m, gk, attribute_messages, attribute_keys, "Alice", ca);
	}
	private static void testElgamal(BigInteger m, BigInteger k, BigInteger p, BigInteger g, BigInteger B, Server server) throws IOException {
		BigInteger kE = g.modPow(k, p);
		BigInteger kM = B.modPow(k, p);
		BigInteger message = m.multiply(kM).mod(p);
		server.testElgamal(message, kE);
	}
	private static BigInteger[] generatePolicyTuple(BigInteger p, BigInteger g, Server server) {
		Random r = new Random();
		BigInteger attrs[] = new BigInteger[5];
		attrs[0] = server.getRandomNum(p, r); // Valid attribute
		attrs[1] = BigInteger.ONE;
		attrs[2] = server.getRandomNum(p, r); // Valid attribute
		attrs[3] = server.getRandomNum(p, r); // Invalid attribute, generating random for it
		attrs[4] = server.getRandomNum(p, r); // Valid attribute
		return attrs;
	}
}
