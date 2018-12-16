package fr.triozer.mentionplayer.api.ui;

import fr.triozer.mentionplayer.api.ui.builder.ItemBuilder;
import fr.triozer.mentionplayer.misc.XMaterial;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * @author Cédric / Triozer
 */
public class ClickableItem {

    public static final ClickableItem EMPTY = ClickableItem
            .empty(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial()).name(" ").durability(15));

    private ItemStack                     item;
    private Consumer<InventoryClickEvent> consumer;

    private ClickableItem(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        this.item = item;
        this.consumer = consumer;
    }

    public static ClickableItem empty(ItemStack item) {
        return of(item, e -> {
        });
    }

    public static ClickableItem empty(ItemBuilder item) {
        return of(item, e -> {
        });
    }

    public static ClickableItem of(ItemStack item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item, consumer);
    }

    public static ClickableItem of(ItemBuilder item, Consumer<InventoryClickEvent> consumer) {
        return new ClickableItem(item.build(), consumer);
    }

    public void accept(InventoryClickEvent e) {
        if (this.consumer != null) this.consumer.accept(e);
    }

    public ItemStack getItem() {
        return item;
    }

}
