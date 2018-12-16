package fr.triozer.mentionplayer.api.player;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.ui.popup.BukkitPopup;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import fr.triozer.mentionplayer.misc.Settings;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.*;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Cédric / Triozer
 */
public class MPlayer {
    private static Map<UUID, MPlayer> players = new HashMap<>();

    private       Map<String, Setting> settings;
    private final UUID                 uuid;
    private final ConfigurationSection data;

    private long      lastMessage;
    private ColorData color;
    private Sound     sound;
    private Set<UUID> ignoredPlayers;

    public static Map<UUID, MPlayer> getPlayers() {
        return players;
    }

    public MPlayer(ConfigurationSection data, UUID uuid, long lastMessage, ColorData color, Sound sound,
                   Set<String> ignored, Setting... settings) {
        this.data = data;
        this.uuid = uuid;
        this.settings = new HashMap<>();

        for (Setting setting : settings) {
            this.settings.put(setting.getKey(), setting);
        }

        this.lastMessage = lastMessage;
        this.color = color;
        this.sound = sound;
        this.ignoredPlayers = new HashSet<>();
        this.ignoredPlayers = ignored.stream()
                .collect(HashSet::new,
                        (list, element) -> list.add(UUID.fromString(element)),
                        (list, element) -> {
                        });
        players.put(uuid, this);
        this.save();
    }

    public static MPlayer get(UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else {
            Setting     a       = new Setting("sound");
            Setting     b       = new Setting("mention");
            Setting     c       = new Setting("action-bar");
            Setting     d       = new Setting("visible");
            Setting     e       = new Setting("popup");
            Set<String> ignored = new HashSet<>();

            long      lastMessage = 0L;
            ColorData color       = ColorData.get(MentionPlayer.getInstance().getConfig().getString("options.default.color"));
            Sound     sound       = Settings.getSound(false);

			ConfigurationSection section = MentionPlayer.getInstance().getData().getConfigurationSection(uuid.toString());
			if (section != null) {
				a = new Setting(section, "sound");
				b = new Setting(section, "mention");
				c = new Setting(section, "action-bar");
				d = new Setting(section, "visible");
				e = new Setting(section, "popup");
				lastMessage = section.getLong("last-message");
				color = ColorData.get(section.getString("color"));
				sound = Sound.valueOf(section.getString("notification").toUpperCase());
				ignored = new HashSet<>(section.getStringList("ignored"));
			} else {
				MentionPlayer.getInstance().getData().createSection(uuid.toString());
				section = MentionPlayer.getInstance().getData().getConfigurationSection(uuid.toString());
			}

            return new MPlayer(section, uuid, lastMessage, color, sound, ignored, a, b, c, d, e);
        }

    }

    public void setColor(ColorData newColor) {
        if (!this.canUseTag(newColor)) {
            this.getPlayer().sendMessage(this.get("messages.tag.cant-use").replace("{color}", newColor.parse(newColor.getName())));
        } else if (this.color == newColor) {
            this.getPlayer().sendMessage(this.get("messages.tag.already-use").replace("{color}", this.color.parse(this.color.getName())));
        } else {
            this.getPlayer().sendMessage(this.get("messages.color.change").replace("{last}", this.color.parse(this.color.getName())).replace("{new}", newColor.parse(newColor.getName())));
            this.color = newColor;
            this.save();
        }

    }

    public void setVisible(boolean value) {
        if (this.settings.get("visible").is() && value) {
            this.sendMessage("messages.visible.already.visible");
        } else if (!this.settings.get("visible").is() && !value) {
            this.sendMessage("messages.visible.already.hidden");
        } else {
            this.settings.get("visible").setState(value);
            this.sendMessage("messages.visible." + (this.settings.get("visible").is() ? "toggle-on" : "toggle-off"));
            this.save();
        }

    }

    public void setSound(boolean value) {
        if (this.settings.get("sound").is() && value) {
            this.sendMessage("errors.sound.already-on");
        } else if (!this.settings.get("sound").is() && !value) {
            this.sendMessage("errors.sound.already-off");
        } else {
            this.settings.get("sound").setState(value);
            this.sendMessage("messages.sound." + (this.settings.get("sound").is() ? "toggle-on" : "toggle-off"));
            this.save();
        }

    }

    public void setActionbar(boolean value) {
        if (this.settings.get("action-bar").is() && value) {
            this.sendMessage("messages.action-bar.already-on");
        } else if (!this.settings.get("action-bar").is() && !value) {
            this.sendMessage("messages.action-bar.already-off");
        } else {
            this.settings.get("action-bar").setState(value);
            this.sendMessage("messages.action-bar." + (this.settings.get("action-bar").is() ? "toggle-on" : "toggle-off"));
            this.save();
        }

    }

