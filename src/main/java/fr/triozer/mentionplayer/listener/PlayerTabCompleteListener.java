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

        if (!Settings.canTabComplete() || (Settings.getOnlyTag().length() > 0 && !lastToken.startsWith(Settings.getOnlyTag())))
            return;

        event.getTabCompletions().clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (lastToken.length() == 1) {
                event.getTabCompletions().add(Settings.getOnlyTag() + player.getName());
            } else if (player.getName().startsWith(event.getLastToken().substring(1))) {
                event.getTabCompletions().add(Settings.getOnlyTag() + player.getName());
            }
        }
    }

}
