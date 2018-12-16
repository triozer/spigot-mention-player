package fr.triozer.mentionplayer.misc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

/**
 * @author Triozer.
 */
public class Console {
    private final String name;

    public Console(String name) {
        this.name = name;
    }

    public void danger(String message) {
        Bukkit.getConsoleSender().sendMessage(this.name + " " + ChatColor.RED + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage(this.name + " " + ChatColor.DARK_RED + message);
    }

    public void fine(String message) {
        Bukkit.getConsoleSender().sendMessage(this.name + " " + message);
    }

    public void send(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public void sendWarning(String message) {
        this.send("    " + ChatColor.YELLOW + "[" + ChatColor.AQUA + "" + ChatColor.BOLD + "!" + ChatColor.YELLOW + "] " + ChatColor.GRAY + message);
    }

    public void stacktrace(String message, Exception exception) {
        this.error(message + ". Error " + exception.getLocalizedMessage() + "\n at " + exception.getCause());
    }

    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(this.name + " " + ChatColor.YELLOW + message);
    }

    public final String getName() {
        return this.name;
    }
}
