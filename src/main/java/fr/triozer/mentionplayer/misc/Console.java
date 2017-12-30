package fr.triozer.mentionplayer.misc;

import org.bukkit.Bukkit;

/**
 * @author Triozer.
 */
public class Console {
    private String name;

    public Console(String name) {
        this.name = name;
    }

    public void danger(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + name + "] §c" + message);
    }

    public void error(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + name + "] §4" + message);
    }

    public void fine(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + name + "] " + message);
    }

    public void stacktrace(String message, Exception exception) {
        error(message + ". Error " + exception.getLocalizedMessage() + "\n at " + exception.getCause());
    }

    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage("[" + name + "] §e" + message);
    }

    public String getName() {
        return name;
    }
}