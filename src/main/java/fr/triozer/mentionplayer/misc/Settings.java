package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Cédric / Triozer
 */
public class Settings {

    public static String getTag() {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                MentionPlayer.getInstance().getConfig().getString("option.tag"));
    }

    public static boolean hasTag(String message) {
        return message.contains(getOnlyTag());
    }

    public static String getOnlyTag() {
        String[] split = Settings.getTag().split("(§.)");
        return split[split.length == 0 ? 0 : split.length - 1];
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

        return getTag() + color.parse(replace.replace(getTag(), ""));
    }

    public static String formatActionBar(String playerName) {
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                MentionPlayer.getInstance().getConfig().getString("format.action-bar")
                        .replace("{player-name}", playerName));
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

    /**
     * Registers custom colors from the configurations file.
     */
    public static void registerColors() {
        // clear all the custom tags (e.g. /mention reload)
        Set<String> values = new HashSet<>();
        for (ColorData colorData : MentionPlayer.getInstance().getColors().values())
            if (colorData.isCustom()) values.add(colorData.getID());
        for (String value : values) MentionPlayer.getInstance().getColors().remove(value);

        ConfigurationSection section = MentionPlayer.getInstance().getConfig().getConfigurationSection("colors");
        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            ConfigurationSection color = section.getConfigurationSection(key);

            String[]                            pattern    = color.getString("pattern").replace("&", "").split(",");
            String[]                            clone      = pattern.clone();
            List<net.md_5.bungee.api.ChatColor> chatColors = new ArrayList<>();
            List<DyeColor>                      dyeColors  = new ArrayList<>();

            for (int i = 0; i < clone.length; i++) {
                for (String code : clone[i].split("")) {
                    net.md_5.bungee.api.ChatColor chatColor = net.md_5.bungee.api.ChatColor.getByChar(code.charAt(0));
                    if (chatColor == null) {
                        MentionPlayer.LOG.error("The plugin can't find color '" + RED + code.charAt(0) + GRAY + "'." +
                                "Custom color '" + YELLOW + color.getString("name") + "'" + GRAY + " will not be loaded.");
                        return;
                    }
                    chatColors.add(chatColor);
                    if (!ColorData.isMagic(chatColor)) dyeColors.add(Utils.COLORS.get(chatColor));
                }
            }

            new ColorData(color.getString("name"), color.getString("permission"),
                    chatColors.toArray(new net.md_5.bungee.api.ChatColor[0]), dyeColors.toArray(new DyeColor[0]), true);
        });

    }

}
