package me.ccgreen.Storinator.pojo;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LastOpenedData {
  private final UUID uuid;
  private final String vaultType;

  public LastOpenedData(UUID uuid, String type) {
    this.uuid = uuid;
    this.vaultType = type;
  }
}
