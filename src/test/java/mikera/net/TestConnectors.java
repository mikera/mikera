package mikera.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;

import mikera.net.BufferCache;
import mikera.net.ByteBufferOutputStream;
import mikera.net.ClientConnector;
import mikera.net.Connection;
import mikera.net.MessageHandler;
import mikera.net.ServerConnector;
import mikera.net.Util;
import mikera.util.Bits;
import mikera.util.Rand;

import org.junit.Test;

public class TestConnectors {
	private class Receiver implements MessageHandler {

		@Override
		public boolean handleMessage(ByteBuffer data, Connection c) {
			// int recLen=data.remaining();
			received[recCount]=BufferCache.instance().getBuffer(data.remaining());
			received[recCount].put(data);
			received[recCount].flip();
			recCount++;
			// System.err.println("TestConnectors.Receiver: "+ recLen+" bytes in message received");
			return true;
		}
	}
	
	int recCount=0;
	ByteBuffer[] received=new ByteBuffer[10];
	
	@Test public void testConnect() {
		ServerConnector sc=new ServerConnector();
		ClientConnector cc=new ClientConnector();
		try {
			sc.setMessageHandler(ServerConnector.ECHO_HANDLER);
			
			sc.startListening(1900);
			Thread.sleep(10);
			
			
			cc.connect("127.0.0.1", 1900);
			cc.setMessageHandler(new Receiver());
			Thread.sleep(10);

			assertEquals(1,sc.countConnections());
			assertTrue(sc.getConnection(1)!=null);
			assertTrue(sc.getConnection(2)==null);
			Connection scon=sc.getConnection(1);
			assertTrue(scon.getChannel().isConnected());
			
			// System.err.println(scon.getChannel().socket().getSendBufferSize());
			
			// write an int
			ByteBuffer bb1=BufferCache.instance().getBuffer(10000);
			ByteBuffer bb2=BufferCache.instance().getBuffer(100000);
			bb1.putInt(1000000);
			bb1.putInt(2000000);
			bb1.flip();
			assertEquals(8,cc.getConnection().write(bb1));
			
			// 9000 bytes
			for (int i=0; i<9000; i++) {
				bb2.put((byte)100);
			}
			bb2.flip();
			assertEquals(9000,cc.getConnection().write(bb2));
			
			// now write zero bytes
			assertEquals(0,cc.getConnection().write(bb2));
			
			// and write zero bytes from server
			assertEquals(0,sc.getConnection(1).write(bb2));
			
			// wait for all three messages
			int i=0;
			Thread.sleep(10);
			while (received[3]==null) {
				if (i++>100) throw new Error("Only "+recCount+" messages received");
				Thread.sleep(10);
			}

			for (int ii=0; ii<4; ii++) {
				if (received[ii].remaining()==8) {
					int t=received[ii].getInt(0);
					assertEquals(1000000,t);
				}
			}
			
			// assertEquals(9000,received[1].remaining());

			//assertEquals(0,received[3].remaining());

			assertEquals(4,recCount);
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
	
	@Test public void testByteBufferOutputStream() {
		ByteBufferOutputStream bbos=new ByteBufferOutputStream();
		
		bbos.write(10);
		bbos.write(20);
		
		ByteBuffer bb=bbos.getFlipedBuffer();
		assertEquals(2,bb.remaining());
		assertEquals(10,bb.get(0));
		bbos.clear();
		
		bbos.close();
		
	}
	
	@Test public void testStrings() {
		ByteBuffer bb=ByteBuffer.allocate(1000);

		int i=0;
		i+=Util.writeASCIIString(bb, null);
		i+=Util.writeASCIIString(bb, "A");
		i+=Util.writeASCIIString(bb, "Dffdeuvcfdeghvuohvoehvrett");
		i+=Util.writeASCIIString(bb, "");

		assertEquals(i,bb.position());
		
		bb.flip();
		String[] ss=new String[1];
		
		Util.readASCIIString(bb, ss);
		assertEquals(null,ss[0]);

		Util.readASCIIString(bb, ss);
		assertEquals('A',ss[0].charAt(0));
		assertEquals("A",ss[0]);

		Util.readASCIIString(bb, ss);
		assertEquals("Dffdeuvcfdeghvuohvoehvrett",ss[0]);

		Util.readASCIIString(bb, ss);
		assertEquals("",ss[0]);

		assertEquals(i,bb.position());

	}
	
	
	@Test public void testCompacted() {
		ByteBuffer bb=ByteBuffer.allocate(1000);
		
		for (int i=0; i<100; i++) {
			long l=Rand.d(1000)*(Rand.d(10)-Rand.d(10))+(Rand.d(10)-Rand.d(10));
			if (Rand.d(2)==1) {
				l=Rand.nextLong();
			}
			int len=Util.writeCompacted(bb, l);
			
			assertEquals(len,Util.compactedLength(l));
			
			int sb=Bits.significantSignedBits(l);
			assertEquals((sb+6)/7,len); // right number of bits
			
			bb.flip();
			long ll=Util.readCompacted(bb);
			// System.err.println(len);
			assertEquals(Long.toBinaryString(l),Long.toBinaryString(ll));
			bb.clear();
		}
		
		assertEquals(1,Util.writeCompacted(bb, 10));
		assertEquals(1,Util.writeCompacted(bb, 0));
		assertEquals(1,Util.writeCompacted(bb, 63));
		assertEquals(2,Util.writeCompacted(bb, 64));
		
		assertEquals(1,Util.writeCompacted(bb, -64));
		assertEquals(2,Util.writeCompacted(bb, -65));
		
	}

}
