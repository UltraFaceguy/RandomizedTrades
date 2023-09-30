package me.ccgreen.Storinator.managers;

import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import lombok.Getter;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.pojo.LastOpenedData;
import me.ccgreen.Storinator.pojo.Vault;
import me.ccgreen.Storinator.pojo.VaultPage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class VaultManager {

  private final StorinatorPlugin plugin;
  @Getter
  private final Map<UUID, Vault> vaults = new HashMap<>();
  @Getter
  private final Map<UUID, LastOpenedData> lastOpenedData = new HashMap<>();

  public static String PERSONAL_VAULT = "bank-vault";

  public VaultManager(final StorinatorPlugin plugin) {
    this.plugin = plugin;
  }

  public void openVault(UUID uuid, String vaultType, Player player, int page) {
    if (vaults.containsKey(uuid)) {
      vaults.get(uuid).openPage(player, page);
      return;
    }
    createVault(uuid, vaultType, player);
  }

  public void openVault(UUID uuid, String vaultType, Player player) {
    if (vaults.containsKey(uuid)) {
      vaults.get(uuid).openPage(player);
      return;
    }
    createVault(uuid, vaultType, player);
  }

  public void createVault(UUID uuid, String vaultType, @Nullable Player player) {
    if ((vaults.containsKey(uuid))) {
      return;
    }
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      Map<Integer, VaultPage> pages = new HashMap<>();
      EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
      for (int i = 0; i <= 8; i++) {
        String uuidKey = uuid.toString() + "_" + i;
        VaultPage page = loadPage(uuidKey, vaultType);
        pages.put(i, page);
      }
      entityManager.close();
      Bukkit.getScheduler().runTask(plugin, () -> {
        vaults.put(uuid, new Vault(plugin, uuid, vaultType, pages));
        if (player != null) {
          vaults.get(uuid).openPage(player);
        }
      });
    });
  }

  private VaultPage loadPage(String uuidKey, String vaultType) {
    String title = switch (vaultType) {
      case "guild-vault" -> StorinatorPlugin.GUILD_INVY_NAME;
      default -> StorinatorPlugin.PERSONAL_INVY_NAME;
    };
    EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
    VaultPage vaultPage = entityManager.find(VaultPage.class, uuidKey);
    if (vaultPage == null) {
      Inventory invy = Bukkit.createInventory(null, 54, title);
      vaultPage = new VaultPage(uuidKey, invy);
      entityManager.getTransaction().begin();
      entityManager.persist(vaultPage);
      entityManager.getTransaction().commit();
      entityManager.close();
    } else {
      vaultPage.setInventory(VaultPage.fromBase64(vaultPage.getData(), vaultType));
    }
    return vaultPage;
  }

  public void saveAllAsync() {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, this::saveAll);
  }

  public void saveAll() {
    EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
    entityManager.getTransaction().begin();
    getVaults().values().forEach(v -> v.savePages(entityManager));
    entityManager.getTransaction().commit();
    entityManager.close();
  }
}