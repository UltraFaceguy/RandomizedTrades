package me.ccgreen.Storinator.commands;

import com.tealcube.minecraft.bukkit.facecore.utilities.MessageUtils;
import com.tealcube.minecraft.bukkit.shade.acf.BaseCommand;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandAlias;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandCompletion;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.CommandPermission;
import com.tealcube.minecraft.bukkit.shade.acf.annotation.Subcommand;
import com.tealcube.minecraft.bukkit.shade.acf.bukkit.contexts.OnlinePlayer;
import me.ccgreen.Storinator.StorinatorMain;
import org.bukkit.command.CommandSender;

@CommandAlias("storinator|storage|vault|bank")
public class StorinatorCommand extends BaseCommand {

  private final StorinatorMain plugin;

  public StorinatorCommand(StorinatorMain plugin) {
    this.plugin = plugin;
  }

  @Subcommand("reload")
  @CommandPermission("storinator.reload")
  public void reloadCommand(CommandSender sender) {
    plugin.onDisable();
    plugin.onEnable();
    MessageUtils.sendMessage(sender, "&aStorinator Reloaded");
  }

  @Subcommand("open")
  @CommandCompletion("@players")
  @CommandPermission("storinator.open")
  public void openCommand(CommandSender sender, OnlinePlayer target) {
    StorinatorMain.winMan.createVaultWindow(target.getPlayer());
  }

  @Subcommand("inspect")
  @CommandCompletion("@players")
  @CommandPermission("storinator.inspect")
  public void inspectCommand(OnlinePlayer sender, OnlinePlayer target) {
    StorinatorMain.winMan.createVaultWindow(sender.getPlayer(), target.getPlayer());
  }
}
