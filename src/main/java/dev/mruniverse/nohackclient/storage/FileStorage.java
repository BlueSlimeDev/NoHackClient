package dev.mruniverse.nohackclient.storage;

import dev.mruniverse.nohackclient.NoHackClient;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FileStorage {
    private final NoHackClient plugin;
    private FileConfiguration settings;
    private FileConfiguration messages;
    private FileConfiguration users;
    private final File rxSettings;
    private final File rxMessages;
    private final File rxUsers;
    public FileStorage(NoHackClient plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        rxSettings = new File(dataFolder, "settings.yml");
        settings = loadConfig("settings");
        rxMessages = new File(dataFolder, "messages.yml");
        messages = loadConfig("messages");
        rxUsers = new File(dataFolder, "users.yml");
        users = loadConfig("users");
    }

    public File getFile(GuardianFiles fileToGet) {
        switch (fileToGet) {
            default:
            case SETTINGS:
                return rxSettings;
            case MESSAGES:
                return rxMessages;
            case PLAYERS:
                return rxUsers;
        }
    }

    /**
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param configName config to create/reload.
     */
    public FileConfiguration loadConfig(String configName) {
        File configFile = new File(plugin.getDataFolder(), configName + ".yml");

        if (!configFile.exists()) {
            saveConfig(configName);
        }

        FileConfiguration cnf = null;
        try {
            cnf = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            plugin.getLogs().warn(String.format("A error occurred while loading the settings file. Error: %s", e));
            e.printStackTrace();
        }

        plugin.getLogs().info(String.format("&7File &e%s.yml &7has been loaded", configName));
        return cnf;
    }
    /**
     * Creates a config File if it doesn't exists,
     * reloads if specified file exists.
     *
     * @param rigoxFile config to create/reload.
     */
    public FileConfiguration loadConfig(File rigoxFile) {
        if (!rigoxFile.exists()) {
            saveConfig(rigoxFile);
        }

        FileConfiguration cnf = null;
        try {
            cnf = YamlConfiguration.loadConfiguration(rigoxFile);
        } catch (Exception e) {
            plugin.getLogs().warn(String.format("A error occurred while loading the settings file. Error: %s", e));
            e.printStackTrace();
        }

        plugin.getLogs().info(String.format("&7File &e%s &7has been loaded", rigoxFile.getName()));
        return cnf;
    }

    /**
     * Reload plugin file(s).
     *
     * @param Mode mode of reload.
     */
    public void reloadFile(FileSaveMode Mode) {
        switch (Mode) {
            case PLAYERS:
                users = YamlConfiguration.loadConfiguration(rxUsers);
                break;
            case SETTINGS:
                settings = YamlConfiguration.loadConfiguration(rxSettings);
                break;
            case MESSAGES:
                messages = YamlConfiguration.loadConfiguration(rxMessages);
                break;
            case ALL:
            default:
                users = YamlConfiguration.loadConfiguration(rxUsers);
                settings = YamlConfiguration.loadConfiguration(rxSettings);
                messages = YamlConfiguration.loadConfiguration(rxMessages);
                break;
        }
    }

    /**
     * Save config File using FileStorage
     *
     * @param fileToSave config to save/create with saveMode.
     */
    public void save(FileSaveMode fileToSave) {
        try {
            switch (fileToSave) {
                case PLAYERS:
                    getControl(GuardianFiles.PLAYERS).save(rxUsers);
                    break;
                case MESSAGES:
                    getControl(GuardianFiles.MESSAGES).save(rxMessages);
                    break;
                case SETTINGS:
                    getControl(GuardianFiles.SETTINGS).save(rxSettings);
                    break;
                case ALL:
                default:
                    getControl(GuardianFiles.SETTINGS).save(rxSettings);
                    getControl(GuardianFiles.MESSAGES).save(rxMessages);
                    getControl(GuardianFiles.PLAYERS).save(rxUsers);
                    break;
            }
        } catch (Throwable throwable) {
            plugin.getLogs().error("Can't save a file!");

        }
    }
    /**
     * Save config File Changes & Paths
     *
     * @param configName config to save/create.
     */
    public void saveConfig(String configName) {
        File folderDir = plugin.getDataFolder();
        File file = new File(plugin.getDataFolder(), configName + ".yml");
        if (!folderDir.exists()) {
            boolean createFile = folderDir.mkdir();
            if(createFile) plugin.getLogs().info("&7Folder created!");
        }

        if (!file.exists()) {
            try (InputStream in = plugin.getResource(configName + ".yml")) {
                if(in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (Throwable throwable) {
                plugin.getLogs().error(String.format("A error occurred while copying the config %s to the plugin data folder. Error: %s", configName, throwable));
                plugin.getLogs().error(throwable);
            }
        }
    }
    /**
     * Save config File Changes & Paths
     *
     * @param fileToSave config to save/create.
     */
    public void saveConfig(File fileToSave) {
        if (!fileToSave.getParentFile().exists()) {
            boolean createFile = fileToSave.mkdir();
            if(createFile) plugin.getLogs().info("&7Folder created!!");
        }

        if (!fileToSave.exists()) {
            plugin.getLogs().debug(fileToSave.getName());
            try (InputStream in = plugin.getResource(fileToSave.getName() + ".yml")) {
                if(in != null) {
                    Files.copy(in, fileToSave.toPath());
                }
            } catch (Throwable throwable) {
                plugin.getLogs().error(String.format("A error occurred while copying the config %s to the plugin data folder. Error: %s", fileToSave.getName(), throwable));
                plugin.getLogs().error(throwable);
            }
        }
    }

    /**
     * Control a file, getControl() will return a FileConfiguration.
     *
     * @param fileToControl config to control.
     */
    public FileConfiguration getControl(GuardianFiles fileToControl) {
        switch (fileToControl) {
            case MESSAGES:
                if (messages == null) messages = loadConfig(rxMessages);
                return messages;
            case PLAYERS:
                if (users == null) users = loadConfig(rxUsers);
                return users;
            default:
            case SETTINGS:
                if (settings == null) settings = loadConfig(rxSettings);
                return settings;
        }
    }

    public List<String> getContent(GuardianFiles file, String path, boolean getKeys) {
        List<String> rx = new ArrayList<>();
        ConfigurationSection section = getControl(file).getConfigurationSection(path);
        if(section == null) return rx;
        rx.addAll(section.getKeys(getKeys));
        return rx;
    }

}

