package fr.triozer.mentionplayer.misc;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Random;

/**
 * @author Cédric / Triozer
 */
public enum ColorData {

    BLACK("Black", ChatColor.BLACK, DyeColor.BLACK),
    BLUE("Blue", ChatColor.BLUE, DyeColor.BLUE),
    CYAN("Cyan", ChatColor.AQUA, DyeColor.CYAN),
    DARK_GRAY("Dark Gray", ChatColor.DARK_GRAY, DyeColor.GRAY),
    DARK_GREEN("Dark Green", ChatColor.DARK_GREEN, DyeColor.GREEN),
    DARK_RED("Dark Red", ChatColor.DARK_RED, DyeColor.BROWN),
    GREEN("Green", ChatColor.GREEN, DyeColor.LIME),
    ORANGE("Orange", ChatColor.GOLD, DyeColor.ORANGE),
    LIGHT_GRAY("Light Gray", ChatColor.GRAY, DyeColor.SILVER),
    LIGHT_PURPLE("Light Purple", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA),
    RED("Red", ChatColor.RED, DyeColor.RED),
    WHITE("White", ChatColor.WHITE, DyeColor.WHITE),
    YELLOW("Yellow", ChatColor.YELLOW, DyeColor.YELLOW),
    RAINBOW("Rainbow", ChatColor.WHITE, DyeColor.WHITE);

    private String    id;
    private ChatColor chatColor;
    private DyeColor  dyeColor;

    ColorData(String id, ChatColor chatColor, DyeColor dyeColor) {
        this.id = id;
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
    }

    public String rainbow(String message) {
        ChatColor[] colours = new ChatColor[]{
                ChatColor.AQUA, ChatColor.BLACK, ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
                ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD,
                ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.WHITE, ChatColor.YELLOW};

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < message.toCharArray().length; i++) {
            if (message.charAt(i) == '§') {
                i++;
                continue;
            }

            builder.append(colours[new Random().nextInt(colours.length)]).append(message.charAt(i));
        }

        return builder.toString();
    }

    public static ColorData get(String name) {
        return valueOf(name.toUpperCase());
    }

    public static DyeColor toDyeColor(String id) {
        for (ColorData colorData : values()) {
            if (colorData.id.equalsIgnoreCase(id)) {
                return colorData.dyeColor;
            }
        }

        return null;
    }

    public static ChatColor toChatColor(String id) {
        for (ColorData colorData : values()) {
            if (colorData.id.equalsIgnoreCase(id)) {
                return colorData.chatColor;
            }
        }

        return null;
    }

    public final String getID() {
        return this.id;
    }

    public ChatColor getChatColor() {
        return this.chatColor;
    }

    public DyeColor getDyeColor() {
        return this.dyeColor;
    }

}
