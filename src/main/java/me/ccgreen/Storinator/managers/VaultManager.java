package me.ccgreen.Storinator.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.pojo.Vault;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VaultManager {

  private final StorinatorPlugin plugin;
  @Getter
  private final Map<UUID, Vault> vaults = new HashMap<>();

  public static String PERSONAL_VAULT = "bank-vault";

  public VaultManager(final StorinatorPlugin plugin) {
    this.plugin = plugin;
  }

  public void loadPersonalVault(Player player) {
    if (vaults.containsKey(player.getUniqueId())) {
      return;
    }
    vaults.put(player.getUniqueId(), new Vault(plugin, player.getUniqueId(),
        PERSONAL_VAULT, List.of(0, 1, 2, 3, 4, 5, 6, 7, 8)));
  }

  public void openVault(UUID uuid, Player player, int page) {
    Vault vault = vaults.get(uuid);
    vault.openPage(player, page);
  }

  public void openVault(UUID uuid, Player player) {
    Vault vault = vaults.get(uuid);
    vault.openPage(player);
  }

  public void saveAllAsync() {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      saveAll();
      Bukkit.getScheduler().runTask(plugin, this::clearEmptyVaults);
    });
  }

  public void saveAll() {
    for (Vault v : new HashSet<>(getVaults().values())) {
      v.savePages();
    }
  }

  private void clearEmptyVaults() {
    Set<String> onlinePlayers = new HashSet<>();
    for (Player p : Bukkit.getOnlinePlayers()) {
      onlinePlayers.add(p.getUniqueId().toString());
    }
    vaults.keySet().removeIf(uuid -> !onlinePlayers.contains(uuid.toString()));
  }
}