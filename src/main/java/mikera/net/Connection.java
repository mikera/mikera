package mikera.net;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import mikera.data.Data;

public class Connection {
	private static final boolean DEBUG = false;
	
	// header uses one int for message length, can be zero
	private static final int HEADER_LENGTH=4;

	public int MAX_MESSAGE_SIZE=10000000;
	
	private SocketChannel channel;
	private Selector selector;
	MessageHandler handler;
	
	/**
	 * Tag that identifies the connection to the server, assigned on creation
	 */
	Object internalTag;
	
	/**
	 * Tag that can be freely assigned by application using this connection
	 * Typically ID of player / connection
	 */
	public Object userTag=null;

	// temporary receiving buffer for one message
	private ByteBuffer receiveBuffer;
	
	// write queue, can hold multiple messages
	// we synchronise access to writeQueue on writeQueue itself
	private LinkedList<ByteBuffer> writeQueue=new LinkedList<>();

	public Connection(SocketChannel clientChannel, Selector s) {
		channel = clientChannel;
		selector=s;
	}

	public static final int DEFAULT_RECEIVE_BUFFER_SIZE = 4000;

	private static BufferCache bufferCache = BufferCache.instance();

	private ByteBuffer getReceiveBuffer() {
		return getReceiveBuffer(DEFAULT_RECEIVE_BUFFER_SIZE);
	}

	private ByteBuffer getReceiveBuffer(int size) {
		if (receiveBuffer == null) {
			receiveBuffer = bufferCache.getBuffer(size);
		}
		return receiveBuffer;
	}

	/**
	 * Clears receive buffer
	 * @param recycle true if buffer should be recycles (false implies the caller wants to keep the ByteBuffer
	 */
	public void clearReceiveBuffer(boolean recycle) {
		if (receiveBuffer != null) {
			if (recycle) BufferCache.recycle(receiveBuffer);
			receiveBuffer = null;
		}
	}

	private void debugMessage(String s) {
		if (DEBUG) {
			System.err.println(s);
		}
	}

	private ByteBuffer growReceiveBuffer(int size) {
		receiveBuffer = bufferCache.grow(receiveBuffer, size);
		return receiveBuffer;
	}

	public SocketChannel getChannel() {
		return channel;
	}

	public void close() {
		try {
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void handleEvent(SelectionKey key) {
		if (key.isWritable()) {
			handleWrite(key);
		}
		
		if (key.isReadable()) {
			handleRead(key);
		}
	}
	
	/**
	 * Handles incoming data from the client checking applied to ensure complete
	 * message is received passes on buffered data excluding message length
	 * 
	 * @param key
	 */
	private void handleRead(SelectionKey key) {
		try {

			ByteBuffer buffer = getReceiveBuffer();

			if (buffer.position() < 4) {
				// get message length
				buffer.limit(4);
				int bytesread = channel.read(buffer);

				if (bytesread == -1) {
					// end of stream so close client
					close();
					return;
				}

				// if (bytesread>0)
				// System.err.println("Connection: Reading "+bytesread+" bytes");

				int receivedBytes = buffer.position();
				if (receivedBytes < 4) {
					// return and continue to wait for rest of header
					return;
				}
			}

			int messageLength = buffer.getInt(0);
			if (messageLength>MAX_MESSAGE_SIZE) {
				System.err.println("Message too large: "+messageLength+" bytes");
				close();
			}

			if (messageLength > buffer.capacity()) {
				debugMessage("Connection.handleReadEvent(): Growing receive buffer to "
						+ messageLength + " bytes");
				buffer = growReceiveBuffer(messageLength);
			}
			buffer.limit(messageLength);

			int bytesread = channel.read(buffer);
			if (bytesread == -1) {
				// end of stream so close client
				close();
				return;
			}

			if (buffer.position() < messageLength) {
				// still waiting for data
				// receiveBuffer should be fully updated so exit
				return;
			}

			debugMessage("Connection.handleReadEvent(): Read full message of "
					+ messageLength + " bytes including header");

			// we now have a full buffered message!
			try {
				// prepare to read
				buffer.flip();
				buffer.getInt(); // take away the message length
				//System.err.println("Buffer recd: "+buffer.remaining());
				boolean recycleBuffer=handleMessage(buffer);
				clearReceiveBuffer(recycleBuffer);
			} catch (Throwable t) {
				t.printStackTrace();
				key.cancel();
			}

		} catch (Exception e) {
			// close the connection
			close();
			e.printStackTrace();
			return;
		}
	}

	private boolean handleMessage(ByteBuffer data) {
		if (handler != null) {
			try {
				return handler.handleMessage(data, this);
			} catch (Exception e) {
				System.err.println("Error in handleMessage!");
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Write data, prepending total message length as an integer
	 * Assumes data is already flipped
	 * 
	 * @param data
	 * @return number of bytes in message, excluding header
	 */
	public int write(ByteBuffer data) {
		synchronized (this) {
			try {
				int fullMessageLength = data.remaining() + HEADER_LENGTH;
				ByteBuffer bb = bufferCache.getBuffer(fullMessageLength);
				debugMessage("Connection.write(): writing " + fullMessageLength
						+ " bytes including header");
				bb.putInt(fullMessageLength);

				// flip bb to make it ready for writing
				bb.put(data);
				bb.flip();

				// try to write, but if not then defer writing
				tryWrite(bb);

				return fullMessageLength - HEADER_LENGTH;
			} catch (Throwable e) {
				debugMessage("Error writing data to connection");
				e.printStackTrace();
				throw new Error(e);
			}
		}
	}
	
	public int write(Data data) {
		ByteBuffer bb=data.toFlippedByteBuffer();
		return write(bb);
	}

	
	private void handleWrite(SelectionKey key) {
		try {		
			synchronized (writeQueue) {
				while (!writeQueue.isEmpty()) {
					ByteBuffer bb=writeQueue.getFirst();
					if (!bb.hasRemaining()) {
						// message completed so recycle buffer
						writeQueue.removeFirst();
						BufferCache.recycle(bb);
						continue;
					}
					
					int byteswritten=channel.write(bb);
					if ((byteswritten==0)||bb.hasRemaining()) {
						// escape if unable to complete write
						break;
					}
				}
				
				if (writeQueue.isEmpty()) {
					// deregister for writing
					channel.register(selector, SelectionKey.OP_READ,this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// synchronised to avoid simultaneous Queue creation
	private void tryWrite(ByteBuffer bb)
			throws ClosedChannelException {

		synchronized (writeQueue) {
			boolean empty=writeQueue.isEmpty();
			if (!empty) {
				// queue immediately
				writeQueue.add(bb);
				return;
			}
			
			try {
				channel.write(bb);
			} catch (Exception e) {
				throw new Error("Write Failed!");
			}
			
			// recycle buffer and exit if write is fully completed
			if (!bb.hasRemaining()) {
				BufferCache.recycle(bb);
				return;
			}
			
			// initiate write queue with this element
			if (selector==null) throw new Error("FGEGREH");
			writeQueue.add(bb);
			
			// if queue was originally empty, we need to register for write events
			if (empty) {
				channel.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ,this);
				
				// wake up selector so that it picks up new registration
				selector.wakeup();
			}
		}
	}
}