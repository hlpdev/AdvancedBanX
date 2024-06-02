package net.hnt8.advancedban.bungee.cloud.support;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.hnt8.advancedban.bungee.cloud.CloudSupport;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

public class CloudNetV3Support implements CloudSupport {

    @Override
    public void kick(UUID uniqueID, String reason) {
        String result = reason;
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacy('§');
        result = serializer.serialize(miniMessage.deserialize(result));
        
        CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).getPlayerExecutor(uniqueID).kick(result);
    }
}
