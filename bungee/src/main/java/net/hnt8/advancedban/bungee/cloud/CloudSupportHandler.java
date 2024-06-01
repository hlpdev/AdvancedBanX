package net.hnt8.advancedban.bungee.cloud;

import net.hnt8.advancedban.bungee.cloud.support.CloudNetV2Support;
import net.hnt8.advancedban.bungee.cloud.support.CloudNetV3Support;
import net.md_5.bungee.api.ProxyServer;

public class CloudSupportHandler {

    public static CloudSupport getCloudSystem(){
        if (ProxyServer.getInstance().getPluginManager().getPlugin("CloudNet-Bridge") != null)  {
            return new CloudNetV3Support();
        }
        if (ProxyServer.getInstance().getPluginManager().getPlugin("CloudNetAPI") != null) {
            return new CloudNetV2Support();
        }
        return null;
    }
}
