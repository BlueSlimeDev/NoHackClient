package dev.mruniverse.nohackclient.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("unused")
public enum PlayerType {
    PLAYER,
    UNKNOWN,
    ID;

    private String value = "";

    public PlayerType setPlayer(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        switch (this) {
            case ID:
                return "UUID";
            case UNKNOWN:
                return "Unknown";
            default:
            case PLAYER:
                return "Player";

        }
    }

    public String getUnknownType(String paramString) {
        if(paramString.contains("-")) {
            return "UUID";
        }
        return "Player";
    }

    public static PlayerType fromUnknown(String paramString) {
        if(paramString.contains("-")) {
            return PlayerType.ID;
        }
        return PlayerType.PLAYER;
    }

    public static Player getPlayerFromData(String nameOrUUID,PlayerType type) {
        switch (type) {
            case UNKNOWN:
                if(nameOrUUID.contains("-")) {
                    UUID uuid = UUID.fromString(nameOrUUID);
                    return Bukkit.getPlayer(uuid);
                }
                return Bukkit.getPlayer(nameOrUUID);
            case ID:
                UUID uuid = UUID.fromString(nameOrUUID);
                return Bukkit.getPlayer(uuid);
            default:
            case PLAYER:
                return Bukkit.getPlayer(nameOrUUID);
        }
    }
}
