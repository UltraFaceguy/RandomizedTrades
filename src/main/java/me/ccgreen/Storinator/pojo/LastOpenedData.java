package me.ccgreen.Storinator.pojo;

import java.util.UUID;
import lombok.Data;

@Data
public class LastOpenedData {
  private final UUID uuid;
  private final String vaultType;

  public LastOpenedData(UUID uuid, String type) {
    this.uuid = uuid;
    this.vaultType = type;
  }
}
