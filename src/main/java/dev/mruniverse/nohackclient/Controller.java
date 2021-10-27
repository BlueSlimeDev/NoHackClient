package dev.mruniverse.nohackclient;

import dev.mruniverse.nohackclient.listeners.Alerts;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import dev.mruniverse.nohackclient.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Controller {
    private final NoHackClient plugin;

    public Controller(NoHackClient plugin) {
        this.plugin = plugin;
    }

    public void sendAlert(Alerts alert, String nick) {
        String alertMessage;
        alertMessage = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString(alert.getPath() + "alert", "&6<player>&f has been kicked for &6Strange Nick&f.");
        alertMessage = ChatColor.translateAlternateColorCodes('&', alertMessage.replace("<player>", nick));
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.hasPermission("nohackclient.alerts")) {
                player.sendMessage(alertMessage);
            }
        }
    }

    public String getKick(Alerts alert, String nick, String caseName, String caseID) {
        FileConfiguration messages = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        return Utils.ListToString(messages.getStringList(alert.getPath() + "value"), caseName, caseID, nick, "NM1", true);
    }
}
