package me.ccgreen.Storinator.managers;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.intellij.lang.annotations.Language;

public class SQLManager {

  @Language("MySQL")
  private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS storinator_data_v2 (uuidInv varchar(40) NOT NULL PRIMARY KEY, data mediumtext)";
  @Language("MySQL")
  private static final String REPLACE = "REPLACE INTO storinator_data_v2 SET uuidInv = ?, data = ?";
  @Language("MySQL")
  private static final String GET_ROW = "SELECT * FROM storinator_data_v2 WHERE uuidInv = ? LIMIT 1";

  public void initialize() {
    try {
      DB.executeUpdate("CREATE TABLE IF NOT EXISTS storinator_data_v2 (uuidInv varchar(40) NOT NULL PRIMARY KEY, data mediumtext)");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setDataRow(final String uuidInvId, final String data) {
    try {
      DB.executeUpdateAsync("REPLACE INTO storinator_data_v2 SET uuidInv = ?, data = ?", uuidInvId, data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getDataRow(final String uuidInvId) {
    try {
      final CompletableFuture<List<DbRow>> rs1 = DB.getResultsAsync("SELECT * FROM storinator_data_v2 WHERE uuidInv = ? LIMIT 1", uuidInvId);
      final Iterator<DbRow> iterator = rs1.get().iterator();
      if (iterator.hasNext()) {
        DbRow row = iterator.next();
        return row.getString("data");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

