package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
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
            if (MentionPlayer.getInstance().getDatabase().isConnected())
                MentionPlayer.getInstance().getDatabase().create(uniqueId, (player) -> MPlayer.getPlayers().put(uniqueId, player));
        }, 5L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (MPlayer.getPlayers().containsKey(event.getPlayer().getUniqueId())) {
            MPlayer.get(event.getPlayer()).save();
        }
    }

}
