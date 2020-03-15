package me.ccgreen.Storinator;

import me.ccgreen.Storinator.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ccgreen.SQLlib.SQLlibMain;
import me.ccgreen.Storinator.listeners.EntryExitListener;
import me.ccgreen.Storinator.listeners.InventoryListener;
import me.ccgreen.Storinator.managers.PlayerManager;
import me.ccgreen.Storinator.windows.WindowManager;
import se.ranzdo.bukkit.methodcommand.CommandHandler;


public class StorinatorMain extends JavaPlugin implements Listener {

	public static WindowManager winMan;
	public static PlayerManager playMan;
	public static config Config;

	private CommandHandler commandHandler;
	
	public static SQLlibMain SQL;
	private static ConsoleCommandSender CONSOLE;
	
	public static String userTable = "storinator_data_v2";
	
	@Override
	public void onEnable() {
		
		SQL = (SQLlibMain) Bukkit.getServer().getPluginManager().getPlugin("SQLlib");
		CONSOLE = Bukkit.getServer().getConsoleSender();
		
		initTables();

		commandHandler = new CommandHandler(this);
		
		winMan = new WindowManager(this);
		playMan = new PlayerManager(this);
		Config = new config(this);

		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new EntryExitListener(), this);

		commandHandler.registerCommands(new BaseCommand(this));
		
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			playMan.newPlayer(player);
		}
	}
	
	public StorinatorMain getMain() {
		return this;
	}

	@Override
	public void onDisable() {
		winMan.closeInventoryies();
	}
	
	private void initTables() {
		SQL.initialiseTable(userTable, "uuidInv", "uuidInv VARCHAR(39) NOT NULL , data TEXT NULL DEFAULT NULL");
	}

	public static void printInfo(String line) {
		CONSOLE.sendMessage(ChatColor.GREEN + "[Storinator9000] : " + line);
	}

	public static void printWarning(String line) {
		CONSOLE.sendMessage(ChatColor.YELLOW + "[Storinator9000] : " + line);
	}

	public static void printError(String line) {
		CONSOLE.sendMessage(ChatColor.RED + "[Storinator9000] : " + line);
	}
}