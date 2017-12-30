package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

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
        return ChatColor.valueOf(MentionPlayer.getInstance().getConfig().getString("format.text-color"));
    }

    public static String formatChat(ColorData color, String playerName) {
        String replace = MentionPlayer.getInstance().getConfig().getString("format.chat")
                .replaceAll("&", "§")
                .replace("{player-name}", playerName)
                .replace("{tag}", getTag());

        return color == ColorData.RAINBOW ? color.rainbow(replace) : color.getChatColor() + replace;
    }

    public static String formatActionBar(ColorData color, String playerName) {
        String target = MentionPlayer.getInstance().getConfig().getString("format.chat")
                .replaceAll("&", "§")
                .replace("{player-name}", playerName)
                .replace("{tag}", getTag());

        String replace = MentionPlayer.getInstance().getConfig().getString("format.action-bar")
                .replaceAll("&", "§")
                .replace("{player-name}", color == ColorData.RAINBOW ? color.rainbow(target) : color.getChatColor() + target);

        return replace;
    }

    public static Sound getSound() {
        Sound sound;

        try {
            sound = Sound.valueOf(MentionPlayer.getInstance().getConfig().getString("option.sound"));
        } catch (IllegalArgumentException var2) {
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

}
