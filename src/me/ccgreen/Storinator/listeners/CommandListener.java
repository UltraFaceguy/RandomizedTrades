package me.ccgreen.Storinator.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ccgreen.Storinator.StorinatorMain;

public class CommandListener implements CommandExecutor {

	StorinatorMain plugin;

	public CommandListener(StorinatorMain plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			return false;
		}
		if(args.length > 0) {
			if(args[0].equalsIgnoreCase("open")) {
				if(args.length > 1) {
					Player player = plugin.getServer().getPlayer(args[1]);
					if(player != null) {
						StorinatorMain.winMan.createVaultWindow(player);
					} else {
						printHelp();
					}
				}
			}
			else if(args[0].equalsIgnoreCase("reload")) {
				StorinatorMain.Config.loadConfig();
			} else {
				printHelp();
			}
		} else {
			printHelp();
		}
		return false;
	}

	void printHelp() {
		StorinatorMain.printInfo("Use 'storinator open <user>' to open the vault for the user");
		StorinatorMain.printInfo("Use 'storinator reload' to reload the config");
	}
}