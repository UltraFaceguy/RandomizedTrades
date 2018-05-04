package me.ccgreen.Storinator.listeners;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.ccgreen.Storinator.StorinatorMain;

public class CommandListener implements CommandExecutor {
	
	public CommandListener(StorinatorMain plugin) {
		
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			return false;
		}
		if(args[0].equalsIgnoreCase("open")) {
			//return menu(args);
		}
		return false;
	}
}