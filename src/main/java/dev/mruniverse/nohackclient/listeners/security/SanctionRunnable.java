package dev.mruniverse.nohackclient.listeners.security;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.listeners.Alerts;
import dev.mruniverse.nohackclient.listeners.common.Sanction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SanctionRunnable extends BukkitRunnable {

    private final NoHackClient plugin;
    private final Player player;
    private final Alerts alert;
    private final Sanction sanction;

    public SanctionRunnable(NoHackClient plugin, Player player, Alerts alert, Sanction sanction) {
        this.plugin = plugin;
        this.player = player;
        this.alert = alert;
        this.sanction = sanction;
    }

    @Override
    public void run() {
        String alert = ChatColor.translateAlternateColorCodes('&',plugin.getController().getKick(this.alert,player.getName(), sanction.getName(), sanction.getId()));
        player.kickPlayer(alert);
        plugin.getLogs().debug(alert);
    }
}
