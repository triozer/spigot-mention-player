package fr.triozer.mentionplayer.api.ui.popup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.triozer.mentionplayer.MentionPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.UUID;

public class BukkitPopup {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final NamespacedKey id;
    private final TextComponent title;
    private final String        icon;
    private final int           data;

    public BukkitPopup(String title) {
        this(title, "book");
    }

    public BukkitPopup(String title, String icon) {
        this(title, icon, 0);
    }

    public BukkitPopup(String title, String icon, int data) {
        this.id = new NamespacedKey(MentionPlayer.getInstance(), "mentions/" + UUID.randomUUID());
        this.title = new TextComponent(title);
        this.icon = icon;
        this.data = data;
    }

    private static JsonElement getJsonFromComponent(TextComponent textComponent) {
        return gson.fromJson(ComponentSerializer.toString(textComponent), JsonElement.class);
    }

    private String getJSON() {
        JsonObject json = new JsonObject();
        JsonObject icon = new JsonObject();
        icon.addProperty("item", "minecraft:" + this.icon);
        if (this.data != 0) icon.addProperty("data", this.data);
        JsonObject display = new JsonObject();
        display.add("icon", icon);
        display.add("title", getJsonFromComponent(this.title));
        display.addProperty("description", "this is a description");
        display.addProperty("frame", "goal");
        display.addProperty("announce_to_chat", false);
        display.addProperty("show_toast", true);
        display.addProperty("hidden", true);
        JsonObject criteria = new JsonObject();
        JsonObject trigger  = new JsonObject();
        trigger.addProperty("trigger", "minecraft:impossible");
        criteria.add("mentioned", trigger);
        json.add("criteria", criteria);
        json.add("display", display);
        return gson.toJson(json);
    }

    public void show(JavaPlugin plugin, Player... players) {
        this.add();
        this.grant(players);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            this.revoke(players);
            this.remove();
        }, 20L);
    }

    private void add() {
        try {
            Bukkit.getUnsafe().loadAdvancement(this.id, this.getJSON());
        } catch (IllegalArgumentException var2) {
            MentionPlayer.LOG.error("    Error registering advancement " + this.id + " . It seems to already exist!");
        }
    }

    private void grant(Player... players) {
        Advancement advancement = this.getAdvancement();
        for (Player player : players) {
            if (!player.getAdvancementProgress(advancement).isDone()) {
                Collection<String> remainingCriteria = player.getAdvancementProgress(advancement).getRemainingCriteria();

                for (String remainingCriterion : remainingCriteria) {
                    player.getAdvancementProgress(this.getAdvancement()).awardCriteria(remainingCriterion);
                }
            }
        }
    }

    private void revoke(Player... players) {
        Advancement advancement = this.getAdvancement();
        for (Player player : players) {
            if (player.getAdvancementProgress(advancement).isDone()) {
                Collection<String> awardedCriteria = player.getAdvancementProgress(advancement).getAwardedCriteria();
                for (String awardedCriterion : awardedCriteria) {
                    player.getAdvancementProgress(this.getAdvancement()).revokeCriteria(awardedCriterion);
                }
            }
        }
    }

    private void remove() {
        Bukkit.getUnsafe().removeAdvancement(this.id);
    }

    public Advancement getAdvancement() {
        return Bukkit.getAdvancement(this.id);
    }

}
