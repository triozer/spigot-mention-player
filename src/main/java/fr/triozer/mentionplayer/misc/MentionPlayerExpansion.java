package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.api.player.MPlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class MentionPlayerExpansion extends PlaceholderExpansion {
    public MentionPlayerExpansion() {
    }

    public boolean canRegister() {
        return true;
    }

    public String getAuthor() {
        return "Triozer";
    }

    public String getIdentifier() {
        return "mention";
    }

    public String getPlugin() {
        return null;
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equals("raw_tag")) {
            return Settings.getOnlyTag();
        } else if (identifier.equals("tag")) {
            return Settings.getTag();
        } else if (player == null) {
            return "";
        } else {
            MPlayer mPlayer = MPlayer.get(player);
            return identifier.equals("player_tag") ? Settings.formatChat(mPlayer.getColor(), mPlayer) : null;
        }
    }
}