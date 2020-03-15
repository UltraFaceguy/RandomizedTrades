package me.ccgreen.Storinator.commands;

import me.ccgreen.Storinator.StorinatorMain;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;

public class BaseCommand {

  private final StorinatorMain plugin;

  public BaseCommand(StorinatorMain plugin) {
    this.plugin = plugin;
  }

  @Command(identifier = "storinator reload", permissions = "strife.command.stats")
  public void reloadCommand(CommandSender sender) {
    StorinatorMain.Config.loadConfig();
  }

  @Command(identifier = "storinator open", permissions = "strife.command.stats")
  public void openCommand(CommandSender sender, @Arg(name = "target") Player target) {
    StorinatorMain.winMan.createVaultWindow(target);
  }
}
