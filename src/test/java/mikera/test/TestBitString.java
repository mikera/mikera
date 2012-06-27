package mikera.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.Test;

import mikera.data.BitString;
import mikera.util.Rand;

public class TestBitString {

	@Test
	public void testBitSetMapping() {
		
		int length=Rand.d(3,30);
		for (int i=0; i<10; i++) {
			BitSet bs=new BitSet(length);
			for (int j=0; j<length; j++) {
				bs.set(j,Rand.chance(0.5));
			}
			bs.set(length-1,true); // ensure BitSet is of full length
			
			BitString b=new BitString(bs);
			
			assertEquals(bs,b.toBitSet());
			assertEquals(length,b.length());
			assertEquals(bs.length(),b.length());
			
			assertEquals(b,b.substring(0, length));
			
			for (int j=0; j<length; j++) {
				assertEquals(bs.get(j),b.get(j));
			}
			
			b.validate();
		}
	}
	

}
