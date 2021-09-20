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

  private final Inventory displayInventory;
  private final Player owner;
  private final Player viewer;
  private final PlayerData data;
  private final ItemStack[] buttons;

  private int openedPageNumber;

  VaultWindow(final Player owner, final Player viewer) {
    this.owner = owner;
    this.viewer = ((viewer == null) ? owner : viewer);

    buttons = new ItemStack[10];
    data = StorinatorMain.playMan.getPlayerData(owner);
    openedPageNumber = (hasPageAccess(owner, data.lastOpenPage()) ? data.lastOpenPage() : 0);
    displayInventory = Bukkit.createInventory(null, 54, PlayerData.INVY_NAME);
    createButtons();
    applyPageData(data.getPage(openedPageNumber));
    this.viewer.openInventory(displayInventory);
  }

  private void createButtons() {
    String configName = StorinatorMain.Config.tabName();
    for (int i = 0; i < 9; ++i) {
      final String name = configName.replaceAll("%number%", Integer.toString(i + 1));
      ItemStack button;
      if (this.hasPageAccess(this.owner, i)) {
        button = new ItemStack(Material.MAP, 1);
        final ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        button.setItemMeta(meta);
      } else {
        button = StorinatorMain.Config.getIcon(i);
      }
      buttons[i] = button;
    }
  }

  private ItemStack activeButton() {
    String name = StorinatorMain.Config.tabName().replaceAll("%number%",
        Integer.toString(openedPageNumber + 1));
    ItemStack button = new ItemStack(Material.FILLED_MAP, 1);
    ItemMeta meta = button.getItemMeta();
    assert meta != null;
    meta.addEnchant(Enchantment.MENDING, 1, true);
    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    meta.setDisplayName(name);
    button.setItemMeta(meta);
    return button;
  }

  private void switchToPage(final int page) {
    if (page == openedPageNumber) {
      return;
    }
    owner.playSound(owner.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0f, 0.9f + page * 0.03f);
    saveDisplayToPage();
    openedPageNumber = page;
    applyPageData(data.getPage(page));
    owner.updateInventory();
    if (viewer != null) {
      viewer.updateInventory();
    }
  }

  void handleClick(final InventoryClickEvent event) {
    final int slot = event.getSlot();
    if (slot < 0 || slot >= displayInventory.getSize()) {
      return;
    }
    if (event.getClickedInventory().getType() == InventoryType.CHEST) {
      if (slot < 9) {
        event.setCancelled(true);
        if (slot != openedPageNumber) {
          if (hasPageAccess(owner, slot)) {
            switchToPage(slot);
            return;
          }
          MessageUtils.sendMessage(owner, "&eSorry! You don't have this page unlocked!");
          owner.playSound(this.owner.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1.0f, 0.7f);
        }
      }
    } else if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
      event.setCancelled(true);
    }
  }

  public static boolean hasPageAccess(final Player player, final int pageNumber) {
    if (pageNumber == 0) {
      return true;
    }
    if (player.hasPermission("Storinator.vault." + pageNumber)) {
      return true;
    }
    return switch (pageNumber) {
      case 4 -> QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 50;
      case 5 -> QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 120;
      case 6 -> QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 200;
      case 7 -> QuestWorldPlugin.getAPI().getPlayerStatus(player).getQuestPoints() >= 300;
      default -> false;
    };
  }

  private void close() {
    saveDisplayToPage();
    if (owner.getOpenInventory().getTopInventory() == displayInventory) {
      owner.closeInventory();
    }
    if (viewer != null && viewer.getOpenInventory().getTopInventory() == displayInventory) {
      viewer.closeInventory();
    }
  }

  private void applyPageData(Inventory pageInvy) {
    displayInventory.setContents(pageInvy.getContents());
    applyButtons();
  }

  public void saveDisplayToPage() {
    Inventory pageInvy = data.getPage(openedPageNumber);
    pageInvy.setContents(displayInventory.getContents());
    pageInvy.setItem(0, null);
    pageInvy.setItem(1, null);
    pageInvy.setItem(2, null);
    pageInvy.setItem(3, null);
    pageInvy.setItem(4, null);
    pageInvy.setItem(5, null);
    pageInvy.setItem(6, null);
    pageInvy.setItem(7, null);
    pageInvy.setItem(8, null);
  }

  private void applyButtons() {
    for (int i = 0; i < 9; ++i) {
      if (i == openedPageNumber) {
        displayInventory.setItem(i, activeButton());
      } else {
        displayInventory.setItem(i, buttons[i]);
      }
    }
  }
}