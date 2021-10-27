package dev.mruniverse.nohackclient.listeners.containers;

import dev.mruniverse.nohackclient.NoHackClient;
import dev.mruniverse.nohackclient.listeners.common.Sanction;
import dev.mruniverse.nohackclient.storage.GuardianFiles;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ContainerController {

    private final NoHackClient plugin;

    private final ArrayList<Sanction> sanctionArrayList = new ArrayList<>();

    public ContainerController(NoHackClient plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        sanctionArrayList.clear();
        FileConfiguration storage = plugin.getStorage().getControl(GuardianFiles.SETTINGS);
        int id = 1;
        for(String path : plugin.getStorage().getContent(GuardianFiles.SETTINGS,"settings.checks.name",false)) {
            String name = storage.getString("settings.checks.name." + path + ".name","C" + id);
            String cID = storage.getString("settings.checks.name." + path + ".id","CB" + id);
            int numberLength = storage.getInt("settings.checks.name." + path + ".numberLength",3);
            List<String> container = storage.getStringList("settings.checks.name." + path + ".contains-text");
            sanctionArrayList.add(new Sanction(name,cID,numberLength,container));
            id++;
        }
        plugin.getLogs().info("The plugin has been registered " + sanctionArrayList.size() + " case(s) for nick-check.");
    }


    public ArrayList<Sanction> getCases() {
        return sanctionArrayList;
    }


}