    public void setMention(boolean value) {
        if (this.settings.get("mention").is() && value) {
            this.sendMessage("messages.mention.already-on");
        } else if (!this.settings.get("mention").is() && !value) {
            this.sendMessage("messages.mention.already-off");
        } else {
            this.settings.get("mention").setState(value);
            this.sendMessage("messages.mention." + (this.settings.get("mention").is() ? "toggle-on" : "toggle-off"));
            this.save();
        }

    }

    public void setPopup(boolean value) {
        if (this.settings.get("popup").is() && value) {
            this.sendMessage("messages.popup.already-on");
        } else if (!this.settings.get("popup").is() && !value) {
            this.sendMessage("messages.popup.already-off");
        } else {
            this.settings.get("popup").setState(value);
            this.sendMessage("messages.popup." + (this.settings.get("popup").is() ? "toggle-on" : "toggle-off"));
            this.save();
        }
    }

    public BukkitPopup createBukkitPopup(MPlayer sender) {
        String icon = MentionPlayer.getInstance().getConfig().getString("options.popup.icon.minecraft");
        String line1 = ChatColor.translateAlternateColorCodes('&', MentionPlayer.getInstance().getConfig()
                .getString("options.popup.title.1")
                .replace("{who}", sender.getPlayer().getName())
                .replace("{player}", this.getPlayer().getName()));
        String line2 = ChatColor.translateAlternateColorCodes('&', MentionPlayer.getInstance().getConfig()
                .getString("options.popup.title.2").replace("{who}", sender.getPlayer().getName()).replace("{player}", this.getPlayer().getName()));
        String line3 = ChatColor.translateAlternateColorCodes('&', MentionPlayer.getInstance().getConfig()
                .getString("options.popup.title.3").replace("{who}", sender.getPlayer().getName()).replace("{player}", this.getPlayer().getName()));
        boolean fit   = MentionPlayer.getInstance().getConfig().getBoolean("options.popup.allow-out-of-boxes");
        String  title = line1 + "\n" + line2 + "\n" + line3;
        if (ChatColor.stripColor(title).replace("\n", "").length() > 60 && fit) {
            MentionPlayer.LOG.error("The plugin can't send popup notification to " + AQUA + this.getPlayer().getName() + DARK_RED + " because the content length is too long for the box.");
            MentionPlayer.LOG.fine(GRAY + "You can hide this message by setting the value of '" + AQUA + "options.popup.allow-out-of-boxes" + GRAY + "' to " + GREEN + "true" + GRAY + " in the configuration file.");
            return null;
        }

		if (MentionPlayer.getInstance().getConfig().contains("options.popup.icon.data"))
            return new BukkitPopup(title, icon, MentionPlayer.getInstance().getConfig().getInt("options.popup.icon.data"));
        else
        	return new BukkitPopup(title, icon);
    }

    public void sendCurrentColor() {
        this.getPlayer().sendMessage(this.get("messages.color.current").replace("{color}", this.color.parse(this.color.getName())));
    }

    public void sendCantUse() {
        this.sendMessage("errors.feature.disabled");
    }

    public void sendSpamError() {
        this.sendMessage("errors.spam");
    }

