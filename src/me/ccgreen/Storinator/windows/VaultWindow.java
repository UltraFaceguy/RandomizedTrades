package me.ccgreen.Storinator.windows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.ccgreen.Storinator.StorinatorMain;
import me.ccgreen.Storinator.players.PlayerData;

public class VaultWindow {

	private Inventory inv;
	private Player player;
	private PlayerData data;
	private ItemStack[] buttons = new ItemStack[10];
	int page;

	public VaultWindow(Player play) {
		player = play;
		data = StorinatorMain.playMan.getData(play);
		page = data.lastOpenPage();

		inv = data.getPage(page);
		createButtons();
		for(int i = 0; i < 9; i++) {
			if(i == page) {
				inv.setItem(i, activeButton());
			} else {
				inv.setItem(i, buttons[i]);
			}
			inv.setItem(i + 9, buttons[9]);
		}

		player.openInventory(inv);
	}

	public VaultWindow(Player play, int page) {
		player = play;
		this.page = page;
		data = StorinatorMain.playMan.getData(play);

		inv = data.getPage(page);
		createButtons();
		for(int i = 0; i < 9; i++) {
			if(i == page) {
				inv.setItem(i, activeButton());
			} else {
				inv.setItem(i, buttons[i]);
			}
			inv.setItem(i + 9, buttons[9]);
		}

		player.openInventory(inv);
	}

	private void createButtons() {
		ItemStack barrier = new ItemStack(Material.IRON_FENCE, 1);
		ItemMeta barMeta = barrier.getItemMeta();
		barMeta.setDisplayName("");
		barrier.setItemMeta(barMeta);

		buttons[9] = barrier;

		String configName = StorinatorMain.Config.tabName();
		for(int i = 0; i < 9; i++) {
			String name = configName.replaceAll("%number%", "" + (i + 1));
			ItemStack button;

			if(i == 0 || player.hasPermission("Storinator.vault." + i)) {
				button = new ItemStack(Material.STAINED_GLASS_PANE, 1);
				ItemMeta meta = button.getItemMeta();
				meta.setDisplayName(name);
				button.setItemMeta(meta);
			} else {
				button = StorinatorMain.Config.getIcon(i);
			}

			buttons[i] = button;
		}
	}

	private ItemStack activeButton() {
		String name = StorinatorMain.Config.tabName().replaceAll("%number%", "" + (page + 1));
		ItemStack button = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4);
		ItemMeta meta = button.getItemMeta();
		meta.addEnchant(Enchantment.MENDING, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName(name);
		button.setItemMeta(meta);

		return button;
	}

	private void reloadInv(int page) {
		this.page = page;

		inv.clear();
		for(int i = 0; i < 9; i++) {
			if(i == page) {
				inv.setItem(i, activeButton());
			} else {
				inv.setItem(i, buttons[i]);
			}
			inv.setItem(i + 9, buttons[9]);
		}
		ItemStack[] items = data.getPage(page).getContents();
		for(int i = 18; i < items.length; i++) {
			inv.setItem(i, items[i]);
		}
		player.updateInventory();
	}

	public void HandleClickEvent(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(slot < 0 || slot >= inv.getSize()) {
			return;
		}
		if(event.getClickedInventory().getType() == InventoryType.CHEST) {
			if(slot < 18) {
				if(slot < 9) {
					if(slot == page) {
						//cur page event?
					} else if(player.hasPermission("Storinator.vault." + slot)) {
						event.setCancelled(true);
						Inventory newInv = Bukkit.createInventory(null, 54, inv.getTitle());
						newInv.setContents(inv.getContents());
						savePage(newInv);
						//StorinatorMain.winMan.changeVaultWindow(player);
						page = slot;
						reloadInv(slot);

						return;
					} else {
						player.sendMessage("Button locked!");
					}
					event.setCancelled(true);
				} else {
					//iron bars
					event.setCancelled(true);
				}
			} else {
				//main vault inv, do nothing

			}
		} else if(event.getClickedInventory().getType() == InventoryType.PLAYER){
			//player inv, allow transactions, maybe do somthing later
		} else {
			event.setCancelled(true);
		}
	}



	public void savePage() {
		data.updatePage(inv, page);
	}
	
	public void savePage(Inventory inv) {
		data.updatePage(inv, page);
	}
}