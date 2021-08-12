package me.ccgreen.Storinator.managers;

import java.util.Map;
import java.util.Vector;

import java.util.WeakHashMap;
import me.ccgreen.Storinator.tasks.CreatePlayerTask;
import me.ccgreen.Storinator.pojo.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import me.ccgreen.Storinator.StorinatorMain;

public class PlayerManager {

	private final StorinatorMain plugin;
	private final Map<Player, PlayerData> playerData = new WeakHashMap<>();

	public PlayerManager(StorinatorMain main) {
		plugin = main;
	}

	public void newPlayer(Player player) {
			CreatePlayerTask creator = new CreatePlayerTask(player);
			Bukkit.getScheduler().runTaskAsynchronously(plugin, creator);
	}

	public boolean hasPlayer(Player player) {
		return playerData.containsKey(player);
	}

	public void addData(Player player, PlayerData data) {	
		playerData.put(player, data);
	}

	public void saveAll() {
		Vector<String> batchStatement = new Vector<>();
		for(PlayerData data : playerData.values()) {
			Vector<String> playerBatch = PlayerData.saveAll(data);
			batchStatement.addAll(playerBatch);
		}
		StorinatorMain.SQL.sendBatchOnMainThread(batchStatement);
	}

	public void saveData(Player player, Inventory inv, int page) {
		PlayerData.updatePage(playerData.get(player), inv, page);
	} 

	public PlayerData getData(Player player) {
		return playerData.getOrDefault(player, null);
	}

	public void playerLeave(Player player) {
		playerData.remove(player);
		player.closeInventory();
	}
}