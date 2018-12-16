package fr.triozer.mentionplayer;

import com.google.common.base.Charsets;
import com.google.gson.JsonParser;
import fr.triozer.mentionplayer.api.Database;
import fr.triozer.mentionplayer.api.ui.builder.InventoryBuilder;
import fr.triozer.mentionplayer.command.MentionCommand;
import fr.triozer.mentionplayer.listener.*;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import fr.triozer.mentionplayer.misc.Console;
import fr.triozer.mentionplayer.misc.MentionPlayerExpansion;
import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * The main class of the plugin.
 *
 * @author Cédric / Triozer
 */
public class MentionPlayer extends JavaPlugin {

    public static Console LOG = new Console("[MentionPlayer" + LIGHT_PURPLE + "+" + WHITE + "]");

    private static MentionPlayer instance;


    private List<InventoryBuilder> inventories;
    private Map<String, ColorData> colorData;
    private Database               database;

    private FileConfiguration data;
    private FileConfiguration messages;
    private FileConfiguration colors;
    private File              messagesFile;
    private File              dataFile;
    private File              colorsFile;
    private String            lastVersion;

    /**
     * Gets the instance of the plugin.
     *
     * @return The instance of the plugin.
     */
    public static MentionPlayer getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.inventories = new ArrayList<>();
        this.colorData = new HashMap<>();

        long started = System.currentTimeMillis();
        LOG.send(DARK_GRAY + "___________________________________________________");
        LOG.send("");
        LOG.send(GREEN + "                                      _____");
        LOG.send(GREEN + "                   .--'''--.         / ___ `.");
        LOG.send(GREEN + "                ,' .--'''--. `.     |_/___) |");
        LOG.send(GREEN + "               ' '           `. \\    .'____.'");
        LOG.send(GREEN + "              / /    _..--''-. \\ :  / /_____");
        LOG.send(GREEN + "             | |   .' .-'''/ / ' |  |_______|");
        LOG.send(GREEN + "             : :  ' /     /.'  / /  ");
        LOG.send(GREEN + "             ' '  ' '._.' '._.' /   ");
        LOG.send(GREEN + "              \\ \\ `.._..'._..''   ");
        LOG.send(GREEN + "               . '.                 ");
        LOG.send(GREEN + "                '. '-..__..'|       ");
        LOG.send(GREEN + "                  `-...____.'       ");
        LOG.send("");

