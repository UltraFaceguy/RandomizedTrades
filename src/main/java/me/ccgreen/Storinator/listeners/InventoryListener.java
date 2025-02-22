package me.ccgreen.Storinator.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.util.List;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.events.PagesRequestEvent;
import me.ccgreen.Storinator.managers.VaultManager;
import me.ccgreen.Storinator.pojo.LastOpenedData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

  private final StorinatorPlugin plugin;
  private final List<String> blockedStrings;
  private final String blockedItemMessage;

  public InventoryListener(StorinatorPlugin plugin, VersionedSmartYamlConfiguration configuration) {
    this.plugin = plugin;
    blockedStrings = configuration.getStringList("blocked-strings");
    blockedItemMessage = PaletteUtil.color(configuration
        .getString("blocked-item-message", "|red|This item can not be moved within a vault!"));
  }

  @EventHandler
  public void onInvyClick(InventoryClickEvent event) {
    if (event.getSlot() < 9 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER &&
        event.getCurrentItem().hasItemMeta()) {
      ItemMeta meta = event.getCurrentItem().getItemMeta();
      if (meta.hasMaxStackSize() && meta.getMaxStackSize() == 25) {
        event.setCancelled(true);
        LastOpenedData lastOpenedData = plugin.getVaultManager().getLastOpenedData()
            .get(event.getWhoClicked().getUniqueId());
        plugin.getVaultManager().openVault(
            lastOpenedData.getUuid(),
            lastOpenedData.getVaultType(),
            (Player) event.getWhoClicked(),
            event.getSlot()
        );
      }
    }
  }

  @EventHandler
  public void onMoveItem(InventoryMoveItemEvent event) {
    if (event.isCancelled() || event.getDestination() == event.getSource()) {
      return;
    }
    if (event.getDestination().getType() != InventoryType.CHEST) {
      return;
    }
    Player player = (Player) event.getSource().getHolder();
    String title = player.getOpenInventory().getTitle();
    if (!StorinatorPlugin.GUILD_INVY_NAME.equals(title)) {
      return;
    }
    if (isBlockedItem(event.getItem())) {
      event.setCancelled(true);
      player.sendMessage(blockedItemMessage);
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
    plugin.getVaultManager().openVault(player.getUniqueId(), VaultManager.PERSONAL_VAULT, player);
  }

  @EventHandler
  public void onPersonalVaultRequest(PagesRequestEvent event) {
    if (!VaultManager.PERSONAL_VAULT.equals(event.getType())) {
      return;
    }
    if (event.getPage() == 0) {
      event.setAllowed(true);
      return;
    }
    if (event.getPlayer().hasPermission("Storinator.vault." + event.getPage())) {
      event.setAllowed(true);
      return;
    }
    if (plugin.getQuestApi() == null) {
      //Bukkit.getLogger().info("[Storinator] No quest API");
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

  @EventHandler
  public void onWorldSave(WorldSaveEvent e) {
    if (e.getWorld().getName().equals("Faceland")) {
      plugin.getVaultManager().saveAllAsync();
      //Bukkit.getLogger().info("[Storinator] Saved all vault data");
    }
  }

  private boolean isBlockedItem(ItemStack item) {
    boolean isBlocked = false;
    if (item == null || !item.hasItemMeta()) {
      return false;
    }

    ItemMeta itemMeta = item.getItemMeta();

    if (itemMeta == null || itemMeta.getLore() == null || itemMeta.getLore().isEmpty()) {
      return false;
    }

    for (String lore : itemMeta.getLore()) {
      String strippedLoreLine = lore.strip();
      for (String blockedString : blockedStrings) {
        if (strippedLoreLine.contains(blockedString)) {
          isBlocked = true;
          break;
        }
      }
    }
    return isBlocked;
  }

}