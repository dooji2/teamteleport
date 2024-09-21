package com.dooji.teamteleport;

import com.dooji.teamteleport.commands.TeamTeleportCommand;
import com.dooji.teamteleport.commands.TeamTeleportTabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class TeamTeleport extends JavaPlugin {

    private File preferencesFile;
    private FileConfiguration preferencesConfig;
    private int teleportDelay;
    private int requestTimeout;
    private int commandTimeout;

    private final HashMap<UUID, Boolean> playerRequestPreferences = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("[TeamTeleport] TeamTeleport enabled!");

        saveDefaultConfig();
        loadPreferences();
        loadConfigValues();

        getCommand("teamtp").setExecutor(new TeamTeleportCommand(this));
        getCommand("teamtp").setTabCompleter(new TeamTeleportTabCompleter());
    }

    @Override
    public void onDisable() {
        getLogger().info("[TeamTeleport] TeamTeleport disabled.");

        savePreferences();
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        teleportDelay = config.getInt("teleport.teleport-delay", 5);
        requestTimeout = config.getInt("teleport.request-timeout", 15);
        commandTimeout = config.getInt("teleport.command-timeout", 10);
    }

    public int getTeleportDelay() {
        return teleportDelay;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public int getCommandTimeout() {
        return commandTimeout;
    }

    private void loadPreferences() {
        preferencesFile = new File(getDataFolder(), "preferences.yml");

        if (!preferencesFile.exists()) {
            preferencesFile.getParentFile().mkdirs();
            saveResource("preferences.yml", false);
        }

        preferencesConfig = YamlConfiguration.loadConfiguration(preferencesFile);

        for (String key : preferencesConfig.getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            boolean denyRequests = preferencesConfig.getBoolean(key);
            playerRequestPreferences.put(playerUUID, denyRequests);
        }
    }

    public void savePreferences() {
        for (UUID uuid : playerRequestPreferences.keySet()) {
            preferencesConfig.set(uuid.toString(), playerRequestPreferences.get(uuid));
        }

        try {
            preferencesConfig.save(preferencesFile);
        } catch (IOException e) {
            getLogger().severe("[TeamTeleport] Could not save preferences.yml: " + e.getMessage());
        }
    }

    public boolean isDenyRequests(UUID playerUUID) {
        return playerRequestPreferences.getOrDefault(playerUUID, false);
    }

    public void setDenyRequests(UUID playerUUID, boolean denyRequests) {
        playerRequestPreferences.put(playerUUID, denyRequests);
        preferencesConfig.set(playerUUID.toString(), denyRequests);
        savePreferences();
    }
}