package fr.triozer.mentionplayer;

import com.google.gson.JsonParser;
import fr.triozer.mentionplayer.api.ui.InventoryBuilder;
import fr.triozer.mentionplayer.command.MentionCommand;
import fr.triozer.mentionplayer.listener.InventoryListener;
import fr.triozer.mentionplayer.listener.PlayerChatListener;
import fr.triozer.mentionplayer.listener.PlayerTabCompleteListener;
import fr.triozer.mentionplayer.misc.Console;
import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Cédric / Triozer
 */
public class MentionPlayer extends JavaPlugin {

    public static Console LOG = new Console("MentionPlayer");

    private static MentionPlayer instance;

    private List<InventoryBuilder> inventories;

    private FileConfiguration data;
    private File              dataFile;

    public static MentionPlayer getInstance() {
        return instance;
    }

    public static boolean update(String update) throws Exception {
        URL               url        = new URL(update);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != 200) {
            LOG.warning("Can't get last version from " + update);
            return false;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String lastVersion = new JsonParser().parse(reader.readLine()).getAsJsonObject().get("version").getAsString();

        if (lastVersion.isEmpty()) return false;
        else if (!lastVersion.matches("(?!\\.)(\\d+(\\.\\d+)+)(?:[-.]+)?(?![\\d.])$")) return false;

        return !instance.getDescription().getVersion().equals(lastVersion);
    }


    @Override
    public void onEnable() {
        instance = this;
        this.inventories = new ArrayList<>();

        if (Settings.canNotify()) {
            LOG.fine("Searching for updates.");

            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    if (update("https://rest.c-dric.eu/api/plugins/mention"))
                        LOG.warning("Please update the plugin for a better support.");
                    else
                        LOG.fine("No update found !");
                } catch (Exception e) {
                    LOG.error("Can't check for update");
                }
            });
        }

        if (!Settings.canGUI()) {
            LOG.warning("Your players can't use '/mention gui' because you disabled this feature.");
        }

        createConfig();

        registerCommand();
        registerListener();
    }

    @Override
    public void onDisable() {
        Set<HumanEntity> openers = new HashSet<>();

        for (InventoryBuilder inventory : this.inventories) openers.addAll(inventory.build().getViewers());
        for (HumanEntity opener : openers) opener.closeInventory();
    }

    private void registerCommand() {
        getCommand("mention").setExecutor(new MentionCommand());
        getCommand("mention").setTabCompleter(new MentionCommand());
    }

    private void registerListener() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
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

    public List<InventoryBuilder> getInventoriesList() {
        return this.inventories;
    }

}
