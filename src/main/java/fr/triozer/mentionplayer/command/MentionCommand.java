package fr.triozer.mentionplayer.command;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.DefaultFontInfo;
import fr.triozer.mentionplayer.api.ui.builder.InventoryBuilder;
import fr.triozer.mentionplayer.gui.MentionUI;
import fr.triozer.mentionplayer.misc.Settings;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.md_5.bungee.api.ChatColor.*;

/**
 * @author Cédric / Triozer
 */
public class MentionCommand implements CommandExecutor, TabCompleter {

	private final MentionPlayer instance;
	private final String        COMMAND;
	private final String        P_FORCE;
	private final String        P_PREFIX;
	private final String        P_UPDATE;
	private final String        P_RELOAD;

	public MentionCommand() {
		this.instance = MentionPlayer.getInstance();
		this.COMMAND = "/mention";
		this.P_FORCE = "mention.set.force";
		this.P_PREFIX = "mention.set.prefix";
		this.P_UPDATE = "mention.update";
		this.P_RELOAD = "mention.reload";
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(RED + "Error, only a player can use this command.");

			return true;
		}

		Player sender = (Player) commandSender;
		MPlayer player = MPlayer.get(sender.getUniqueId());

		if (args.length == 0 || "help".equalsIgnoreCase(args[0]) || "?".equalsIgnoreCase(args[0]) || "h".equalsIgnoreCase(args[0]))
			showHelp(player);

