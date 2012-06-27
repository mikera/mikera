package mikera.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import mikera.data.ByteArrayCache;
import mikera.data.Data;
import mikera.net.DataInputStream;
import mikera.net.DataOutputStream;
import mikera.util.Bits;
import mikera.util.Rand;

import org.junit.Test;

public class TestData {
	@Test public void testData1() {
		Data d=new Data();
		
		d.appendByte((byte) 1);
		d.appendByte((byte) 2);
		assertEquals(1,d.getByte(0));
		assertEquals(2,d.getByte(1));
		assertEquals(2,d.size());
		
		Data dd=d.clone();
		dd.append(d);
		assertEquals(4,dd.size());
		assertEquals(2,dd.getByte(3));
		
		try {
			dd.get(10);
			fail();
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
		
		try {
			dd.get(-1);
			fail();
		} catch (IndexOutOfBoundsException x) {
			// OK!
		}
		
		byte[] bs=dd.toNewByteArray();
		assertEquals(2,bs[3]);
		assertEquals(4,bs.length);
		
		ByteBuffer bb=dd.toFlippedByteBuffer();
		assertEquals(4,bb.remaining());
		
		ByteBuffer bb2=dd.toWrapByteBuffer();
		assertEquals(4,bb2.remaining());

		Data d3=Data.create(bb2);
		assertEquals(4,d3.size());
		assertEquals(2,d3.getByte(3));
		
	}
	
	@Test public void testData2() {
		Data d=new Data();
		int pos=0;
		int size=0;
		
		
		size=d.appendInt(1000);
		assertEquals(1000,d.getInt(pos));
		pos+=size;
		
		size=d.appendInt(-2000);
		assertEquals(-2000,d.getInt(pos));
		pos+=size;

		long lv2=Rand.nextLong();
		size=d.appendFullLong(lv2);
		assertEquals(lv2,d.getFullLong(pos));
		pos+=size;

		long lvb=Rand.d(40)-20;
		size=d.appendLong(lvb);
		assertEquals(1,size);
		assertEquals(lvb,d.getLong(pos));
		pos+=size;

		
		long lv=Rand.nextLong();
		size=d.appendLong(lv);
		assertEquals(lv,d.getLong(pos));
		pos+=size;
		
		
		float fv=Rand.nextFloat();
		size=d.appendFloat(fv);
		assertEquals(fv,d.getFloat(pos),0);
		pos+=size;
		
		double dv=Rand.nextDouble();
		size=d.appendDouble(dv);
		assertEquals(dv,d.getDouble(pos),0);
		pos+=size;

		char cv=(char)Rand.nextInt();
		size=d.appendChar(cv);
		assertEquals(cv,d.getChar(pos),0);
		pos+=size;
		
		assertEquals(pos,d.size());
	}
	
	@Test public void testData3() {
		Data d=new Data();
		assertEquals("",d.toString());
		d.appendFullInt(1000);
		d.appendFullInt(-2000);
		assertEquals("00 00 03 E8 FF FF F8 30",d.toString());
		
		byte[] bs=d.toNewByteArray();	
		Data d2=Data.create(bs);
		bs[1]=88; // check that this does not disrupt data copy
		assertEquals(d2,d);	
	}
	
	@Test public void testData4() {
		Data d=new Data();
		
		// should be fully compressed into bytes
		d.appendString("Hello Mike");
		assertEquals(11,d.size());
		
		// capacity should equal or exceed size
		int cap=d.capacity();
		assertTrue(11<=cap);
		
		// should not change capacity
		d.clearContents();
		assertEquals(cap,d.capacity());
		
		// should zero capacity
		d.clear();
		assertEquals(0,d.capacity());
	}
	
	@Test public void testDataStreams() {
		DataOutputStream dos=new DataOutputStream();
		
		byte val=(byte)Rand.r(1000);
		dos.write(val);
		
		Data d=dos.getData();
		assertEquals(1,d.size());
		assertEquals(val,d.getByte(0));
		d.clear();
		
				
		try {
			ObjectOutputStream oos=new ObjectOutputStream(dos);
			oos.writeObject(new Data());
			oos.writeObject("Hello");
			assertTrue(d.size()>10);
		} catch (IOException e) {
			throw new Error(e);
		}
		
		DataInputStream dis=new DataInputStream(d);
		try {
			ObjectInputStream ois=new ObjectInputStream(dis);
			Object o=ois.readObject();
			assertTrue(o instanceof Data);
			o=ois.readObject();
			assertEquals("Hello",o);
			assertEquals(0,dis.getRemaining());
			assertEquals(-1,dis.read());
			
			try {
				ois.readObject();
				fail();
			} catch (EOFException e) {
				// OK;
			}
		} catch (Exception e) {
			throw new Error(e);
		}
		
	}
	
	/**
	 * Test copying ByteBuffer into Data and back again
	 */
	@Test public void testDataToByteBuffers() {
		ByteBuffer bb=ByteBuffer.allocate(100);
		for (int i=0; i<100; i++) {
			bb.put((byte)i);
		}
		bb.flip();
		assertEquals(100,bb.remaining());
		
		Data d=new Data();
		d.appendByteBuffer(bb);
		assertEquals(0,bb.remaining());
		assertEquals(100,d.size());
		d.appendByteBuffer(bb); // should do nothing
		assertEquals(0,bb.remaining());
		assertEquals(100,d.size());
		
		assertEquals(37,d.getByte(37));
		
		ByteBuffer bb2=d.toFlippedByteBuffer();
		
		assertEquals(100,bb2.remaining());
		for (int i=0; i<100; i++) {
			assertEquals(i,bb2.get());
		}
		assertEquals(0,bb2.remaining());

	}
	
	@Test public void testByteArrayCache() {
		for (int i=0; i<100; i++) {
			int len=Rand.r(100);
			byte[] ba=ByteArrayCache.getByteArray(len);
			assertTrue(ba.length>=len);
			assertTrue(ba.length<=(len*4));
			ByteArrayCache.recycleByteArray(ba);
			
		}
		assertTrue(ByteArrayCache.countCachedArrays()>0);
	}
	
	@Test public void testZigZag() {
		for (int i=-200; i<200; i++) {
			int a=Rand.nextInt();
			assertEquals(a,Bits.zigzagEncodeInt(Bits.zigzagDecodeInt(a)));
			assertEquals(a,Bits.zigzagDecodeInt(Bits.zigzagEncodeInt(a)));
			
			long b=Rand.nextLong();
			assertEquals(b,Bits.zigzagEncodeLong(Bits.zigzagDecodeLong(b)));
			assertEquals(b,Bits.zigzagDecodeLong(Bits.zigzagEncodeLong(b)));
			
			// should all be short unsigned integers
			assertTrue((Bits.zigzagEncodeInt(i)&(~0xFFF))==0);
			assertTrue((Bits.zigzagEncodeLong(i)&(~0xFFF))==0);
		}
	}
	
	@Test public void testVarIntData() {
		Data d=new Data();
		for (int i=-200; i<200; i++) {
			d.clear();
			int a = ((i&1)==1) ? Rand.nextInt() : i;
		
			int l=d.appendVarInt(a);
			assertEquals(l,Data.sizeOfVarInt(a));
			assertEquals(l,d.size());
			
			int b=d.getVarInt(0);
			assertEquals(a,b);
			assertEquals(l,Data.sizeOfVarInt(b));
		}
	}
	
	@Test public void testDataSize() {
		Data d=new Data();
		int total=0;
		
		for (int i=0; i<10; i++) {
			int a=Rand.nextInt();
			int len=d.appendInt(a);
			assertEquals(len,Data.sizeOfInt(a));		
			total+=len;
		}
		assertEquals(total,d.size());

		
		for (int i=0; i<10; i++) {
			char c=Rand.nextChar();
			int len=d.appendFullChar(c);
			assertEquals(len,Data.sizeOfFullChar(c));		
			total+=len;
		}
		assertEquals(total,d.size());

		
		for (int i=0; i<10; i++) {
			char c=Rand.nextChar();
			int len=d.appendChar(c);
			assertEquals(len,Data.sizeOfChar(c));		
			total+=len;
		}
		assertEquals(total,d.size());

		
		for (int i=0; i<10; i++) {
			String a=Rand.nextString();
			int len=d.appendString(a);
			assertEquals(len,Data.sizeOfString(a));		
			total+=len;
		}
		assertEquals(total,d.size());
		
		for (int i=0; i<10; i++) {
			long a=Rand.nextLong();
			int len=d.appendLong(a);
			assertEquals(len,Data.sizeOfLong(a));		
			total+=len;
		}
		assertEquals(total,d.size());
		

		
		for (int i=0; i<10; i++) {
			byte b=Rand.nextByte();
			int len=d.appendByte(b);
			assertEquals(len,Data.sizeOfByte(b));		
			total+=len;
		}
		assertEquals(total,d.size());
		
		for (int i=0; i<10; i++) {
			double db=Rand.nextDouble();
			int len=d.appendDouble(db);
			assertEquals(len,Data.sizeOfDouble(db));		
			total+=len;
		}
		assertEquals(total,d.size());
	}

}
