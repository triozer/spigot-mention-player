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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric / Triozer
 */
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains(Settings.getTag())) {
            List<Player> players = new ArrayList<>();

            for (Player online : Bukkit.getOnlinePlayers())
                for (String word : event.getMessage().split(" ")) {
                    if (word.startsWith(Settings.getTag()))
                        if (word.contentEquals(Settings.getTag() + online.getName())
                                || word.contentEquals(Settings.getTag() + online.getCustomName())
                                || word.contentEquals(Settings.getTag() + online.getDisplayName())) {
                            players.add(online);
                        }
                }


            if (players.isEmpty()) return;

            MPlayer sender = MPlayer.get(event.getPlayer());

            for (Player player : players) {
                MPlayer mPlayer = MPlayer.get(player);

                if (!mPlayer.canBypassAntiSpam() && System.currentTimeMillis() - mPlayer.getLastMessage() <= Settings.getInterval()) {
                    sender.spam();
                    return;
                }

                mPlayer.setLastMessage(System.currentTimeMillis());

                if (sender.canBypassMention() || mPlayer.isMentionable()) {

                    PlayerMentionEvent mentionEvent = new PlayerMentionEvent(event.getPlayer(), player);
                    Bukkit.getServer().getPluginManager().callEvent(mentionEvent);

                    if (mentionEvent.isCancelled()) return;

                    event.getRecipients().remove(player);

                    String mention = Settings.textColor() + event.getMessage().replace(

                            Settings.getTag() + player.getName(),
                            mentionEvent.getColor().getChatColor() + Settings.formatChat(mentionEvent.getColor(), player.getName()) + Settings.textColor());

                    String message = String.format(event.getFormat(), sender.getPlayer().getDisplayName(), mention);

                    if (sender.canBypassSound() || mPlayer.isSoundable())
                        player.playSound(player.getLocation(), Settings.getSound(), 1f, 1f);
                    if (sender.canBypassActionBar() || mPlayer.canReceiveActionBar())
                        Utils.sendActionBar(player, Settings.formatActionBar(mentionEvent.getColor(), sender.getPlayer().getName()));

                    player.sendMessage(message);
                }
            }
        }
    }

}
