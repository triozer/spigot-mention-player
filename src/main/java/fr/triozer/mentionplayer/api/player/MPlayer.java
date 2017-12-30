package fr.triozer.mentionplayer.api.player;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.misc.ColorData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Cédric / Triozer
 */
public class MPlayer {

    private static Map<UUID, MPlayer> players = new HashMap<>();

    private final UUID uuid;

    private boolean receiveSound;
    private boolean receiveMention;
    private boolean actionBar;
    private long    lastMessage;

    private ColorData color;

    private MPlayer(Player player, boolean receiveSound, boolean receiveMention, boolean actionBar, long lastMessage, ColorData color) {
        this.uuid = player.getUniqueId();

        this.receiveSound = receiveSound;
        this.receiveMention = receiveMention;
        this.actionBar = actionBar;
        this.lastMessage = lastMessage;
        this.color = color;

        players.put(player.getUniqueId(), this);

        this.save();
    }

    public static MPlayer get(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            return players.get(player.getUniqueId());
        } else {
            boolean   a           = MentionPlayer.getInstance().getConfig().getBoolean("option.default.sound");
            boolean   b           = MentionPlayer.getInstance().getConfig().getBoolean("option.default.mention");
            boolean   c           = MentionPlayer.getInstance().getConfig().getBoolean("option.default.action-bar");
            long      lastMessage = 0L;
            ColorData color       = ColorData.get(MentionPlayer.getInstance().getConfig().getString("option.default.color"));

            if (MentionPlayer.getInstance().getData().contains("" + player.getUniqueId())) {
                a = MentionPlayer.getInstance().getData().getBoolean(player.getUniqueId() + ".sound");
                b = MentionPlayer.getInstance().getData().getBoolean(player.getUniqueId() + ".mention");
                c = MentionPlayer.getInstance().getData().getBoolean(player.getUniqueId() + ".action-bar");
                lastMessage = MentionPlayer.getInstance().getData().getLong(player.getUniqueId() + ".last-message");
                color = ColorData.get(MentionPlayer.getInstance().getData().getString(player.getUniqueId() + ".color"));
            }

            return new MPlayer(player, a, b, c, lastMessage, color);
        }
    }

    public void changeColor(ColorData newColor) {
        if (!canUse(newColor)) return;

        getPlayer().sendMessage(get("message.color")
                .replace("{last}", color == ColorData.RAINBOW ? color.rainbow(this.color.getID()) : color.getChatColor() + color.getID())
                .replace("{new}", newColor == ColorData.RAINBOW ? color.rainbow(newColor.getID()) : newColor.getChatColor() + newColor.getID()));

        this.color = newColor;
    }

    public void enableSound() {
        if (isSoundable()) {
            sendMessage("message.error.sound-already-on");

            return;
        }

        this.receiveSound = true;
        sendMessage("message.sound-on");
        save();
    }

    public void disableSound() {
        if (!isSoundable()) {
            sendMessage("message.error.sound-already-off");

            return;
        }

        this.receiveSound = false;
        sendMessage("message.sound-off");
        save();
    }

    public void enableActionBar() {
        if (canReceiveActionBar()) {
            sendMessage("message.error.action-bar-already-on");

            return;
        }

        this.actionBar = true;
        sendMessage("message.action-bar-on");
        save();
    }

    public void disableActionBar() {
        if (!canReceiveActionBar()) {
            sendMessage("message.error.action-bar-already-off");

            return;
        }

        this.actionBar = false;
        sendMessage("message.action-bar-off");
        save();
    }

    public void enableMention() {
        if (isMentionable()) {
            sendMessage("message.error.already-on");

            return;
        }

        this.receiveMention = true;
        sendMessage("message.toggle-on");
        save();
    }

    public void disableMention() {
        if (!isMentionable()) {
            sendMessage("message.error.already-off");

            return;
        }

        this.receiveMention = false;
        sendMessage("message.toggle-off");
        save();
    }

    public void spam() {
        sendMessage("message.error.spam");
    }

    private void save() {
        MentionPlayer.getInstance().getData().set(this.uuid + ".sound", this.receiveSound);
        MentionPlayer.getInstance().getData().set(this.uuid + ".mention", this.receiveMention);
        MentionPlayer.getInstance().getData().set(this.uuid + ".action-bar", this.actionBar);
        MentionPlayer.getInstance().getData().set(this.uuid + ".last-message", this.lastMessage);
        MentionPlayer.getInstance().getData().set(this.uuid + ".color", this.color.getID());

        try {
            MentionPlayer.getInstance().getData().save(MentionPlayer.getInstance().getDataFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String path) {
        getPlayer().sendMessage(get(path));
    }

    private String get(String path) {
        return MentionPlayer.getInstance().getConfig().getString(path).replaceAll("&", "§");
    }

    public final long getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
        save();
    }

    public final Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public final boolean isMentionable() {
        return this.receiveMention;
    }

    public final boolean isSoundable() {
        return this.receiveSound;
    }

    public final boolean canReceiveActionBar() {
        return this.actionBar;
    }

    public final ColorData getColor() {
        return this.color;
    }

    public final boolean canBypassMention() {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.bypass-mention"));
    }

    public final boolean canBypassSound() {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.bypass-sound"));
    }

    public final boolean canBypassActionBar() {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.bypass-action-bar"));
    }

    public final boolean canBypassAntiSpam() {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.bypass-anti-spam"));
    }

    public final boolean canColor() {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.color.use"));
    }

    public final boolean canUse(ColorData colorData) {
        return getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("option.permission.color." +
                colorData.getID().toLowerCase().replaceAll(" ", "-")));
    }

}
