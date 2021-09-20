package me.ccgreen.Storinator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class config {

  File configFile;
  YamlConfiguration configYml;
  StorinatorMain main;

  private static final ItemStack[] unlockIcon = new ItemStack[8];
  private String window_title, tab_name;

  config(StorinatorMain Main) {
    main = Main;
    loadConfig();
  }

  public void loadConfig() {

    configFile = new File(main.getDataFolder(), "config.yml");

    if (!configFile.exists()) {
      new File(main.getDataFolder() + "").mkdir();
      try {
        if (configFile.createNewFile()) {
          String[] names = {"Buy me!", "Find me!", "Edit me in config!", "Rename me!",
              "Woot! Settings!", "Im an unconfigured name!", "Powered by memes!", "cc wuz hear!"};

          configYml = YamlConfiguration.loadConfiguration(configFile);

          configYml.set("window_title", ChatColor.GOLD + "VAULT");
          configYml.set("tab_name", ChatColor.RED + "Page %number%!");

          for (int i = 0; i < 8; i++) {
            ItemStack locked = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta meta = locked.getItemMeta();

            meta.setDisplayName(names[i]);
            List<String> lore = new Vector<>();
            lore.add("line 1: ");
            lore.add("line 2: ");
            meta.setLore(lore);
            locked.setItemMeta(meta);

            configYml.set("lockIcon_" + (i + 1), locked);
          }

          try {
            configYml.save(configFile);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
      }
    }

    configYml = YamlConfiguration.loadConfiguration(configFile);

    window_title = configYml.getString("window_title");
    tab_name = configYml.getString("tab_name");
    for (int i = 0; i < 8; i++) {
      unlockIcon[i] = configYml.getItemStack("lockIcon_" + (i + 1));
    }
  }

  public ItemStack getIcon(int icon) {
    return unlockIcon[icon - 1];
  }

  public String windowTitle() {
    return window_title;
  }

  public String tabName() {
    return tab_name;
  }
}