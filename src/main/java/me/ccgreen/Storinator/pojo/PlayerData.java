package me.ccgreen.Storinator.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import me.ccgreen.Storinator.windows.VaultWindow;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.ccgreen.Storinator.StorinatorMain;

public class PlayerData {

  public static String INVY_NAME = ChatColor.DARK_GRAY + StorinatorMain.Config.windowTitle();

  private final Inventory[] inventories;
  private final Player player;
  private int openPage;

  public PlayerData(final Player player) {
    this.inventories = new Inventory[9];
    this.player = player;
    String uuid = player.getUniqueId().toString();
    for (int i = 0; i < 9; ++i) {
      try {
        String data = StorinatorMain.sqlManager.getDataRow(uuid + "_" + i);
        Bukkit.getLogger().info("[Storinator] Found data for " + player.getName() + " page " + i);
        inventories[i] = fromBase64(data);
      } catch (Exception e) {
        inventories[i] = Bukkit.createInventory(null, 54, PlayerData.INVY_NAME);
        Bukkit.getLogger().info("[Storinator] Created new page " + i + " for " + player.getName());
      }
    }
    openPage = 0;
  }

  public int lastOpenPage() {
    return openPage;
  }

  public Inventory getPage(final int page) {
    openPage = page;
    Inventory inv = inventories[page];
    if (inv == null) {
      inv = Bukkit.createInventory(null, 54, PlayerData.INVY_NAME);
    }
    return inv;
  }

  public static void savePage(PlayerData data, int page) {
    Inventory inv = data.inventories[page];
    String pageData = toBase64(inv);
    try {
      StorinatorMain.sqlManager.setDataRow(data.player.getUniqueId() + "_" + page, pageData);
    }
    catch (Exception e) {
      Bukkit.getLogger().warning("[Storinator] Page save failed for: " + data.player.getUniqueId());
      e.printStackTrace();
    }
  }

  private static String toBase64(final Inventory inventory) {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
      dataOutput.writeInt(45);
      for (int i = 9; i < 54; ++i) {
        dataOutput.writeObject(inventory.getItem(i));
      }
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    }
    catch (Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  private static Inventory fromBase64(final String data) throws IOException {
    try {
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      final int size = dataInput.readInt();
      final Inventory inventory = Bukkit.getServer().createInventory(null, 54, PlayerData.INVY_NAME);
      for (int i = 9; i < 54; ++i) {
        try {
          inventory.setItem(i, (ItemStack) dataInput.readObject());
        } catch (Exception e2) {
          inventory.setItem(i, null);
        }
      }
      dataInput.close();
      return inventory;
    } catch (Exception e) {
      throw new IOException("Unable to decode class type.", e);
    }
  }
}
