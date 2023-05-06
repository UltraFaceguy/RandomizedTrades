package me.ccgreen.Storinator.listeners;

import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.managers.VaultManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EntryExitListener implements Listener {

	private final StorinatorPlugin plugin;

	public EntryExitListener(StorinatorPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerConnect(PlayerJoinEvent event) {
		plugin.getVaultManager().createVault(event.getPlayer().getUniqueId(), VaultManager.PERSONAL_VAULT, null);
	}

	@EventHandler
	public void OnPlayerDisconnect(PlayerQuitEvent event) {
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void OnPlayerKicked(PlayerKickEvent event) {
		event.getPlayer().closeInventory();
	}
}
