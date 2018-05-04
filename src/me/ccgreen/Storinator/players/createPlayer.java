package me.ccgreen.Storinator.players;

import org.bukkit.entity.Player;

import me.ccgreen.Storinator.StorinatorMain;

public class createPlayer implements Runnable {

	private StorinatorMain main;
	private Player player;
	
	public createPlayer(StorinatorMain main, Player player) {
		this.main = main;
		this.player = player;
	}
	
	@Override
	public void run() {
		PlayerData data = new PlayerData(main, player);
		StorinatorMain.playMan.addData(player, data);
	}	
}