		else if ("actionbar".equalsIgnoreCase(args[0])) {
			if (args.length == 2) {
				if ("on".equalsIgnoreCase(args[1])) player.setActionbar(true);
				else if ("off".equalsIgnoreCase(args[1])) player.setActionbar(false);
				else help(player, COMMAND + " actionbar <on/off>");
			} else {
				help(player, COMMAND + " actionbar <on/off>");
			}
		} else if ("color".equalsIgnoreCase(args[0])) {
			if (args.length == 2 && MPlayer.get(sender.getUniqueId()).canUseTag()) {
				if ("set".equalsIgnoreCase(args[1]) && Settings.canGUI()) MentionUI.openColor(player, false);
				else help(player, COMMAND + " color set");
			} else player.sendCurrentColor();
		} else if ("force".equalsIgnoreCase(args[0]) && player.getPlayer().hasPermission(P_FORCE)) {
			if (args.length == 2) {
				String arg = args[1].toLowerCase();
				if (Settings.getPrefix(arg) == null)
					help(player, COMMAND + " force <actionbar/mention/popup/sound>");
				else {
					player.getPlayer().sendMessage(player.get("messages.force.current")
							.replace("{what}", arg)
							.replace("{prefix}", Settings.getPrefix(arg)));
				}
			} else if (args.length >= 3) {
				String arg = args[1].toLowerCase();
				instance.getConfig().set("options.prefix.force." + arg, args[2]);
				instance.saveConfig();
				instance.reloadConfig();
				player.getPlayer().sendMessage(player.get("messages.force.change")
						.replace("{what}", arg)
						.replace("{prefix}", Settings.getPrefix(arg)));
			} else {
				player.getPlayer().sendMessage(player.get("messages.force.current").replace("{what}", "actionbar")
						.replace("{prefix}", Settings.getPrefix("actionbar")));
				player.getPlayer().sendMessage(player.get("messages.force.current").replace("{what}", "mention")
						.replace("{prefix}", Settings.getPrefix("mention")));
				player.getPlayer().sendMessage(player.get("messages.force.current").replace("{what}", "popup")
						.replace("{prefix}", Settings.getPrefix("popup")));
				player.getPlayer().sendMessage(player.get("messages.force.current").replace("{what}", "sound")
						.replace("{prefix}", Settings.getPrefix("sound")));
			}
		} else if ("gui".equalsIgnoreCase(args[0])) {
			if (Settings.canGUI()) MentionUI.open(player.getPlayer());
			else
				player.sendCantUse();
		} else if ("ignore".equalsIgnoreCase(args[0])) {
			if (args.length == 2) {
				Player playerExact = Bukkit.getPlayerExact(args[1]);
				if (playerExact != null) {
					player.ignore(playerExact);
				} else {
					player.getPlayer().sendMessage(player.get("errors.cant-find-player")
							.replace("{player}", args[1]));
				}
			} else MentionUI.openIgnored(player, false);
		} else if ("popup".equalsIgnoreCase(args[0])) {
			if (Settings.canPopup())
				if (args.length == 2) {
					if ("on".equalsIgnoreCase(args[1])) player.setPopup(true);
					else if ("off".equalsIgnoreCase(args[1])) player.setPopup(false);
					else help(player, COMMAND + " popup <on/off>");
				} else
					help(player, COMMAND + " popup <on/off>");
			else
				player.sendCantUse();
		} else if ("prefix".equalsIgnoreCase(args[0])) {
			if (args.length == 2 && player.getPlayer().hasPermission(P_PREFIX)) {
				if (args[1].equalsIgnoreCase("none")) {
					instance.getConfig().set("options.prefix.custom", false);
					instance.getConfig().set("options.prefix.value", "none");
				} else {
					String input      = ChatColor.translateAlternateColorCodes('&', args[1]);
					String stripColor = ChatColor.stripColor(input);

					if (stripColor.length() == 0) {
						instance.getConfig().set("options.prefix.custom", false);
						instance.getConfig().set("options.prefix.value", "none");
					} else {
						instance.getConfig().set("options.prefix.custom", true);
						instance.getConfig().set("options.prefix.value", args[1]);
					}
				}

				instance.saveConfig();
				player.getPlayer().sendMessage(player.get("messages.prefix.change")
						.replace("{raw-prefix}", Settings.getTag().replace("§", "&"))
						.replace("{prefix}", Settings.getTag()));
			} else
				player.getPlayer().sendMessage(player.get("messages.prefix.current")
						.replace("{raw-prefix}", Settings.getTag().replace("§", "&"))
						.replace("{prefix}", Settings.getTag()));
		} else if ("sound".equalsIgnoreCase(args[0])) {
			if (args.length == 2)
				if ("on".equalsIgnoreCase(args[1])) player.setSound(true);
				else if ("off".equalsIgnoreCase(args[1])) player.setSound(false);
				else if ("set".equalsIgnoreCase(args[1])) MentionUI.openSound(player, false);
				else help(player, COMMAND + " sound <on/off/set>");
			else
				help(player, COMMAND + " sound <on/off/set>");
		} else if ("visible".equalsIgnoreCase(args[0])) {
			if (args.length == 2)
				if ("on".equalsIgnoreCase(args[1])) player.setVisible(true);
				else if ("off".equalsIgnoreCase(args[1])) player.setVisible(false);
				else help(player, COMMAND + " visible <on/off>");
			else
				help(player, COMMAND + " visible <on/off>");
		} else if ("on".equalsIgnoreCase(args[0])) {
			player.setMention(true);
		} else if ("off".equalsIgnoreCase(args[0])) {
			player.setMention(false);
		} else if ("reload".equalsIgnoreCase(args[0])
				&& commandSender.hasPermission(P_RELOAD)) {
			long started = System.currentTimeMillis();
			instance.reloadConfig();
			instance.reloadMessage();

			Settings.registerColors();
			for (Player mPlayer : Bukkit.getOnlinePlayers()) {
				if (MPlayer.getPlayers().containsKey(mPlayer.getUniqueId()))
					MPlayer.getPlayers().get(mPlayer.getUniqueId()).load();
				else {
					instance.getDatabase().create(mPlayer.getUniqueId(), null);
				}
			}
			Set<HumanEntity> openers = new HashSet<>();

			for (InventoryBuilder inventory : instance.getInventoriesList())
				openers.addAll(inventory.build().getViewers());
			for (HumanEntity opener : openers) opener.closeInventory();

			started = System.currentTimeMillis() - started;
			player.getPlayer().sendMessage(GREEN + "  Successfully reloaded the configuration. " + GRAY + "(took " + started + "ms)");
		} else if ("update".equalsIgnoreCase(args[0]) && commandSender.hasPermission(P_UPDATE)) {
			Bukkit.getScheduler().runTaskAsynchronously(instance,
					() -> instance.checkUpdate(player.getPlayer(), false));
		} else {
			return false;
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage(RED + "Error, only a player can use this command.");
			return new ArrayList<>();
		}

