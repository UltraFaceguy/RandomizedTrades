package me.ccgreen.Storinator.listeners;

import me.ccgreen.Storinator.StorinatorMain;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntryExitListener implements Listener {

	public EntryExitListener() {
	}

	@EventHandler
	public void OnPlayerConnect(PlayerJoinEvent event) {
		StorinatorMain.playMan.getPlayerData(event.getPlayer());
	}

	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent event) {
		StorinatorMain.playMan.playerLeave(event.getPlayer());
	}

	@EventHandler
	public void OnPlayerKicked(PlayerKickEvent event) {
		StorinatorMain.playMan.playerLeave(event.getPlayer());
	}
}
