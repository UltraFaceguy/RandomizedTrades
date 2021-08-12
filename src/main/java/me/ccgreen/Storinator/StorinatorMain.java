package me.ccgreen.Storinator;

import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import me.ccgreen.Storinator.commands.StorinatorCommand;
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


public class StorinatorMain extends JavaPlugin implements Listener {

	public static WindowManager winMan;
	public static PlayerManager playMan;
	public static config Config;
	
	public static SQLlibMain SQL;
	private static ConsoleCommandSender CONSOLE;
	
	public static String userTable = "storinator_data_v2";
	
	@Override
	public void onEnable() {
		
		SQL = (SQLlibMain) Bukkit.getServer().getPluginManager().getPlugin("SQLlib");
		CONSOLE = Bukkit.getServer().getConsoleSender();
		
		initTables();
		
		winMan = new WindowManager(this);
		playMan = new PlayerManager(this);
		Config = new config(this);

		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new EntryExitListener(), this);

		PaperCommandManager commandManager = new PaperCommandManager(this);
		commandManager.registerCommand(new StorinatorCommand(this));
		
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
		playMan.saveAll();
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