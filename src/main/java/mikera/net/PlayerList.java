package mikera.net;

import java.util.List;

import mikera.persistent.IntMap;
import mikera.persistent.ListFactory;
import mikera.persistent.PersistentList;


public class PlayerList {
	public int MAX_PLAYERS=50;
	
	@SuppressWarnings("unchecked")
	private IntMap<Player> playerMap=(IntMap<Player>) IntMap.EMPTY;
	
	private PersistentList<Player> playerList=ListFactory.create();
	private int next_id=0;
	
	public Integer addPlayer(String name, String pass) {
		// find a free player id
		if (playerCount()>=MAX_PLAYERS) throw new Error("Too many players!!");

		int playerNum=next_id++;
		
		Player player=new Player();
		player.name=name;
		player.password=pass;
		player.id=Integer.valueOf(playerNum);
		
		playerMap=playerMap.include(playerNum, player);
		playerList=playerList.append(player);
		
		return player.id;
	}
	
	public int playerCount() {
		return playerMap.size();
	}
	
	public List<Player> getList() {
		return playerList;
	}
	
	public void removePlayer(int id) {
		Player p=playerMap.get(id);
		playerMap=playerMap.delete(id);
		playerList=playerList.delete(p);
	}
	
	public Player getPlayer(int id) {
		return playerMap.get(id);
	}
	
	public Integer findPlayer(String name) {
		for (Player ps: playerList) {
			if ((ps!=null)&&name.equals(ps.name)) {
				return ps.id;
			}
		}
		return null;
	}
}
