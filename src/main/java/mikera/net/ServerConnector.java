package mikera.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Class to manage incoming server messages All incoming messages are handled on
 * one thread Outgoing messages are handled by the appropriate connection or
 * delayed if underlying socket not ready
 * 
 * @author Mike
 * 
 */
public class ServerConnector {
	private static final boolean DEBUG = false;

	// TODO: have a separate thread and selector for writing queue
	Selector selector;

	ServerSocketChannel server;
	SelectionKey serverKey;

	private MessageHandler handler;
	private HashMap<Object, Connection> connections = new HashMap<Object, Connection>();

	// private static final int MAX_BACKLOG=5;

	protected boolean live = false;


	public ServerConnector() {

	}

	private static void debugMessage(String s) {
		if (DEBUG) {
			System.err.println(s);
		}
	}

	public static final MessageHandler ECHO_HANDLER = new MessageHandler() {
		public boolean handleMessage(ByteBuffer data, Connection c) {
			debugMessage("Echoing " + data.remaining() + " bytes");
			c.write(data);
			return true;
		}
	};

	public Connection getConnection(Object tag) {
		return connections.get(tag);
	}

	public int countConnections() {
		return connections.size();
	}

	public void startListening(int port) {
		try {
			selector = Selector.open();

			server = ServerSocketChannel.open();
			server.socket().bind(new java.net.InetSocketAddress(port));
			server.configureBlocking(false);
			serverKey = server.register(selector, SelectionKey.OP_ACCEPT);

			// start listening thread
			new Thread(listener).start();
		} catch (Exception e) {
			throw new Error(e);
		}
		debugMessage("Started listening on port: " + port);
	}

	public Runnable listener = new Runnable() {
		public void run() {
			// loop continuously to handle selection events
			while (true) {
				try {
					try {
						selector.select(1000); // 1 sec heartbeat
						Set<SelectionKey> keys = selector.selectedKeys();

						// System.err.println("Server selected "+keys.size()+" keys");
						if (keys.size()>0) {
							handleKeys(keys);
						}
						
					} catch (ClosedSelectorException e) {
						// bailout, server socket has closed
						return;
					}
				} catch (Exception e) {
					// report error but try to continue
					e.printStackTrace();
				}
			}
		}
	};

	private void handleKeys(Set<SelectionKey> keys) {
		for (Iterator<SelectionKey> i = keys.iterator(); i.hasNext();) {
			SelectionKey key = i.next();
			i.remove();

			if (!key.isValid())
				continue;

			if (key == serverKey) {
				debugMessage("Handling server event");
				handleServerKey(key);
			} else {
				// debugMessage("Handling client event");
				Connection c = (Connection) key.attachment();
				c.handleEvent(key);
			}
		}
	}

	public void setMessageHandler(MessageHandler mh) {
		handler = mh;
		for (Connection c : connections.values()) {
			c.handler = mh;
		}
	}

	private void handleServerKey(SelectionKey key) {
		if (key.isAcceptable()) {
			try {
				try {
					SocketChannel clientChannel = server.accept();
					if (clientChannel == null)
						throw new Error("Null result from accept()");
					clientChannel.configureBlocking(false);
					addClient(clientChannel);

				} catch (ClosedChannelException e) {
					close();
					return;
				}
			} catch (Exception e) {
				System.err.println("Error accepting client");
				e.printStackTrace();
			}
		}
	}

	private int tag_id = 1;

	private Object createTag() {
		return Integer.valueOf(tag_id++);
	}

	private Connection createClientConnection(SocketChannel clientChannel,
			Selector s) {
		Connection cr = new Connection(clientChannel, s);
		cr.handler = handler;
		cr.internalTag = createTag();
		connections.put(cr.internalTag, cr);
		return cr;
	}

	private SelectionKey addClient(SocketChannel clientChannel)
			throws ClosedChannelException {
		Connection connection = createClientConnection(clientChannel, selector);
		SelectionKey clientKey = clientChannel.register(selector,
				SelectionKey.OP_READ, connection);

		debugMessage("Added client ID=" + connection.internalTag);

		return clientKey;
	}

	public void close() {
		try {
			connections.clear();
			server.close();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
