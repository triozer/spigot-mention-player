package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric / Triozer
 */
public class I18N {

    public static String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', MentionPlayer.getInstance().getMessages().getString(path));
    }

    public static String get(String path, String or) {
        if (MentionPlayer.getInstance().getMessages().getString(path) == null) {
            return or;
        } else {
            return get(path);
        }
    }

    public static String[] getStringList(String path) {
        List<String> strings = MentionPlayer.getInstance().getMessages().getStringList(path);
        List<String> list = new ArrayList<>();
        for (String string : strings) {
            list.add(ChatColor.translateAlternateColorCodes('&', string));
        }
        return list.toArray(new String[0]);
    }
}
