package mikera.math;

import static org.junit.Assert.assertEquals;
import mikera.image.Colours;
import mikera.math.Matrix;
import mikera.math.Vector;
import mikera.util.Rand;

import org.junit.Test;

public class TestMathVector {
	@Test public void testInit() {
		Vector v=new Vector(0,1,2);
		v.scale(2);
		assertEquals("{0.0, 2.0, 4.0}",v.toString());
	}
	

	@Test public void testNormalise() {
		Vector v=new Vector(6,0,0);
		
		float len=v.normalise();
		assertEquals("{1.0, 0.0, 0.0}",v.toString());
		assertEquals(6,len,0.0001f);
	}
	
	@Test public void testRotation() {
		Vector v=new Vector(1,0,0);
		Vector v2=new Vector(3,0,0);
		
		Matrix m=new Matrix(3,3);
		
		m.setToRotation3(Rand.nextFloat()-0.5f, Rand.nextFloat()-0.5f, Rand.nextFloat()-0.5f, Rand.nextFloat()*10-5);
		
		Matrix.multiplyVector(m,v,v2);
		
		assertEquals(1.0f,v2.length(),0.0001f);
	}
	
	@Test public void testColours() {
		Vector v3=new Vector(3);
		Vector v4=new Vector(4);
		
		v4.data[0]=Rand.nextFloat();
		v4.data[1]=Rand.nextFloat();
		v4.data[2]=Rand.nextFloat();
		v4.data[3]=1.0f;
		
		v3.data[0]=v4.data[0];
		v3.data[1]=v4.data[1];
		v3.data[2]=v4.data[2];
		
		assertEquals(v4.toARGBColour(),v3.toRGBColour());
		
		int argb=Rand.nextInt();
		Colours.toFloat4(v4.data, 0, argb);
		assertEquals(argb,v4.toARGBColour());
	}
	
}
