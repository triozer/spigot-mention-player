package fr.triozer.mentionplayer.misc;

import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.List;

/**
 * I think it's easier.
 *
 * @author Cédric / Triozer
 */
public class MultipleColor {

    private final List<ChatColor> colors;
    private final ChatColor[]     colorsArray;

    public MultipleColor(ChatColor... colors) {
        this.colors = Arrays.asList(colors);
        this.colorsArray = colors;
    }

    public String parse(String text) {
        return toString() + text;
    }

    public final ChatColor getColorAt(int index) {
        if (index <= -1 || index > this.colors.size() - 1) return null;
        return this.colors.get(index);
    }

    public final ChatColor[] getColors() {
        return this.colorsArray;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (ChatColor color : this.colors) builder.append(color);
        return builder.toString();
    }

}
