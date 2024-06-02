package net.hnt8.advancedban.bungee.listener;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.bungee.BungeeMain;
import net.hnt8.advancedban.manager.PunishmentManager;
import net.hnt8.advancedban.manager.UUIDManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by Leoko @ dev.skamps.eu on 24.07.2016.
 */
public class ConnectionListenerBungee implements Listener {

    @SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOW)
    public void onConnection(LoginEvent event) {
        if(event.isCancelled())
            return;

        UUIDManager.get().supplyInternUUID(event.getConnection().getName(), event.getConnection().getUniqueId());
        event.registerIntent((BungeeMain)Universal.get().getMethods().getPlugin());
        Universal.get().getMethods().runAsync(() -> {
            String result = Universal.get().callConnection(event.getConnection().getName(), event.getConnection().getAddress().getAddress().getHostAddress());

            if (result != null) {
                MiniMessage miniMessage = MiniMessage.miniMessage();
                LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
                result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(miniMessage.deserialize(result)));
                
                if(BungeeMain.getCloudSupport() != null){
                    BungeeMain.getCloudSupport().kick(event.getConnection().getUniqueId(), result);
                }else {
                    event.setCancelled(true);
                    event.setCancelReason(result);
                }
            }

            if (Universal.isRedis()) {
                RedisBungee.getApi().sendChannelMessage("advancedban:connection", event.getConnection().getName() + "," + event.getConnection().getAddress().getAddress().getHostAddress());
            }
            event.completeIntent((BungeeMain) Universal.get().getMethods().getPlugin());
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        Universal.get().getMethods().runAsync(() -> {
            if (event.getPlayer() != null) {
                PunishmentManager.get().discard(event.getPlayer().getName());
            }
        });
    }
}