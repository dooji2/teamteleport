package com.dooji.teamteleport.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamTeleportTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> completions = new ArrayList<>();

        // /teamtp <allow | accept | deny | team | radius | playerName | x>
        if (args.length == 1) {
            completions.addAll(Arrays.asList("allow", "accept", "deny", "team", "radius", "<x>"));
            completions.addAll(getAllOnlinePlayerNames());
        }

        // /teamtp allow <requests>
        if (args.length == 2 && args[0].equalsIgnoreCase("allow")) {
            completions.add("requests");
        }

        // /teamtp deny <requests>
        if (args.length == 2 && args[0].equalsIgnoreCase("deny")) {
            completions.add("requests");
        }

        if (args[0].equalsIgnoreCase("accept")) {
            return completions;
        }

        // /teamtp team <teamName> <playerName | x y z | radius>
        if (args.length == 2 && args[0].equalsIgnoreCase("team")) {
            completions.addAll(getAllTeams());
        }

        // /teamtp team <teamName> <radius | playerName | x>
        if (args.length == 3 && args[0].equalsIgnoreCase("team")) {
            completions.addAll(Arrays.asList("radius"));
            completions.addAll(getAllOnlinePlayerNames());
            completions.add("<x>");
        }

        // /teamtp team <teamName> <x> <y> <z>
        if (args.length == 4 && isNumeric(args[2])) {
            completions.add("<y>");
        }
        if (args.length == 5 && isNumeric(args[2]) && isNumeric(args[3])) {
            completions.add("<z>");
        }

        // /teamtp team <teamName> radius <radius>
        if (args.length == 4 && args[2].equalsIgnoreCase("radius")) {
            completions.add("<radius>");
        }

        // /teamtp team <teamName> radius <radius> <playerName | x>
        if (args.length == 5 && args[2].equalsIgnoreCase("radius")) {
            completions.addAll(getAllOnlinePlayerNames());
            completions.add("<x>");
        }

        // /teamtp team <teamName> radius <radius> x y z
        if (args.length == 6 && isNumeric(args[3])) {
            completions.add("<y>");
        }
        if (args.length == 7 && isNumeric(args[3]) && isNumeric(args[4])) {
            completions.add("<z>");
        }

        // /teamtp radius <radius> <playerName | x y z>
        if (args.length == 2 && args[0].equalsIgnoreCase("radius")) {
            completions.add("<radius>");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("radius")) {
            completions.addAll(getAllOnlinePlayerNames());
            completions.add("<x>");
        }

        if (args.length == 4 || args.length == 5) {
            if (args[0].equalsIgnoreCase("radius")) {
                if (args.length == 4) completions.add("<y>");
                if (args.length == 5) completions.add("<z>");
            }
        }

        // /teamtp <x> <y> <z>
        if (args.length == 2 && isNumeric(args[0])) {
            completions.add("<y>");
        }
        if (args.length == 3 && isNumeric(args[0]) && isNumeric(args[1])) {
            completions.add("<z>");
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<String> getAllTeams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getMainScoreboard();
        return scoreboard.getTeams().stream()
                .map(Team::getName)
                .collect(Collectors.toList());
    }

    private List<String> getAllOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}