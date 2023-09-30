package me.ccgreen.Storinator.pojo;

import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import com.tealcube.minecraft.bukkit.shade.jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
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
  private final Map<Integer, VaultPage> pages;
  private final Map<UUID, Integer> lastOpenPage = new HashMap<>();

  public Vault(StorinatorPlugin plugin, UUID uuid, String type, Map<Integer, VaultPage> pages) {
    this.plugin = plugin;
    this.uuid = uuid;
    this.type = type;
    this.pages = pages;
  }

  public Inventory openPage(Player player) {
    return openPage(player, lastOpenPage.getOrDefault(player.getUniqueId(), 0));
  }

  public Inventory openPage(Player viewer, int page) {
    plugin.getVaultManager().getLastOpenedData().put(viewer.getUniqueId(), new LastOpenedData(uuid, type));
    if (!pages.containsKey(page)) {
      viewer.playSound(viewer.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1.0f, 0.7f);
      PaletteUtil.sendMessage(viewer, "|yellow|This page doesn't exist!");
      return null;
    }
    if (!hasAccess(viewer, page)) {
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

  public void savePages(EntityManager entityManager) {
    //Bukkit.getLogger().info("SAVING PAGES");
    for (VaultPage vp : pages.values()) {
      boolean changes = vp.serialize();
      if (changes) {
        //Bukkit.getLogger().info("CHANGES HAPPENED SAVING");
        entityManager.merge(vp);
        entityManager.detach(vp);
      } else {
        //Bukkit.getLogger().info("NO CHANGES NO SAVING");
      }
    }
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

  public void destroy() {
    plugin = null;
    uuid = null;
    type = null;
  }
}
