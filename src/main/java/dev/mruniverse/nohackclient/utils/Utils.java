package dev.mruniverse.nohackclient.utils;

import org.bukkit.ChatColor;

import java.util.List;

public class Utils {
    @SuppressWarnings("unused")
    //<code>-<codeID>"
    //        - " "
    //        - "&7Nick: &f<nick>#<checkID>"
    public static String ListToString(List<String> list,String code,String codeID,String nick,String checkID,boolean applyColorCodes) {
        StringBuilder builder = new StringBuilder();
        int line = 0;
        int maxLine = list.size();
        for (String lines : list) {
            lines = lines.replace("<code>",code).replace("<codeID>",codeID).replace("<nick>",nick).replace("<checkID>",checkID);
            line++;
            if(applyColorCodes) {
                if (line != maxLine) {
                    builder.append(color(lines)).append("\n");
                } else {
                    builder.append(color(lines));
                }
            } else {
                if (line != maxLine) {
                    builder.append(lines).append("\n");
                } else {
                    builder.append(lines);
                }
            }
        }
        return builder.toString();
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    @SuppressWarnings("unused")
    public static String ListToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        int line = 0;
        int maxLine = list.size();
        for (String lines : list) {
            line++;
            if(line != maxLine) {
                builder.append(lines).append("\n");
            } else {
                builder.append(lines);
            }
        }
        return builder.toString();
    }
}
