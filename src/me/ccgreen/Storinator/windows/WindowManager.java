package me.ccgreen.Storinator.windows;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.ccgreen.Storinator.StorinatorMain;

public class WindowManager {

	private static HashMap<Player, VaultWindow> VAULT_WINDOWS = new HashMap<Player, VaultWindow>();
	public StorinatorMain main;

	public WindowManager(StorinatorMain main){
		this.main = main;
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
			@Override
		      public void run() {
				for(VaultWindow VW : VAULT_WINDOWS.values()) {
					StorinatorMain.printInfo("Saving all open tabs");
					VW.savePage();
					StorinatorMain.printInfo("Data saved");
				}
			}
		}, 6000, 6000);
	}

	public void createVaultWindow(Player player) {
		if(StorinatorMain.playMan.hasPlayer(player)) {
			VaultWindow VW = new VaultWindow(player);
			VAULT_WINDOWS.put(player, VW);
		} else {
			player.sendMessage("Data still loading...");
		}
	}

	public void changeVaultWindow(Player player, int page) {
		player.closeInventory();
		VaultWindow VW = new VaultWindow(player, page);
		VAULT_WINDOWS.put(player, VW);
	}

	public void removePlayer(Player player) {
		if (VAULT_WINDOWS.containsKey(player)) {
			if(player.getOpenInventory().getTitle().startsWith(ChatColor.RED + "" + ChatColor.GRAY + "¤")) {
				VAULT_WINDOWS.get(player).savePage();
			}
			VAULT_WINDOWS.remove(player);
		}
	}
	
	public void removePlayer(Player player, Inventory inv) {
		if (VAULT_WINDOWS.containsKey(player)) {
			if(inv.getTitle().startsWith(ChatColor.RED + "" + ChatColor.GRAY + "¤")) {
				VAULT_WINDOWS.get(player).savePage();
			}
			VAULT_WINDOWS.remove(player);
		}
	}

	public void handleDragEvent(InventoryDragEvent event) {
		if(event.getInventory() == null) {
			return;
		}
		if (event.getInventory().getType() == InventoryType.CHEST) {
			if (VAULT_WINDOWS.containsKey(event.getWhoClicked())){	
				event.setCancelled(true);
			}
		}
	}

	public void handleClickEvent(InventoryClickEvent event) {
		if(event.getInventory() == null) {
			return;
		}
		if (event.getInventory().getType() == InventoryType.CHEST) {
			if (VAULT_WINDOWS.containsKey(event.getWhoClicked())){	
				VaultWindow win = VAULT_WINDOWS.get(event.getWhoClicked());
				win.HandleClickEvent(event);
			}
		}
	}

	public void closeInventoryies() {
		for(Player p : main.getServer().getOnlinePlayers()) {
			p.closeInventory();
			removePlayer(p);
		}
	}
}