package dev.mruniverse.nohackclient.command.sub;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.command.MainCommand;
import dev.mruniverse.nohackclient.command.PlayerType;
import dev.mruniverse.nohackclient.storage.FileSaveMode;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public class WhitelistCommand {
    private final NoHackClient plugin;
    private final String command;

    public WhitelistCommand(NoHackClient plugin, String command) {
        this.plugin = plugin;
        this.command = command;
    }

    public void usage(CommandSender sender, String[] arguments) {
        FileConfiguration file = plugin.getStorage().getControl(GuardianFiles.PLAYERS);
        FileConfiguration msg = plugin.getStorage().getControl(GuardianFiles.MESSAGES);
        if(arguments.length == 1 || arguments.length == 0) {
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("add")) {
            if (arguments.length == 2) {
                String user = arguments[1];
                PlayerType playerType = PlayerType.fromUnknown(user);
                playerType.setPlayer(user);
                List<String> users = file.getStringList("whitelist");
                if(!users.contains(user)) {
                    users.add(user);
                    plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("whitelist",users);
                    plugin.getStorage().save(FileSaveMode.PLAYERS);
                    plugin.getStorage().reloadFile(FileSaveMode.PLAYERS);
                    String message = msg.getString("messages.whitelist.add","&a<type> &e<player> &ahas been&b added &ato the whitelist.");
                    MainCommand.sendMessage(sender,messageReplace(message,playerType));
                    return;
                }
                playerIssue(sender,playerType,true);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        if (arguments[0].equalsIgnoreCase("remove")) {
            if (arguments.length == 2) {
                String user = arguments[1];
                PlayerType playerType = PlayerType.fromUnknown(user);
                playerType.setPlayer(user);
                List<String> users = file.getStringList("whitelist");
                if(users.contains(user)) {
                    users.remove(user);
                    plugin.getStorage().getControl(GuardianFiles.PLAYERS).set("whitelist",users);
                    plugin.getStorage().save(FileSaveMode.PLAYERS);
                    plugin.getStorage().reloadFile(FileSaveMode.PLAYERS);
                    String message = msg.getString("messages.whitelist.remove","&a<type> &e<player> &ahas been&b removed &afrom the whitelist.");
                    MainCommand.sendMessage(sender,messageReplace(message,playerType));
                    return;
                }
                playerIssue(sender,playerType,false);
                return;
            }
            argumentsIssue(sender);
            return;
        }
        argumentsIssue(sender);
    }

    private void argumentsIssue(CommandSender sender) {
        MainCommand.sendMessage(sender,"&aInvalid arguments, please use &b/" + command + " admin &ato see all commands.");
    }

    public void playerIssue(CommandSender sender, PlayerType type, boolean isAdding) {
        String message;
        if(isAdding) {
            message = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.whitelist.already","&a<type> &e<player> &ais already in the whitelist!");
        } else {
            message = plugin.getStorage().getControl(GuardianFiles.MESSAGES).getString("messages.whitelist.not","&a<type> &e<player> &ais not in the whitelist!");
        }
        MainCommand.sendMessage(sender, messageReplace(message,type));
    }
    public String messageReplace(String message,PlayerType type) {
        return message.replace("<type>",type.getName())
                .replace("<player>",type.getValue());
    }
}
