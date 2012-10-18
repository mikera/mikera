package mikera.net;

import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Cache to enable recycling of NIO ByteBuffers
 * 
 * @author Mike
 *
 */
public final class BufferCache {
	public TreeMap<Integer,ByteBuffer> buffers=new TreeMap<Integer, ByteBuffer>();
	
	private BufferCache() {

	}
	
	public synchronized ByteBuffer getBuffer(int size) {
		SortedMap<Integer,ByteBuffer> sm=buffers.tailMap(size);
		if ((sm==null)||(sm.isEmpty())) {
			ByteBuffer newItem=create(size);	
			return newItem;
		}
		Integer k=sm.firstKey();
		ByteBuffer bb=sm.remove(k);
		if (bb==null) throw new Error("Expected ByteBuffer with entry length "+k+" not found!!");
		return bb;
	}
	
	public static void recycle(ByteBuffer bb) {
		if (bb==null) throw new Error("Null ByteBuffer!!");
		instance().recycleBuffer(bb);
	}
	
	public synchronized void recycleBuffer(ByteBuffer bb) {
		bb.clear();
		buffers.put(bb.capacity(),bb);
	}
	
	public synchronized ByteBuffer grow(ByteBuffer bb, int size) {
		ByteBuffer target=create(size);
		bb.flip();
		target.put(bb);
		recycle(bb);
		return target;
	}
	
	private ByteBuffer create(int size) {
		// TODO: Consider allocateDirect here?
		return ByteBuffer.allocateDirect(size);
	}
	
	private static final BufferCache directInstance=new BufferCache();
	
	public static BufferCache instance() {
		return directInstance;
	}
}
