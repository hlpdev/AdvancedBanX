package net.hnt8.advancedban.bungee.cloud.support;

import eu.cloudnetservice.driver.CloudNetDriver;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import net.hnt8.advancedban.bungee.cloud.CloudSupport;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.Objects;
import java.util.UUID;

public class CloudNetV4Support implements CloudSupport {
    
    @Override
    public void kick(UUID uniqueId, String reason) {
        String result = reason.replace('§', '&');
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(miniMessage.deserialize(result)));
        
        Objects.requireNonNull(CloudNetDriver.instance().serviceRegistry()
                .firstProvider(PlayerManager.class)
                .onlinePlayer(uniqueId), "player is null in CloudNetV4")
                .playerExecutor()
                .kick(LegacyComponentSerializer.legacySection().deserialize(result));
    }
    
}
