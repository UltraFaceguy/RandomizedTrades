package me.ccgreen.Storinator.tasks;

import me.ccgreen.Storinator.pojo.PlayerData;
import org.bukkit.entity.Player;

import me.ccgreen.Storinator.StorinatorMain;

public class CreatePlayerTask implements Runnable {

	private Player player;
	
	public CreatePlayerTask(Player player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		PlayerData data = new PlayerData(player);
		StorinatorMain.playMan.addData(player, data);
	}	
}