package me.ccgreen.Storinator;

import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor.ShaderStyle;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
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
  StorinatorPlugin main;

  private static final ItemStack[] LOCKED_ICONS = new ItemStack[9];
  private static final ItemStack[] UNLOCKED_ICONS = new ItemStack[9];
  private static final ItemStack[] OPENED_ICONS = new ItemStack[9];

  private String window_title, tab_name;

  config(StorinatorPlugin Main) {
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
            ItemStack locked = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = locked.getItemMeta();
            meta.setCustomModelData(23);
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
    for (int i = 0; i <= 8; i++) {
      LOCKED_ICONS[i] = configYml.getItemStack("lockIcon_" + i);
      OPENED_ICONS[i] = buildOpenStack(i);
      UNLOCKED_ICONS[i] = buildUnlockedStack(i);
    }
  }

  public ItemStack getIcon(LockStata state,  int icon) {
    return switch (state) {
      case OPENED -> OPENED_ICONS[icon];
      case UNLOCKED -> UNLOCKED_ICONS[icon];
      case LOCKED -> LOCKED_ICONS[icon];
    };
  }

  public enum LockStata {
    LOCKED,
    UNLOCKED,
    OPENED,
  }

  public ItemStack buildOpenStack(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    ItemStackExtensionsKt.setCustomModelData(stack, 25);
    ItemStackExtensionsKt.setDisplayName(stack,
        FaceColor.YELLOW.shaded(ShaderStyle.WAVE) + "Page (" + (index + 1) + ")");
    return stack;
  }

  public ItemStack buildUnlockedStack(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    ItemStackExtensionsKt.setCustomModelData(stack, 24);
    ItemStackExtensionsKt.setDisplayName(stack,
        FaceColor.LIGHT_GRAY + "Page (" + (index + 1) + ")");
    return stack;
  }

  public String windowTitle() {
    return window_title;
  }

  public String tabName() {
    return tab_name;
  }
}