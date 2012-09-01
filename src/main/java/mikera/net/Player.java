/**
 * 
 */
package mikera.net;

import java.util.ArrayList;

import mikera.data.Data;


public class Player {
	String name=null;
	String password=null;
	Connection connection=null;
	
	private Data outgoingMessages=new Data();
	private ArrayList<Data> incomingMessages=new ArrayList<>();
	
	public Integer id=null;
	
	public void getIncomingMessages(ArrayList<Data> messages) {
		synchronized(this) {
			messages.addAll(incomingMessages);
			incomingMessages.clear();
		}		
	}
	
	public Data getOutgoingData() {
		return outgoingMessages;
	}
	
	public void queueIncomingMessage(Data message) {
		synchronized(this) {
			incomingMessages.add(message);
		}
	}
}