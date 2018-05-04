package me.ccgreen.Storinator.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ccgreen.Storinator.StorinatorMain;

public class InvyEvent implements Listener{

	public InvyEvent() {

	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		if (event.getInventory().getType() == InventoryType.ENDER_CHEST){
			//do things
			Player player = (Player) event.getPlayer();
			if(event.getPlayer().hasPermission("Storinator.access")) {
				event.setCancelled(true);
				StorinatorMain.winMan.createVaultWindow(player);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENDERCHEST_OPEN, 1, 1);
			}
		}
	}

	@EventHandler
	public void onClickAction(InventoryClickEvent event)
	{
		if (event.getWhoClicked() instanceof Player){
			StorinatorMain.winMan.handleClickEvent(event);
		}
	}

	@EventHandler
	public void OnWindowClose(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) {
			return;
		}
		StorinatorMain.winMan.removePlayer((Player) e.getPlayer(), e.getInventory()); 
	}
};