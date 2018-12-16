package fr.triozer.mentionplayer.listener;

import fr.triozer.mentionplayer.MentionPlayer;
import fr.triozer.mentionplayer.api.event.PlayerMentionEvent;
import fr.triozer.mentionplayer.api.player.MPlayer;
import fr.triozer.mentionplayer.api.ui.popup.BukkitPopup;
import fr.triozer.mentionplayer.misc.Settings;
import fr.triozer.mentionplayer.misc.Utils;
import me.clip.deluxechat.DeluxeChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.MessageFormat;
import java.util.*;

/**
 * @author Cédric / Triozer
 */
public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (Settings.hasTag(event.getMessage())) {
            List<Player> players = new ArrayList<>();
            String       tag     = Settings.getOnlyTag();
            String       one     = "";
            Set<String>  force   = new HashSet<>();
            MPlayer      sender  = MPlayer.get(event.getPlayer());

            if (tag.length() == 0)
                one = "";
            else if (tag.length() == 1) {
                char c = tag.charAt(0);
                if (c == '*' || c == '!' || c == '$' || c == '^' || c == '+' || c == '\\' || c == '.' || c == '{' || c == '}')
                    one += MessageFormat.format("{0}{1}{2}", '\\', c, "{1}");
                else one = tag;
            } else one += "[" + tag + "]{" + tag.length() + "}";

            for (Player online : Bukkit.getOnlinePlayers())
                for (String word : event.getMessage().split(" ")) {
                    String regex = "(^[" + Settings.allForcePrefix() + "]*" + one + "[" + online.getName() + "]{" + online.getName().length() + "}" +
                            "[\\^*()_+=\\[\\]{}|\\\\,.?!:<>'\"\\/;`%¨-]*(?!.*[ ])$)";
                    if (word.matches(regex)) {
                        for (String letter : word.split("")) {
                            if (letter.equals(tag)) break;

                            if (Settings.getPrefix("actionbar").startsWith(letter) && sender.canBypassActionBar()) {
                                force.add(online.getName() + "actionbar");
                            }
                            if (Settings.getPrefix("mention").startsWith(letter) && sender.canBypassMention()) {
                                force.add(online.getName() + "mention");
                            }
                            if (Settings.getPrefix("popup").startsWith(letter) && sender.canBypassPopup()) {
                                force.add(online.getName() + "popup");
                            }
                            if (Settings.getPrefix("sound").startsWith(letter) && sender.canBypassSound()) {
                                force.add(online.getName() + "sound");
                            }
                        }
                        players.add(online);
                    }
                }

            if (players.isEmpty()) return;

            for (Player player : players) {
                MPlayer mPlayer = MPlayer.get(player);

                if (!sender.canBypassAntiSpam() && System.currentTimeMillis() - sender.getLastMessage() <= Settings.getInterval()) {
                    sender.setLastMessage(System.currentTimeMillis());
                    sender.sendSpamError();
                    event.setCancelled(true);
                    return;
                }

                sender.setLastMessage(System.currentTimeMillis());

                if (mPlayer.getIgnoredPlayers().contains(sender.getPlayer().getUniqueId())) {
                    return;
                }

                if (force.contains(player.getName() + "mention") || mPlayer.allowMention()) {

                    BukkitPopup popup = null;
                    if (Settings.canPopup()) popup = mPlayer.createBukkitPopup(sender);

                    PlayerMentionEvent mentionEvent = new PlayerMentionEvent(event.getPlayer(), player,
                            Settings.canPopup() && (force.contains(player.getName() + "popup") || mPlayer.allowPopup()),
                            popup);
                    Bukkit.getServer().getPluginManager().callEvent(mentionEvent);

                    if (mentionEvent.isCancelled()) return;

                    if (force.contains(player.getName() + "sound") || mPlayer.allowSound()) {
                        player.playSound(player.getLocation(), mPlayer.getSound(), 1f, 1f);
                    }
                    if (force.contains(player.getName() + "actionbar") || mPlayer.allowActionbar()) {
                        Utils.sendActionBar(player, Settings.formatActionBar(sender.getPlayer().getName()));
                    }
                    if (Settings.canPopup() && mentionEvent.canPopup()) {
                        mentionEvent.getPopup().show(MentionPlayer.getInstance(), player);
                    }

                    String cleanMention = event.getMessage().replaceAll("[" + Settings.allForcePrefix() + "]*" + tag + player.getName(),
                            tag + player.getName());
                    String formatMention = Settings.textColor(cleanMention,false) + cleanMention.replace(
                            tag + player.getName(),
                            Settings.formatChat(mentionEvent.getColor(), mPlayer) + Settings.textColor(cleanMention, false));

                    if (force.contains(player.getName() + "visible") || mPlayer.isMentionPublic()) {
                        event.setMessage(formatMention);
                        return;
                    }
                    event.setMessage(cleanMention);
                    event.getRecipients().remove(player);
                    if (Settings.canDeluxeChat() && Settings.canPapi()) {
                        DeluxeChat deluxeChat = (DeluxeChat) Bukkit.getPluginManager().getPlugin("DeluxeChat");

                        String placeholder = PlaceholderAPI.setPlaceholders(player, "%mention_player_tag%");
                        formatMention = Settings.textColor(cleanMention, false) + cleanMention
                                .replace(tag + player.getName(), placeholder + Settings.textColor(cleanMention, false));

                        deluxeChat.getChat().sendDeluxeChat(player,
                                deluxeChat.getFancyChatFormat(player, deluxeChat.getPlayerFormat(player)).toJSONString(),
                                deluxeChat.getChat().convertMsg(player, formatMention), Collections.singleton(player));
                    } else {
                        player.sendMessage(String.format(event.getFormat(), sender.getPlayer().getDisplayName(), formatMention));
                    }

                } else if (sender.canBypassMention()) {
                    TextComponent message = new TextComponent(TextComponent.fromLegacyText(sender.get("messages.mention.disabled")
                            .replace("{player}", mPlayer.getPlayer().getName())));
                    String mention = Settings.getPrefix("mention");

                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            event.getMessage().replace(tag + player.getName(), mention + tag + player.getName())));

                    mention = Settings.textColor(message.getText(), false) + event.getMessage().replace(tag + player.getName(),
                            mention + Settings.formatChat(mPlayer.getColor(), mPlayer) + Settings.textColor(message.getText(), false));

                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(mention)));
                    event.getPlayer().spigot().sendMessage(message);
					event.setCancelled(true);
                }
            }
        }

    }

}