		List<String> subcommand = new ArrayList<>();

		if (args.length == 0) {
			subcommand.add("actionbar");
			subcommand.add("color");
			if (commandSender.hasPermission(P_FORCE)) subcommand.add("force");
			if (Settings.canGUI()) subcommand.add("gui");
			subcommand.add("ignore");
			subcommand.add("on");
			subcommand.add("off");
			if (Settings.canPopup()) subcommand.add("popup");
			subcommand.add("prefix");
			subcommand.add("sound");
			if (commandSender.hasPermission(P_UPDATE)) subcommand.add("update");
			if (commandSender.hasPermission(P_RELOAD)) subcommand.add("reload");
		} else if (args.length == 1) {
			String arg = args[0].toLowerCase();
			if ("actionbar".startsWith(arg)) subcommand.add("actionbar");
			if ("color".startsWith(arg)) subcommand.add("color");
			if (commandSender.hasPermission(P_FORCE) && "force".startsWith(arg)) subcommand.add("force");
			if (Settings.canGUI() && "gui".startsWith(arg)) subcommand.add("gui");
			if ("ignore".startsWith(arg)) subcommand.add("ignore");
			if (Settings.canPopup() && "popup".startsWith(arg)) subcommand.add("popup");
			if ("prefix".startsWith(arg)) subcommand.add("prefix");
			if ("sound".startsWith(arg)) subcommand.add("sound");
			if ("visible".startsWith(arg)) subcommand.add("visible");
			if ("on".startsWith(arg)) subcommand.add("on");
			if ("off".startsWith(arg)) subcommand.add("off");
			if ("reload".startsWith(arg) && commandSender.hasPermission(P_RELOAD)) subcommand.add("reload");
			if ("update".startsWith(arg) && commandSender.hasPermission(P_UPDATE)) subcommand.add("update");
		} else if (args.length == 2) {
			String arg  = args[0].toLowerCase();
			String arg2 = args[1].toLowerCase();
			if ("actionbar".equalsIgnoreCase(arg) || "sound".equalsIgnoreCase(arg) || "visible".equalsIgnoreCase(arg)
					|| "popup".equalsIgnoreCase(arg)) {
				if ("on".startsWith(arg2)) subcommand.add("on");
				if ("off".startsWith(arg2)) subcommand.add("off");
			}
			if ((MPlayer.get(((Player) commandSender).getUniqueId()).canUseTag() && "color".equalsIgnoreCase(arg))
					|| "sound".startsWith(arg)) subcommand.add("set");
			if ("ignore".equalsIgnoreCase(arg)) subcommand.addAll(Bukkit.getOnlinePlayers()
					.stream()
					.filter(player -> player.getName().toLowerCase().startsWith(arg2)).collect(
							ArrayList::new,
							(list, players) -> list.add(players.getName()), (list, players) -> {
							}));
			if ("force".equalsIgnoreCase(arg)) {
				if ("actionbar".startsWith(arg2)) subcommand.add("actionbar");
				if ("mention".startsWith(arg2)) subcommand.add("mention");
				if (Settings.canPopup() && "popup".startsWith(arg2)) subcommand.add("popup");
				if ("sound".startsWith(arg2)) subcommand.add("sound");
			}
			if ("prefix".equalsIgnoreCase(arg) && "none".startsWith(arg2)) subcommand.add("none");

		}

