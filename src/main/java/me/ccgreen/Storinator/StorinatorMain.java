package me.ccgreen.Storinator;

import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration.VersionUpdateType;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import lombok.Getter;
import me.ccgreen.Storinator.commands.StorinatorCommand;
import me.ccgreen.Storinator.managers.SQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.ccgreen.Storinator.listeners.EntryExitListener;
import me.ccgreen.Storinator.listeners.InventoryListener;
import me.ccgreen.Storinator.managers.PlayerManager;
import me.ccgreen.Storinator.windows.WindowManager;


public class StorinatorMain extends JavaPlugin {

	public static WindowManager winMan;
	public static PlayerManager playMan;
	public static config Config;
	private static ConsoleCommandSender CONSOLE;
	public static SQLManager sqlManager;
	public static String userTable;
	@Getter
	private MasterConfiguration settings;

	public void onEnable() {

		VersionedSmartYamlConfiguration configYAML = new VersionedSmartYamlConfiguration(
				new File(getDataFolder(), "config.yml"),
				getResource("config.yml"),
				VersionUpdateType.BACKUP_AND_NEW);
		configYAML.update();

		final String username = configYAML.getString("MySQL.user");
		final String password = configYAML.getString("MySQL.password");
		final String database = configYAML.getString("MySQL.database");
		final String hostAndPort = configYAML.getString("MySQL.host") + ":" + configYAML.getString("MySQL.port");

		if (StringUtils.isBlank(username)) {
			this.getLogger().severe("Missing database username! Plugin will fail to work!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (StringUtils.isBlank(password)) {
			this.getLogger().severe("Missing database password! Plugin will fail to work!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (StringUtils.isBlank(database)) {
			this.getLogger().severe("Missing database field! Plugin will fail to work!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		settings = MasterConfiguration.loadFromFiles(configYAML);

		DatabaseOptions options = DatabaseOptions.builder().mysql(username, password, database, hostAndPort).build();
		Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
		DB.setGlobalDatabase(db);

		sqlManager = new SQLManager();
		sqlManager.initialize();

		StorinatorMain.CONSOLE = Bukkit.getServer().getConsoleSender();
		StorinatorMain.winMan = new WindowManager();
		StorinatorMain.playMan = new PlayerManager(this);
		StorinatorMain.Config = new config(this);

		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		getServer().getPluginManager().registerEvents(new EntryExitListener(), this);

		final PaperCommandManager commandManager = new PaperCommandManager(this);
		commandManager.registerCommand(new StorinatorCommand(this));

		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			StorinatorMain.playMan.getPlayerData(player);
		}
	}

	public void onDisable() {
		StorinatorMain.winMan.closeAllVaults();
		StorinatorMain.playMan.saveAll();
	}

	public static void printInfo(final String line) {
		StorinatorMain.CONSOLE.sendMessage(ChatColor.GREEN + line);
	}

	public static void printWarning(final String line) {
		StorinatorMain.CONSOLE.sendMessage(ChatColor.YELLOW + line);
	}

	public static void printError(final String line) {
		StorinatorMain.CONSOLE.sendMessage(ChatColor.RED + line);
	}

	static {
		StorinatorMain.userTable = "storinator_data_v2";
	}
}
