package me.ccgreen.Storinator.windows;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ccgreen.Storinator.StorinatorMain;

public class WindowManager {

	private StorinatorMain plugin;
  private HashMap<Player, VaultWindow> VAULT_WINDOWS = new HashMap<>();

  public WindowManager(StorinatorMain plugin) {
    this.plugin = plugin;
    Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
      for (VaultWindow VW : VAULT_WINDOWS.values()) {
        StorinatorMain.printInfo("Saving all open tabs");
        VW.savePage();
        StorinatorMain.printInfo("Data saved");
      }
    }, 6000, 6000);
  }

  public void createVaultWindow(Player player) {
    if (StorinatorMain.playMan.hasPlayer(player)) {
      VaultWindow VW = new VaultWindow(player, null);
      VAULT_WINDOWS.put(player, VW);
    } else {
      player.sendMessage("Data still loading...");
    }
  }

  public void createVaultWindow(Player target, Player viewer) {
    if (StorinatorMain.playMan.hasPlayer(target)) {
      VaultWindow VW = new VaultWindow(target, viewer);
      VAULT_WINDOWS.put(viewer, VW);
    } else {
      viewer.sendMessage("Data still loading...");
    }
  }

  public void changeVaultWindow(Player player, int page) {
    player.closeInventory();
    VaultWindow VW = new VaultWindow(player, null, page);
    VAULT_WINDOWS.put(player, VW);
  }

  public void removePlayer(Player player) {
    if (VAULT_WINDOWS.containsKey(player)) {
      VAULT_WINDOWS.get(player).savePage();
      VAULT_WINDOWS.remove(player);
    }
  }

  public void handleDragEvent(InventoryDragEvent event) {
    if (event.getInventory().getType() == InventoryType.CHEST) {
      if (VAULT_WINDOWS.containsKey(event.getWhoClicked())) {
        event.setCancelled(true);
      }
    }
  }

  public void handleClickEvent(InventoryClickEvent event) {
    if (event.getInventory().getType() == InventoryType.CHEST) {
      if (VAULT_WINDOWS.containsKey(event.getWhoClicked())) {
        VaultWindow win = VAULT_WINDOWS.get(event.getWhoClicked());
        win.HandleClickEvent(event);
      }
    }
  }

  public void closeInventoryies() {
    for (Player p : plugin.getServer().getOnlinePlayers()) {
      p.closeInventory();
      removePlayer(p);
    }
  }
}