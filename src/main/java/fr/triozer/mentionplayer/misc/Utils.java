package fr.triozer.mentionplayer.misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Cédric / Triozer
 */
public class Utils {

    public static void sendActionBar(Player player, String message) {
        String version = Bukkit.getServer().getClass().getPackage().getName();

        version = version.substring(version.lastIndexOf(".") + 1);

        try {
            if (version.equals("v1_12_R1")) {
                ProtocolHack.actionBar(player, message);
            } else if (!(version.equalsIgnoreCase("v1_8_R1") || (version.contains("v1_7_")))) {
                Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                Object   p  = c1.cast(player);
                Object   ppoc;
                Class<?> c4 = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
                Class<?> c5 = Class.forName("net.minecraft.server." + version + ".Packet");

                Class<?> c2 = Class.forName("net.minecraft.server." + version + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
                Object   o  = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);

                Method getHandle = c1.getDeclaredMethod("getHandle");
                Object handle    = getHandle.invoke(p);

                Field  fieldConnection  = handle.getClass().getDeclaredField("playerConnection");
                Object playerConnection = fieldConnection.get(handle);

                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
                sendPacket.invoke(playerConnection, ppoc);
            } else {
                Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
                Object   p  = c1.cast(player);
                Object   ppoc;
                Class<?> c4 = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
                Class<?> c5 = Class.forName("net.minecraft.server." + version + ".Packet");

                Class<?> c2  = Class.forName("net.minecraft.server." + version + ".ChatSerializer");
                Class<?> c3  = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
                Method   m3  = c2.getDeclaredMethod("a", String.class);
                Object   cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);

                Method getHandle = c1.getDeclaredMethod("getHandle");
                Object handle    = getHandle.invoke(p);

                Field  fieldConnection  = handle.getClass().getDeclaredField("playerConnection");
                Object playerConnection = fieldConnection.get(handle);

                Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", c5);
                sendPacket.invoke(playerConnection, ppoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
