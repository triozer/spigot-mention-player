package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.misc.xseries.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * @author Cédric / Triozer
 */
public class ProtocolHack {

    private static final String packageVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];


    private static Class<?> getNMSClass(String nmsClassName) {
        try {
            return Class.forName("net.minecraft.server." + packageVersion + "." + nmsClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void actionBar(Player player, String message) {
        try {
            Object chatComponentText = getNMSClass("ChatComponentText").getConstructor(new Class[]{String.class}).newInstance(message);
            Object chatMessageType   = getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
            Object packetPlayOutChat = getNMSClass("PacketPlayOutChat").getConstructor(new Class[]{getNMSClass("IChatBaseComponent"), getNMSClass("ChatMessageType")}).newInstance(chatComponentText, chatMessageType);
            Object getHandle         = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
            Object playerConnection  = getHandle.getClass().getField("playerConnection").get(getHandle);

            playerConnection.getClass().getMethod("sendPacket", new Class[]{getNMSClass("Packet")}).invoke(playerConnection, packetPlayOutChat);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

}
