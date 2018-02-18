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

        ItemStack visible = new ItemBuilder(Material.INK_SACK)
                .name("§r§6Visible")
                .lore("", "§7» " + (mPlayer.isVisible() ? "§cDeactivate" : "§aActivate") + " visibility of your tag.", "(only for other player)", "")
                .durability(mPlayer.isVisible() ? 10 : 8)
                .build();

        ItemBuilder color = new ItemBuilder(Material.WOOL)
                .name("§r§6Color")
                .lore("", "§7» Choose your mention color.", "");

        InventoryBuilder contents = new InventoryBuilder("§b» Options", 3 * 9, true)
                .fill(EMPTY)
                .setItem(11, ClickableItem.of(sound,
                        event -> {
                            if (mPlayer.isSoundable()) mPlayer.disableSound();
                            else mPlayer.enableSound();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isSoundable() ? "§cDeactivate" : "§aActivate") + " mentions sound.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.isSoundable() ? 10 : 8));
                        }))
                .setItem(12, ClickableItem.of(mention,
                        event -> {
                            if (mPlayer.isMentionable()) mPlayer.disableMention();
                            else mPlayer.enableMention();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isMentionable() ? "§cDeactivate" : "§aActivate") + " mentions.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.isMentionable() ? 10 : 8));
                        }))
                .setItem(13, ClickableItem.of(action,
                        event -> {
                            if (mPlayer.canReceiveActionBar()) mPlayer.disableActionBar();
                            else mPlayer.enableActionBar();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.canReceiveActionBar() ? "§cDeactivate" : "§aActivate") + " actionbar's notifications.", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.canReceiveActionBar() ? 10 : 8));
                        }))
                .setItem(14, ClickableItem.of(visible,
                        event -> {
                            if (mPlayer.isVisible()) mPlayer.hideForAll();
                            else mPlayer.showForAll();

                            ItemMeta meta = event.getCurrentItem().getItemMeta();
                            meta.setLore(Arrays.asList("", "§7» " + (mPlayer.isVisible() ? "§cDeactivate" : "§aActivate") + " visibility of your tag.", "(only for other player)", ""));
                            event.getCurrentItem().setItemMeta(meta);
                            event.getCurrentItem().setDurability((short) (mPlayer.isVisible() ? 10 : 8));
                        }));

        if (mPlayer.canColor()) {
            if (mPlayer.getColor() == ColorData.RAINBOW || mPlayer.getColor().isCustom()) {
                // hack this shit x)
                final int[] tries = {0};
                new BukkitRunnable() {
                    Random random = new Random();

                    @Override
                    public void run() {
                        if (player.getPlayer() == null || !player.getPlayer().isOnline()) cancel();

                        if (player.getPlayer().getOpenInventory().getTopInventory().equals(contents.build())) {
                            contents.setItem(16, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                            .name("§r§6Color")
                                            .lore("", "§7» Choose your mention color.", "")
                                            .durability((mPlayer.getColor().isCustom() ?
                                                    mPlayer.getColor().getDyeColor()[tries[0]++] :
                                                    DyeColor.values()[random.nextInt(16)]
                                            ).getWoolData()),

                                    event -> {
                                        openColor(mPlayer);
                                        cancel();
                                    }));

                            if (tries[0] == mPlayer.getColor().getDyeColor().length) tries[0] = 0;
                        } else cancel();
                    }
                }.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);

            } else contents.setItem(16, ClickableItem.of(color
                    .durability(mPlayer.getColor().getDyeColor()[0].getWoolData())
                    .build(), event -> openColor(mPlayer)));
        }

        player.openInventory(contents.build());
    }

    private static void openColor(MPlayer player) {
        ItemStack back = new ItemBuilder(Material.ARROW)
                .name("§r§6Back")
                .lore("", "§7» Return to options menu.", "")
                .build();

        int size = Math.round((float) MentionPlayer.getInstance().getColors().size() / 9f) * 9 + 9;
        System.out.println(size);
        InventoryBuilder color = new InventoryBuilder("§b» Color",
                size, true)
                .fill(EMPTY)
                .setItem(size - 1, ClickableItem.of(back, event -> open(player.getPlayer())));

        int slot = 0;
        for (ColorData colorData : MentionPlayer.getInstance().getColors().values()) {
            if (!player.canUse(colorData)) continue;

            if (colorData == ColorData.RAINBOW || colorData.isCustom()) {
                int finalI = slot;
                // hack this shit x)
                final int[] tries = {0};
                new BukkitRunnable() {
                    Random random = new Random();

                    @Override
                    public void run() {
                        if (player.getPlayer() == null || !player.getPlayer().isOnline()) cancel();

                        if (player.getPlayer().getOpenInventory().getTopInventory().equals(color.build())) {
                            color.setItem(finalI, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                            .name(colorData.parse(colorData.getName()))
                                            .durability((colorData.isCustom() ?
                                                    colorData.getDyeColor()[tries[0]++]
                                                    : DyeColor.values()[random.nextInt(16)]
                                            ).getWoolData()),
                                    event -> {
                                        player.changeColor(colorData);
                                        openColor(player);
                                        cancel();
                                    }));
                            if (tries[0] == colorData.getDyeColor().length) tries[0] = 0;
                        } else cancel();
                    }
                }.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);

            } else if (player.getColor() == colorData)
                color.setItem(slot, ClickableItem.empty(new ItemBuilder(Material.STAINED_GLASS_PANE)
                        .name(colorData.parse(colorData.getName()))
                        .lore("", "§7» This is your actual tag color.", "")
                        .durability(colorData.getDyeColor()[0].getWoolData())));
            else {
                color.setItem(slot, ClickableItem.of(new ItemBuilder(Material.WOOL)
                                .name(colorData.parse(colorData.getName()))
                                .durability(colorData.getDyeColor()[0].getWoolData()),
                        event -> {
                            player.changeColor(colorData);
                            openColor(player);
                        }
                ));
            }
            slot++;
        }

        player.getPlayer().openInventory(color.build());

    }

}
