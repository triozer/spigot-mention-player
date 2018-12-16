package fr.triozer.mentionplayer.api.event;

import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.popup.BukkitPopup;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Called when a player mentions another player
 *
 * @author Cédric / Triozer
 */
public class PlayerMentionEvent extends PlayerEvent  implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final MPlayer     mPlayer;
    private final Player      mentioned;
    private final MPlayer     mentionedPlayer;
    private       boolean     cancelled;
    private       boolean     canPopup;
    private       ColorData   color;
    private       long        last;
    private       BukkitPopup popup;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public final Player getMentioned() {
        return this.mentioned;
    }

    public PlayerMentionEvent(Player who, Player mentioned, boolean canPopup, BukkitPopup popup) {
        super(who);
        this.canPopup = canPopup;
        this.mPlayer = MPlayer.get(who.getUniqueId());
        this.last = this.mPlayer.getLastMessage();
        this.mentioned = mentioned;
        this.mentionedPlayer = MPlayer.get(mentioned.getUniqueId());
        this.color = this.mentionedPlayer.getColor();
        this.popup = popup;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

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

    public final MPlayer getMentionedPlayer() {
        return this.mentionedPlayer;
    }

    public final long getLast() {
        return this.last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public final boolean canPopup() {
        return this.canPopup;
    }

    public void setCanPopup(boolean canPopup) {
        this.canPopup = canPopup;
    }

    public final BukkitPopup getPopup() {
        return this.popup;
    }

    public void setPopup(BukkitPopup popup) {
        this.popup = popup;
    }

}
