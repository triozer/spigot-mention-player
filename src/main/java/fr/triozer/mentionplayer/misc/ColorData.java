package fr.triozer.mentionplayer.misc;

import fr.triozer.mentionplayer.MentionPlayer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Random;

/**
 * @author Cédric / Triozer
 */
public class ColorData {

    public final static ColorData BLACK        = new ColorData("Black", ChatColor.BLACK, DyeColor.BLACK);
    public final static ColorData BLUE         = new ColorData("Blue", ChatColor.BLUE, DyeColor.BLUE);
    public final static ColorData CYAN         = new ColorData("Cyan", ChatColor.AQUA, DyeColor.CYAN);
    public final static ColorData DARK_GRAY    = new ColorData("Dark Gray", ChatColor.DARK_GRAY, DyeColor.GRAY);
    public final static ColorData DARK_GREEN   = new ColorData("Dark Green", ChatColor.DARK_GREEN, DyeColor.GREEN);
    public final static ColorData DARK_RED     = new ColorData("Dark Red", ChatColor.DARK_RED, DyeColor.BROWN);
    public final static ColorData GREEN        = new ColorData("Green", ChatColor.GREEN, DyeColor.LIME);
    public final static ColorData ORANGE       = new ColorData("Orange", ChatColor.GOLD, DyeColor.ORANGE);
    public final static ColorData LIGHT_GRAY   = new ColorData("Light Gray", ChatColor.GRAY, DyeColor.SILVER);
    public final static ColorData LIGHT_PURPLE = new ColorData("Light Purple", ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
    public final static ColorData RED          = new ColorData("Red", ChatColor.RED, DyeColor.RED);
    public final static ColorData WHITE        = new ColorData("White", ChatColor.WHITE, DyeColor.WHITE);
    public final static ColorData YELLOW       = new ColorData("Yellow", ChatColor.YELLOW, DyeColor.YELLOW);
    public final static ColorData RAINBOW      = new ColorData("Rainbow", ChatColor.WHITE, DyeColor.WHITE);

    private final String      name;
    private final String      id;
    private final boolean     custom;
    private final ChatColor[] chatColor;
    private final DyeColor[]  dyeColor;
    private final String      permission;

    public ColorData(String name, String permission, ChatColor[] chatColor, DyeColor[] dyeColor, boolean custom) {
        this.name = name;
        this.id = name.replace(" ", "-").toLowerCase();
        this.custom = custom;
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.permission = permission;

        MentionPlayer.getInstance().getColors().put(id.toLowerCase(), this);
    }

    private ColorData(String name, ChatColor chatColor, DyeColor dyeColor) {
        this(name, "option.permission.color." + name.replace(" ", "-").toLowerCase(), new ChatColor[]{chatColor}, new DyeColor[]{dyeColor}, false);
    }

    public static String rainbow(String message) {
        ChatColor[] colours = new ChatColor[]{
                ChatColor.AQUA, /* too ugly ChatColor.BLACK, */ ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
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
        return MentionPlayer.getInstance().getColors().get(name.toLowerCase());
    }

    public static DyeColor[] toDyeColor(String id) {
        for (ColorData colorData : MentionPlayer.getInstance().getColors().values()) {
            if (colorData.id.equalsIgnoreCase(id)) {
                return colorData.dyeColor;
            }
        }

        return null;
    }

    public static ChatColor[] toChatColor(String id) {
        for (ColorData colorData : MentionPlayer.getInstance().getColors().values()) {
            if (colorData.id.equalsIgnoreCase(id)) {
                return colorData.chatColor;
            }
        }

        return null;
    }

    public String parse(String text) {
        if (this.id.equals(RAINBOW.id)) return rainbow(text);

        StringBuilder result = new StringBuilder();
        double        length = Math.ceil((double) text.length() / (double) this.chatColor.length);
        String[]      parts  = text.split(String.format("(?<=\\G.{%1$d})", (int) length));

        for (int i = 0; i < this.chatColor.length; i++) {
            result.append(chatColor[i]).append(parts[i]);
        }

        return result.toString();
    }

    public final String getName() {
        return this.name;
    }

    public final String getID() {
        return this.id;
    }

    public final ChatColor[] getChatColor() {
        return this.chatColor;
    }

    public final DyeColor[] getDyeColor() {
        return this.dyeColor;
    }

    public final boolean isCustom() {
        return this.custom;
    }

    public final String getPermission() {
        return this.permission;
    }

}
