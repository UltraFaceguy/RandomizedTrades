package me.ccgreen.Storinator.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandCompletion;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import me.ccgreen.Storinator.StorinatorPlugin;
import me.ccgreen.Storinator.managers.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

@CommandAlias("storinator|storage|vault|bank")
public class StorinatorCommand extends BaseCommand {

  private final StorinatorPlugin plugin;

  public StorinatorCommand(StorinatorPlugin plugin) {
    this.plugin = plugin;
  }

  @Subcommand("reload all")
  @CommandPermission("storinator.reload")
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
    MessageUtils.sendMessage(sender, "&aStorinator Reloaded");
  }

  @Subcommand("reload titles")
  @CommandPermission("storinator.reload")
  public void reloadTitlesCommand(CommandSender sender) {
    for (Player p : Bukkit.getOnlinePlayers()) {
      if (p.getInventory().getType() == InventoryType.CHEST) {
        p.closeInventory();
        p.sendMessage(FaceColor.ORANGE + "Inventory CLOSED! Reason? An admin is fiddling with something! Sorry!");
      }
    }
    StorinatorPlugin.Config.rebuildTitles();
    MessageUtils.sendMessage(sender, "&aStorinator Titles Reloaded");
  }

  @Subcommand("open")
  @CommandCompletion("@players")
  @CommandPermission("storinator.open")
  public void openCommand(CommandSender sender, OnlinePlayer target) {
    plugin.getVaultManager().openVault(target.getPlayer().getUniqueId(), VaultManager.PERSONAL_VAULT, target.getPlayer());
  }

  @Subcommand("inspect")
  @CommandCompletion("@players")
  @CommandPermission("storinator.inspect")
  public void inspectCommand(Player player, OnlinePlayer target) {
    plugin.getVaultManager().openVault(target.getPlayer().getUniqueId(), VaultManager.PERSONAL_VAULT, player);
  }
}
