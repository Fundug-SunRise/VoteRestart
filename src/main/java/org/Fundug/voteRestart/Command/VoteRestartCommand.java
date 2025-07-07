package org.Fundug.voteRestart.Command;

import com.google.common.collect.Lists;
import org.Fundug.voteRestart.VoteRestart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteRestartCommand extends AbstractCommand{

    public VoteRestartCommand(){
        super("voterestart");
    }

    @Override
    public void execute(CommandSender sender, Command command, String s, String[] strings) {
        if(strings.length == 0){
            sender.sendMessage(ChatColor.BLUE + "Reload plugin: " + s + " reload\nStart vote for restart: " + s + " vote");
        }

        if(strings[1].equalsIgnoreCase("reload")){

            if (!sender.hasPermission("voterestart.start")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to start votes!");
                return;
            }

            VoteRestart.getInst().reloadConfig();
            return;
        }

        if (strings[0].equalsIgnoreCase("vote")) {
            if (strings.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /voterestart vote <start|yes|no>");
                return;
            }


            if (strings[1].equalsIgnoreCase("start")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can start voting!");
                    return;
                }



                if (isVotingActive) {
                    sender.sendMessage(ChatColor.RED + "Voting is already underway!");
                    return;
                }

                startVote((Player) sender);
                return;
            }
            else if (strings[1].equalsIgnoreCase("yes") || strings[1].equalsIgnoreCase("no")) {
                if (!isVotingActive) {
                    sender.sendMessage(ChatColor.RED + "There is no active voting!");
                    return;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Only players can vote!");
                    return;
                }

                Player voter = (Player) sender;
                boolean vote = strings[1].equalsIgnoreCase("yes");
                votes.put(voter.getUniqueId(), vote);
                voter.sendMessage(ChatColor.GREEN + "Your vote: " + (vote ? "Yes" : "No"));
                return;
            }

        }


        sender.sendMessage(ChatColor.RED + "Unknown command: " + strings[0]);
    }

    public List<String> complete(CommandSender sender, String[] args){
        if(args.length == 1) return Lists.newArrayList("reload","vote");
        if((args[0].equalsIgnoreCase("vote")) && (args.length == 2)) return Lists.newArrayList("start", "yes", "no");
        return Lists.newArrayList();
    }

    private final Map<UUID, Boolean> votes = new HashMap<>();
    private boolean isVotingActive = false;

    private void startVote(Player starter) {
        isVotingActive = true;
        votes.clear();

        TextComponent message = new TextComponent(ChatColor.GOLD + "Poll: Restart server? ");

        TextComponent yesButton = new TextComponent(ChatColor.GREEN + "[Yes]");
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voterestart vote yes"));

        TextComponent noButton = new TextComponent(ChatColor.RED + "[No]");
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voterestart vote no"));

        message.addExtra(yesButton);
        message.addExtra(" ");
        message.addExtra(noButton);

        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(message));

        Bukkit.getScheduler().runTaskLater(VoteRestart.getInst(), () -> {
            if (!isVotingActive) return;

            int yesVotes = (int) votes.values().stream().filter(v -> v).count();
            int noVotes = votes.size() - yesVotes;

            Bukkit.broadcastMessage(ChatColor.YELLOW + "Voting is closed! Results: ");
            Bukkit.broadcastMessage(ChatColor.GREEN + "Yes: " + yesVotes);
            Bukkit.broadcastMessage(ChatColor.RED + "No: " + noVotes);

            if (yesVotes > noVotes) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "The server will be rebooted!");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "The vote failed.");
            }

            isVotingActive = false;
            votes.clear();
        }, 20 * 60);
    }
}
