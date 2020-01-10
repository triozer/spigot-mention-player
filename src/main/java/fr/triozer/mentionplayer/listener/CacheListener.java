package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * @author Cédric / Triozer
 */
public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uniqueId = event.getUniqueId();
        Bukkit.getScheduler().runTaskLaterAsynchronously(MentionPlayer.getInstance(), () -> {
            if (Settings.canSQL() && MentionPlayer.getInstance().getDatabase() != null && MentionPlayer.getInstance().getDatabase().isConnected()) {
                MentionPlayer.getInstance().getDatabase().create(uniqueId, (player) -> MPlayer.getPlayers().put(uniqueId, player));
            } else {
                MPlayer.getPlayers().put(uniqueId, MPlayer.get(uniqueId));
            }
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (MPlayer.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
            MPlayer.get(event.getPlayer().getUniqueId()).save();
        }
    }

}
