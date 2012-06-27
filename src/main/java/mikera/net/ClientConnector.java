package mikera.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientConnector {
	Selector selector;

	
	Connection connection=null;
	private MessageHandler handler;
	
	public ClientConnector() {
		
	}
	
	public void setMessageHandler(MessageHandler mh) {
		if (connection!=null) connection.handler=mh;
		handler=mh;
	}
	
	public Connection connect(InetAddress address, int port) {
		SocketAddress a = new InetSocketAddress(address, port);
		return connect(a);
	}
	
	public Connection connect(String address, int port) {
		SocketAddress a = new InetSocketAddress(address, port);
		return connect(a);
	}
	
	public Connection connect(SocketAddress sa) {	
		close();
		
		try {
			selector=Selector.open();
			
			SocketChannel clientChannel = SocketChannel.open(sa);
			clientChannel.finishConnect();
			clientChannel.configureBlocking(false);
			
			Connection c=new Connection(clientChannel,selector);
			c.handler=handler;
			connection=c;
			
			clientChannel.register(selector, SelectionKey.OP_READ,c);
			
			new Thread(listener).start();
			
		} catch (Exception e) {
			throw new Error(e);
		}
		return connection;
	}

	public Runnable listener = new Runnable() {
		public void run() {
			try {
				while (selector!=null) {
					try {
						selector.select(1000); // 1 sec heartbeat				
						Set<SelectionKey> keys = selector.selectedKeys();
						
						if (keys.size()==0) {
							// System.err.println("Client listening.... nobody calling");
						} else {
							handleKeys(keys);
						}
					} catch (ClosedSelectorException e) {
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private void handleKeys(Set<SelectionKey> keys) {
		for (Iterator<SelectionKey> i = keys.iterator(); i.hasNext();) {
			SelectionKey key = i.next();
			i.remove();
			
			// System.err.println(key.readyOps());
			
			if (!key.isValid()) continue;

			connection.handleEvent(key);
		}
	}
	
	public void close() {
		try {
			if (selector!=null) {
				selector.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			selector=null;
		}
	}
	
	public Connection getConnection() {
		return connection;
	}
}
