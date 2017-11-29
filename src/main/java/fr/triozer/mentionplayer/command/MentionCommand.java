package fr.triozer.mentionplayer.command;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.gui.OptionUI;
import fr.triozer.mentionplayer.misc.MPlayer;
import fr.triozer.mentionplayer.misc.Settings;
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

        if (args.length == 0) {
            if (Settings.canGUI())
                player.getPlayer().sendMessage("§cUse: §a/mention [actionbar | gui | sound | on | off | reload]");
            else
                player.getPlayer().sendMessage("§cUse: §a/mention [actionbar | sound | on | off | reload]");
            return true;
        }

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
            }
        if ("gui".equalsIgnoreCase(args[0])) {
            if (Settings.canGUI())
                OptionUI.INVENTORY.open(player.getPlayer());
            else player.getPlayer().sendMessage("§cYou can't use this future.");
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
        } else if ("reload".equalsIgnoreCase(args[0])
                && commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                .getString("option.permission.bypass-reload"))) {
            MentionPlayer.getInstance().reloadConfig();
            player.getPlayer().sendMessage("§aSuccessfully reloaded the configuration.");
        } else {
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cError, only a player can change her mention settings.");

            return null;
        }

        List<String> subcommand = new ArrayList<>();

        if (args.length == 1) {
            if (args[0].startsWith("a")) {
                subcommand.add("actionbar");
            } else if (Settings.canGUI() && args[0].startsWith("g")) {
                subcommand.add("gui");
            } else if (args[0].startsWith("s")) {
                subcommand.add("sound");
            } else if (args[0].startsWith("o")) {
                subcommand.add("on");
                subcommand.add("off");
            } else if (args[0].startsWith("r")
                    && commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                    .getString("option.permission.bypass-reload"))) {
                subcommand.add("reload");
            } else {
                subcommand.add("actionbar");
                if (Settings.canGUI()) subcommand.add("gui");
                subcommand.add("on");
                subcommand.add("off");
                subcommand.add("sound");

                if (commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                        .getString("option.permission.bypass-reload")))
                    subcommand.add("reload");
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