package dev.mruniverse.nohackclient;

import dev.mruniverse.nohackclient.command.MainCommand;
import dev.mruniverse.nohackclient.listeners.containers.ContainerListener;
import dev.mruniverse.nohackclient.listeners.security.SecurityController;
import dev.mruniverse.nohackclient.storage.FileStorage;
import dev.mruniverse.nohackclient.utils.GuardianLogger;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoHackClient extends JavaPlugin {

    private FileStorage storage;

    private SecurityController securityController;

    private ContainerListener joinListener;

    private GuardianLogger logger;

    private Controller controller;

    @Override
    public void onEnable() {
        logger = new GuardianLogger("NoHackClient","dev.mruniverse.nohackclient.");

        storage = new FileStorage(this);

        securityController = new SecurityController(this);

        joinListener = new ContainerListener(this);

        controller = new Controller(this);

        PluginManager manager = getServer().getPluginManager();

        manager.registerEvents(joinListener,this);

        loadCommand("nhackc");

        loadCommand("nohackclient");

    }

    public ContainerListener getListener(){
        return joinListener;
    }

    public void loadCommand(String command) {
        try{
            PluginCommand cmd = getCommand(command);
            if(cmd == null) return;
            cmd.setExecutor(new MainCommand(this,command));
        }catch (Throwable ignored){}
    }

    public SecurityController getSecurity() {
        return securityController;
    }

    public FileStorage getStorage() {
        return storage;
    }

    public GuardianLogger getLogs() {
        return logger;
    }

    public Controller getController() {
        return controller;
    }
}
