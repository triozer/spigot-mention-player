package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import fr.triozer.mentionplayer.misc.xseries.XMaterial;
import fr.triozer.mentionplayer.misc.xseries.XSound;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * This class lists many of the parameters contained in the configuration file.
 * These are more "tools" and simpler ways of obtaining values.
 *
 * @author Cédric / Triozer
 */
public class Settings {

    /**
     * Gets the current mention prefix.
     *
     * @return The current mention prefix.
     */
    public static String getTag() {
        String text;

        if (customTag())
            text = translateAlternateColorCodes('&', MentionPlayer.getInstance().getConfig().getString("options.prefix.value"));
        else
            text = MentionPlayer.getInstance().getConfig().getString("options.prefix.value");

        if (text.equalsIgnoreCase("none")) text = "";

        return text;
    }

    /**
     * Returns <tt>true</tt> if the plugin should color format the tag.
     *
     * @return <tt>true</tt> if the plugin should color format the tag.
     */
    public static boolean customTag() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.prefix.custom");
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of the GUI.
     *
     * @return <tt>true</tt> if the configuration allow the use of the GUI.
     */
    public static boolean canGUI() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.gui");
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of PlaceholderAPI and the server has the plugin.
     *
     * @return <tt>true</tt> if the configuration allow the use of PlaceholderAPI and the server has the plugin.
     */
    public static boolean canPapi() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.hook.placeholderapi")
                && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Returns <tt>true</tt> if the server has the DeluxeChat.
     *
     * @return <tt>true</tt> if the server has the DeluxeChat.
     */
    public static boolean canDeluxeChat() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.hook.deluxechat")
                && Bukkit.getPluginManager().getPlugin("DeluxeChat") != null;
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of the popups and the server version is 1.12+ (for the moment).
     *
     * @return <tt>true</tt> if the configuration allow the use of the popups and the server version is 1.12+.
     */
    public static boolean canPopup() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.popup.enabled")
                && XMaterial.isVersionOrHigher(XMaterial.MinecraftVersion.V1_13);
    }

    /**
     * Gets the waiting time between two mentions. In milliseconds.
     * <p>
     * Note: if the configuration does not enable anti-spam then the interval will be 0.
     *
     * @return The waiting time.
     */
    public static long getInterval() {
        if (MentionPlayer.getInstance().getConfig().getBoolean("options.anti-spam.enable"))
            return MentionPlayer.getInstance().getConfig().getLong("options.anti-spam.interval") * 1000;
        else
            return 0L;
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of mentions tab completer.
     *
     * @return <tt>true</tt> if the configuration allow the use of mentions tab completer.
     */
    public static boolean canTabComplete() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.tab-complete");
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of the action bar.
     *
     * @return <tt>true</tt> if the configuration allow the use of the action bar.
     */
    public static boolean canActionBar() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.action-bar");
    }

    /**
     * Returns <tt>true</tt> if the configuration allow the use of mysql database.
     *
     * @return <tt>true</tt> if the configuration allow the use of mysql database.
     */
    public static boolean canSQL() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.mysql.enabled");
    }

    /**
     * Gets the color for the mention message.
     * <p>
     * Note: it is possible to bypass this system by adding colors before and after the mention.
     *
     * @return The color for the mention message.
     */
    public static String textColor(String previous, boolean log) {
        String defaultColor = MentionPlayer.getInstance().getConfig().getString("format.text-color");
        String color        = "";

        try {
            if (defaultColor.equalsIgnoreCase("none"))
                color = Utils.getLastColor(previous);
            else
                color = defaultColor;
        } catch (IllegalArgumentException e) {
            if (log) {
                MentionPlayer.LOG.sendWarning("The plugin can't find color '" + RED + defaultColor + GRAY + "'.");
            }
        }

        return color;
    }

    /**
     * Transforms a raw message that contains a mention into a message with the specified color tag.
     *
     * @param color  The color tag
     * @param player The person mentioned
     * @return A message with the specified mention color tag.
     */
    public static String formatChat(ColorData color, MPlayer player) {
        String replace = MentionPlayer.getInstance().getConfig().getString("format.chat")
                .replaceAll("&", "§")
                .replace("{player-name}", player.getPlayer().getName())
                .replace("{prefix}", getTag());

        String text = replace;
        if (player.canUseTag()) { // if the player can't use the tag just returns the raw message
            if (customTag())
                text = getTag() + color.parse(replace.replace(getTag(), ""));
            else
                text = color.parse(replace);
        }

        return text;
    }


    /**
     * Transforms the configuration action-bar message into a message with the specified sender name.
     *
     * @param playerName The player name of the sender.
     * @return A message with the specified sender name.
     */
    public static String formatActionBar(String playerName) {
        return translateAlternateColorCodes('&',
                MentionPlayer.getInstance().getConfig().getString("format.action-bar")
                        .replace("{player-name}", playerName));
    }

    /**
     * Gets the sound for the mention message.
     * <p>
     * Note: it is possible to bypass this system by adding colors before and after the mention.
     *
     * @return The sound for the mention message.
     */
    public static Sound getSound(boolean log) {
        String defaultSound = MentionPlayer.getInstance().getConfig().getString("options.default.notification");
        Sound  sound;

        try {
            sound = XSound.matchXSound(defaultSound).orElseThrow(IllegalArgumentException::new)
                    .parseSound();
        } catch (IllegalArgumentException e) {
            sound = XSound.BLOCK_NOTE_BLOCK_PLING.parseSound();

            MentionPlayer.getInstance().getConfig().set("options.default.notification", sound.name());
            MentionPlayer.getInstance().saveConfig();
            if (log) {
                MentionPlayer.LOG.sendWarning("The plugin can't find sound '" + RED + defaultSound + GRAY + "'" +
                        " automatically set to '" + GREEN + sound.name() + "'" + GRAY + ".");
            }
        }

        return sound;
    }

    /**
     * Returns <tt>true</tt> if the plugin can warn about updates at startup.
     *
     * @return <tt>true</tt> if the plugin can warn about updates at startup.
     */
    public static boolean canNotify() {
        return MentionPlayer.getInstance().getConfig().getBoolean("options.update-notifier");
    }

    /**
     * Registers custom colors from the configurations file.
     */
    public static void registerColors() {
        // clear all the custom tags (e.g. /mention reload)
        Set<String> values = new HashSet<>();
        for (ColorData colorData : MentionPlayer.getInstance().getColorData().values())
            if (colorData.isCustom()) values.add(colorData.getID());
        for (String value : values) MentionPlayer.getInstance().getColorData().remove(value);

        FileConfiguration colors = MentionPlayer.getInstance().getColors();
        colors.getKeys(false).forEach(key -> {
            ConfigurationSection color = colors.getConfigurationSection(key);

            String[]        pattern    = color.getString("pattern").replace("&", "").split(",");
            String[]        clone      = pattern.clone();
            List<ChatColor> chatColors = new ArrayList<>();
            List<DyeColor>  dyeColors  = new ArrayList<>();

            for (int i = 0; i < clone.length; i++) {
                for (String code : clone[i].split("")) {
                    ChatColor chatColor = getByChar(code.charAt(0));
                    if (chatColor == null) {
                        MentionPlayer.LOG.sendWarning("The plugin can't find color '" + RED + code.charAt(0) + GRAY + "'." +
                                "Custom color '" + YELLOW + color.getString("name") + "'" + GRAY + " will not be loaded.");
                        return;
                    }
                    chatColors.add(chatColor);
                    if (!ColorData.isMagic(chatColor)) dyeColors.add(Utils.COLORS.get(chatColor));
                }
            }

            new ColorData(color.getString("name"), color.getString("permission"),
                    chatColors.toArray(new ChatColor[0]), dyeColors.toArray(new DyeColor[0]), true);
        });

    }

    /**
     * Returns <tt>true</tt> if the specified message contains the mention prefix.
     *
     * @param message The message.
     * @return <tt>true</tt> if the specified message contains the mention prefix.
     */
    public static boolean hasTag(String message) {
        return message.contains(getOnlyTag());
    }

    /**
     * Gets only the mention prefix without any format.
     *
     * @return The mention prefix without any format.
     */
    public static String getOnlyTag() {
        String[] split = Settings.getTag().split("(§.)");
        return split[split.length == 0 ? 0 : split.length - 1];
    }

    /**
     * Gets the prefix for bypassing the specified settings.
     *
     * @param prefix The prefix
     * @return The prefix for bypassing the specified settings.
     */
    public static String getPrefix(String prefix) {
        return MentionPlayer.getInstance().getConfig().getString("options.prefix.force." + prefix);
    }

    /**
     * Gets a String composed of all the bypassing prefix.
     *
     * @return a String composed of all the bypassing prefix.
     */
    public static String allForcePrefix() {
        return Settings.getPrefix("actionbar") + Settings.getPrefix("mention") +
                Settings.getPrefix("popup") + Settings.getPrefix("sound") + Settings.getPrefix("visible");
    }

}
