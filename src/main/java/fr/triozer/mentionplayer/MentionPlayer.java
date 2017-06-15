package fr.triozer.mentionplayer;

import fr.triozer.mentionplayer.command.MentionCommand;
import fr.triozer.mentionplayer.listener.PlayerChatListener;
import fr.triozer.mentionplayer.listener.PlayerTabCompleteListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * @author Cédric / Triozer
 */
public class MentionPlayer extends JavaPlugin {

    private static MentionPlayer instance;

    private FileConfiguration data;
    private File              dataFile;

    public static MentionPlayer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        createConfig();

        registerCommand();
        registerListener();
    }

    private void registerCommand() {
        getCommand("mention").setExecutor(new MentionCommand());
        getCommand("mention").setTabCompleter(new MentionCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTabCompleteListener(), this);
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            File file = new File(getDataFolder(), "config.yml");
            dataFile = new File(getDataFolder(), "data.yml");

            if (!file.exists()) {
                saveDefaultConfig();
            }

            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }

            data = new YamlConfiguration();

            try {
                getConfig().load(file);
                data.load(dataFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getData() {
        return data;
    }

    public File getDataFile() {
        return dataFile;
    }
}
