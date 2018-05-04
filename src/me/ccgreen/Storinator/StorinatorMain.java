package me.ccgreen.Storinator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.ccgreen.SQLlib.SQLlibMain;
import me.ccgreen.Storinator.config;
import me.ccgreen.Storinator.listeners.CommandListener;
import me.ccgreen.Storinator.listeners.EntryExitListener;
import me.ccgreen.Storinator.listeners.InvyEvent;
import me.ccgreen.Storinator.players.PlayerManager;
import me.ccgreen.Storinator.windows.WindowManager;


public class StorinatorMain extends JavaPlugin implements Listener {

	public static WindowManager winMan;
	public static PlayerManager playMan;
	public static config Config;
	
	public static SQLlibMain SQL;
	private static ConsoleCommandSender CONSOLE;
	
	private static String prefix = "Storinator_";
	public static String userTable = prefix + "playerData"; 
	public static String configTable = prefix + "config";
	
	@Override
	public void onEnable() {
		
		SQL = (SQLlibMain) Bukkit.getServer().getPluginManager().getPlugin("SQLlib");
		CONSOLE = Bukkit.getServer().getConsoleSender();
		
		initTables();
		
		winMan = new WindowManager(this);
		playMan = new PlayerManager(this);
		Config = new config(this);
		
		getCommand("storinator").setExecutor(new CommandListener(this));
		getServer().getPluginManager().registerEvents(new InvyEvent(), this);
		getServer().getPluginManager().registerEvents(new EntryExitListener(), this);
		
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			playMan.newPlayer(player);
		}
	}

	@Override
	public void onDisable() {
		winMan.closeInventoryies();
	}
	
	private void initTables() {
		SQL.initialiseTable(configTable, "setting", "setting VARCHAR(20) NOT NULL , data VARCHAR(50) NOT NULL");
		SQL.initialiseTable(userTable, "uuidInv", "uuidInv VARCHAR(39) NOT NULL , data TEXT NULL DEFAULT NULL");
	}

	public static void printInfo(String line){
		CONSOLE.sendMessage(ChatColor.GREEN + "[Storinator9000] : " + line);
	}

	public static void printWarning(String line){
		CONSOLE.sendMessage(ChatColor.YELLOW + "[Storinator9000] : " + line);
	}

	public static void printError(String line){
		CONSOLE.sendMessage(ChatColor.RED + "[Storinator9000] : " + line);
	}

	public static String convertToMColors(String line){
		return line.replaceAll("&", "§");
	}
}