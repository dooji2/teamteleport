package com.dooji.teamteleport.utils;

import com.dooji.teamteleport.TeamTeleport;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TeleportRequestManager {

    private final JavaPlugin plugin;
    private final HashMap<UUID, UUID> teleportRequests = new HashMap<>();
    private final HashMap<UUID, Location> teleportLocations = new HashMap<>();

    public TeleportRequestManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void requestTeamTeleport(Player requester, List<Player> teamMembers, Location targetLocation) {
        int requestTimeout = ((TeamTeleport) plugin).getRequestTimeout();

        for (Player teamMember : teamMembers) {
            if (((TeamTeleport) plugin).isDenyRequests(teamMember.getUniqueId())) {
                continue;
            }

            if (!teamMember.equals(requester)) {
                teamMember.sendMessage(ChatColor.GOLD + requester.getName() + " has requested a teleport. Type /teamtp accept or /teamtp deny.");
                teleportRequests.put(teamMember.getUniqueId(), requester.getUniqueId());
                teleportLocations.put(teamMember.getUniqueId(), targetLocation);

                if (!teamMember.isOp()) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (teleportRequests.containsKey(teamMember.getUniqueId())) {
                                teamMember.sendMessage(ChatColor.RED + "Teleport request from " + requester.getName() + " timed out.");
                                teleportRequests.remove(teamMember.getUniqueId());
                                teleportLocations.remove(teamMember.getUniqueId());
                            }
                        }
                    }.runTaskLater(plugin, requestTimeout * 20L);
                }
            }
        }

        requester.teleport(targetLocation);
        requester.sendMessage(ChatColor.GREEN + "You have been teleported to the requested location. Your team members have been asked to approve the teleport.");
    }

    public void approveTeleport(Player teamMember) {
        UUID requesterUUID = teleportRequests.get(teamMember.getUniqueId());
        Location targetLocation = teleportLocations.get(teamMember.getUniqueId());

        if (requesterUUID != null && targetLocation != null) {
            Player requester = Bukkit.getPlayer(requesterUUID);

            if (requester != null) {
                teamMember.sendMessage(ChatColor.GREEN + "Teleport approved.");
                teamMember.teleport(targetLocation);

                teleportRequests.remove(teamMember.getUniqueId());
                teleportLocations.remove(teamMember.getUniqueId());
            }
        } else {
            teamMember.sendMessage(ChatColor.RED + "No teleport requests found.");
        }
    }

    public void denyTeleport(Player teamMember) {
        UUID requesterUUID = teleportRequests.get(teamMember.getUniqueId());
        if (requesterUUID != null) {
            Player requester = Bukkit.getPlayer(requesterUUID);
            if (requester != null) {
                teamMember.sendMessage(ChatColor.RED + "You have denied the teleport request from " + requester.getName() + ".");
            }
            teleportRequests.remove(teamMember.getUniqueId());
            teleportLocations.remove(teamMember.getUniqueId());
        } else {
            teamMember.sendMessage(ChatColor.RED + "No teleport requests found.");
        }
    }
}
