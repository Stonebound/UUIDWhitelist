package net.stonebound.uuidwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonReader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UuidWhitelist extends JavaPlugin {

    private Logger log = Bukkit.getLogger();
    private File configFolder;

    private final String KICKMESSAGE = "kick-message";
    private String stringKickMessage;

    private final String WHITELISTURL = "whitelist-url";
    private String stringWhitelistURL;

    @Override
    public void onEnable() {
        // event reg
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new WhitelistListener(this), this);

        // config init
        configFolder = getDataFolder();
        if (!configFolder.exists()) {
            configFolder.mkdir();
        }

        File configFile = new File(configFolder.getAbsolutePath() + File.separator + "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                Properties propConfig = new Properties();
                propConfig.setProperty(KICKMESSAGE, "You are not whitelisted on this server!");
                propConfig.setProperty(WHITELISTURL, "");

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(configFile.getAbsolutePath()));
                propConfig.store(stream, "Auto generated config file, please modify");
            } catch (IOException ex) {
                log.log(Level.WARNING, "[UUIDWhitelist] Failed to create config file!" + ex);
            }
        }

        // load config
        loadConfig();
    }

    public boolean isWhitelisted(String username) {
        try {
            UUID actualUUID = UUIDFetcher.getUUIDOf(username);
            URL url = new URL(stringWhitelistURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                JsonParser parser = new JsonParser();
                JsonArray userArray = parser.parse(reader).getAsJsonArray();
                for (JsonElement user : userArray) {
                    JsonObject userObj = user.getAsJsonObject();
                    UUID userUuid = UUID.fromString(userObj.get("uuid").getAsString());
                    if (userUuid.equals(actualUUID)) {
                        return true;
                    }
                }
            } catch (Exception ex) {
                log.log(Level.SEVERE, "[UUIDWhitelist] Error during whitelist check!", ex);
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "[UUIDWhitelist] failed to open connection!", ex);
        }
        return false;
    }

    public boolean loadConfig() {
        log.info("[UUIDWhitelist] Trying to load settings...");
        try {
            Properties propConfig = new Properties();
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(configFolder.getAbsolutePath() + File.separator + "config.yml"));
            propConfig.load(stream);
            stringKickMessage = propConfig.getProperty(KICKMESSAGE);
            if (stringKickMessage == null) {
                stringKickMessage = "You are not whitelisted on this server!";
            }
            stringWhitelistURL = propConfig.getProperty(WHITELISTURL);
            if (stringWhitelistURL == null) {
                stringWhitelistURL = "";
            }
        } catch (Exception ex) {
            log.log(Level.WARNING, "[UUIDWhitelist] Loading config failed!", ex);
            return false;
        }
        return true;
    }

    public String getKickMessage() {
        return stringKickMessage;
    }
}
