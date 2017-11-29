package fr.triozer.mentionplayer.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.triozer.mentionplayer.misc.MPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * @author Cédric / Triozer
 */
public class OptionUI implements InventoryProvider {
    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("menu")
            .provider(new OptionUI())
            .size(3, 9)
            .title(ChatColor.BLUE + "» OPTIONS")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15)));

        MPlayer mPlayer = MPlayer.get(player);

        ItemStack sound = new ItemBuilder(Material.INK_SACK)
                .name("§r§6Sound")
                .lore("", "§7» " + (mPlayer.isSoundable() ? "§cDeactivate" : "§aActivate") + " mentions sound.", "")
                .durability((mPlayer.isSoundable() ? 10 : 8))
                .build();

        ItemStack mention = new ItemBuilder(Material.INK_SACK)
                .name("§r§6Mention")
                .lore("", "§7» " + (mPlayer.isMentionable() ? "§cDeactivate" : "§aActivate") + " mentions.", "")
                .durability((mPlayer.isMentionable() ? 10 : 8))
                .build();

        ItemStack action = new ItemBuilder(Material.INK_SACK)
                .name("§r§6Actionbar")
                .lore("", "§7» " + (mPlayer.canReceiveActionBar() ? "§cDeactivate" : "§aActivate") + " actionbar's notifications.", "")
                .durability(mPlayer.canReceiveActionBar() ? 10 : 8)
                .build();

        contents.set(1, 3, ClickableItem.of(sound,
                e -> {
                    if (mPlayer.isSoundable()) mPlayer.disableSound();
                    else mPlayer.enableSound();

                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isSoundable() ? "§cDeactivate" : "§aActivate") + " mentions sound.", ""));
                    e.getCurrentItem().setItemMeta(meta);
                    e.getCurrentItem().setDurability((short) (mPlayer.isSoundable() ? 10 : 8));
                }));

        contents.set(1, 4, ClickableItem.of(mention,
                e -> {
                    if (mPlayer.isMentionable()) mPlayer.disableMention();
                    else mPlayer.enableMention();

                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isMentionable() ? "§cDeactivate" : "§aActivate") + " mentions.", ""));
                    e.getCurrentItem().setItemMeta(meta);
                    e.getCurrentItem().setDurability((short) (mPlayer.isMentionable() ? 10 : 8));
                }));

        contents.set(1, 5, ClickableItem.of(action,
                e -> {
                    if (mPlayer.canReceiveActionBar()) mPlayer.disableActionBar();
                    else mPlayer.enableActionBar();

                    ItemMeta meta = e.getCurrentItem().getItemMeta();
                    meta.setLore(Arrays.asList("", "§7» " + (mPlayer.canReceiveActionBar() ? "§cDeactivate" : "§aActivate") + " actionbar's notifications.", ""));
                    e.getCurrentItem().setItemMeta(meta);
                    e.getCurrentItem().setDurability((short) (mPlayer.canReceiveActionBar() ? 10 : 8));
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }

}
