package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Cédric / Triozer
 */
public class Settings {

    public static String getTag() {
        return MentionPlayer.getInstance().getConfig().getString("option.tag");
    }

    public static boolean canGUI() {
        return MentionPlayer.getInstance().getConfig().getBoolean("option.gui");
    }

    public static long getInterval() {
        if (MentionPlayer.getInstance().getConfig().getBoolean("option.anti-spam.enable"))
            return MentionPlayer.getInstance().getConfig().getLong("option.anti-spam.interval") * 1000;
        else
            return 0L;
    }

    public static boolean canTabComplete() {
        return MentionPlayer.getInstance().getConfig().getBoolean("option.tab-complete");
    }

    public static ChatColor textColor() {
        ChatColor color;

        try {
            color = ChatColor.valueOf(MentionPlayer.getInstance().getConfig().getString("format.text-color"));
        } catch (IllegalArgumentException e) {
            color = ChatColor.GRAY;

            MentionPlayer.LOG.error("\"config.yml\" is configured improperly! Set text color to " + color.name() + ".");
            MentionPlayer.getInstance().getConfig().set("format.text-color", color.name());
            MentionPlayer.getInstance().saveConfig();
        }

        return color;
    }

    public static String formatChat(ColorData color, String playerName) {
        String replace = MentionPlayer.getInstance().getConfig().getString("format.chat")
                .replaceAll("&", "§")
                .replace("{player-name}", playerName)
                .replace("{tag}", getTag());

        return color.parse(replace);
    }

    public static String formatActionBar(ColorData color, String playerName) {
        String target = MentionPlayer.getInstance().getConfig().getString("format.chat")
                .replaceAll("&", "§")
                .replace("{player-name}", playerName)
                .replace("{tag}", getTag());

        return MentionPlayer.getInstance().getConfig().getString("format.action-bar")
                .replaceAll("&", "§")
                .replace("{player-name}", color.parse(target));
    }

    public static Sound getSound() {
        Sound sound;

        try {
            sound = Sound.valueOf(MentionPlayer.getInstance().getConfig().getString("option.sound"));
        } catch (IllegalArgumentException e) {
            String version = Bukkit.getServer().getClass().getPackage().getName();
            version = version.substring(version.lastIndexOf(".") + 1);

            if (version.contains("v1_8")) {
                sound = Sound.valueOf("NOTE_PLING");
                MentionPlayer.LOG.error("\"config.yml\" is configured improperly! Set '" + sound.name() + "' sound.");
                MentionPlayer.getInstance().getConfig().set("option.sound", sound.name());
                MentionPlayer.getInstance().saveConfig();
            } else {
                sound = Sound.valueOf("BLOCK_NOTE_PLING");
            }
        }

        return sound;
    }

    public static boolean canNotify() {
        return MentionPlayer.getInstance().getConfig().getBoolean("option.update-notifier");
    }

    public static void registerColors() {
        ConfigurationSection section = MentionPlayer.getInstance().getConfig().getConfigurationSection("colors");

        section.getKeys(false).forEach(key -> {
            ConfigurationSection color   = section.getConfigurationSection(key);
            String[]             pattern = color.getString("pattern").replace("§", "").split(",");

            ChatColor[] chatColors = new ChatColor[pattern.length];
            DyeColor[]  dyeColors  = new DyeColor[pattern.length];
            for (int i = 0; i < pattern.length; i++) {
                chatColors[i] = ChatColor.getByChar(pattern[i]);
                dyeColors[i] = Utils.COLORS.get(chatColors[i].asBungee());
            }
            new ColorData(color.getString("name"), color.getString("permission"), chatColors, dyeColors, true);
        });

    }
}
