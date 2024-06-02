package net.hnt8.advancedban.bukkit;

import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.bukkit.listener.ChatListener;
import net.hnt8.advancedban.bukkit.listener.CommandListener;
import net.hnt8.advancedban.bukkit.listener.ConnectionListener;
import net.hnt8.advancedban.bukkit.listener.InternalListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {
    private static BukkitMain instance;

    public static BukkitMain get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Universal.get().setup(new BukkitMethods());

        ConnectionListener connListener = new ConnectionListener();
        this.getServer().getPluginManager().registerEvents(connListener, this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        this.getServer().getPluginManager().registerEvents(new InternalListener(), this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            AsyncPlayerPreLoginEvent apple = new AsyncPlayerPreLoginEvent(player.getName(), player.getAddress().getAddress(), player.getUniqueId());
            connListener.onConnect(apple);
            if (apple.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) {
                String result = apple.getKickMessage();
                MiniMessage miniMessage = MiniMessage.miniMessage();
                LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
                result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(miniMessage.deserialize(result)));
                
                player.kickPlayer(result);
            }
        });

    }

    @Override
    public void onDisable() {
        Universal.get().shutdown();
    }
}