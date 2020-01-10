package fr.triozer.mentionplayer.api.ui.builder;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.ui.ClickableItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cédric / Triozer
 */
public class InventoryBuilder {

    private String    name;
    private int       size;
    private Inventory inventory;

    private InventoryListener listener;

    private Map<Integer, ClickableItem> itemBySlot;
    private Map<ClickableItem, Integer> slotByItem;
    private boolean                     closeable;

    public InventoryBuilder(String name, int size, boolean closeable) {
        this.name = name;
        this.size = size;

        this.itemBySlot = new HashMap<>();
        this.slotByItem = new HashMap<>();

        this.closeable = closeable;

        this.inventory = Bukkit.createInventory(null, size, name);

        MentionPlayer.getInstance().getInventoriesList().add(this);
    }

    public InventoryBuilder(String name, InventoryType inventoryType, boolean closeable) {
        this.name = name;
        this.size = inventoryType.getDefaultSize();

        inventory = Bukkit.createInventory(null, inventoryType, name);

        this.itemBySlot = new HashMap<>();
        this.slotByItem = new HashMap<>();

        this.closeable = closeable;
    }

    public InventoryBuilder setItem(int slot, ClickableItem item) {
        inventory.setItem(slot, item.getItem());
        itemBySlot.put(slot, item);
        slotByItem.put(item, slot);

        return this;
    }

    public InventoryBuilder fill(ClickableItem item) {
        for (int i = 0; i < size; i++)
            setItem(i, item);

        return this;
    }

    public InventoryBuilder fillColumn(ClickableItem item, int column) {
        for (int i = column - 1; i < this.size; i += 9) {
            setItem(i, item);
        }

        return this;
    }

    public InventoryBuilder fillLine(ClickableItem item, int line) {
        for (int i = 0; i < 9; i++) {
            setItem(((line - 1) * 9) + i, item);
        }

        return this;
    }

    public InventoryBuilder fillBorder(ClickableItem item) {
        fillColumn(item, 1);
        fillColumn(item, 9);
        fillLine(item, 1);
        fillLine(item, this.size / 9);

        return this;
    }

    public void removeItem(int slot) {
        inventory.clear(slot);
        slotByItem.remove(itemBySlot.get(slot));
        itemBySlot.remove(slot);
    }

    public void removeItem(ClickableItem item) {
        inventory.remove(item.getItem());

        itemBySlot.remove(slotByItem.get(item));
        slotByItem.remove(item);
    }

    public void clear() {
        inventory.clear();
        itemBySlot.clear();
        slotByItem.clear();
    }

    public Inventory build() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public final Inventory getInventory() {
        return this.inventory;
    }

    public final boolean isCloseable() {
        return this.closeable;
    }

    public final InventoryListener getListener() {
        return this.listener;
    }

    public InventoryBuilder setListener(InventoryListener listener) {
        this.listener = listener;

        return this;
    }

    public final ClickableItem getContent(int slot) {
        return this.itemBySlot.get(slot);
    }

    public abstract static class InventoryListener {
        public abstract void interact(Player player, InventoryClickEvent event);

        public abstract void close(Player player, InventoryCloseEvent event);

        public abstract void drag(Player player, InventoryDragEvent event);
    }

}
