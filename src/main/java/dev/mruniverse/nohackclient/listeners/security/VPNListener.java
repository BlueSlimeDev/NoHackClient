package dev.mruniverse.nohackclient.listeners.security;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.listeners.Alerts;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.net.InetAddress;

public class VPNListener implements Listener {

    private final NoHackClient plugin;

    private final SecurityController securityController;


    public VPNListener(NoHackClient plugin, SecurityController securityController) {
        this.plugin = plugin;
        this.securityController = securityController;
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent event) {
        String name = event.getPlayer().getName();
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.PLAYERS);
        if (!configuration.getStringList("whitelist").contains(name) && !configuration.getStringList("users").contains(name)) {
            InetAddress address = event.getAddress();

            securityController.execute(address.getHostAddress(),event.getPlayer());
        } else {
            if (plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users").contains(name)) {
                if (!plugin.getStorage().getControl(GuardianFiles.PLAYERS).contains("casesString." + name)) {
                    if(!plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("casesString." + name,"VPN,").contains("NAME,")) {
                        plugin.getController().sendAlert(Alerts.BLOCKED, name);
                        try {
                            String[] caseString = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("casesString." + name, "VPN,MMN1,C01").split(",");
                            plugin.getLogs().info("&fUser &c" + name + "&f has been blocked.");
                            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getController().getKick(Alerts.VPN, name, caseString[1], caseString[2])));
                            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                        } catch (Throwable ignored) {
                            plugin.getLogs().info("&fUser &c" + name + "&f has been blocked.");
                            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getController().getKick(Alerts.VPN, name, "MMN1", "C01")));
                            event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
                        }
                    }
                }
            }
        }
    }
}
