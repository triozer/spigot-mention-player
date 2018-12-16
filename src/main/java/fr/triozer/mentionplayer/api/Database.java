package fr.triozer.mentionplayer.api;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.player.Setting;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import fr.triozer.mentionplayer.misc.ProtocolHack;
import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Cédric / Triozer
 */
public class Database {

    private final MentionPlayer instance = MentionPlayer.getInstance();
    private final String host;
    private final int    port;
    private final String database;

    private Connection connection;
    private boolean    connected;

    public Database(String host, int port, String database) {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public Database auth(String username, String password) {
        MentionPlayer.LOG.fine(YELLOW + "[" + AQUA + "!" + YELLOW + "] " + DARK_GRAY + "Database: " + GRAY + "Trying to connect to '" + AQUA + database + GRAY + "' database at '" + AQUA + host + ":" + port + GRAY + "'.");

        try {
            Class.forName("com.mysql.jdbc.Driver");

            this.connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true&useSSL=true",
                    username, password);
            MentionPlayer.LOG.fine(YELLOW + "[" + AQUA + "!" + YELLOW + "] " + DARK_GRAY + "Database: " + GREEN + "Connected.");

            this.init();
            this.connected = true;
        } catch (ClassNotFoundException | SQLException e) {
            MentionPlayer.LOG.fine(YELLOW + "[" + AQUA + "!" + YELLOW + "] " + RED + "The plugin can't contact the MySQL database. "
                    + DARK_GRAY + "Error: " + YELLOW + e.getLocalizedMessage());
            this.connected = false;
        }

        return this;
    }

