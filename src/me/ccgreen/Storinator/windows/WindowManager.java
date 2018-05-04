package me.ccgreen.Storinator.windows;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ccgreen.Storinator.StorinatorMain;

public class WindowManager {

	private static HashMap<Player, VaultWindow> VAULT_WINDOWS = new HashMap<Player, VaultWindow>();
	private StorinatorMain main;

	public WindowManager(StorinatorMain main){
		this.main = main;
	}


	public void createVaultWindow(Player player) {
		if(StorinatorMain.playMan.hasPlayer(player)) {
			VaultWindow VW = new VaultWindow(player, 0);
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
				StorinatorMain.playMan.saveData(player, player.getOpenInventory().getTopInventory(), VAULT_WINDOWS.get(player).getPage());
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