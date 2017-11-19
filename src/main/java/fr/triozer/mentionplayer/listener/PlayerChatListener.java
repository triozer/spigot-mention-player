package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.misc.MPlayer;
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
        if (event.getMessage().contains("@")) {
            List<Player> players = new ArrayList<>();

            for (Player online : Bukkit.getOnlinePlayers())
                if (event.getMessage().contains("@" + online.getName()))
                    players.add(online);

            if (players.isEmpty()) return;

            MPlayer sender = MPlayer.get(event.getPlayer());

            String message;
            for (Player player : players) {
                MPlayer mPlayer = MPlayer.get(player);
                event.setCancelled(true);

                if (System.currentTimeMillis() - mPlayer.getLastMessage() <= Settings.getInterval()) {
                    sender.spam();
                    return;
                }

                mPlayer.setLastMessage(System.currentTimeMillis());

                if (sender.canBypassMention() || mPlayer.isMentionable()) {
                    event.setCancelled(false);
                    event.getRecipients().remove(player);

                    String mention = Settings.textColor() + event.getMessage().replace("@" + player.getName(), Settings.formatChat(player.getName()) + Settings.textColor());
                    message = String.format(event.getFormat(), sender.getPlayer().getName(), mention);

                    if (sender.canBypassSound() || mPlayer.isSoundable())
                        player.playSound(player.getLocation(), Settings.getSound(), 1f, 1f);
                    if (sender.canBypassActionBar() || mPlayer.canReceiveActionBar())
                        Utils.sendActionBar(player, Settings.formatActionBar(sender.getPlayer().getName()));

                    player.sendMessage(message);
                }
            }
        }
    }

}
