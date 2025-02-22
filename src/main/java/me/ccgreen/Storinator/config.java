package me.ccgreen.Storinator;

import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor;
import com.tealcube.minecraft.bukkit.facecore.utilities.FaceColor.ShaderStyle;
import com.tealcube.minecraft.bukkit.facecore.utilities.PaletteUtil;
import io.pixeloutlaw.minecraft.spigot.hilt.ItemStackExtensionsKt;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

  private static final ItemStack[] LOCKED_ICONS_HOME = new ItemStack[9];
  private static final ItemStack[] UNLOCKED_ICONS_HOME = new ItemStack[9];
  private static final ItemStack[] OPENED_ICONS_HOME = new ItemStack[9];

  @Getter @Setter
  private String window_title, tab_name, windowTitleHome, tabNameHome;

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

          configYml.set("window_title_home", ChatColor.GOLD + "VAULT");
          configYml.set("tab_name_home", ChatColor.RED + "Page %number%!");

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
            configYml.set("lockIconHome_" + (i + 1), locked);
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
    windowTitleHome = configYml.getString("window_title_home");
    tab_name = configYml.getString("tab_name");
    tabNameHome = configYml.getString("tab_name_home");
    for (int i = 0; i <= 8; i++) {
      var is1 = configYml.getItemStack("lockIcon_" + i);
      if (is1 != null) {
        is1.editMeta(m -> {
          m.setMaxStackSize(25);
          m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page_locked"));
        });
      }
      LOCKED_ICONS[i] = is1;
      var is2 = configYml.getItemStack("lockIconHome_" + i);
      if (is2 != null) {
        is2.editMeta(m -> {
          m.setMaxStackSize(25);
          m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page_home_locked"));
        });
      }
      LOCKED_ICONS_HOME[i] = is2;

      OPENED_ICONS[i] = buildOpenStack(i);
      OPENED_ICONS_HOME[i] = buildOpenStackHome(i);

      UNLOCKED_ICONS[i] = buildUnlockedStack(i);
      UNLOCKED_ICONS_HOME[i] = buildUnlockedStackHome(i);
    }

    rebuildTitles();
  }

  public ItemStack getIcon(LockStata state, int icon, boolean home) {
    return switch (state) {
      case OPENED -> home ? OPENED_ICONS_HOME[icon] : OPENED_ICONS[icon];
      case UNLOCKED -> home ? UNLOCKED_ICONS_HOME[icon] : UNLOCKED_ICONS[icon];
      case LOCKED -> home ? LOCKED_ICONS_HOME[icon] : LOCKED_ICONS[icon];
    };
  }

  public enum LockStata {
    LOCKED,
    UNLOCKED,
    OPENED,
  }

  public void rebuildTitles() {
    configFile = new File(main.getDataFolder(), "config.yml");
    configYml = YamlConfiguration.loadConfiguration(configFile);

    StorinatorPlugin.PERSONAL_INVY_NAME = PaletteUtil.culturallyEnrich(configYml.getString("PERSONAL_INVY_NAME",
        FaceColor.TRUE_WHITE + "\uF808拽"));
    StorinatorPlugin.GUILD_INVY_NAME = PaletteUtil.culturallyEnrich(configYml.getString("GUILD_INVY_NAME",
        FaceColor.TRUE_WHITE + "\uF808抭"));
    StorinatorPlugin.HOME_INVY_NAME = PaletteUtil.culturallyEnrich(configYml.getString("HOME_INVY_NAME",
        FaceColor.TRUE_WHITE + "\uF808儺"));
  }

  public ItemStack buildOpenStack(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    stack.editMeta(m -> {
      m.setDisplayName(FaceColor.YELLOW.shaded(ShaderStyle.WAVE) + "Page (" + (index + 1) + ")");
      m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page_selected"));
      m.setMaxStackSize(25);
    });
    return stack;
  }

  public ItemStack buildUnlockedStack(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    stack.editMeta(m -> {
      m.setDisplayName(FaceColor.LIGHT_GRAY + "Page (" + (index + 1) + ")");
      m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page"));
      m.setMaxStackSize(25);
    });
    return stack;
  }

  public ItemStack buildOpenStackHome(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    stack.editMeta(m -> {
      m.setDisplayName(FaceColor.YELLOW.shaded(ShaderStyle.WAVE) + "Page (" + (index + 1) + ")");
      m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page_home_selected"));
      m.setMaxStackSize(25);
    });
    return stack;
  }

  public ItemStack buildUnlockedStackHome(int index) {
    ItemStack stack = new ItemStack(Material.PAPER, 1);
    stack.editMeta(m -> {
      m.setDisplayName(FaceColor.LIGHT_GRAY + "Page (" + (index + 1) + ")");
      m.setItemModel(NamespacedKey.fromString("faceland:icons/vault_page_home"));
      m.setMaxStackSize(25);
    });
    return stack;
  }
}