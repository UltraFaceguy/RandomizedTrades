package me.ccgreen.Storinator.players;

import java.util.HashMap;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.ccgreen.Storinator.StorinatorMain;

public class PlayerManager {

	private StorinatorMain plugin;

	private HashMap<Player, PlayerData> playerData = new HashMap<Player, PlayerData>();

	public PlayerManager(StorinatorMain main) {
		plugin = main;
	}

	public void newPlayer(Player player) {
			createPlayer creator = new createPlayer(plugin, player);
			Bukkit.getScheduler().runTaskAsynchronously(plugin, creator);
	}

	public boolean hasPlayer(Player player) {
		return playerData.containsKey(player);
	}

	public void addData(Player player, PlayerData data) {	
		playerData.put(player, data);
	}

	public void saveAll() {
		Vector<String> batchStatement = new Vector<String>();
		for(PlayerData data : playerData.values()) {
			Vector<String> playerBatch = data.saveAll();
			for(int i = 0; i < playerBatch.size(); i++) {
				batchStatement.add(playerBatch.get(i));
			}
		}
		StorinatorMain.SQL.sendBatchOnMainThread(batchStatement);
	}

	public void saveData(Player player, Inventory inv, int page) { 
		playerData.get(player).updatePage(inv, page); 
	} 

	public PlayerData getData(Player player) {
		if(playerData.containsKey(player)) {
			return playerData.get(player);
		} else {
			return null;
		}
	}

	public void playerLeave(Player player) {
		playerData.remove(player);
		player.closeInventory();
	}




}