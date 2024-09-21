package com.dooji.teamteleport.utils;

import com.dooji.teamteleport.TeamTeleport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeleportUtils {

    public static void handleTeamOrPlayerTeleport(Player player, Location targetLocation, JavaPlugin plugin, TeleportRequestManager requestManager) {
        String teamName = getPlayerTeam(player);

        if (teamName != null) {
            List<Player> teamMembers = getTeamPlayers(teamName);

            if (!teamMembers.contains(player)) {
                teamMembers.add(player);
            }

            if (plugin instanceof TeamTeleport && ((TeamTeleport) plugin).isDenyRequests(player.getUniqueId()) && !player.isOp()) {
                return;
            }

            if (player.isOp()) {
                teleportTeamInstantly(teamMembers, targetLocation, plugin);
            } else {
                requestManager.requestTeamTeleport(player, teamMembers, targetLocation);
            }
        } else {
            handlePlayerTeleportToLocation(player, targetLocation, plugin);
        }
    }

    public static void handleRadiusTeleport(Player player, int radius, Location targetLocation, JavaPlugin plugin, TeleportRequestManager requestManager) {
        List<Player> nearbyPlayers = player.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distance(player.getLocation()) <= radius)
                .collect(Collectors.toList());

        if (!nearbyPlayers.contains(player)) {
            nearbyPlayers.add(player);
        }

        if (nearbyPlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No players found within " + radius + " blocks.");
            return;
        }

        String teamName = getPlayerTeam(player);

        if (teamName != null && player.isOp()) {
            teleportTeamInstantly(nearbyPlayers, targetLocation, plugin);
        } else if (teamName != null && !player.isOp()) {
            for (Player target : nearbyPlayers) {
                if (!((TeamTeleport) plugin).isDenyRequests(target.getUniqueId())) {
                    requestManager.requestTeamTeleport(player, List.of(target), targetLocation);
                }
            }
        } else {
            for (Player target : nearbyPlayers) {
                handlePlayerTeleportToLocation(target, targetLocation, plugin);
            }
        }
    }

    public static void handlePlayerTeleportToLocation(Player player, Location location, JavaPlugin plugin) {
        startCountdown(player, location, "Teleporting you to specified coordinates...", plugin);
    }

    public static void handleTeamTeleportToLocation(String teamName, Location location, JavaPlugin plugin) {
        List<Player> teamPlayers = getTeamPlayers(teamName);

        Player player = Bukkit.getPlayer(teamName);
        if (!teamPlayers.contains(player)) {
            teamPlayers.add(player);
        }

        teleportTeamInstantly(teamPlayers, location, plugin);
    }

    public static void handleTeamTeleportToPlayer(String teamName, Player targetPlayer, JavaPlugin plugin) {
        List<Player> teamPlayers = getTeamPlayers(teamName);

        if (!teamPlayers.contains(targetPlayer)) {
            teamPlayers.add(targetPlayer);
        }

        teleportTeamInstantly(teamPlayers, targetPlayer.getLocation(), plugin);
    }

    public static void teleportTeamInstantly(List<Player> teamPlayers, Location location, JavaPlugin plugin) {
        for (Player teamMember : teamPlayers) {
            startCountdown(teamMember, location, "Teleporting your team...", plugin);
        }
    }

    public static String getPlayerTeam(Player player) {
        if (player.getScoreboard().getEntryTeam(player.getName()) != null) {
            return Objects.requireNonNull(player.getScoreboard().getEntryTeam(player.getName())).getName();
        }
        return null;
    }

    public static List<Player> getTeamPlayers(String teamName) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getScoreboard().getEntryTeam(player.getName()) != null &&
                        Objects.requireNonNull(player.getScoreboard().getEntryTeam(player.getName())).getName().equalsIgnoreCase(teamName))
                .collect(Collectors.toList());
    }

    public static void startCountdown(Player player, Location location, String message, JavaPlugin plugin) {
        player.sendMessage(ChatColor.GOLD + message);
        player.playSound(player.getLocation(), "minecraft:block.note_block.bell", 1, 1);

        int teleportDelay = ((TeamTeleport) plugin).getTeleportDelay();

        new BukkitRunnable() {
            int countdown = teleportDelay;

            @Override
            public void run() {
                if (countdown > 0) {
                    player.sendMessage(ChatColor.YELLOW + "Teleporting in " + countdown + " seconds...");
                    player.playSound(player.getLocation(), "minecraft:block.note_block.hat", 1, 1);
                    countdown--;
                } else {
                    player.teleport(location);
                    player.sendMessage(ChatColor.GREEN + "Teleported!");
                    player.playSound(player.getLocation(), "minecraft:entity.enderman.teleport", 1, 1);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }
}
