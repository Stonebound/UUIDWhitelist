package net.stonebound.uuidwhitelist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class WhitelistListener implements Listener {

    private final UuidWhitelist plugin;

    public WhitelistListener(UuidWhitelist instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        if (!plugin.isWhitelisted(playerName)) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', plugin.getKickMessage()));
        }
    }
}