    public void init() {
        Bukkit.getScheduler().runTaskAsynchronously(this.instance, () -> {
            try {
                PreparedStatement statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `users` (" +
                        "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                        "`uuid` VARCHAR(36) NOT NULL, " +
                        "`mention` TINYINT NOT NULL DEFAULT '" + getDefaultValue("mention") + "', " +
                        "`sound` TINYINT NOT NULL DEFAULT '" + getDefaultValue("sound") + "', " +
                        "`actionbar` TINYINT NOT NULL DEFAULT '" + getDefaultValue("actionbar") + "', " +
                        "`visible` TINYINT NOT NULL DEFAULT '" + getDefaultValue("visible") + "', " +
                        "`popup` TINYINT NOT NULL DEFAULT '" + getDefaultValue("popup") + "', " +
                        "`lastMessage` BIGINT NOT NULL DEFAULT '0', " +
                        "`notification` VARCHAR(32) NOT NULL DEFAULT '" + Settings.getSound(false) + "', " +
                        "`color` VARCHAR(32) NOT NULL DEFAULT '" + instance.getConfig().getString("options.default.color") + "', " +
                        "PRIMARY KEY (`id`), UNIQUE INDEX `uuid` (`uuid`))");
                statement.execute();
                statement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ignores` (" +
                        "`uuid` VARCHAR(36) NOT NULL, " +
                        "`ignored` VARCHAR(36) NOT NULL, " +
                        "PRIMARY KEY (`uuid`), UNIQUE INDEX `uuid` (`uuid`))");
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public Map<String, Setting> getSettingsOf(UUID uuid) {
        Map<String, Setting> settings = new HashMap<>();

        try {
            PreparedStatement preparedStatement = this.connection
                    .prepareStatement("SELECT actionbar, mention, popup, sound, visible FROM users WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                settings.put("action-bar", new Setting("action-bar", resultSet.getBoolean(1)));
                settings.put("mention", new Setting("mention", resultSet.getBoolean(2)));
                settings.put("popup", new Setting("popup", resultSet.getBoolean(3)));
                settings.put("sound", new Setting("sound", resultSet.getBoolean(4)));
                settings.put("visible", new Setting("visible", resultSet.getBoolean(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return settings;
    }

    public long getLastMessageOf(UUID uuid) {
        long lastMessage = 0L;

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT lastMessage FROM users WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) lastMessage = resultSet.getLong("lastMessage");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lastMessage;
    }

    public ColorData getColorOf(UUID uuid) {
        ColorData color = ColorData.get(instance.getConfig().getString("options.default.color"));

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT color FROM users WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                if (ColorData.get(resultSet.getString("color")) != null)
                    color = ColorData.get(resultSet.getString("color"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return color;
    }

    public Sound getSoundOf(UUID uuid) {
        Sound sound = Sound.valueOf(instance.getConfig().getString("options.default.notification").toUpperCase());

        try {
            PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT notification FROM users WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                try {
                    sound = Sound.valueOf(resultSet.getString("notification"));
                } catch (IllegalArgumentException e) {
					sound = ProtocolHack.getSound();
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sound;
    }

	public Set<String> getIgnoredPlayers(UUID uuid) {
        Set<String> players = new HashSet<>();

        try {
            PreparedStatement preparedStatement = this.connection
                    .prepareStatement("SELECT ignored FROM ignores WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) players.add(resultSet.getString("ignored"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return players;
    }

    public boolean save(UUID uuid, MPlayer player) {
        try {
            PreparedStatement preparedStatement = this.connection
                    .prepareStatement("SET @u = ?, @a = ?, @m = ?, @p = ?, @s = ?, @v = ?, @lM = ?, @c = ?, @n = ?");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setBoolean(2, player.allowActionbar());
            preparedStatement.setBoolean(3, player.allowMention());
            preparedStatement.setBoolean(4, player.allowPopup());
            preparedStatement.setBoolean(5, player.allowSound());
            preparedStatement.setBoolean(6, player.isMentionPublic());
            preparedStatement.setLong(7, player.getLastMessage());
            preparedStatement.setString(8, player.getColor().getID());
            preparedStatement.setString(9, player.getSound().name());
            preparedStatement.execute();
            preparedStatement = this.connection
                    .prepareStatement("INSERT INTO users (uuid, mention, sound, actionbar, visible, popup, lastMessage, color, notification) " +
                            "VALUES (@u, @a, @m, @p, @s, @v, @lM, @c, @n) ON DUPLICATE KEY UPDATE " +
                            "mention = ?, sound = ? , actionbar = ?, visible = ?, popup = ?, lastMessage = ?, color = ?, notification = ?");
            preparedStatement.setBoolean(1, player.allowMention());
            preparedStatement.setBoolean(2, player.allowSound());
            preparedStatement.setBoolean(3, player.allowActionbar());
            preparedStatement.setBoolean(4, player.isMentionPublic());
            preparedStatement.setBoolean(5, player.allowPopup());
            preparedStatement.setLong(6, player.getLastMessage());
            preparedStatement.setString(7, player.getColor().getID());
            preparedStatement.setString(8, player.getSound().name());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void create(UUID uuid, Consumer<MPlayer> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(MentionPlayer.getInstance(), () -> {
            try {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("INSERT INTO users (uuid) VALUES (?) ON DUPLICATE KEY UPDATE uuid = ?");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, uuid.toString());
                preparedStatement.execute();

                ConfigurationSection section = MentionPlayer.getInstance().getData().getConfigurationSection(uuid.toString());
                if (section == null) {
                    section = MentionPlayer.getInstance().getData().createSection(uuid.toString());
                }

                Setting[]   settings    = this.getSettingsOf(uuid).values().toArray(new Setting[0]);
                long        lastMessage = this.getLastMessageOf(uuid);
                ColorData   color       = this.getColorOf(uuid);
                Sound       sound       = this.getSoundOf(uuid);
                Set<String> ignored     = this.getIgnoredPlayers(uuid);

                MPlayer player = new MPlayer(section, uuid, lastMessage, color, sound, ignored, settings);
                if (callback != null)
                    Bukkit.getScheduler().runTask(MentionPlayer.getInstance(), () -> callback.accept(player));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    private int getDefaultValue(String path) {
        return this.instance.getConfig().getBoolean("options.default." + path) ? 1 : 0;
    }

    public final String getHost() {
        return this.host;
    }

    public final int getPort() {
        return this.port;
    }

    public final String getDatabase() {
        return this.database;
    }

    public final Connection getConnection() {
        return this.connection;
    }

    public final boolean isConnected() {
        return this.connected;
    }

}
