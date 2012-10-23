package mikera.util;

import static org.junit.Assert.assertEquals;
import mikera.math.PerlinNoise;
import mikera.util.Maths;
import mikera.util.Rand;

import org.junit.Test;

public class TestMaths {
	@Test public void testClamp() {
		assertEquals(1,Maths.bound(1,0,2));
		assertEquals(10,Maths.bound(0,10,20));
		assertEquals(20,Maths.bound(0,100,20));
	}
	
	@Test public void testPerlinNoise() {
		PerlinNoise p=new PerlinNoise();
		
		float min=10;
		float max=-10;
		for (int i=0; i<100; i++) {
			float f=p.noise3(Rand.n(0,10),Rand.n(0,10),Rand.n(0,10));
			min=Math.min(f,min);
			max=Math.max(f,max);
		}
		
		//System.err.println(min);
		//System.err.println(max);
	}
	
	@Test public void testMod() {
		assertEquals(0.2f,Maths.fmod(1.2f, 1.0f),0.00001f);
		assertEquals(0.2f,Maths.fmod(-0.8f, 1.0f),0.00001f);

		assertEquals(0.2f,Maths.fmod(1.6f, 1.4f),0.00001f);
		assertEquals(0.0f,Maths.fmod(1.6f, 1.6f),0.00001f);

		
		assertEquals(2.0f,Maths.fmod(-8f, 10.0f),0.00001f);

		
	}
	
	@Test public void testTanh() {
		assertEquals(1.0f,Maths.tanh(1000000),0.001f);
		assertEquals(-1.0f,Maths.tanh(-1000000),0.001f);
		assertEquals(0f,Maths.tanh(0),0.001f);
	
	}
	
	@Test public void testSigmoid() {
		assertEquals(0.0f,Maths.sigmoid(-1000000),0.001f);
		assertEquals(0.0f,Maths.sigmoid(-800),0.001f);
		assertEquals(0.0f,Maths.sigmoid(-100),0.001f);
		assertEquals(0.0f,Maths.sigmoid(-30),0.001f);
		
		assertEquals(1.0f,Maths.sigmoid(1000000),0.001f);
		assertEquals(1.0f,Maths.sigmoid(800),0.001f);
		assertEquals(1.0f,Maths.sigmoid(100),0.001f);
		assertEquals(1.0f,Maths.sigmoid(30),0.001f);
		
		assertEquals(0.5f,Maths.sigmoid(0),0.001f);

		assertEquals(0.0f,Maths.sigmoid(Maths.inverseSigmoid(0.0f)),0.001f);
		assertEquals(1.0f,Maths.sigmoid(Maths.inverseSigmoid(1.0f)),0.001f);
		assertEquals(0.5f,Maths.sigmoid(Maths.inverseSigmoid(0.5f)),0.001f);

		assertEquals(0.0f,Maths.sigmoidDerivative(100),0.001f);
		assertEquals(0.25f,Maths.sigmoidDerivative(0),0.001f);
		assertEquals(0.0f,Maths.sigmoidDerivative(-100),0.001f);

		
		assertEquals(0.0f,Maths.inverseSigmoid(Maths.sigmoid(0.0f)),0.001f);
		assertEquals(2.0f,Maths.inverseSigmoid(Maths.sigmoid(2.0f)),0.001f);
		assertEquals(-2.0f,Maths.inverseSigmoid(Maths.sigmoid(-2.0f)),0.001f);
	}
	
	@Test public void testIntMod() {
		assertEquals(2,Maths.mod(2, 5));
		assertEquals(2,Maths.mod(7, 5));
		assertEquals(1,Maths.mod(-7, 8));
		assertEquals(0,Maths.mod(10, 10));

		
	}
	
	@Test public void testAbs() {
		assertEquals(0.0f,Maths.abs(0.0f),0.00001f);
	}
	
	@Test public void testIntAbs() {
		for (int i=-100; i<100; i++) {
			assertEquals(Math.abs(i),Maths.abs(i));
			assertEquals(Math.abs(i),Maths.abs2(i));
			assertEquals(Math.abs(i),Maths.abs3(i));
		}
	}
	
	@Test public void testSign() {
		for (int i=-100; i<100; i++) {
			assertEquals((int)Math.signum(i),Maths.sign(i));
			assertEquals((int)Math.signum(i),Maths.sign2(i));
		}
		assertEquals((int)Math.signum(Integer.MIN_VALUE),Maths.sign(Integer.MIN_VALUE));	
		assertEquals((int)Math.signum(Integer.MIN_VALUE),Maths.sign2(Integer.MIN_VALUE));
		
	}
	
	@Test public void testMinMax() {
		for (int i=-100; i<100; i++) {
			int a=Rand.xorShift32(i);
			int b=Rand.xorShift32(a);
			
			assertEquals(Math.min(a,b),Maths.min(a,b));
			assertEquals(Math.max(a,b),Maths.max(a,b));
			assertEquals(Math.min(a,b),Maths.min2(a,b));
			assertEquals(Math.max(a,b),Maths.max2(a,b));
		}
	}
	
	@Test public void testFastSqrt() {
		assertEquals(0.5,Maths.fastInverseSqrt(4),0.01f);
		
		// System.out.println(Maths.alternateSqrt(33f)+" vs."+Maths.sqrt(33f));
	}
	
	@Test public void testFloor() {
		assertEquals(0,Maths.floor(0));
		assertEquals(1,Maths.floor(1));
		assertEquals(-1,Maths.floor(-1));
		assertEquals(1,Maths.floor(1.2));
		assertEquals(-1,Maths.floor(-0.0001));
		assertEquals(-1,Maths.floor(-0.9001));
		
	}
	
	@Test public void testSpeed1() {
		for (int i=0; i<100000; i++) {
			float f=Maths.sqrt(i);
			assertEquals(f,f,0.01f);
		}
	}
	
	@Test public void testSpeed2() {
		for (int i=0; i<100000; i++) {
			float f=Maths.alternateSqrt(i);
			assertEquals(f,f,0.01f);
		}
	}


}
