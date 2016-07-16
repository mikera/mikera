package mikera.net;

import java.nio.ByteBuffer;
import java.util.List;

import mikera.data.Data;


/**
 * Standard game server implementation
 * 
 * Handles queueing of messages to / from each player
 * 
 * 
 * @author Mike
 *
 */
public abstract class Server {
	public static final int SERVER_PORT=8080;
	public static final int SERVER_TICK_MILLIS=150;
	
	private ServerConnector serverConnector;
	
	private boolean running=false;
	
	//private Thread serverThread;
	//private int load_avg=0;
	
	private PlayerList playerList=new PlayerList();
	
	
	public List<Player> getPlayerList() {
		return playerList.getList();
	}
	
	public Player getPlayer(int id) {
		return playerList.getPlayer(id);
	}
	
	public int getPort() {
		return SERVER_PORT;
	}

	
	/*
	 * ===============================================================
	 * Server startup and shutdown
	 * ===============================================================
	 */
	
	public void start() {
		stop();
		serverConnector=new ServerConnector();
		serverConnector.setMessageHandler(new Receiver());
		serverConnector.startListening(SERVER_PORT);
		
		// initServer();

		System.err.println("Server started on port: "+SERVER_PORT);
	}
	
	
	public void stop() {
		if (serverConnector==null) return;
		
		running=false;
		serverConnector.close();
		serverConnector=null;
		System.err.println("Server stopped");
		
	}
	
	/*
	private void initServer() {
		running=true;
		Thread theThread=new Thread(new Runnable() {
			
			public void run() {
				while (running) {
					try {
						long lastMillis=System.currentTimeMillis();

						// server tick
						doTick();
						
						// calculate load
						long timeNow=System.currentTimeMillis();					
						int load=(int)((100*(timeNow-lastMillis))/SERVER_TICK_MILLIS);
						load_avg=(load_avg*9+load)/10;
						//System.err.println("Server tick load = "+load+"%");
						lastMillis=timeNow;
						
						// sleep
						int sleepTime=SERVER_TICK_MILLIS;
						Thread.sleep(sleepTime);

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		serverThread=theThread;
		serverThread.start();
	}
	
	protected void doTick() {
		
	}
*/
	
	/*
	 * ===============================================================
	 * Server logging
	 * ===============================================================
	 */
	private static final boolean DEBUG_LOG=false;
	
	protected void logMessage(String s) {
		System.err.println(s);
	}
	
	private void debugMessage(String s) {
		if (DEBUG_LOG) {
			System.out.println(s);
		}
	}

	/*
	 * ===============================================================
	 * Messaging
	 * ===============================================================
	 */
	
	protected int transmitMessage(int playerNo, Data data) {
		Player p=playerList.getPlayer(playerNo);
		synchronized(p) {
			Connection c=p.connection;
			
			try {
				if (c!=null) return c.write(data);
			} catch (Throwable t) {
				t.printStackTrace();
				p.connection=null;
			}
		}
		return 0;
	}
	
	/*
	 * ===============================================================
	 * Connection message handling
	 * ===============================================================
	 */
	private class Receiver implements MessageHandler {
		@Override
		public boolean handleMessage(ByteBuffer buffer, Connection c) {
			if (c.userTag==null) {
				handleConnectRequest(buffer,c);
				return true; // since message should be fully handled, can safely recycle
			}
			Data data=Data.create(buffer);
			queueIncomingMessage(data,(Integer)c.userTag);
			return false; // since we want to keep the ByteBuffer data.....
		}
	}
	

	
	private void handleConnectRequest(ByteBuffer data, Connection c) {
		// TODO: security! validate name / pass
		byte m=data.get();
		if (m!=CommonMessages.JOIN_GAME) {
			c.close();
			throw new Error("First message must be JOIN_GAME was "+m+" with length "+(data.remaining()+1));
		}
		String name=CommonMessages.getString(data);
		String pass=CommonMessages.getString(data);
		
		Integer playerID=playerList.addPlayer(name, pass);
		Player p=playerList.getPlayer(playerID);
		p.connection=c;
		c.userTag=playerID;
		
		Data d=new Data();
		CommonMessages.addConfirmJoinMessage(d, playerID);
		c.write(d);
		
		logMessage("Player connected: ID="+playerID+" name='"+name+"' pass='"+p.password+"'");
		
		onPlayerConnected(p);
	}
	
	protected void onPlayerConnected(Player p) {
		// nothing to do, should be overridden if needed
	}

	// queues message for the relevant player
	private void queueIncomingMessage(Data data, Integer playerID) {
		Player p=playerList.getPlayer(playerID);
		if (p==null) {
			debugMessage("Message received from non-existent player?!?!? ID="+playerID);
			return;
		}
		p.queueIncomingMessage(data);
	}

	public boolean isRunning() {
		return running;
	}
}
