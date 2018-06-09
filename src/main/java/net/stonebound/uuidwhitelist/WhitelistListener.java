package net.stonebound.uuidwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class WhitelistListener implements Listener {

    private final UuidWhitelist plugin;

    public WhitelistListener(UuidWhitelist instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        String playerName = event.getPlayer().getName();
        if (!plugin.isWhitelisted(playerName)) {
            // Bukkit.getLogger().info("[UUIDWhitelist] " + playerName + " is not whitelisted!");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.translateAlternateColorCodes('&', plugin.getKickMessage()));
        } else {
            // Bukkit.getLogger().info("[UUIDWhitelist] " + playerName + " is whitelisted!");
        }
    }
}

