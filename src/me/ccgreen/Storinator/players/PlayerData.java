package me.ccgreen.Storinator.players;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import me.ccgreen.SQLlib.SQLlibMain;
import me.ccgreen.Storinator.StorinatorMain;

public class PlayerData {

	private Inventory[] vaultData = new Inventory[9];

	private static SQLlibMain SQL;
	private static String chestName = ChatColor.RED + "" + ChatColor.GRAY + "¤" + ChatColor.GOLD + "" + ChatColor.BOLD + "VAULT";
	private Player player;

	public PlayerData(StorinatorMain main, Player player) {
		PlayerData.SQL = StorinatorMain.SQL;
		this.player = player;
		for(int i = 0; i < 9; i++) {
			ResultSet invResult = SQL.get(StorinatorMain.userTable, "uuidInv = '" + player.getUniqueId() + "_" + i + "'");
			try {
				if(invResult.next()) {
					vaultData[i] = fromBase64(invResult.getString("data"));
				}
			} catch (SQLException e) {
				//page not saved before (never been opened)
			} catch (IOException e) {
				StorinatorMain.printError("decoding data error for player: " + player.getDisplayName());
				e.printStackTrace();
			}
		}
	}

	public Inventory getPage(int page) {
		Inventory inv = vaultData[page];
		if(inv == null) {
			inv = Bukkit.createInventory(null, 54, ChatColor.RED + "" + ChatColor.GRAY + "¤" + ChatColor.GOLD + "" + ChatColor.BOLD + "VAULT");
		}
		return inv;
	}
	
	public void updatePage(Inventory inv) {
		int page = 0;
		for(; page < 9; page++) {
			if(inv.getItem(page).getType() == Material.STAINED_GLASS) {
				break;
			}
		}
		if(page == 9) {
			player.sendMessage("Saving error, no page found");
			return;
		}
		
		for(int i = 0; i < 18; i++) {
			inv.setItem(i, null);
		}
		
		vaultData[page] = inv;
		String pageData = toBase64(inv);
		SQL.set(StorinatorMain.userTable, "uuidInv, data", "'" + player.getUniqueId() + "_" + page + "', '" + pageData + "'");
	}
	
    private static String toBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize() - 18);
            
            // Save every element in the list
            for (int i = 0; i < inventory.getSize() - 18; i++) {
                dataOutput.writeObject(inventory.getItem(i + 18));
            }
            
            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }        
    }
    
    private static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt() + 18, chestName);
    
            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize() - 18; i++) {
                inventory.setItem(i + 18, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}