        createConfig();
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            LOG.send("");
            if (Settings.canNotify()) checkUpdate(Bukkit.getConsoleSender(), true);
            else {
                LOG.fine(YELLOW + "[" + AQUA + "!" + YELLOW + "] " + GRAY + "The update checker is currently " + RED + "off" + GRAY + ", you can " + GREEN +
                        "enable" + GRAY + " it in the configuration file.");
                LOG.send("");
            }
            if (Settings.canSQL()) {
                String host     = getConfig().getString("options.mysql.host");
                int    port     = getConfig().getInt("options.mysql.port");
                String database = getConfig().getString("options.mysql.database");
                this.database = new Database(host, port, database).auth(getConfig().getString("options.mysql.user"),
                        getConfig().getString("options.mysql.password"));
            } else {
                LOG.fine(YELLOW + "[" + AQUA + "!" + YELLOW + "]" + GRAY + "The plugin can't use MySQL because you " + RED + "disabled" + GRAY + " this feature.");
            }
            LOG.send("");
        });

        if (!Settings.canTabComplete()) {
            LOG.sendWarning("Your players can't use tab-completion because you " + RED + "disabled" + GRAY + " this feature.");
        }
        if (!Settings.canGUI()) {
            LOG.sendWarning("Your players can't use '" + AQUA + "/mention " + WHITE + "gui" + GRAY +
                    "' because you " + RED + "disabled" + GRAY + " this feature.");
        }
        if (!Settings.canPopup()) {
            if (Bukkit.getVersion().contains("1.12") || Bukkit.getVersion().contains("1.13") )
                LOG.sendWarning("Your players can't use popups because you " + RED + "disabled" + GRAY + " this feature.");
            else
                LOG.sendWarning("Your players can't use popups. You should update your server to " + RED + "1.12+" + GRAY + ".");
        } else {
            LOG.sendWarning("Don't reload the server, it could cause serious glitches on popups. " + RED + "Restart it" + GRAY + ".");
        }
        Settings.getSound(true); // here we are checking for mistakes on configuration
        Settings.textColor("",true); // here we are checking for mistakes on configuration
        LOG.send("");

        if (Settings.canPapi()) {
            new MentionPlayerExpansion().register();
            LOG.send("    Plugin hooked into " + AQUA + "PlaceholderAPI" + GRAY + ".");
        } else {
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null
                    && getConfig().getBoolean("options.hook.placeholderapi")) {
                LOG.sendWarning("Can't find PlaceholderAPI plugin." +
                        " Please " + RED + "disable" + GRAY + " this feature or " + AQUA + "install PlaceholderAPI" +
                        GRAY + " on your server and" + RED + " restart " + GRAY + "the server.");
            } else {
                LOG.sendWarning("You " + RED + "disabled" + GRAY + " the PlaceholderAPI feature.");
            }
        }

        if (Settings.canDeluxeChat()) {
            LOG.send("    Plugin hooked into " + AQUA + "DeluxeChat" + GRAY + ".");
        } else {
            if (Bukkit.getPluginManager().getPlugin("DeluxeChat") == null
                    && getConfig().getBoolean("options.hook.deluxechat")) {
                LOG.sendWarning("Can't find DeluxeChat plugin." +
                        " Please " + RED + "disable" + GRAY + " this feature or " + AQUA + "install DeluxeChat" +
                        GRAY + " on your server and" + RED + " restart " + GRAY + "the server.");
            } else {
                LOG.sendWarning("You " + RED + "disabled" + GRAY + " the DeluxeChat feature.");
            }
        }
        LOG.send("");

        started = System.currentTimeMillis() - started;
        LOG.send("    " + GREEN + "I'm ready !" + DARK_GRAY + " (took " + started + "ms)");
        LOG.send(DARK_GRAY + "___________________________________________________");

        Settings.registerColors();

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
        Bukkit.getPluginManager().registerEvents(new CacheListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTabCompleteListener(), this);
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdirs();

            File file = new File(getDataFolder(), "config.yml");
            this.messagesFile = new File(getDataFolder(), "messages.yml");
            this.dataFile = new File(getDataFolder(), "data.yml");
            this.colorsFile = new File(getDataFolder(), "colors.yml");
            this.data = new YamlConfiguration();
            this.colors = new YamlConfiguration();
            this.messages = new YamlConfiguration();

            checkForChanges(file, "config.yml");
            checkForChanges(this.messagesFile, "messages.yml");

            if (!this.colorsFile.exists()) {
                saveResource("colors.yml", false);
                LOG.send("    " + DARK_GRAY + "Configuration: " + GREEN + "the default " + AQUA + "colors.yml" + GREEN + " has been saved" + DARK_GRAY + ". " +
                        "Please watch it and modify it as you wish.");
            }

            if (!this.dataFile.exists()) this.dataFile.createNewFile();

            getConfig().load(file);
            this.messages.load(this.messagesFile);
            this.data.load(this.dataFile);
            this.colors.load(this.colorsFile);
            LOG.send("    " + DARK_GRAY + "Configuration: " + GREEN + "Loaded" + DARK_GRAY + ".");
            LOG.send("    " + DARK_GRAY + "Messages: " + GREEN + "Loaded" + DARK_GRAY + ".");
            LOG.send("    " + DARK_GRAY + "Colors: " + GREEN + "Loaded" + DARK_GRAY + ".");
            LOG.send("");
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void checkForChanges(File file, String fileName) throws IOException {
        if (!file.exists()) {
            if (fileName.equals("config.yml")) saveDefaultConfig();
            else saveResource(fileName, false);
            LOG.send("    " + DARK_GRAY + "Configuration: " + GREEN + "the default " + AQUA + fileName + GREEN + " has been saved" + DARK_GRAY + ". " +
                    "Please watch it and modify it as you wish.");
        } else {
            // check if the packed configuration has more keys
            boolean           changes = false;
            YamlConfiguration temp    = YamlConfiguration.loadConfiguration(file);
            YamlConfiguration config  = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource(fileName), Charsets.UTF_8));

            for (String key : config.getKeys(true)) {
                if (!temp.getKeys(true).contains(key) && !key.equals("options.popup.icon.data")) {
                    Object value = config.get(key);
                    temp.set(key, value);
                    changes = true;
                    if (temp.isConfigurationSection(key)) continue;
                    LOG.send("    " + AQUA + fileName + DARK_GRAY + ": " + GRAY + "added default key " + GREEN + key + DARK_GRAY + ".");
                }
            }

            if (changes) temp.save(file);
        }
    }

    public void checkUpdate(CommandSender sender, boolean log) {
        List<String> update = new ArrayList<>();

        if (!log) update.add("");
        String text = (log ? "" : "  ") + GRAY + "Actually running " + AQUA + getName() + GRAY + " v" + AQUA + getDescription().getVersion() + GRAY + " ! ";
        if (this.update()) {
            text += "A new version is out (" + RED + this.lastVersion + GRAY + "). Please update the plugin for better support.";
        } else {
            text += "You have the latest version. Thanks for support ! " + ColorData.RAINBOW.parse(":)");
        }
        update.add(text);
        update.add("");

        if (log) {
            LOG.send("");
            MentionPlayer.LOG.fine(text);
        } else {
            for (String message : update) sender.sendMessage(message);
        }
    }

    private boolean update() {
        try {
            URL               url        = new URL("https://api.triozer.fr/plugins/1");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                LOG.warning("Can't get last version from " + url.toString());
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder a = new StringBuilder();
			String        str;
			while ((str = reader.readLine()) != null) {
				a.append(str);
			}
			reader.close();

			this.lastVersion = new JsonParser().parse(a.toString()).getAsJsonObject().get("version").getAsString();

            if (this.lastVersion.isEmpty()) return false;
            else if (!this.lastVersion.matches("(?!\\.)(\\d+(\\.\\d+)+)(?:[-.]+)?(?![\\d.])$")) return false;

            return !getDescription().getVersion().equals(this.lastVersion);
        } catch (Exception e) {
            LOG.error("Can't check for update");
			e.printStackTrace();
			return false;
        }
    }

    /**
     * Gets the list of all opened inventories.
     *
     * @return The list of all opened inventories.
     */
    public final List<InventoryBuilder> getInventoriesList() {
        return this.inventories;
    }

    /**
     * Gets the colors tags by ID.
     *
     * @return a Map with the {@link ColorData} by ID.
     */
    public final Map<String, ColorData> getColorData() {
        return this.colorData;
    }

    /**
     * Gets the players data configuration.
     *
     * @return The players data configuration.
     */
    public final FileConfiguration getData() {
        return this.data;
    }

    /**
     * Gets the players data file.
     *
     * @return The players data file.
     */
    public final File getDataFile() {
        return this.dataFile;
    }

    public final FileConfiguration getMessages() {
        return this.messages;
    }

    public final FileConfiguration getColors() {
        return this.colors;
    }

    public final Database getDatabase() {
        return this.database;
    }

    public void reloadMessage() {
        this.messages = YamlConfiguration.loadConfiguration(this.messagesFile);

        final InputStream defConfigStream = getResource("message.yml");
        if (defConfigStream == null) {
            return;
        }

        this.messages.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

}
