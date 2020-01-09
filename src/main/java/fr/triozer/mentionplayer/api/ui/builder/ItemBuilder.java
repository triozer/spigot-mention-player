package fr.triozer.mentionplayer.api.ui.builder;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.misc.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

/**
 * @author Cédric / Triozer
 */
public class ItemBuilder {

	private String                    name;
	private Material                  material;
	private int                       amount;
	private short                     durability;
	private List<String>              lore;
	private List<ItemFlag>            flags;
	private boolean                   hideFlag;
	private Map<Enchantment, Integer> enchantments;

	/**
	 * @param material Material
	 */
	public ItemBuilder(Material material) {
		this.material = material;
		this.amount = 1;
		this.durability = (byte) 0;
	}

	/**
	 * @param material Material
	 * @param amount   int
	 */
	public ItemBuilder(Material material, int amount) {
		this.material = material;
		this.amount = amount;
		this.durability = (byte) 0;
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return material;
	}

	public int getAmount() {
		return amount;
	}

	public short getDurability() {
		return durability;
	}

	public List<String> getLore() {
		return lore;
	}

	public List<ItemFlag> getFlags() {
		return flags;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return enchantments;
	}

	public ItemBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ItemBuilder amount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemBuilder hideEnchant() {
		if (this.flags == null) {
			this.flags = new ArrayList<>();
		}
		this.flags.add(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemBuilder durability(int data) {
		this.durability = (short) data;
		return this;
	}

	public ItemBuilder lore(String... lore) {
		this.lore = Arrays.asList(lore);
		return this;
	}

	public ItemBuilder itemFlag(ItemFlag flag) {
		if (this.flags == null) {
			this.flags = new ArrayList<>();
		}
		this.flags.add(flag);
		return this;
	}

	public ItemBuilder hideFlag() {
		hideFlag = true;

		return this;
	}

	public ItemBuilder enchant(Enchantment enchant, int level) {
		if (this.enchantments == null) {
			this.enchantments = new HashMap<>();
		}
		this.enchantments.put(enchant, level);
		return this;
	}

	public ItemStack build() {
		ItemStack item = new ItemStack(this.material, this.amount);
		item.setDurability(this.durability);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(this.lore);

		if (this.name != null) {
			meta.setDisplayName(this.name);
		}

		if (this.enchantments != null) {
			for (Map.Entry<Enchantment, Integer> enchant : this.enchantments.entrySet()) {
				meta.addEnchant(enchant.getKey(), enchant.getValue(), true);
			}
		}

		if (this.flags != null) {
			meta.addItemFlags(this.flags.toArray(new ItemFlag[this.flags.size()]));
		}

		if (hideFlag) {
			meta.getItemFlags().forEach(meta::removeItemFlags);
		}

		item.setItemMeta(meta);
		return item;
	}

	public static class Skull extends ItemBuilder {
		private final UUID uniqueId;

		public Skull(UUID uniqueId) {
			super(XMaterial.PLAYER_HEAD.parseMaterial());
			this.durability(3);

			this.uniqueId = uniqueId;
		}

		@Override
		public ItemStack build() {
			ItemStack item = super.build();

			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwningPlayer(Bukkit.getOfflinePlayer(uniqueId));
			item.setItemMeta(meta);

			return item;
		}
	}

}