		return subcommand;
	}

	private void showHelp(MPlayer player) {
		List<String> help = new ArrayList<>();

		help.add("");
		help.add(DARK_GRAY + " *--------------------------------------------------*");
		help.add(DefaultFontInfo.center(instance.getName() + DARK_GRAY + " v" + instance.getDescription().getVersion()));
		help.add("");
		help.add(buildSubcommand(YELLOW + "<help/h/?/>", "Show this messages."));
		help.add(buildSubcommand(YELLOW + "<" + GREEN + "on" + YELLOW + "/" + RED + "off" + YELLOW + ">", "Make sure to be mentionable."));
		help.add(buildSubcommand("actionbar", "Play notifications in action bar.", "on", "off"));
		if (player.canUseTag())
			help.add(buildSubcommand("color", "Show / " + LIGHT_PURPLE + "Set" + GRAY + " your tag color.", LIGHT_PURPLE + "set"));
		else
			help.add(buildSubcommand("color", "Show your tag color."));
		help.add(buildSubcommand("ignore", "Ignore a player.", "player"));
		if (Settings.canPopup())
			help.add(buildSubcommand("popup", "Play notifications in a popup.", "on", "off"));
		else help.add(buildSubcommand("popup", "Play notifications in a popup.", "on", "off", "disabled"));
		help.add(buildSubcommand("sound", "Play a sound on mention.", "on", "off", "set"));
		help.add(buildSubcommand("visible", "Show your tag to everyone.", "on", "off"));
		help.add("");
		if (Settings.canGUI()) help.add(buildSubcommand("gui", "Open GUI settings."));
		else help.add(buildSubcommand("gui", "Open GUI settings.", "disabled"));
		if (player.getPlayer().hasPermission(P_PREFIX))
			help.add(buildSubcommand("prefix", "Show / " + LIGHT_PURPLE + "Set" + GRAY + " the actual prefix.",
					LIGHT_PURPLE + "prefix", LIGHT_PURPLE + "none"));
		else
			help.add(buildSubcommand("prefix", "Show the actual prefix."));
		if (player.getPlayer().hasPermission(P_FORCE))
			help.add(buildSubcommand(LIGHT_PURPLE + "force", LIGHT_PURPLE + "Show" + GRAY + " / " + LIGHT_PURPLE + "Set" +
							GRAY + " the actual force prefix.",
					LIGHT_PURPLE + "actionbar", LIGHT_PURPLE + "mention", LIGHT_PURPLE + "sound", LIGHT_PURPLE + "popup"));
		if (player.getPlayer().hasPermission(P_RELOAD))
			help.add(buildSubcommand(LIGHT_PURPLE + "reload", "Reload the plugin's configuration."));
		if (player.getPlayer().hasPermission(P_UPDATE))
			help.add(buildSubcommand(LIGHT_PURPLE + "update", "Search for an update."));
		help.add("");
		help.add(DARK_GRAY + " *--------------------------------------------------*");
		help.add("");

		for (String string : help) player.getPlayer().spigot().sendMessage(TextComponent.fromLegacyText(string));
	}

	private void help(MPlayer player, String usage) {
		player.getPlayer().spigot().sendMessage(TextComponent.fromLegacyText(buildHelp(usage)));
	}

	private String buildHelp(String usage) {
		return "  " + RED + "Should be used like" + AQUA + " - " + GRAY + usage;
	}

	private String buildSubcommand(String subcommand, String description, String... args) {
		String line;

		if (args != null && args.length != 0) {
			int           iMax = args.length - 1;
			StringBuilder b    = new StringBuilder();
			b.append(YELLOW).append('<');
			for (int i = 0; ; i++) {
				if (args[i].equalsIgnoreCase("on")) b.append(GREEN);
				else if (args[i].equalsIgnoreCase("off")) b.append(RED);
				else if (args[i].equalsIgnoreCase("disabled")) {
					line = "  " + GRAY + "" + STRIKETHROUGH + COMMAND + " " + subcommand + " - " + description + RED + " (disabled)";
					return line;
				}

				b.append(args[i]);
				if (i == iMax) {
					b.append(YELLOW).append('>');
					break;
				}
				b.append(YELLOW).append('/');
			}
			line = AQUA + "  " + COMMAND + " " + WHITE + subcommand + " " + b.toString() + AQUA + " - " + GRAY + description;
		} else {
			line = AQUA + "  " + COMMAND + " " + WHITE + subcommand + " " + AQUA + "- " + GRAY + description;

		}
		return line;
	}

}