package me.ccgreen.Storinator.windows;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.ccgreen.Storinator.StorinatorMain;
import me.ccgreen.Storinator.players.PlayerData;

public class VaultWindow {

	private Inventory inv;
	private Player player;
	private PlayerData data;
	int page;


	public VaultWindow(Player play, int page) {
		player = play;
		this.page = page;
		data = StorinatorMain.playMan.getData(play);

		createWindow();

		player.openInventory(inv);
	}

	private void createWindow() {
		inv = data.getPage(page);
		ItemStack barrier = new ItemStack(Material.IRON_FENCE, 1);
		for(int i = 0; i < 9; i++) {
			
			ItemStack button;
			if(i == page) {
				button = new ItemStack(Material.STAINED_GLASS, 1);
			} else {
				if(i == 0 || player.hasPermission("Storinator.vault." + i)) {
					button = new ItemStack(Material.STAINED_GLASS_PANE, 1);
				} else {
					button = new ItemStack(Material.THIN_GLASS);
					ItemMeta meta = button.getItemMeta();
					meta.setDisplayName(StorinatorMain.Config.getLore(i));
					button.setItemMeta(meta);
				}
			}
			inv.setItem(9 + i, barrier);
			inv.setItem(i, button);
		}
	}

	public void HandleClickEvent(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(slot < 0 || slot >= inv.getSize()) {
			return;
		}
		if(event.getClickedInventory().getType() == InventoryType.CHEST) {
			if(slot < 18) {
				if(slot < 9) {
					if(event.isShiftClick()) {
						//TODO move tab
					} else if(event.isRightClick()) {
						//TODO change tab color
					} else {
						Material itemType = inv.getItem(slot).getType();
						if(itemType == Material.STAINED_GLASS_PANE) {
							event.setCancelled(true);
							StorinatorMain.winMan.changeVaultWindow(player, slot);
							return;
						} else if(itemType == Material.STAINED_GLASS) {
							//cur page event?
						} else {
							player.sendMessage("Button locked!");
						}
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
}