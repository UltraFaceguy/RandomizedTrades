package me.ccgreen.Storinator;

import com.questworld.QuestWorldPlugin;
import com.questworld.QuestingImpl;
import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.shade.acf.PaperCommandManager;
import com.tealcube.minecraft.bukkit.shade.apache.commons.lang3.StringUtils;
import com.tealcube.minecraft.bukkit.shade.google.gson.Gson;
import com.tealcube.minecraft.bukkit.shade.google.gson.JsonObject;
import com.tealcube.minecraft.bukkit.shade.google.gson.stream.JsonReader;
import com.tealcube.minecraft.bukkit.shade.hibernate.SessionFactory;
import io.pixeloutlaw.minecraft.spigot.config.MasterConfiguration;
import io.pixeloutlaw.minecraft.spigot.config.VersionedConfiguration.VersionUpdateType;
import io.pixeloutlaw.minecraft.spigot.config.VersionedSmartYamlConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Properties;
import lombok.Getter;
import me.ccgreen.Storinator.commands.StorinatorCommand;
import me.ccgreen.Storinator.listeners.EntryExitListener;
import me.ccgreen.Storinator.listeners.InventoryListener;
import me.ccgreen.Storinator.managers.VaultManager;
import me.ccgreen.Storinator.pojo.VaultPage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;


public class StorinatorPlugin extends JavaPlugin {

	@Getter
	public VaultManager vaultManager;

	public static config Config;
	private static ConsoleCommandSender CONSOLE;
	@Getter
	private MasterConfiguration settings;
	@Getter
	private SessionFactory sessionFactory;
	@Getter
	private QuestingImpl questApi = null;

	@Getter
	private static StorinatorPlugin instance;

	public static String INVY_NAME;

	public void onEnable() {
		instance = this;
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

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("FaceQuest")) {
			questApi = QuestWorldPlugin.getAPI();
		}

		settings = MasterConfiguration.loadFromFiles(configYAML);

		File file = new File(this.getDataFolder(), "connection.json");

		Gson gson = new Gson();
		JsonObject json;
		try {
			json = gson.fromJson(new JsonReader(new FileReader(file)), JsonObject.class);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", json.get("hibernate.dialect").getAsString());
		properties.setProperty("connection.driver_class", json.get("connection.driver_class").getAsString());
		properties.setProperty("connection.pool_size", Integer.toString(20));
		properties.setProperty("hibernate.connection.url", "jdbc:mysql://" + hostAndPort + "/" + database +"?useUnicode=true");
		properties.setProperty("hibernate.connection.username", username);
		properties.setProperty("hibernate.connection.password", password);
		properties.setProperty("hibernate.hbm2ddl.auto", json.get("hibernate.hbm2ddl.auto").getAsString());
		properties.setProperty("current_session_context_class", json.get("current_session_context_class").getAsString());

		try {
			sessionFactory = new com.tealcube.minecraft.bukkit.shade.hibernate.cfg.Configuration()
					.addProperties(properties)
					.addAnnotatedClass(VaultPage.class)
					.buildSessionFactory();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		StorinatorPlugin.CONSOLE = Bukkit.getServer().getConsoleSender();
		vaultManager = new VaultManager(this);
		StorinatorPlugin.Config = new config(this);

		INVY_NAME = FaceColor.TRUE_WHITE + "\uF808拽\uF80C\uF80A\uF808\uF804" + FaceColor.ORANGE + "Bank Vault";

		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		getServer().getPluginManager().registerEvents(new EntryExitListener(this), this);

		final PaperCommandManager commandManager = new PaperCommandManager(this);
		commandManager.registerCommand(new StorinatorCommand(this));

		for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
			vaultManager.createVault(player.getUniqueId(), VaultManager.PERSONAL_VAULT, null);
		}
	}

	public void onDisable() {
		Bukkit.getServer().getLogger().info("[Storinator] Disabling...");
		vaultManager.saveAll();
		HandlerList.unregisterAll(this);
		Bukkit.getScheduler().cancelTasks(this);
		Bukkit.getServer().getLogger().info("[Storinator] Disabled!");
	}
}
