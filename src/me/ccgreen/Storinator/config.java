package me.ccgreen.Storinator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class config {

	StorinatorMain main;

	private static String[] unlockLore = new String[8];

	config(StorinatorMain Main) {
		main = Main;
		loadConfig();
	}

	public void loadConfig() {
		ResultSet data = StorinatorMain.SQL.get(StorinatorMain.configTable);

		try {
			if (!data.isBeforeFirst() ) {    
				saveDefaultConfig();
			} else {
				while(data.next()) {
					String setting = data.getString("setting");
					//icon settings
					if(setting.startsWith("iconName_")) {
						setting = setting.replace("iconName_", "");
					}
					unlockLore[Integer.parseInt(setting)] = data.getString("data");
				}
			}
		} catch (SQLException e) {
			saveDefaultConfig();
			StorinatorMain.printError("Config load failure");
			e.printStackTrace();
		}

	}

	private void saveDefaultConfig() {
		Vector<String> batchStatement = new Vector<String>();
		String[] defaultNames = {"Buy me!", "Find me!", "Edit me in config!", "Rename me!", "Woot! Settings!", "Im an unconfigured name!", "Powered by memes!", "cc wuz hear!"};

		for(int i = 0; i < 8; i++) {
			batchStatement.add("replace into storinator_config (setting, data) VALUES ('iconName_" + i + "', '" + defaultNames[i] + "')");
			unlockLore[i] = defaultNames[i];
		}

		StorinatorMain.SQL.sendBatch(batchStatement);
	}

	public String getLore(int icon) {
		return unlockLore[icon - 1];
	}
}