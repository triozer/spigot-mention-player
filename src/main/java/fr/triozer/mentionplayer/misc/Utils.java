package fr.triozer.mentionplayer.misc;

import com.google.common.collect.ImmutableMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.RESET;

/**
 * @author Cédric / Triozer
 */
public class Utils {

	public static final Map<ChatColor, DyeColor> COLORS = ImmutableMap.<ChatColor, DyeColor>builder()
			.putAll(new HashMap<ChatColor, DyeColor>() {
				{
					put(ChatColor.AQUA, DyeColor.LIGHT_BLUE);
					put(ChatColor.BLACK, DyeColor.BLACK);
					put(ChatColor.BLUE, DyeColor.BLUE);
					put(ChatColor.DARK_AQUA, DyeColor.CYAN);
					put(ChatColor.DARK_BLUE, DyeColor.BLUE);
					put(ChatColor.DARK_GRAY, DyeColor.GRAY);
					put(ChatColor.DARK_GREEN, DyeColor.GREEN);
					put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
					put(ChatColor.DARK_RED, DyeColor.RED);
					put(ChatColor.GOLD, DyeColor.ORANGE);
					put(ChatColor.GRAY, Bukkit.getVersion().contains("1.13") ? DyeColor.LIGHT_GRAY : DyeColor.valueOf("GRAY"));
					put(ChatColor.GREEN, DyeColor.LIME);
					put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
					put(ChatColor.RED, DyeColor.RED);
					put(ChatColor.WHITE, DyeColor.WHITE);
					put(ChatColor.YELLOW, DyeColor.YELLOW);
				}
			}).build();

	public static String getLastColor(String input) {
		String result = "";
		input = input.split("@")[0];
		for(int index = input.length() - 1; index > -1; --index) {
			char section = input.charAt(index);
			if ((int) section == (int) '&' && index < input.length() - 1) {
				char                 c     = input.charAt(index + 1);
				org.bukkit.ChatColor color = org.bukkit.ChatColor.getByChar(c);
				if (color != null) {
					result = color.toString() + result;
					if (color.isColor() || color.equals(RESET)) {
						break;
					}
				}
			}
		}
		return result.isEmpty() ? "§r" : result;
	}

	public static void sendActionBar(Player player, String message) {
		String version = Bukkit.getServer().getClass().getPackage().getName();

		version = version.substring(version.lastIndexOf(".") + 1);

		try {
			if (version.startsWith("v1_12") || version.startsWith("v1_13")) {
				ProtocolHack.actionBar(player, message);
				return;
			}

			Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
			Object   p  = c1.cast(player);
			Object   ppoc;
			Class<?> c4 = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + version + ".Packet");

			Class<?> c2 = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
			Class<?> c3 = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");

			if (!(version.equalsIgnoreCase("v1_8_R1") || (version.contains("v1_7_")))) {
				Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
				ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);

			} else {
				Method m3  = c2.getDeclaredMethod("a", String.class);
				Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
				ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
			}

			Method getHandle = c1.getDeclaredMethod("getHandle");
			Object handle    = getHandle.invoke(p);

			Field  fieldConnection  = handle.getClass().getDeclaredField("playerConnection");
			Object playerConnection = fieldConnection.get(handle);

			Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
			sendPacket.invoke(playerConnection, ppoc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
