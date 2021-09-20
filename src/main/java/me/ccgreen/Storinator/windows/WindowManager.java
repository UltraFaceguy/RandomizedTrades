package me.ccgreen.Storinator.windows;

import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ccgreen.Storinator.StorinatorMain;

public class WindowManager {

  private final Map<Player, VaultWindow> VAULT_WINDOWS = new WeakHashMap<>();

  public void openVaultWindow(final Player player, final Player viewer) {
    if (StorinatorMain.playMan.hasPlayer(player)) {
      if (VAULT_WINDOWS.containsKey(player)) {
        Bukkit.getLogger().warning("Player attempted to open a vault when a vault is open?");
      }
      VaultWindow VW = new VaultWindow(player, viewer);
      VAULT_WINDOWS.put(viewer != null ? viewer : player, VW);
    } else {
      (viewer != null ? viewer : player).sendMessage("Data still loading...");
    }
  }
  public void removePlayer(Player player) {
    if (VAULT_WINDOWS.containsKey(player)) {
      VAULT_WINDOWS.get(player).saveDisplayToPage();
      VAULT_WINDOWS.remove(player);
    }
  }

  public void handleDragEvent(final InventoryDragEvent event) {
    if (event.getInventory().getType() == InventoryType.CHEST &&
        VAULT_WINDOWS.containsKey(event.getWhoClicked())) {
      event.setCancelled(true);
    }
  }

  public void handleClickEvent(final InventoryClickEvent event) {
    if (event.getInventory().getType() == InventoryType.CHEST &&
        VAULT_WINDOWS.containsKey(event.getWhoClicked())) {
      VaultWindow win = VAULT_WINDOWS.get(event.getWhoClicked());
      win.handleClick(event);
    }
  }

  public void saveAllOpenToData() {
    for (VaultWindow vw : VAULT_WINDOWS.values()) {
      vw.saveDisplayToPage();
    }
  }

  public void closeAllVaults() {
    saveAllOpenToData();
    for (Player p : VAULT_WINDOWS.keySet()) {
      p.closeInventory();
    }
    VAULT_WINDOWS.clear();
  }
}