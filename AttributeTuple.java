import java.math.BigInteger;
import java.util.Random;

public class AttributeTuple {
	BigInteger[] attr;
	public AttributeTuple() {
		this.attr = new BigInteger[5];
	}
	public int getSize() {
		return this.attr.length;
	}
	public void setAttributeTuple(BigInteger[] attr) {
		if(attr.length > this.attr.length) {
			for(int i=0; i<this.attr.length; ++i) {
				this.attr[i] = attr[i];
			}
		}
		else {
			for(int i=0; i<attr.length; ++i) {
				this.attr[i] = attr[i];
			}
		}
	}
	public String toString() {
		String result = "";
		for(int i=0; i<this.attr.length; ++i) {
			if(i==0) {
				result = result+this.attr[i];
			}
			else {
				result = result+" "+this.attr[i];
			}
		}
		return result;
	}
}