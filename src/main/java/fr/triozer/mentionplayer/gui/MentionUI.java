package fr.triozer.mentionplayer.gui;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.ClickableItem;
import fr.triozer.mentionplayer.api.ui.builder.InventoryBuilder;
import fr.triozer.mentionplayer.api.ui.builder.ItemBuilder;
import fr.triozer.mentionplayer.api.ui.color.ColorData;
import fr.triozer.mentionplayer.misc.Settings;
import fr.triozer.mentionplayer.misc.XMaterial;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Cédric / Triozer
 */
public class MentionUI {
	public static void open(Player player) {
		MPlayer          mPlayer  = MPlayer.get(player.getUniqueId());
		InventoryBuilder contents = new InventoryBuilder(AQUA + "- Settings", 36, true).fill(ClickableItem.EMPTY);
		ItemStack        sounds   = new ItemBuilder(XMaterial.NOTE_BLOCK.parseMaterial()).name(ChatColor.GOLD + "Sounds").lore("", AQUA + "- " + GRAY + "Change your mention sound.", "").build();
		ItemStack        ignored  = new ItemBuilder(XMaterial.BARRIER.parseMaterial()).name(ChatColor.GOLD + "Ignored players").lore("", AQUA + "- " + GRAY + "Manage ignored players.", "").build();

		if (mPlayer.canUseTag()) {
			ItemBuilder color = new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial()).name(ChatColor.GOLD + "Color")
					.lore("", AQUA + "- " + GRAY + "Choose your mention color.", "");
			contents.setItem(20, ClickableItem.of(sounds, (event) -> openSound(mPlayer, true)));
			contents.setItem(21, ClickableItem.of(ignored, (event) -> openIgnored(mPlayer, true)));
			if (mPlayer.getColor() != ColorData.RAINBOW && !mPlayer.getColor().isCustom()) {
				contents.setItem(19, ClickableItem.of(color.durability(mPlayer.getColor().getDyeColor()[0].getWoolData()).build(),
						(event) -> openColor(mPlayer, true)));
			} else {
				final int[] tries = {0};
				new BukkitRunnable() {
					Random random = new Random();

					@Override
					public void run() {
						if (player.getPlayer() == null || !player.getPlayer().isOnline()) cancel();

						if (player.getPlayer().getOpenInventory().getTopInventory().equals(contents.build())) {
							DyeColor var1;
							if (mPlayer.getColor().isCustom()) {
								DyeColor[] var10003 = mPlayer.getColor().getDyeColor();
								int        var10007 = tries[0];
								int        var10004 = tries[0];
								tries[0]++;
								var1 = var10003[var10004];
							} else {
								var1 = DyeColor.values()[this.random.nextInt(16)];
							}

							contents.setItem(19, ClickableItem.of(color.durability(var1.getWoolData()), (event) -> {
								MentionUI.openColor(mPlayer, true);
								this.cancel();
							}));
							if (tries[0] == mPlayer.getColor().getDyeColor().length) {
								tries[0] = 0;
							}
						} else {
							this.cancel();
						}
					}
				}.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);
			}
		} else {
			contents.setItem(19, ClickableItem.of(sounds, (event) -> openSound(mPlayer, true)));
			contents.setItem(20, ClickableItem.of(ignored, (event) -> openIgnored(mPlayer, true)));
		}

		setContents(contents, mPlayer, 10);
		player.openInventory(contents.build());
	}


	private static void setContents(InventoryBuilder inventoryBuilder, MPlayer mPlayer, int firstSlot) {
		ItemStack sound = new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
				.name(ChatColor.GOLD + "Sound").lore("", AQUA + "- " + (mPlayer.allowSound() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " mentions sound.", "")
				.durability(mPlayer.allowSound() ? 10 : 8).build();
		ItemStack mention = new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
				.name(ChatColor.GOLD + "Mention").lore("", AQUA + "- " + (mPlayer.allowMention() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " mentions.", "")
				.durability(mPlayer.allowMention() ? 10 : 8).build();
		ItemStack action = new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
				.name(ChatColor.GOLD + "Actionbar").lore("", AQUA + "- " + (mPlayer.allowActionbar() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " actionbar's notifications.", "")
				.durability(mPlayer.allowActionbar() ? 10 : 8).build();
		ItemStack visible = new ItemBuilder(XMaterial.INK_SAC.parseMaterial())
				.name(ChatColor.GOLD + "Public").lore("", AQUA + "- " + (mPlayer.isMentionPublic() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " visibility of your tag.", ChatColor.DARK_GRAY + "(only for other player)", "")
				.durability(mPlayer.isMentionPublic() ? 10 : 8).build();

		inventoryBuilder.setItem(firstSlot++, ClickableItem.of(sound, (event) -> {
			mPlayer.setSound(!mPlayer.allowSound());
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setLore(Arrays.asList("", AQUA + "- " + (mPlayer.allowSound() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " mentions sound.", ""));
			event.getCurrentItem().setItemMeta(meta);
			event.getCurrentItem().setDurability((short) (mPlayer.allowSound() ? 10 : 8));
			open(mPlayer.getPlayer());
		})).setItem(firstSlot++, ClickableItem.of(mention, (event) -> {
			mPlayer.setMention(!mPlayer.allowMention());
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setLore(Arrays.asList("", AQUA + "- " + (mPlayer.allowMention() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " mentions.", ""));
			event.getCurrentItem().setItemMeta(meta);
			event.getCurrentItem().setDurability((short) (mPlayer.allowMention() ? 10 : 8));
			open(mPlayer.getPlayer());
		})).setItem(firstSlot++, ClickableItem.of(action, (event) -> {
			mPlayer.setActionbar(!mPlayer.allowActionbar());
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setLore(Arrays.asList("", AQUA + "- " + (mPlayer.allowActionbar() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " actionbar's notifications.", ""));
			event.getCurrentItem().setItemMeta(meta);
			event.getCurrentItem().setDurability((short) (mPlayer.allowActionbar() ? 10 : 8));
			open(mPlayer.getPlayer());
		})).setItem(firstSlot++, ClickableItem.of(visible, (event) -> {
			mPlayer.setVisible(!mPlayer.isMentionPublic());
			ItemMeta meta = event.getCurrentItem().getItemMeta();
			meta.setLore(Arrays.asList("", AQUA + "- " + (mPlayer.isMentionPublic() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " visibility of your tag.", "(only for other player)", ""));
			event.getCurrentItem().setItemMeta(meta);
			event.getCurrentItem().setDurability((short) (mPlayer.isMentionPublic() ? 10 : 8));
			open(mPlayer.getPlayer());
		}));
		if (Settings.canPopup()) {
			ItemStack popup = (new ItemBuilder(XMaterial.INK_SAC.parseMaterial())).name(ChatColor.GOLD + "Popup").lore("", AQUA + "- " + (mPlayer.allowPopup() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " popup's notifications.", "").durability(mPlayer.allowPopup() ? 10 : 8).build();
			inventoryBuilder.setItem(firstSlot, ClickableItem.of(popup, (event) -> {
				mPlayer.setPopup(!mPlayer.allowPopup());
				ItemMeta meta = event.getCurrentItem().getItemMeta();
				meta.setLore(Arrays.asList("", AQUA + "- " + (mPlayer.allowPopup() ? ChatColor.RED + "Disable" : ChatColor.GREEN + "Enable") + " popup's notifications.", ""));
				event.getCurrentItem().setItemMeta(meta);
				event.getCurrentItem().setDurability((short) (mPlayer.allowPopup() ? 10 : 8));
				open(mPlayer.getPlayer());
			}));
		}
	}

	public static void openColor(MPlayer player, boolean fromGui) {
		ItemStack back = null;
		if (fromGui)
			back = new ItemBuilder(Material.ARROW).name(ChatColor.GOLD + "Back").lore("", ChatColor.AQUA + "- " + ChatColor.GRAY + "Return to options menu.", "").build();

		int size = Math.round((float) MentionPlayer.getInstance().getColorData().size() / 9.0F) * 9 + 9;
		InventoryBuilder color = new InventoryBuilder(ChatColor.AQUA + "- Color", size, true)
				.fill(ClickableItem.EMPTY);
		if (fromGui) color.setItem(size - 1, ClickableItem.of(back, (event) -> open(player.getPlayer())));
		int slot = 0;
		for (ColorData colorData : MentionPlayer.getInstance().getColorData().values()) {
			if (!player.canUseTag(colorData)) continue;

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
							color.setItem(finalI, ClickableItem
									.of(new ItemBuilder(player.getColor() == colorData ? XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial() : XMaterial.WHITE_WOOL.parseMaterial())
													.name(colorData.parse(colorData.getName()))
													.durability((colorData.isCustom() ?
															colorData.getDyeColor()[tries[0]++]
															: DyeColor.values()[random.nextInt(16)]
													).getWoolData()),
											event -> {
												player.setColor(colorData);
												openColor(player, fromGui);
												cancel();
											}));
							if (tries[0] == colorData.getDyeColor().length) tries[0] = 0;
						} else cancel();
					}
				}.runTaskTimerAsynchronously(MentionPlayer.getInstance(), 1L, 5L);

			} else if (player.getColor() == colorData)
				color.setItem(slot, ClickableItem.empty(new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial())
						.name(colorData.parse(colorData.getName()))
						.lore("", AQUA + "- " + GRAY + "This is your actual tag color.", "")
						.durability(colorData.getDyeColor()[0].getWoolData())));
			else {
				color.setItem(slot, ClickableItem.of(new ItemBuilder(XMaterial.WHITE_WOOL.parseMaterial())
								.name(colorData.parse(colorData.getName()))
								.durability(colorData.getDyeColor()[0].getWoolData()),
						event -> {
							player.setColor(colorData);
							openColor(player, fromGui);
						}
				));
			}
			slot++;
		}

		player.getPlayer().openInventory(color.build());

	}

	public static void openSound(MPlayer player, boolean fromGui) {
		ItemStack back = null;
		if (fromGui)
			back = new ItemBuilder(Material.ARROW).name(ChatColor.GOLD + "Back").lore("", ChatColor.AQUA + "- " + ChatColor.GRAY + "Return to options menu.", "").build();

		Set<Sound> listSound = new HashSet<>();
		if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.7") || Bukkit.getVersion().contains("1.6")) {
			listSound.add(Sound.valueOf("NOTE_BASS"));
			listSound.add(Sound.valueOf("NOTE_PIANO"));
			listSound.add(Sound.valueOf("NOTE_BASS_DRUM"));
			listSound.add(Sound.valueOf("NOTE_STICKS"));
			listSound.add(Sound.valueOf("NOTE_BASS_GUITAR"));
			listSound.add(Sound.valueOf("NOTE_SNARE_DRUM"));
			listSound.add(Sound.valueOf("NOTE_PLING"));
			listSound.add(Sound.valueOf("ORB_PICKUP"));
			listSound.add(Sound.valueOf("VILLAGER_HIT"));
			listSound.add(Sound.valueOf("VILLAGER_NO"));
			listSound.add(Sound.valueOf("VILLAGER_YES"));
		} else if (Bukkit.getVersion().contains("1.13")) {
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_BASEDRUM"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_BASS"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_BELL"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_CHIME"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_FLUTE"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_GUITAR"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_HARP"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_HAT"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_PLING"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_SNARE"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BLOCK_XYLOPHONE"));
			listSound.add(Sound.valueOf("ENTITY_PLAYER_HURT"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_HURT"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_NO"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_TRADE"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_YES"));
			listSound.add(Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"));
		} else {
			listSound.add(Sound.valueOf("BLOCK_NOTE_BASEDRUM"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BASS"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_BELL"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_CHIME"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_FLUTE"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_GUITAR"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_HARP"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_HAT"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_PLING"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_SNARE"));
			listSound.add(Sound.valueOf("BLOCK_NOTE_XYLOPHONE"));
			listSound.add(Sound.valueOf("ENTITY_PLAYER_HURT"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_HURT"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_NO"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_TRADING"));
			listSound.add(Sound.valueOf("ENTITY_VILLAGER_YES"));
			listSound.add(Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"));
		}
		final Sound[] uuid = listSound.toArray(new Sound[0]);
		int           size = Math.round((float) uuid.length / 9.0F) * 9 + 9;
		InventoryBuilder sound = new InventoryBuilder(ChatColor.AQUA + "- Sound", size, true)
				.fill(ClickableItem.EMPTY);
		if (fromGui) sound.setItem(size - 1, ClickableItem.of(back, (event) -> open(player.getPlayer())));

		final Sound[]     choose  = {null};
		final ItemStack[] choosed = {null};
		for (int i = 0; i < uuid.length; i++) {
			Sound _sound = uuid[i];

			if (player.getSound() == _sound) {
				sound.setItem(i, ClickableItem.of(new ItemBuilder(Material.NOTE_BLOCK)
								.name(GREEN + _sound.name())
								.lore("", AQUA + "- " + GRAY + "This is your actual sound.", ""),
						(event) -> player.getPlayer().playSound(player.getPlayer().getLocation(), _sound, 1f, 1f)));
			} else {
				sound.setItem(i, ClickableItem.of(new ItemBuilder(Material.NOTE_BLOCK)
								.name(GRAY + _sound.name())
								.lore("", AQUA + "- " + GRAY + "Click to play the sound.", ""),
						(event) -> {
							if (choose[0] != _sound) {
								ItemMeta meta;
								if (choosed[0] != null &&
										!choosed[0].getItemMeta().getDisplayName().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName())) {
									meta = choosed[0].getItemMeta();
									meta.setLore(Arrays.asList("", AQUA + "- " + GRAY + "Click to play the sound.", ""));
									choosed[0].setItemMeta(meta);
								}
								meta = event.getCurrentItem().getItemMeta();
								meta.setLore(Arrays.asList("", AQUA + "- " + GRAY + "Click to play the sound.", ITALIC + "(click again to select it)", ""));
								event.getCurrentItem().setItemMeta(meta);
								choose[0] = _sound;
								choosed[0] = event.getCurrentItem();
							} else {
								player.setSound(_sound);
								openSound(player, fromGui);
							}
							player.getPlayer().playSound(player.getPlayer().getLocation(), _sound, 1f, 1f);
						}));
			}
		}

		player.getPlayer().openInventory(sound.build());
	}

	public static void openIgnored(MPlayer player, boolean fromGui) {
		ItemStack back = null;
		if (fromGui)
			back = new ItemBuilder(Material.ARROW).name(ChatColor.GOLD + "Back")
					.lore("", ChatColor.AQUA + "- " + ChatColor.GRAY + "Return to options menu.", "").build();

		final List<UUID> uuids = new ArrayList<>(player.getIgnoredPlayers());
		int          size = Math.round((float) uuids.size() / 9.0F) * 9 + 9;
		if (size < 18) size += 9;
		InventoryBuilder ignore = new InventoryBuilder(ChatColor.AQUA + "- Ignored players", size, true)
				.fill(ClickableItem.EMPTY);
		if (fromGui) ignore.setItem(size - 1, ClickableItem.of(back, (event) -> open(player.getPlayer())));

		for (int i = 0; i < uuids.size(); i++) {
			OfflinePlayer target = Bukkit.getOfflinePlayer(uuids.get(i));
			if (target == null) continue;
			String name = target.getName();

			ignore.setItem(i, ClickableItem.of(new ItemBuilder.Skull(target.getUniqueId())
							.name(GRAY + name)
							.lore("", AQUA + "- " + GRAY + "Click to un-ignore.", ""),
					(event) -> {
						player.ignore(target);
						openIgnored(player, fromGui);
					}));
		}

		player.getPlayer().openInventory(ignore.build());
	}

}