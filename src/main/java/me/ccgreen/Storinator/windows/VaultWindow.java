package me.ccgreen.Storinator.windows;

import com.questworld.QuestWorldPlugin;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.ccgreen.Storinator.StorinatorMain;
import me.ccgreen.Storinator.pojo.PlayerData;

public class VaultWindow {

  private final Inventory inv;
  private final Player owner;
  private final Player viewer;
  private final PlayerData data;
  private final ItemStack[] buttons = new ItemStack[10];
  private int page;

  VaultWindow(Player owner, Player viewer) {
    this.owner = owner;
    this.viewer = viewer == null ? owner : viewer;
    data = StorinatorMain.playMan.getData(owner);
    page = data.lastOpenPage();

    inv = data.getPage(page);
    createButtons();
    for (int i = 0; i < 9; i++) {
      if (i == page) {
        inv.setItem(i, activeButton());
      } else {
        inv.setItem(i, buttons[i]);
      }
    }

    this.viewer.openInventory(inv);
  }

  VaultWindow(Player owner, Player viewer, int page) {
    this.owner = owner;
    this.viewer = (viewer == null) ? owner : viewer;
    this.page = page;
    data = StorinatorMain.playMan.getData(owner);

    inv = data.getPage(page);
    createButtons();
    for (int i = 0; i < 9; i++) {
      if (i == page) {
        inv.setItem(i, activeButton());
      } else {
        inv.setItem(i, buttons[i]);
      }
    }
    this.viewer.openInventory(inv);
  }

  private void createButtons() {
    String configName = StorinatorMain.Config.tabName();
    for (int i = 0; i < 9; i++) {
      String name = configName.replaceAll("%number%", "" + (i + 1));
      ItemStack button;

      if (hasPageAccess(owner, i)) {
        button = new ItemStack(Material.MAP, 1);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        button.setItemMeta(meta);
      } else {
        button = StorinatorMain.Config.getIcon(i);
      }

      buttons[i] = button;
    }
  }

  private ItemStack activeButton() {
    String name = StorinatorMain.Config.tabName().replaceAll("%number%", "" + (page + 1));
    ItemStack button = new ItemStack(Material.FILLED_MAP, 1);
    ItemMeta meta = button.getItemMeta();
    assert meta != null;
    meta.addEnchant(Enchantment.MENDING, 1, true);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setDisplayName(name);
    button.setItemMeta(meta);

    return button;
  }

  private void reloadInv(int page) {
    this.page = page;

    inv.clear();
    for (int i = 0; i < 9; i++) {
      if (i == page) {
        inv.setItem(i, activeButton());
      } else {
        inv.setItem(i, buttons[i]);
      }
    }
    ItemStack[] items = data.getPage(page).getContents();
    for (int i = 9; i < items.length; i++) {
      inv.setItem(i, items[i]);
    }
    owner.updateInventory();
    if (viewer != null) {
      viewer.updateInventory();
    }
  }

  void HandleClickEvent(InventoryClickEvent event) {
    int slot = event.getSlot();
    if (slot < 0 || slot >= inv.getSize()) {
      return;
    }
    if (event.getClickedInventory().getType() == InventoryType.CHEST) {
      if (slot < 9) {
        if (slot == page) {
          //cur page event?
        } else if (hasPageAccess(owner, slot)) {
          owner.playSound(owner.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, 1);
          event.setCancelled(true);
          Inventory newInv = Bukkit.createInventory(null, 54, PlayerData.INVY_NAME);
          newInv.setContents(inv.getContents());
          savePage(newInv);
          // StorinatorMain.winMan.changeVaultWindow(player);
          page = slot;
          reloadInv(slot);
          return;
        } else {
          MessageUtils.sendMessage(owner, "&eSorry! You don't have this page unlocked!");
          owner.playSound(owner.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, 1);
        }
        event.setCancelled(true);
      }
    } else if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
      //player inv, allow transactions, maybe do somthing later
    } else {
      event.setCancelled(true);
    }
  }

  private boolean hasPageAccess(Player player, int pageNumber) {
    if (pageNumber == 0) {
      return true;
    }
    if (player.hasPermission("Storinator.vault." + pageNumber)) {
      return true;
    }
    switch (pageNumber) {
      case 4:
        return QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 50;
      case 5:
        return QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 120;
      case 6:
        return QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 200;
      case 7:
        return QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 300;
    }
    return false;
  }

  void savePage() {
    PlayerData.updatePage(data, inv, page);
  }

  private void savePage(Inventory inv) {
    PlayerData.updatePage(data, inv, page);
  }
}