    public void save() {
        if (Settings.canSQL() && MentionPlayer.getInstance().getDatabase() != null && MentionPlayer.getInstance().getDatabase().isConnected()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(MentionPlayer.getInstance(),
                    () -> MentionPlayer.getInstance().getDatabase().save(this.uuid, this), 0L);
        }

        this.data.set("sound", this.settings.get("sound").is());
        this.data.set("mention", this.settings.get("mention").is());
        this.data.set("action-bar", this.settings.get("action-bar").is());
        this.data.set("visible", this.settings.get("visible").is());
        this.data.set("popup", this.settings.get("popup").is());
        this.data.set("last-message", this.lastMessage);
        if (this.color != null) {
            this.data.set("color", this.color.getID());
        } else {
            this.data.set("color", ColorData.get(MentionPlayer.getInstance().getConfig().getString("options.default.color")).getID());
        }
        this.data.set("notification", this.sound.name());
        this.data.set("ignored", ignoredPlayers.stream().collect(
                ArrayList::new,
                (list, id) -> list.add(id.toString()), (list, id) -> {
                }));

        try {
            MentionPlayer.getInstance().getData().save(MentionPlayer.getInstance().getDataFile());
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public void load() {
        ConfigurationSection section = MentionPlayer.getInstance().getData().getConfigurationSection(this.getPlayer().getUniqueId().toString());
        if (Settings.canSQL() && MentionPlayer.getInstance().getDatabase() != null && MentionPlayer.getInstance().getDatabase().isConnected()) {
            Bukkit.getScheduler().runTaskAsynchronously(MentionPlayer.getInstance(),
                    () -> {
                        long lastMessage = MentionPlayer.getInstance().getDatabase().getLastMessageOf(this.uuid);

                        if (lastMessage >= section.getLong("last-message")) {
                            this.settings = MentionPlayer.getInstance().getDatabase().getSettingsOf(this.uuid);
                            this.lastMessage = lastMessage;
                            this.color = MentionPlayer.getInstance().getDatabase().getColorOf(this.uuid);
                            this.sound = MentionPlayer.getInstance().getDatabase().getSoundOf(this.uuid);
                            this.ignoredPlayers =
                                    MentionPlayer.getInstance().getDatabase().getIgnoredPlayers(this.uuid)
                                            .stream().collect(HashSet::new,
                                            (list, element) -> list.add(UUID.fromString(element)),
                                            (list, element) -> {
                                            });
                        } else {
                            loadFromData(section);
                        }
                    });
        } else loadFromData(section);
    }

    private void loadFromData(ConfigurationSection section) {
        if (section == null) return;
        this.settings.replace("sound", new Setting(section, "sound"));
        this.settings.replace("mention", new Setting(section, "mention"));
        this.settings.replace("action-bar", new Setting(section, "action-bar"));
        this.settings.replace("visible", new Setting(section, "visible"));
        this.settings.replace("popup", new Setting(section, "popup"));
        this.lastMessage = section.getLong("last-message");
        this.color = ColorData.get(section.getString("color"));
        try {
            this.sound = Sound.valueOf(section.getString("notification").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.sound = Settings.getSound(false);
        }
        if (this.color == null || this.color.getID() == null) {
            this.data.set("color",
                    ColorData.get(MentionPlayer.getInstance().getConfig().getString("options.default.color")).getID());
            this.save();
            this.color = ColorData.get(section.getString("color"));
        }
    }

    public void sendMessage(String path) {
        this.getPlayer().sendMessage(this.get(path));
    }

    public String get(String path) {
        return ChatColor.translateAlternateColorCodes('&', MentionPlayer.getInstance().getMessages().getString(path));
    }

    public final long getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(long lastMessage) {
        this.lastMessage = lastMessage;
        this.save();
    }

    public void ignore(Player player) {
        if (this.ignoredPlayers.contains(player.getUniqueId())) {
            this.ignoredPlayers.remove(player.getUniqueId());
            this.getPlayer().sendMessage(get("messages.ignore.remove").replace("{player}", player.getName()));
        } else {
            this.ignoredPlayers.add(player.getUniqueId());
            this.getPlayer().sendMessage(get("messages.ignore.add").replace("{player}", player.getName()));
        }

        this.save();
    }

    public final Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public final boolean allowMention() {
        return this.settings.get("mention").is();
    }

    public final boolean allowSound() {
        return this.settings.get("sound").is();
    }

    public final boolean allowPopup() {
        return this.settings.get("popup").is();
    }

    public final boolean allowActionbar() {
        return this.settings.get("action-bar").is();
    }

    public final boolean isMentionPublic() {
        return this.settings.get("visible").is();
    }

    public final ColorData getColor() {
        return this.color != null && this.color.getID() != null ?
                this.color : ColorData.get(MentionPlayer.getInstance().getConfig().getString("options.default.color"));
    }

    public final Sound getSound() {
        return this.sound;
    }

    public void setSound(Sound sound) {
        if (this.sound == sound) {
            this.getPlayer().sendMessage(this.get("messages.sound.already.use").replace("{sound}", this.sound.name()));
        } else {
            this.getPlayer().sendMessage(this.get("messages.sound.change").replace("{last}", this.sound.name()).replace("{new}", sound.name()));
            this.sound = sound;
            this.save();
        }
    }

    public final boolean canBypassMention() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.bypass.mention"));
    }

    public final boolean canBypassSound() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.bypass.sound"));
    }

    public final boolean canBypassActionBar() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.bypass.action-bar"));
    }

    public final boolean canBypassAntiSpam() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.bypass.anti-spam"));
    }

    public final boolean canBypassPopup() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.bypass.popup"));
    }

    public final boolean canUseTag() {
        return this.getPlayer().hasPermission(MentionPlayer.getInstance().getConfig().getString("options.permission.color.use"));
    }

    public final boolean canUseTag(ColorData colorData) {
        return this.getPlayer().hasPermission(colorData.getPermission());
    }

    public final Set<UUID> getIgnoredPlayers() {
        return this.ignoredPlayers;
    }

}
