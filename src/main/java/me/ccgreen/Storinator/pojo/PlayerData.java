package me.ccgreen.Storinator.pojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.ccgreen.SQLlib.SQLlibMain;
import me.ccgreen.Storinator.StorinatorMain;

public class PlayerData {

  private final Inventory[] vaultData = new Inventory[9];

  private static SQLlibMain SQL;
  public static String INVY_NAME = ChatColor.RED + "" + ChatColor.GRAY
      + StorinatorMain.Config.windowTitle();
  private Player player;
  private int openPage;

  public PlayerData(Player player) {
    PlayerData.SQL = StorinatorMain.SQL;
    this.player = player;
    for (int i = 0; i < 9; i++) {
      ResultSet invResult = SQL
          .get(StorinatorMain.userTable, "uuidInv = '" + player.getUniqueId() + "_" + i + "'");
      try {
        if (invResult.next()) {
          vaultData[i] = fromBase64(invResult.getString("data"));
        } else {
          vaultData[i] = Bukkit.createInventory(null, 54, INVY_NAME);
        }
      } catch (SQLException e) {
        //page not saved before (never been opened)
      } catch (IOException e) {
        StorinatorMain.printError("decoding data error for player: " + player.getDisplayName());
        e.printStackTrace();
      }
    }
    openPage = 0;
  }

  public int lastOpenPage() {
    return openPage;
  }

  public Inventory getPage(int page) {
    openPage = page;
    Inventory inv = vaultData[page];
    if (inv == null) {
      inv = Bukkit.createInventory(null, 54, INVY_NAME);
    }
    return inv;
  }

  public static void updatePage(PlayerData data, Inventory inv, int page) {
    data.vaultData[page] = inv;
    String pageData = toBase64(inv);
    try {
      SQL.set(StorinatorMain.userTable, "uuidInv, data", "'" + data.player.getUniqueId()
          + "_" + page + "', '" + pageData + "'");
    } catch (Exception e) {
      Bukkit.getLogger().warning("Error trying to input page for user " + data.player.getUniqueId());
      e.printStackTrace();
    }
  }

  public static Vector<String> saveAll(PlayerData data) {
    Vector<String> retval = new Vector<>();
    for (int i = 0; i < 9; i++) {
      if (data.vaultData[i] != null) {
        String pageData = toBase64(data.vaultData[i]);
        retval.add("replace into " + StorinatorMain.userTable + " (uuidInv, data) VALUES ('" +
            data.player.getUniqueId() + "_" + i + "', '" + pageData + "')");
      }
    }
    return retval;
  }

  private static String toBase64(Inventory inventory) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

      // Write the size of the inventory
      dataOutput.writeInt(45);

      // Save every element in the list
      for (int i = 9; i < 54; i++) {
        dataOutput.writeObject(inventory.getItem(i));
      }

      // Serialize that array
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  private static Inventory fromBase64(String data) throws IOException {
    try {
      ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      int size = dataInput.readInt();
      Inventory inventory = Bukkit.getServer().createInventory(null, 54, INVY_NAME);

      // Read the serialized inventory
      for (int i = 9; i < 54; i++) {
        try {
          inventory.setItem(i, (ItemStack) dataInput.readObject());
        } catch (Exception e) {
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