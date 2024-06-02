
package net.hnt8.advancedban.bungee.cloud.support;

import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.bridge.CloudServer;
import net.hnt8.advancedban.bungee.cloud.CloudSupport;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class CloudNetV2Support implements CloudSupport {

    @Override
    public void kick(UUID uniqueID, String reason) {
        String result = reason;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        result = ChatColor.translateAlternateColorCodes('&', serializer.serialize(miniMessage.deserialize(result)));
        
        PlayerExecutorBridge.INSTANCE.kickPlayer(CloudServer.getInstance().getCloudPlayers().get(uniqueID), result);
    }
}
