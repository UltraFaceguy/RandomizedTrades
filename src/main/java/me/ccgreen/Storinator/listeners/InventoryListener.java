package me.ccgreen.Storinator.listeners;

import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.events.PagesRequestEvent;
import me.ccgreen.Storinator.managers.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

  private final StorinatorPlugin plugin;

  public InventoryListener(StorinatorPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInvyClick(InventoryClickEvent event) {
    if (event.getSlot() < 9 && event.getCurrentItem() != null &&
        event.getCurrentItem().getType() == Material.PAPER &&
        event.getCurrentItem().hasItemMeta()) {
      ItemMeta meta = event.getCurrentItem().getItemMeta();
      if (meta.hasCustomModelData() && (meta.getCustomModelData() <= 25
          && meta.getCustomModelData() >= 23)) {
        event.setCancelled(true);
        plugin.getVaultManager().openVault(event.getWhoClicked().getUniqueId(),
            (Player) event.getWhoClicked(), event.getSlot());
      }
    }
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onInventoryOpen(InventoryOpenEvent event) {
    if (event.getInventory().getType() != InventoryType.ENDER_CHEST) {
      return;
    }
    Player player = (Player) event.getPlayer();
    if (!event.getPlayer().hasPermission("Storinator.access")) {
      return;
    }
    event.setCancelled(true);
    plugin.getVaultManager().openVault(player.getUniqueId(), player);
  }

  @EventHandler
  public void onPageRequest(PagesRequestEvent event) {
    if (VaultManager.PERSONAL_VAULT.equals(event.getType())) {
      if (event.getPage() == 0) {
        event.setAllowed(false);
        return;
      }
      if (event.getPlayer().hasPermission("Storinator.vault." + event.getPage())) {
        event.setAllowed(true);
        return;
      }
      if (plugin.getQuestApi() == null) {
        event.setAllowed(false);
        return;
      }
      event.setAllowed(switch (event.getPage()) {
        case 4 -> plugin.getQuestApi().getPlayerStatus(event.getPlayer()).getQuestPoints() >= 50;
        case 5 -> plugin.getQuestApi().getPlayerStatus(event.getPlayer()).getQuestPoints() >= 120;
        case 6 -> plugin.getQuestApi().getPlayerStatus(event.getPlayer()).getQuestPoints() >= 200;
        case 7 -> plugin.getQuestApi().getPlayerStatus(event.getPlayer()).getQuestPoints() >= 300;
        default -> false;
      });
    }
  }

  @EventHandler
  public void onWorldSave(WorldSaveEvent e) {
    if (e.getWorld().getName().equals("Faceland")) {
      plugin.getVaultManager().saveAllAsync();
      Bukkit.getLogger().info("[Storinator] Saved all vault data");
    }
  }
}