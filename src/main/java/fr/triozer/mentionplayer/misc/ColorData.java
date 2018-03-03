package fr.triozer.mentionplayer.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static fr.triozer.mentionplayer.MentionPlayer.getInstance;

/**
 * This class represents color tags for mentions.
 *
 * @author Cédric / Triozer
 */
public class ColorData {

    // default tags
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

    private final String        name;
    private final String        id;
    private final boolean       custom;
    private final MultipleColor chatColor;
    private final DyeColor[]    dyeColor;
    private final String        permission;

    /**
     * Create a new tag.
     *
     * @param name       The tag name.
     * @param permission The permission to use this tag.
     * @param chatColor  The chat colors of this tag.
     * @param dyeColor   The wool colors of this tag.
     * @param custom     If the tag is created by the configuration.
     */
    public ColorData(String name, String permission, ChatColor[] chatColor, DyeColor[] dyeColor, boolean custom) {
        this.name = name;
        this.id = name.replace(" ", "-").toLowerCase();
        this.custom = custom;
        this.chatColor = new MultipleColor(chatColor);
        this.dyeColor = dyeColor;
        this.permission = permission;

        getInstance().getColors().put(this.id, this);
    }

    /**
     * Create a default tag.
     *
     * @param name      The tag name.
     * @param chatColor The chat colors of this tag.
     * @param dyeColor  The wool colors of this tag.
     */
    private ColorData(String name, ChatColor chatColor, DyeColor dyeColor) {
        this(name, getInstance().getConfig().getString("option.permission.color." + name.replace(" ", "-").toLowerCase()), new ChatColor[]{chatColor}, new DyeColor[]{dyeColor}, false);
    }

    /**
     * Transform a String into a randomly colored String of characters.
     *
     * @param message The string.
     * @return a randomly colored String of characters.
     */
    public static String rainbow(String message) {
        ChatColor[] colours = new ChatColor[]{
                ChatColor.AQUA, /* too ugly ChatColor.BLACK, */ ChatColor.BLUE, ChatColor.DARK_AQUA, ChatColor.DARK_BLUE,
                ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.GOLD,
                ChatColor.GRAY, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.RED, ChatColor.WHITE, ChatColor.YELLOW};

        List<ChatColor> colors = new ArrayList<>();
        char[]          l      = message.toCharArray();
        message = getString(message, colors, l);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.toCharArray().length; i++) {
            builder.append(colours[new Random().nextInt(colours.length)]);
            for (ChatColor color : colors) builder.append(color);
            builder.append(message.charAt(i));
        }

        return builder.toString();
    }

    /**
     * Returns <tt>true</tt> if the specified {@link ChatColor} is a magic code.
     *
     * @param color The chat color.
     * @return <tt>true</tt> if the specified {@link ChatColor} is a magic code.
     */
    public static boolean isMagic(ChatColor color) {
        return color == ChatColor.BOLD || color == ChatColor.UNDERLINE || color == ChatColor.MAGIC ||
                color == ChatColor.STRIKETHROUGH || color == ChatColor.ITALIC || color == ChatColor.RESET;
    }

    /**
     * Gets a tag by this name.
     *
     * @param name The name
     * @return The tag.
     */
    public static ColorData get(String name) {
        return getInstance().getColors().get(name.toLowerCase());
    }

    /**
     * Colors a String. This method will keep the ChatColor magic and add the colors of this tag.
     *
     * @param text The string.
     * @return A parsed String.
     */
    public String parse(String text) {
        /* IT'S JUST A LITTLE BIT HACKY.. JUST A LITTLE BIT ! */
        if (this.id.equals(RAINBOW.id)) return rainbow(text);

        StringBuilder result = new StringBuilder();
        double        length = Math.ceil((double) text.length() / (double) this.chatColor.getColors().length);
        String[]      parts  = text.split(String.format("(?<=\\G.{%1$d})", (int) length));

        if (custom) {
            List<ChatColor> colors = new ArrayList<>();
            char[]          l      = text.toCharArray();
            text = getString(text, colors, l);
            MultipleColor color = new MultipleColor(colors.toArray(new ChatColor[0]));

            length = Math.ceil((double) text.length() / (double) this.chatColor.getColors().length);
            parts = text.split(String.format("(?<=\\G.{%1$d})", (int) length));

            if (parts.length < this.chatColor.getColors().length) {
                int j = 0;
                for (int i = 0; i < parts.length; i++) {
                    result.append(this.chatColor.getColorAt(j++));
                    if (isMagic(this.chatColor.getColorAt(j))) result.append(this.chatColor.getColorAt(j++));
                    if (j == this.chatColor.getColors().length) j = 0;
                    result.append(color.parse(parts[i]));
                }
            } else {
                for (int i = 0; i < this.chatColor.getColors().length; i++) {
                    result.append(this.chatColor.getColorAt(i));
                    if (this.chatColor.getColorAt(i + 1) != null)
                        if (isMagic(this.chatColor.getColorAt(i + 1))) {
                            result.append(this.chatColor.getColorAt(i + 1));
                        }
                    result.append(color.parse(parts[i]));
                }
            }
        } else {
            if (parts.length < this.chatColor.getColors().length) {
                parts = text.split("");
                int j = 0;
                for (int i = 0; i < parts.length; i++) {
                    result.append(this.chatColor.getColorAt(j++)).append(parts[i]);
                    if (j == this.chatColor.getColors().length) j = 0;
                }
            } else {
                for (int i = 0; i < this.chatColor.getColors().length; i++)
                    result.append(this.chatColor.getColorAt(i)).append(parts[i]);
            }
        }

        return result.toString();
    }

    private static String getString(String text, List<ChatColor> colors, char[] l) {
        for (int i = 0; i < l.length; i++) {
            if (l[i] == '§') {
                ChatColor color = ChatColor.getByChar(l[i + 1]);
                if (color != null && isMagic(color)) {
                    text = text.replace("" + color, "");
                    colors.add(color);
                }
            }
        }
        return text;
    }

    /**
     * Gets the name of this tag.
     *
     * @return The name of this tag.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the ID of this tag.
     *
     * @return The ID of this tag.
     */
    public final String getID() {
        return this.id;
    }

    /**
     * Gets the {@link MultipleColor} of this tag.
     *
     * @return The {@link MultipleColor} of this tag.
     */
    public final MultipleColor getChatColor() {
        return this.chatColor;
    }

    /**
     * Gets the chat colors of this tag.
     *
     * @return The chat colors of this tag.
     */
    public final ChatColor[] getColors() {
        return this.chatColor.getColors();
    }

    /**
     * Gets the dye colors of this tag.
     *
     * @return The dye colors of this tag.
     */
    public final DyeColor[] getDyeColor() {
        return this.dyeColor;
    }

    /**
     * Returns <tt>true</tt> if this is a custom tag.
     *
     * @return <tt>true</tt> if this is a custom tag.
     */
    public final boolean isCustom() {
        return this.custom;
    }

    /**
     * Gets the permission required to use this tag.
     *
     * @return The permission to use this tag.
     */
    public final String getPermission() {
        return this.permission;
    }

}
