package com.dooji.teamteleport.commands;

import com.dooji.teamteleport.TeamTeleport;
import com.dooji.teamteleport.utils.TeleportRequestManager;
import com.dooji.teamteleport.utils.TeleportUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TeamTeleportCommand implements CommandExecutor {

    private final TeamTeleport plugin;
    private final TeleportRequestManager requestManager;
    private final HashMap<UUID, Long> nonOpCooldowns = new HashMap<>();

    public TeamTeleportCommand(TeamTeleport plugin) {
        this.plugin = plugin;
        this.requestManager = new TeleportRequestManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("teamteleport.use")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        int commandTimeout = plugin.getCommandTimeout();

        if (!player.isOp() && !player.hasPermission("teamteleport.admin")) {
            long lastCommandTime = nonOpCooldowns.getOrDefault(player.getUniqueId(), 0L);
            if (System.currentTimeMillis() - lastCommandTime < commandTimeout * 1000L) {
                player.sendMessage(ChatColor.RED + "You must wait " + commandTimeout + " seconds between commands.");
                return true;
            }
            nonOpCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        }

        // /teamtp accept/deny
        if (args.length == 1 && (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny"))) {
            if (args[0].equalsIgnoreCase("accept")) {
                requestManager.approveTeleport(player);
            } else {
                requestManager.denyTeleport(player);
            }
            return true;
        }

        // /teamtp allow/deny requests
        if (args.length == 2 && args[1].equalsIgnoreCase("requests")) {
            if (args[0].equalsIgnoreCase("deny")) {
                plugin.setDenyRequests(player.getUniqueId(), true);
                player.sendMessage(ChatColor.RED + "You are now denying all non-OP team teleport requests.");
                return true;
            } else if (args[0].equalsIgnoreCase("allow")) {
                plugin.setDenyRequests(player.getUniqueId(), false);
                player.sendMessage(ChatColor.GREEN + "You are now allowing all non-OP team teleport requests.");
                return true;
            }
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("radius")) {
            try {
                int radius = Integer.parseInt(args[1]);

                if (args.length == 5) { // /teamtp radius <radius> <x> <y> <z>
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location targetLocation = new Location(player.getWorld(), x, y, z);
                    TeleportUtils.handleRadiusTeleport(player, radius, targetLocation, plugin, requestManager);
                } else if (args.length == 3) { // /teamtp radius <radius> playerName
                    Player targetPlayer = Bukkit.getPlayer(args[2]);
                    if (targetPlayer != null) {
                        TeleportUtils.handleRadiusTeleport(player, radius, targetPlayer.getLocation(), plugin, requestManager);
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } else { // /teamtp radius <radius>
                    Location playerLocation = player.getLocation();
                    TeleportUtils.handleRadiusTeleport(player, radius, playerLocation, plugin, requestManager);
                }
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid radius or coordinates.");
            }
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("team") && player.hasPermission("teamteleport.admin")) {
            String teamName = args[1];

            // /teamtp team <teamName> playerName
            if (args.length == 3) {
                Player targetPlayer = Bukkit.getPlayer(args[2]);
                if (targetPlayer != null) {
                    TeleportUtils.handleTeamTeleportToPlayer(teamName, targetPlayer, plugin);
                } else {
                    player.sendMessage(ChatColor.RED + "Player not found.");
                }
                return true;
            }

            // /teamtp team <teamName> radius <radius> playerName
            if (args.length == 5 && args[2].equalsIgnoreCase("radius")) {
                try {
                    int radius = Integer.parseInt(args[3]);
                    Player targetPlayer = Bukkit.getPlayer(args[4]);
                    if (targetPlayer != null) {
                        TeleportUtils.handleRadiusTeleport(player, radius, targetPlayer.getLocation(), plugin, requestManager);
                    } else {
                        player.sendMessage(ChatColor.RED + "Player not found.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid radius or player name.");
                }
                return true;
            }

            // /teamtp team <teamName> <x> <y> <z>
            if (args.length == 5) {
                try {
                    double x = Double.parseDouble(args[2]);
                    double y = Double.parseDouble(args[3]);
                    double z = Double.parseDouble(args[4]);
                    Location targetLocation = new Location(player.getWorld(), x, y, z);
                    TeleportUtils.handleTeamTeleportToLocation(teamName, targetLocation, plugin);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Coordinates must be numbers.");
                }
                return true;
            }

            // /teamtp team <teamName> radius <radius> <x> <y> <z>
            if (args.length == 7 && args[2].equalsIgnoreCase("radius")) {
                try {
                    int radius = Integer.parseInt(args[3]);

                    if (isNumeric(args[4]) && isNumeric(args[5]) && isNumeric(args[6])) {
                        double x = Double.parseDouble(args[4]);
                        double y = Double.parseDouble(args[5]);
                        double z = Double.parseDouble(args[6]);
                        Location targetLocation = new Location(player.getWorld(), x, y, z);
                        TeleportUtils.handleRadiusTeleport(player, radius, targetLocation, plugin, requestManager);
                    } else {
                        player.sendMessage(ChatColor.RED + "Coordinates must be valid numbers.");
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid radius or coordinates.");
                }
                return true;
            }

            // /teamtp team <teamName> radius <radius>
            if (args.length == 4 && args[2].equalsIgnoreCase("radius")) {
                try {
                    int radius = Integer.parseInt(args[3]);
                    Location playerLocation = player.getLocation();
                    TeleportUtils.handleRadiusTeleport(player, radius, playerLocation, plugin, requestManager);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid radius.");
                }
                return true;
            }
        }

        if (args.length == 3) { // /teamtp <x> <y> <z>
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                Location targetLocation = new Location(player.getWorld(), x, y, z);
                TeleportUtils.handleTeamOrPlayerTeleport(player, targetLocation, plugin, requestManager);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Coordinates must be numbers.");
            }
            return true;
        } else if (args.length == 1) { // /teamtp playerName
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null) {
                TeleportUtils.handleTeamOrPlayerTeleport(player, targetPlayer.getLocation(), plugin, requestManager);
            } else {
                player.sendMessage(ChatColor.RED + "Player not found.");
            }
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "For command usage please see https://modrinth.com/project/teamteleport");
        return true;
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
