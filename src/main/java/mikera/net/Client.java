package mikera.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import mikera.data.Data;

public class Client {
	protected ClientConnector clientConnector=new ClientConnector();
	protected Connection connection=null;
	
	private ArrayList<Data> incomingMessages=new ArrayList<>();
	private ArrayList<Data> requeueMessages=new ArrayList<>();
	
	public void connectLocal() {
		connect("127.0.0.1", Server.SERVER_PORT);
	}
	
	public void connect(String address, int port) {
		clientConnector.setMessageHandler(new Receiver());
		closeConnection();
		connection=clientConnector.connect(address, port);		
	}
	
	private void closeConnection() {
		if (connection!=null) {
			connection.close();
			connection=null;
		}
	}

	public void login(String name, String pass) {
		if (connection==null) throw new Error("Connection not established!");
		ByteBuffer bb=BufferCache.instance().getBuffer(1000);
		CommonMessages.addJoinMessage(bb, name, pass);
		bb.flip();
		connection.write(bb);
	}
	
	public void getIncomingMessages(ArrayList<Data> dest) {
		synchronized (incomingMessages) {
			dest.addAll(incomingMessages);
			incomingMessages.clear();
		}
	}
	
	public void requeueIncomingMessages(ArrayList<Data> src) {
		synchronized (incomingMessages) {
			requeueMessages.addAll(src);
			requeueMessages.addAll(incomingMessages);
			ArrayList<Data> t=incomingMessages;
			incomingMessages=requeueMessages;
			
			t.clear();
			requeueMessages=t;
		}
	}
	
	/*
	 * ===============================================================
	 * Connection message handling
	 * ===============================================================
	 */
	private class Receiver implements MessageHandler {
		public boolean handleMessage(ByteBuffer buffer, Connection c) {
			Data data=Data.create(buffer);
			queueIncomingMessage(data);
			return true;
		}
	}
	
	private void queueIncomingMessage(Data data) {
		synchronized (incomingMessages) {
			incomingMessages.add(data);
			//System.err.print("Message received by client! Length = " + data.size()+ "\n");
		}
	}
}
