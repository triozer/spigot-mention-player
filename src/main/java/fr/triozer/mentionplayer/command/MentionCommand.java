package fr.triozer.mentionplayer.command;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.gui.MentionUI;
import fr.triozer.mentionplayer.misc.Settings;
import org.bukkit.Bukkit;
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
            String[] subs = new String[7];

            subs[0] = "actionbar";
            subs[1] = "sound";
            subs[2] = "on";
            subs[3] = "off";

            int i = 3;

            if (Settings.canGUI()) {
                subs[4] = "gui";
                i++;
            }
            if (player.getPlayer().hasPermission("mention.reload")) {
                subs[5] = "reload";
                i++;
            }
            if (player.getPlayer().hasPermission("mention.update")) {
                subs[6] = "update";
                i++;
            }

            StringBuilder b = new StringBuilder();
            b.append('[');
            for (int x = 0; ; x++) {
                b.append(String.valueOf(subs[x]));
                if (x == i) {
                    b.append(']');
                    break;
                }
                b.append(" | ");
            }

            player.getPlayer().sendMessage("§cUse: §a/mention " + b.toString());
        } else if ("actionbar".equalsIgnoreCase(args[0])) {
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
        } else if ("gui".equalsIgnoreCase(args[0])) {
            if (Settings.canGUI())
                MentionUI.open(player.getPlayer());
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
                .getString("option.permission.reload"))) {
            MentionPlayer.getInstance().reloadConfig();
            player.getPlayer().sendMessage("§aSuccessfully reloaded the configuration.");
        } else if ("update".equalsIgnoreCase(args[0])
                && commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                .getString("option.permission.check-update"))) {
            Bukkit.getScheduler().runTaskAsynchronously(MentionPlayer.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        player.getPlayer().sendMessage("§aSearching for updates.");

                        if (MentionPlayer.update("https://rest.c-dric.eu/api/plugins/mention"))
                            player.getPlayer().sendMessage("§dPlease update the plugin for a better support.");
                        else
                            player.getPlayer().sendMessage("§eNo update found !");
                    } catch (Exception e) {
                        player.getPlayer().sendMessage("§cCan't check for update");
                    }
                }
            });
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

        if (args.length == 0) {
            subcommand.add("actionbar");
            if (Settings.canGUI()) subcommand.add("gui");
            subcommand.add("on");
            subcommand.add("off");
            subcommand.add("sound");
            if (commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                    .getString("option.permission.reload"))) subcommand.add("update");
            if (commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                    .getString("option.permission.reload"))) subcommand.add("reload");
        } else if (args.length == 1) {
            String arg = args[0].toLowerCase();
            if ("actionbar".startsWith(arg)) subcommand.add("actionbar");
            if ("color".startsWith(arg)) subcommand.add("color");
            if (Settings.canGUI() && "gui".startsWith(arg)) subcommand.add("gui");
            if ("sound".startsWith(arg)) subcommand.add("sound");
            if ("visible".startsWith(arg)) subcommand.add("visible");
            if ("on".startsWith(arg)) subcommand.add("on");
            if ("off".startsWith(arg)) subcommand.add("off");
            if ("reload".startsWith(arg) && commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                    .getString("option.permission.reload"))) subcommand.add("reload");
            if ("update".startsWith(arg) && commandSender.hasPermission(MentionPlayer.getInstance().getConfig()
                    .getString("option.permission.reload"))) subcommand.add("update");
        } else if (args.length == 2) {
            String arg  = args[0].toLowerCase();
            String arg2 = args[1].toLowerCase();
            if ("actionbar".equalsIgnoreCase(arg) || "sound".equalsIgnoreCase(arg) || "visible".equalsIgnoreCase(arg)) {
                if ("on".startsWith(arg2)) subcommand.add("on");
                if ("off".startsWith(arg2)) subcommand.add("off");
            }
        }

        return subcommand;
    }
}