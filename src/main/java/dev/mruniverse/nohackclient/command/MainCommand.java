package dev.mruniverse.nohackclient.command;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.command.sub.WhitelistCommand;
import dev.mruniverse.nohackclient.storage.FileSaveMode;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private final NoHackClient plugin;
    private final String cmdPrefix;
    private final WhitelistCommand whitelist;

    public MainCommand(NoHackClient plugin, String command) {
        this.plugin = plugin;
        this.cmdPrefix = "&6&o/" + command;
        whitelist = new WhitelistCommand(plugin,command);
    }

    public static void sendMessage(Player player,String message) {
        if(message == null) message = "Unknown Message";
        message = ChatColor.translateAlternateColorCodes('&',message);
        player.sendMessage(message);
    }
    public static void sendMessage(CommandSender sender,String message) {
        if(message == null) message = "Unknown Message";
        message = ChatColor.translateAlternateColorCodes('&',message);
        sender.sendMessage(message);
    }

    private boolean hasPermission(CommandSender sender, String permission, boolean sendMessage) {
        boolean check = true;
        if(sender instanceof Player) {
            Player player = (Player)sender;
            check = player.hasPermission(permission);
            if(sendMessage) {
                String permissionMsg = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.command.no-perms","&cYou need permission &7%permission% &cfor this action.");
                if (!check)
                    sendMessage(player, permissionMsg.replace("%permission%", permission));
            }
        }
        return check;
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (args.length == 0 || args[0].equalsIgnoreCase("help") && (hasPermission(sender,"nohackclient.usage",false) || hasPermission(sender,"nohackclient.*",false) || hasPermission(sender,"nohackclient.*",false))) {
                sender.sendMessage(" ");
                sendMessage(sender, "&e&lNoHackClient &f- &oEasy Anti Hack Client");
                sendMessage(sender,"&7&oCreated by MrUniverse44");
                sendMessage(sender,cmdPrefix + " admin &e- &fAdmin commands");
                sendMessage(sender, "&7Currently on beta phase");
                return true;
            }
            if (args[0].equalsIgnoreCase("admin")) {
                if(args.length == 1 || args[1].equalsIgnoreCase("1")) {
                    if (hasPermission(sender, "nohackclient.admin", true)) {
                        sender.sendMessage(" ");
                        sendMessage(sender, "&e&lNoHackClient &f- &oEasy Anti Hack Client");
                        sendMessage(sender, cmdPrefix + " admin whitelist add [player or uuid] &e- &fAdd player to whitelist.");
                        sendMessage(sender, cmdPrefix + " admin whitelist remove [player or uuid] &e- &fRemove player from whitelist.");
                        sendMessage(sender,cmdPrefix + " admin info [player] &e- &fInfo of a player in black-list");
                        sendMessage(sender,cmdPrefix + " admin debug [player] &e- &fCheck manually a player");
                        sendMessage(sender,cmdPrefix + " admin debugAll [player] &e- &fCheck manually a player");
                        sendMessage(sender,cmdPrefix + " admin users");
                        sendMessage(sender, cmdPrefix + " admin reload &e- &fReload the plugin.");
                        sendMessage(sender, "&7Currently on beta phase");
                    }
                    return true;
                }

                if (args[1].equalsIgnoreCase("debug")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        if (args.length == 3 && sender instanceof Player) {
                            String user = args[2];
                            Player player = plugin.getServer().getPlayer(user);
                            if (player != null || plugin.getStorage().getControl(GuardianFiles.PLAYERS).contains("ips." + user)) {
                                String ip;
                                if (player == null) {
                                    ip = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("ips." + user, "none");
                                } else {
                                    InetSocketAddress socket = player.getAddress();
                                    if (socket != null) {
                                        InetAddress address = socket.getAddress();
                                        ip = address.getHostAddress();
                                    } else {
                                        ip = "none";
                                    }
                                }
                                if (ip.equalsIgnoreCase("none")) {
                                    MainCommand.sendMessage(sender, "&cCan't find the player");
                                    return true;
                                }
                                plugin.getSecurity().debug(false,ip, user, (Player) sender);
                                return true;
                            }
                            MainCommand.sendMessage(sender, "&cCan't find the player");
                            return true;
                        }
                        MainCommand.sendMessage(sender, "&cYou have enought arguments or...");
                        MainCommand.sendMessage(sender, "&cThis command is being executed by console, this command is only for players.");
                        return true;
                    }
                }

                if (args[1].equalsIgnoreCase("debugAll")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        if (args.length == 3 && sender instanceof Player) {
                            String user = args[2];
                            Player player = plugin.getServer().getPlayer(user);
                            if (player != null || plugin.getStorage().getControl(GuardianFiles.PLAYERS).contains("ips." + user)) {
                                String ip;
                                if (player == null) {
                                    ip = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("ips." + user, "none");
                                } else {
                                    InetSocketAddress socket = player.getAddress();
                                    if (socket != null) {
                                        InetAddress address = socket.getAddress();
                                        ip = address.getHostAddress();
                                    } else {
                                        ip = "none";
                                    }
                                }
                                if (ip.equalsIgnoreCase("none")) {
                                    MainCommand.sendMessage(sender, "&cCan't find the player");
                                    return true;
                                }
                                plugin.getSecurity().debug(true,ip, user, (Player) sender);
                                return true;
                            }
                            MainCommand.sendMessage(sender, "&cCan't find the player");
                            return true;
                        }
                        MainCommand.sendMessage(sender, "&cYou have enought arguments or...");
                        MainCommand.sendMessage(sender, "&cThis command is being executed by console, this command is only for players.");
                        return true;
                    }
                }

                if(args[1].equalsIgnoreCase("users")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        List<String> users = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users");
                        List<String> whitelist = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("whitelist");
                        if(users.size() >= 1) {
                            sendMessage(sender, "&aBlocked Users: &6(&e" + users.size() + "&6)");
                            for(String user : users) {
                                sendMessage(sender, "&6 - &f" + user);
                            }
                        } else {
                            sendMessage(sender,"&cNo user has been blocked yet.");
                        }
                        if(whitelist.size() >= 1) {
                            sendMessage(sender, "&aWhitelist Users: &6(&e" + whitelist.size() + "&6)");
                            for(String user : whitelist) {
                                sendMessage(sender, "&6 - &f" + user);
                            }
                        } else {
                            sendMessage(sender,"&c0 users in whitelist");
                        }
                    }
                    return true;
                }

                if(args[1].equalsIgnoreCase("reload")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        long timeMS = System.currentTimeMillis();
                        try {
                            plugin.getStorage().reloadFile(FileSaveMode.ALL);
                            plugin.getSecurity().update();
                            plugin.getListener().update();
                            sendMessage(sender, "&3» &aReload completed in " + (System.currentTimeMillis() - timeMS) + "s!");

                        }catch (Throwable throwable) {
                            plugin.getLogs().error("Something bad happened, maybe the plugin is broken, please check if you have all without issues");
                            plugin.getLogs().error("If you are sure than this isn't your error, please contact the developer.");
                            plugin.getLogs().error(throwable);
                        }
                    }
                    return true;
                }
                if (args[1].equalsIgnoreCase("info")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        if (args.length == 3) {
                            String user = args[2];
                            if (plugin.getStorage().getControl(GuardianFiles.PLAYERS).getStringList("users").contains(user) || plugin.getStorage().getControl(GuardianFiles.PLAYERS).contains("casesString." + user)) {
                                String[] caseString = plugin.getStorage().getControl(GuardianFiles.PLAYERS).getString("casesString." + user, "VPN,MMN1,C01").split(",");
                                sendMessage(sender, "&a");
                                sendMessage(sender, "&aInformation of &8" + user + "&7:");
                                sendMessage(sender, "&7Reason: &f" + caseString[0]);
                                sendMessage(sender, "&7Detect: &e" + caseString[1]);
                                sendMessage(sender, "&7Code: &6" + caseString[2]);
                                sendMessage(sender, "&a");
                                return true;
                            }
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eeThis user is not on blacklist."));
                            return true;
                        }
                        sendMessage(sender, "&cInvalid Arguments.");
                        return true;
                    }
                }
                if(args[1].equalsIgnoreCase("whitelist")) {
                    if(hasPermission(sender,"nohackclient.admin",true) || hasPermission(sender,"nohackclient.*",true)) {
                        whitelist.usage(sender,getArguments(args));

                    }
                    return true;
                }
            }
            return true;
        } catch (Throwable throwable) {
            plugin.getLogs().error(throwable);
        }
        return true;
    }
    private String[] getArguments(String[] args){
        String[] arguments = new String[args.length - 2];
        int argID = 0;
        int aID = 0;
        for(String arg : args) {
            if(aID != 0 && aID != 1) {
                arguments[argID] = arg;
                argID++;
            }
            aID++;
        }
        return arguments;
    }
}
