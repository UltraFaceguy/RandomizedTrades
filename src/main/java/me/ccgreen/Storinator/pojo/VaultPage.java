package me.ccgreen.Storinator.pojo;

import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.Column;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.Entity;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.Id;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.Table;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.Transient;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.ccgreen.Storinator.StorinatorPlugin;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

@Getter
@Setter
@Entity
@Table(name = "storinator_data_v2")
public class VaultPage implements Serializable {

  @Id
  @Column(name = "uuidInv", length = 64)
  private String uuidInv;

  @Column(name = "data", length = 50000000)
  private String data;

  @Transient
  private Inventory inventory;
  @Transient
  private boolean hasBeenUpdated = false;

  public VaultPage() {

  }

  public VaultPage(String uuidInv, Inventory inventory) {
    this.uuidInv = uuidInv;
    this.inventory = inventory;
    data = toBase64(inventory);
  }

  // Return true if changes occurred
  public boolean serialize() {
    String oldData = data;
    data = toBase64(inventory);
    return !data.equals(oldData);
  }

  public static String toBase64(final Inventory inventory) {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      final BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
      dataOutput.writeInt(45);
      for (int i = 9; i < 54; ++i) {
        dataOutput.writeObject(inventory.getItem(i));
      }
      dataOutput.close();
      return Base64Coder.encodeLines(outputStream.toByteArray());
    } catch (Exception e) {
      throw new IllegalStateException("Unable to save item stacks.", e);
    }
  }

  public static Inventory fromBase64(final String data, final String vaultType) {
    String title = switch (vaultType) {
      case "guild-vault" -> StorinatorPlugin.GUILD_INVY_NAME;
      case "home-vault" -> StorinatorPlugin.HOME_INVY_NAME;
      default -> StorinatorPlugin.PERSONAL_INVY_NAME;
    };
    try {
      final ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
      final BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
      final int size = dataInput.readInt();

      final Inventory inventory = Bukkit.getServer().createInventory(null, 54, title);
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
      Bukkit.getLogger().warning("[Storinator] Unable to load a data invy! Creating new...");
      return Bukkit.getServer().createInventory(null, 54, title);
    }
  }
}