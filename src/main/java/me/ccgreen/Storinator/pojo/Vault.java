package me.ccgreen.Storinator.pojo;

import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.config.LockStata;
import me.ccgreen.Storinator.events.PagesRequestEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Data
public class Vault {

  private StorinatorPlugin plugin;

  private UUID uuid;
  private String type;
  private Map<Integer, VaultPage> pages = new HashMap<>();
  private Map<UUID, Integer> lastOpenPage = new HashMap<>();

  public Vault(StorinatorPlugin plugin, UUID uuid, String type,
      Collection<Integer> availablePages) {
    this.plugin = plugin;
    this.uuid = uuid;
    this.type = type;
    Set<Integer> lootPages = new HashSet<>(availablePages);
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
      for (int i : lootPages) {
        String uuidKey = uuid.toString() + "_" + i;
        VaultPage page = loadPage(uuidKey, i);
        Bukkit.getScheduler().runTask(plugin, () -> pages.put(i, page));
      }
      entityManager.close();
    });
  }

  public Inventory openPage(Player player) {
    return openPage(player, lastOpenPage.getOrDefault(player.getUniqueId(), 0));
  }

  public Inventory openPage(Player viewer, int page) {
    if (!pages.containsKey(page) || !hasAccess(viewer, page)) {
      viewer.playSound(viewer.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1.0f, 0.7f);
      PaletteUtil.sendMessage(viewer, "|yellow|You don't have access to this vault page!");
      return null;
    }
    viewer.playSound(viewer.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0f, 0.9f + page * 0.03f);
    lastOpenPage.put(viewer.getUniqueId(), page);
    updateButtons(viewer, pages.get(page).getInventory());
    Inventory invy = pages.get(page).getInventory();
    viewer.openInventory(invy);
    return invy;
  }

  public VaultPage loadPage(String uuidKey, int page) {
    EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
    VaultPage vaultPage = entityManager.find(VaultPage.class, uuidKey);
    if (vaultPage == null) {
      vaultPage = new VaultPage(uuidKey, emptyInvy());
      entityManager.getTransaction().begin();
      entityManager.persist(vaultPage);
      entityManager.getTransaction().commit();
      entityManager.close();
    } else {
      vaultPage.setInventory(VaultPage.fromBase64(vaultPage.getData()));
    }
    return vaultPage;
  }

  public void savePages() {
    EntityManager entityManager = plugin.getSessionFactory().createEntityManager();
    entityManager.getTransaction().begin();
    for (VaultPage vp : pages.values()) {
      vp.serialize();
      entityManager.merge(vp);
      entityManager.detach(vp);
    }
    entityManager.getTransaction().commit();
    entityManager.close();
  }

  private void updateButtons(Player player, Inventory inventory) {
    for (int i = 0; i < 9; i++) {
      ItemStack button;
      if (lastOpenPage.getOrDefault(player.getUniqueId(), 0) == i) {
        button = StorinatorPlugin.Config.getIcon(LockStata.OPENED, i);
      } else if (hasAccess(player, i)) {
        button = StorinatorPlugin.Config.getIcon(LockStata.UNLOCKED, i);
      } else {
        button = StorinatorPlugin.Config.getIcon(LockStata.LOCKED, i);
      }
      inventory.setItem(i, button);
    }
  }

  public boolean hasAccess(Player player, int page) {
    PagesRequestEvent pagesRequestEvent = new PagesRequestEvent(player, type, page);
    Bukkit.getPluginManager().callEvent(pagesRequestEvent);
    return pagesRequestEvent.isAllowed();
  }

  public static Inventory emptyInvy() {
    return Bukkit.createInventory(null, 54, StorinatorPlugin.INVY_NAME);
  }
}
