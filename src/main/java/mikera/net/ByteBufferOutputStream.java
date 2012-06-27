package mikera.net;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
	protected ByteBuffer buffer;
	
	private static final int MIN_GROW_SIZE=100;
	private static final float MIN_GROW_RATIO=1.3f;
	
	@Override
	public void write(int b)  {
		ensureSpaceForWrite(1);
		buffer.put((byte)b);
	}
	
	@Override
	public void write(byte[] b,int offset, int len)  {
		ensureSpaceForWrite(len);
		buffer.put(b,offset,len);
	}
	
	private void ensureSpaceForWrite(int size) {
		ensureBufferSize((buffer==null)?MIN_GROW_SIZE:buffer.position()+size);	
	}

	public void ensureBufferSize(int size) {
		if (buffer==null) {
			buffer=BufferCache.instance().getBuffer(size);
		} else {
			int currentCapacity=buffer.capacity();
			if (currentCapacity<size) {
				int targetSize=Math.max(size,currentCapacity+MIN_GROW_SIZE);
				targetSize=Math.max(targetSize,(int)Math.floor(currentCapacity*MIN_GROW_RATIO));
				buffer=BufferCache.instance().grow(buffer, targetSize);
			}
		}
	}
	
	@Override
	public void close()  {
		clear();
	}
	
	/**
	 * Returns the ByteBuffer
	 * 
	 * Note - this is still owned by the ByteBufferOuputStream
	 * In particular clear() must be called to recycle
	 * @return
	 */
	public ByteBuffer getFlipedBuffer() {
		buffer.flip();
		return buffer;
	}
	
	public void clear() {
		if (buffer!=null) BufferCache.recycle(buffer);
		buffer=null;
	}
}
