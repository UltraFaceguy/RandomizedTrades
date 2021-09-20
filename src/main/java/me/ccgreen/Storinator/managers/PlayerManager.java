package me.ccgreen.Storinator.managers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.UUID;
import java.util.WeakHashMap;
import me.ccgreen.Storinator.pojo.PlayerData;
import me.ccgreen.Storinator.windows.VaultWindow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.ccgreen.Storinator.StorinatorMain;

public class PlayerManager {

  private final StorinatorMain plugin;
  private final Map<UUID, PlayerData> playerData;

  public PlayerManager(final StorinatorMain plugin) {
    this.plugin = plugin;
    playerData = new WeakHashMap<>();
  }

  public PlayerData getPlayerData(final Player player) {
    if (playerData.containsKey(player.getUniqueId())) {
      return playerData.get(player.getUniqueId());
    }
    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
      PlayerData data = new PlayerData(player);
      StorinatorMain.playMan.addData(player, data);
    }, 0L);
    return null;
  }

  public boolean hasPlayer(final Player player) {
    return playerData.containsKey(player.getUniqueId());
  }

  public void addData(final Player player, final PlayerData data) {
    playerData.put(player.getUniqueId(), data);
  }

  public void saveAll() {
    StorinatorMain.winMan.saveAllOpenToData();
    Set<UUID> uuids = new HashSet<>();
    for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
      PlayerData.savePage(entry.getValue(), 0);
      PlayerData.savePage(entry.getValue(), 1);
      PlayerData.savePage(entry.getValue(), 2);
      PlayerData.savePage(entry.getValue(), 3);
      PlayerData.savePage(entry.getValue(), 4);
      PlayerData.savePage(entry.getValue(), 5);
      PlayerData.savePage(entry.getValue(), 6);
      PlayerData.savePage(entry.getValue(), 7);
      PlayerData.savePage(entry.getValue(), 8);
      boolean online = false;
      for (Player p : Bukkit.getOnlinePlayers()) {
        if (p.getUniqueId().equals(entry.getKey())) {
          online = true;
          break;
        }
      }
      if (!online) {
        uuids.add(entry.getKey());
      }
    }
    for (final UUID uuid : uuids) {
      playerData.remove(uuid);
    }
  }

  public void playerLeave(final Player player) {
    player.closeInventory();
  }
}