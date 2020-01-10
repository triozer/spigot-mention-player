package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

/**
 * @author Cédric / Triozer
 */
public class PlayerTabCompleteListener implements Listener {

    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent event) {
        String lastToken = event.getLastToken();

        if (Settings.canTabComplete()
                && (!Settings.getOnlyTag().isEmpty() || lastToken.startsWith(Settings.getOnlyTag()))) {
            event.getTabCompletions().clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().startsWith(lastToken.substring(Settings.getOnlyTag().length()))) {
                    event.getTabCompletions().add(Settings.getOnlyTag() + player.getName());
                }
            }
        }
    }

}
