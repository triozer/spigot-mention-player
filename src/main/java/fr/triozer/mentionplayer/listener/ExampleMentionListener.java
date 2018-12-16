package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.api.event.PlayerMentionEvent;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Cédric / Triozer
 */
public class ExampleMentionListener implements Listener {

	@EventHandler
	public void onMention(PlayerMentionEvent event) {
		if (event.getPlayer().getName().equals("_JustDoIt"))
			event.setColor(ColorData.RED);
	}

}
