package me.ccgreen.Storinator.players;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.ccgreen.Storinator.StorinatorMain;

public class PlayerManager {

	private StorinatorMain plugin;

	private HashMap<UUID, PlayerData> playerData = new HashMap<UUID, PlayerData>();

	public PlayerManager(StorinatorMain main) {
		plugin = main;
	}

	public void newPlayer(Player player) {
		if(!hasPlayer(player)) {
			createPlayer creator = new createPlayer(plugin, player);
			Bukkit.getScheduler().runTaskAsynchronously(plugin, creator);
			StorinatorMain.printInfo(player.getDisplayName());
		}
	}

	public boolean hasPlayer(Player player) {
		return playerData.containsKey(player.getUniqueId());
	}

	public void addData(Player player, PlayerData data) {	
		playerData.put(player.getUniqueId(), data);
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
		playerData.get(player.getUniqueId()).updatePage(inv, page); 
	} 

	public PlayerData getData(Player player) {
		if(playerData.containsKey(player.getUniqueId())) {
			return playerData.get(player.getUniqueId());
		} else {
			return null;
		}
	}

	public void playerLeave(Player player) {
		//playerData.remove(player);
		player.closeInventory();
	}




}