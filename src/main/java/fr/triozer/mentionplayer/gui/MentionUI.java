package fr.triozer.mentionplayer.gui;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.ClickableItem;
import fr.triozer.mentionplayer.api.ui.InventoryBuilder;
import fr.triozer.mentionplayer.api.ui.ItemBuilder;
import fr.triozer.mentionplayer.misc.ColorData;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Cédric / Triozer
 */
public class MentionUI {
    public static final ClickableItem EMPTY = ClickableItem
            .empty(new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15));

    public static void open(Player player) {
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

        ItemBuilder color = new ItemBuilder(Material.WOOL)
                .name("§r§6Color")
                .lore("", "§7» Choose your mention color.", "");

        InventoryBuilder contents = new InventoryBuilder("§b» Options", 3 * 9, true)
                .fill(EMPTY)
                .setItem(12, ClickableItem.of(sound,
                        event -> {
                            if (mPlayer.isSoundable()) mPlayer.disableSound();
                            else mPlayer.enableSound();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isSoundable() ? "§cDeactivate" : "§aActivate") + " mentions sound.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.isSoundable() ? 10 : 8));
                        }))
                .setItem(13, ClickableItem.of(mention,
                        event -> {
                            if (mPlayer.isMentionable()) mPlayer.disableMention();
                            else mPlayer.enableMention();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isMentionable() ? "§cDeactivate" : "§aActivate") + " mentions.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.isMentionable() ? 10 : 8));
                        }))
                .setItem(14, ClickableItem.of(action,
                        event -> {
                            if (mPlayer.canReceiveActionBar()) mPlayer.disableActionBar();
                            else mPlayer.enableActionBar();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.canReceiveActionBar() ? "§cDeactivate" : "§aActivate") + " actionbar's notifications.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.canReceiveActionBar() ? 10 : 8));
                        }));

        if (mPlayer.canColor()) {

            if (mPlayer.getColor() != ColorData.RAINBOW)
                contents.setItem(16, ClickableItem.of(color
                        .durability(mPlayer.getColor().getDyeColor().getWoolData())
                        .build(), event -> openColor(mPlayer)));
            else {
                new BukkitRunnable() {
                    Random random = new Random();

                    @Override
                    public void run() {
                        if (player.getPlayer() == null || !player.getPlayer().isOnline()) cancel();

                        if (player.getPlayer().getOpenInventory().getTopInventory().equals(contents.build())) {
                            contents.setItem(16, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                            .name("§r§6Color")
                                            .lore("", "§7» Choose your mention color.", "")
                                            .durability(DyeColor.values()[random.nextInt(16)].getWoolData()),
                                    event -> {
                                        openColor(mPlayer);
                                        cancel();
                                    }));
                        } else cancel();
                    }
                }.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);

            }
        }

        player.openInventory(contents.build());
    }

    private static void openColor(MPlayer player) {

        ItemStack back = new ItemBuilder(Material.ARROW)
                .name("§r§6Back")
                .lore("", "§7» Return to options menu.", "")
                .build();

        InventoryBuilder color = new InventoryBuilder("§b» Color", 3 * 9, true)
                .fill(EMPTY)
                .setItem(26, ClickableItem.of(back, event -> open(player.getPlayer())));

        for (int i = 0; i < ColorData.values().length; i++) {
            ColorData colorData = ColorData.values()[i];

            if (!player.canUse(colorData)) continue;

            if (colorData == ColorData.RAINBOW) {
                int finalI = i;
                new BukkitRunnable() {
                    Random random = new Random();

                    @Override
                    public void run() {
                        if (player.getPlayer() == null || !player.getPlayer().isOnline()) cancel();

                        if (player.getPlayer().getOpenInventory().getTopInventory().equals(color.build())) {
                            color.setItem(finalI, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                            .name(colorData.rainbow(colorData.getID()))
                                            .durability(DyeColor.values()[random.nextInt(16)].getWoolData()),
                                    event -> {
                                        player.changeColor(colorData);
                                        openColor(player);
                                        cancel();
                                    }));
                        } else cancel();
                    }
                }.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);

            } else if (player.getColor() == colorData)
                color.setItem(i, ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(colorData.getChatColor() + colorData.getID())
                        .lore("", "§7» This is your actual tag color.", "")
                        .durability(colorData.getDyeColor().getWoolData())));
            else {
                color.setItem(i, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                .name(colorData.getChatColor() + colorData.getID())
                                .durability(colorData.getDyeColor().getWoolData()),
                        event -> {
                            player.changeColor(colorData);
                            openColor(player);
                        }
                ));
            }
        }

        player.getPlayer().openInventory(color.build());

    }

}
