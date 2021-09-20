package me.ccgreen.Storinator.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

import me.ccgreen.Storinator.StorinatorMain;
import org.bukkit.event.world.WorldSaveEvent;

public class InventoryListener implements Listener {

  public InventoryListener(StorinatorMain plugin) {
  }

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent event) {
    if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
      Player player = (Player) event.getPlayer();
      if (event.getPlayer().hasPermission("Storinator.access")) {
        event.setCancelled(true);
        StorinatorMain.winMan.openVaultWindow(player, null);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
      }
    }
  }

  @EventHandler
  public void onClickAction(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player) {
      StorinatorMain.winMan.handleClickEvent(event);
    }
  }

  @EventHandler
  public void OnWindowClose(InventoryCloseEvent e) {
    StorinatorMain.winMan.removePlayer((Player) e.getPlayer());
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent e) {
    if (e.getWorld().getName().equals("Faceland")) {
      StorinatorMain.playMan.saveAll();
      Bukkit.getLogger().info("[Storinator] Saved all vault data");
    }
  }
}