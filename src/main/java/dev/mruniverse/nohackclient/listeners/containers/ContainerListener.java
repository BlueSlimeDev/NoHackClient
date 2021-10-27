package dev.mruniverse.nohackclient.listeners.containers;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.listeners.Alerts;
import dev.mruniverse.nohackclient.listeners.common.Sanction;
import dev.mruniverse.nohackclient.storage.FileSaveMode;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.List;

public class ContainerListener implements Listener {

    private final NoHackClient plugin;

    private final ContainerController containerController;

    private final ContainerDetector containerDetector;

    public ContainerListener(NoHackClient plugin) {
        this.plugin = plugin;
        this.containerController = new ContainerController(plugin);
        this.containerDetector = new ContainerDetector();
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent event){
        String name = event.getPlayer().getName();
        String[] lastCheck = name.replaceAll("[^0-9]", "").split("");
        FileConfiguration configuration = plugin.getStorage().getControl(GuardianFiles.PLAYERS);
        if(!configuration.getStringList("whitelist").contains(name) && !configuration.getStringList("users").contains(name)) {
            for (Sanction cases : containerController.getCases()) {
                if (containerDetector.execute(cases, lastCheck, name)) {
                    plugin.getLogs().info("&fUsuario &c" + name + "&f ha sido bloqueado.");
                    plugin.getController().sendAlert(Alerts.NAME,name);
                    event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getController().getKick(Alerts.NAME,name,cases.getName(),cases.getId())));
                    event.setResult(Result.KICK_BANNED);
                    if(!plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users").contains(name)) {
                        List<String> users = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users");
                        users.add(name);
                        plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("users",users);
                        plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("casesString." + name,"NAME," + cases.getName() + "," + cases.getId());
                        plugin.getStorage().save(FileSaveMode.PLAYERS);
                        plugin.getStorage().reloadFile(FileSaveMode.PLAYERS);
                    }
                    return;
                }
            }
        } else {
            if(plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users").contains(name)) {
                if(plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("casesString." + name, "UNKNOWN,MMN1,C01").contains("NAME,")) {
                    plugin.getController().sendAlert(Alerts.NAME, name);
                    try {
                        String[] caseString = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("casesString." + name, "NAME,MMN1,C01").split(",");
                        plugin.getLogs().info("&fUsuario &c" + name + "&f ha sido bloqueado.");
                        event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getController().getKick(Alerts.NAME, name, caseString[1], caseString[2])));
                        event.setResult(Result.KICK_BANNED);
                    } catch (Throwable ignored) {
                        plugin.getLogs().info("&fUsuario &c" + name + "&f ha sido bloqueado.");
                        event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getController().getKick(Alerts.NAME, name, "MMN1", "C01")));
                        event.setResult(Result.KICK_BANNED);
                    }
                }
            }
        }
    }

    public void update() {
        containerController.load();
    }
}

