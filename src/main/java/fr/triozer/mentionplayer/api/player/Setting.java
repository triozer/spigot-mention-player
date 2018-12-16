package fr.triozer.mentionplayer.api.player;

import fr.triozer.mentionplayer.MentionPlayer;
import org.bukkit.configuration.ConfigurationSection;

public class Setting {
    private final String  key;
    private       boolean state;

    public Setting(String key) {
        this(MentionPlayer.getInstance().getConfig().getConfigurationSection("options.default"), key);
    }

    public Setting(ConfigurationSection section, String key) {
        this.key = key;
        this.state = section.getBoolean(key);
    }

    public Setting(String key, Boolean value) {
        this.key = key;
        this.state = value;
    }

    public final String getKey() {
        return this.key;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public final boolean is() {
        return this.state;
    }
}
