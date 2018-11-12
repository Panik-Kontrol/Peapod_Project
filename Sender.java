import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class Sender {
	private BigInteger k;
	public static void main(String[] args) throws IOException {
		CertAuth ca = new CertAuth();
		ca.newSender("Alice");
		
		Server server = new Server();
		
		// Get the paramters we need to work with from the server
		BigInteger p = server.getP();
		BigInteger g = server.getG();
		BigInteger pubKey = server.getPubKey();
		BigInteger[] attrs = new BigInteger[5];
		//AttributeTuple attrTuple = new AttributeTuple();
		generatePolicyTuple(p, g, attrs, server);
		
		BigInteger k = attrs[0].multiply(attrs[2]).multiply(attrs[4]).mod(p);
		String m_tmp = "Hey, it looks like this works";
		BigInteger m_bytes = new BigInteger(m_tmp.getBytes());
		BigInteger kE = server.sam(g, k, p); // compute ephemeral key
		BigInteger kM = server.sam(pubKey, k, p);
		BigInteger m = m_bytes.multiply(kM).mod(p); // encrypt message with ephemeral key
		for(int i=0; i<attrs.length; ++i) {
			attrs[i] = server.sam(pubKey, attrs[i], p); // this can't be? pubKey ^ 1 would be known
		}
	}
	
	private static void generatePolicyTuple(BigInteger p, BigInteger g, BigInteger[] attrs, Server server) {
		Random r = new Random();
		attrs[0] = server.getRandomNum(p, r);
		attrs[1] = BigInteger.ONE;
		attrs[2] = server.getRandomNum(p, r);
		attrs[3] = server.getRandomNum(p, r);
		attrs[4] = server.getRandomNum(p, r);
		//attrs.setAttributeTuple(tuple);
	}
}
