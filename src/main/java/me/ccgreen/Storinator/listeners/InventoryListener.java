package me.ccgreen.Storinator.listeners;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.apache.commons.validator.routines.checkdigit.ISBN10CheckDigit;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.events.PagesRequestEvent;
import me.ccgreen.Storinator.managers.VaultManager;
import me.ccgreen.Storinator.pojo.LastOpenedData;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

  private final StorinatorPlugin plugin;

  public InventoryListener(StorinatorPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onInvyClick(InventoryClickEvent event) {

    LastOpenedData lastOpenedData = plugin.getVaultManager().getLastOpenedData()
            .get(event.getWhoClicked().getUniqueId());


//    if (event.getClick() == ClickType.NUMBER_KEY) {
//      if (lastOpenedData.getVaultType().equals("guild-vault")){
//        if (isBlockedItem(event.getWhoClicked().getInventory().getItem(event.getHotbarButton())) || isBlockedItem(event.getCurrentItem())) {
//          MessageUtils.sendMessage(event.getWhoClicked(), plugin.getBlockedItemMessage());
//          event.setCancelled(true);
//        }
//      }
//    }
//
//
//    if (event.getCurrentItem().hasItemMeta()){
//      if (lastOpenedData.getVaultType().equals("guild-vault") && isBlockedItem(event.getCurrentItem())){
//        MessageUtils.sendMessage(event.getWhoClicked(), plugin.getBlockedItemMessage());
//        event.setCancelled(true);
//      }
//    }

    if (lastOpenedData.getVaultType().equals("guild-vault") && isBlockedItem(event.getCurrentItem())) {
      MessageUtils.sendMessage(event.getWhoClicked(), plugin.getBlockedItemMessage());
      event.setCancelled(true);
    } else if (event.getClick() == ClickType.NUMBER_KEY) {
      ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
      if (lastOpenedData.getVaultType().equals("guild-vault") && (isBlockedItem(hotbarItem) || isBlockedItem(event.getCurrentItem()))) {
        MessageUtils.sendMessage(event.getWhoClicked(), plugin.getBlockedItemMessage());
        event.setCancelled(true);
      }
    }

    if (event.getSlot() < 9 && event.getCurrentItem() != null &&
        event.getCurrentItem().getType() == Material.PAPER &&
        event.getCurrentItem().hasItemMeta()) {
      ItemMeta meta = event.getCurrentItem().getItemMeta();

      if (meta.hasCustomModelData() && (meta.getCustomModelData() <= 25 && meta.getCustomModelData() >= 23)) {
        event.setCancelled(true);

        plugin.getVaultManager().openVault(
            lastOpenedData.getUuid(),
            lastOpenedData.getVaultType(),
            (Player) event.getWhoClicked(),
            event.getSlot()
        );
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

  private boolean isBlockedItem(ItemStack item){
    boolean isBlocked = false;
    if (item == null || !item.hasItemMeta()){
      return false;
    }

    ItemMeta itemMeta = item.getItemMeta();

    if (itemMeta.getLore() == null || itemMeta.getLore().isEmpty()){
      return false;
    }

    for (String lore : itemMeta.getLore()){
      String strippedLoreLine = lore.strip();
      if (strippedLoreLine.contains(plugin.getUntradableSymbol())){
        isBlocked = true;
        break;
      }
    }
    return isBlocked;
  }

}