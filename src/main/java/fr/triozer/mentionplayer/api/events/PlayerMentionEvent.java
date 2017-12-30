package fr.triozer.mentionplayer.api.events;

import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.misc.ColorData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player mentions another player
 *
 * @author Cédric / Triozer
 */
public class PlayerMentionEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final MPlayer mPlayer;
    private final Player  mentioned;

    private boolean   cancelled;
    private ColorData color;
    private long      last;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PlayerMentionEvent(Player who, Player mentioned) {
        super(who);

        this.mPlayer = MPlayer.get(who);
        this.color = mPlayer.getColor();
        this.last = mPlayer.getLastMessage();
        this.mentioned = mentioned;
    }

    public final Player getMentioned() {
        return this.mentioned;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public final ColorData getColor() {
        return this.color;
    }

    public void setColor(ColorData color) {
        this.color = color;
    }

    public final MPlayer getMentionPlayer() {
        return this.mPlayer;
    }

    public final long getLast() {
        return this.last;
    }

    public void setLast(long last) {
        this.last = last;
    }

}
