package mikera.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import mikera.data.Data;
import mikera.math.Point3i;
import mikera.math.Vector;
import mikera.net.DataInputStream;
import mikera.net.DataOutputStream;
import mikera.persistent.IntSet;
import mikera.persistent.Pair;
import mikera.persistent.Text;
import mikera.persistent.impl.RepeatList;
import mikera.randomz.Randomz;

import org.junit.Test;

public class CommonTests {
	
	@Test public void testCommonData() {
		testCommonData(new Pair<>(1,"Hello"));
		testCommonData(new Data());
		testCommonData(Integer.valueOf(3));
		testCommonData(Short.valueOf((short)3));
		testCommonData(0.7543245);
		testCommonData(new Vector(3));
		testCommonData(new Point3i(3,4,5));
		testCommonData(Text.create("Hello serialized world!"));
		testCommonData(RepeatList.create("Spam ",100));
		testCommonData(IntSet.create(new int[]{1,4,6,7}));		
		testCommonData(Randomz.getGenerator());
	}
	
	public static void testCommonData(Object o) {
		if (o instanceof Serializable) {
			testSerialization((Serializable)o);
		}
	}

	
	@SuppressWarnings("resource")
	public static void testSerialization(Serializable s) {
		
		Data d=new Data();
		DataOutputStream dos=new DataOutputStream(d);
		DataInputStream dis=new DataInputStream(d);
		
		try {
			ObjectOutputStream oos=new ObjectOutputStream(dos);
			ObjectInputStream ois=new ObjectInputStream(dis);
			
			oos.writeObject(s);
			
			Serializable s2=(Serializable)ois.readObject();

			assertEquals(s,s2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}		
	}
}
