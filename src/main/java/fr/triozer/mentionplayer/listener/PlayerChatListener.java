package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.api.events.PlayerMentionEvent;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.misc.Settings;
import fr.triozer.mentionplayer.misc.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric / Triozer
 */
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (Settings.hasTag(event.getMessage())) {
            List<Player> players = new ArrayList<>();
            String       tag     = Settings.getOnlyTag();
            String       one     = "";

            if (tag.length() == 0)
                one = "";
            else if (tag.length() == 1) {
                char c = tag.charAt(0);
                if (c == '*' || c == '!' || c == '$' || c == '^' || c == '+' || c == '\\' || c == '.' || c == '{' || c == '}')
                    one += MessageFormat.format("{0}{1}{2}", '\\', c, "{1}");
                else one = tag;
            } else one += "[" + tag + "]{" + tag.length() + "}";

            for (Player online : Bukkit.getOnlinePlayers())
                for (String word : event.getMessage().split(" ")) {
                    String regex = "(^" + one + "[" + online.getName() + "]{" + online.getName().length() + "}" +
                            "[\\^*()_+=\\[\\]{}|\\\\,.?!:<>'\"\\/;`%¨-]*(?!.*[ ])$)";
                    if (word.matches(regex)) {
                        players.add(online);
                    }
                }

            if (players.isEmpty()) return;

            MPlayer sender = MPlayer.get(event.getPlayer());

            for (Player player : players) {
                MPlayer mPlayer = MPlayer.get(player);

                if (!sender.canBypassAntiSpam() && System.currentTimeMillis() - sender.getLastMessage() <= Settings.getInterval()) {
                    sender.spam();
                    return;
                }

                sender.setLastMessage(System.currentTimeMillis());

                if (sender.canBypassMention() || mPlayer.isMentionable()) {

                    PlayerMentionEvent mentionEvent = new PlayerMentionEvent(event.getPlayer(), player);
                    Bukkit.getServer().getPluginManager().callEvent(mentionEvent);

                    if (mentionEvent.isCancelled()) return;

                    if (sender.canBypassSound() || mPlayer.isSoundable())
                        player.playSound(player.getLocation(), Settings.getSound(), 1f, 1f);
                    if (sender.canBypassActionBar() || mPlayer.canReceiveActionBar())
                        Utils.sendActionBar(player, Settings.formatActionBar(sender.getPlayer().getName()));

                    String mention = Settings.textColor() + event.getMessage().replace(
                            tag + player.getName(),
                            Settings.formatChat(mentionEvent.getColor(), player.getName()) + Settings.textColor());

                    event.getRecipients().remove(player);
                    player.sendMessage(String.format(event.getFormat(), sender.getPlayer().getDisplayName(), mention));
                    if (mPlayer.isVisible()) {
                        event.setMessage(mention);
                    }
                }
            }
        }
    }

}
