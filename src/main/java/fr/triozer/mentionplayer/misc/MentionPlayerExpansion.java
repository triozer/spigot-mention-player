package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
		}

		if (player == null) {
			return null;
		} else if (identifier.equals("player")) {
			MPlayer mPlayer = MPlayer.get(player.getUniqueId());
			return Settings.formatChat(mPlayer.getColor(), mPlayer);
		} else if (identifier.contains("color")) {
			String[] args = identifier.split(" ");
			if (args.length > 1) {
				ColorData color = ColorData.get(args[1]);
				if (color == null) return ChatColor.RED + "bad color id '" + args[1] + "'" + ChatColor.RESET;
				if (args.length == 3)
					return (color.parse(args[2]));
				else
					return (color.parse(player.getDisplayName()));
			}
		}

		return null;

	}
}