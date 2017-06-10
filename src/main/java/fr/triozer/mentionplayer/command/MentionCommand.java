package fr.triozer.mentionplayer.command;

import fr.triozer.mentionplayer.misc.MPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cédric / Triozer
 */
public class MentionCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cError, only a player can change her mention settings.");

            return true;
        }

        MPlayer player = MPlayer.get((Player) commandSender);

        if (args.length == 0) return false;

        if (args.length >= 1)
            if ("actionbar".equalsIgnoreCase(args[0])) {
                if (args.length == 2)
                    if ("on".equalsIgnoreCase(args[1])) {
                        player.enableActionBar();
                    } else if ("off".equalsIgnoreCase(args[1])) {
                        player.disableActionBar();
                    } else {
                        player.getPlayer().sendMessage("§cUse: §a/mention actionbar [on | off]");
                    }
                else
                    player.getPlayer().sendMessage("§cUse: §a/mention actionbar [on | off]");
            } else if ("sound".equalsIgnoreCase(args[0])) {
                if (args.length == 2)
                    if ("on".equalsIgnoreCase(args[1])) {
                        player.enableSound();
                    } else if ("off".equalsIgnoreCase(args[1])) {
                        player.disableSound();
                    } else {
                        player.getPlayer().sendMessage("§cUse: §a/mention sound [on | off]");
                    }
                else
                    player.getPlayer().sendMessage("§cUse: §a/mention sound [on | off]");
            } else if ("on".equalsIgnoreCase(args[0])) {
                player.enableMention();
            } else if ("off".equalsIgnoreCase(args[0])) {
                player.disableMention();
            } else {
                return false;
            }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        List<String> subcommand = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].startsWith("a")) {
                subcommand.add("actionbar");
            } else if (args[0].startsWith("s")) {
                subcommand.add("sound");
            } else if (args[0].startsWith("o")) {
                subcommand.add("on");
                subcommand.add("off");
            } else {
                subcommand.add("actionbar");
                subcommand.add("sound");
                subcommand.add("on");
                subcommand.add("off");
            }
        } else if (args.length == 2) {
            if ("actionbar".equalsIgnoreCase(args[0]) || "sound".equalsIgnoreCase(args[0])) {
                subcommand.add("on");
                subcommand.add("off");
            }
        }

        return subcommand;
    }
}