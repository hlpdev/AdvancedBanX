package net.hnt8.advancedban.bungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.hnt8.advancedban.Universal;
import net.hnt8.advancedban.bungee.cloud.CloudSupport;
import net.hnt8.advancedban.bungee.cloud.CloudSupportHandler;
import net.hnt8.advancedban.bungee.listener.ChatListenerBungee;
import net.hnt8.advancedban.bungee.listener.ConnectionListenerBungee;
import net.hnt8.advancedban.bungee.listener.InternalListener;
import net.hnt8.advancedban.bungee.listener.PubSubMessageListener;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {

    private static BungeeMain instance;

    private static CloudSupport cloudSupport;
    
    private static BungeeAudiences adventure;


    public static BungeeMain get() {
        return instance;
    }
    
    public static BungeeAudiences getAdventure() { return adventure; }

    public static CloudSupport getCloudSupport() {
        return cloudSupport;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        adventure = BungeeAudiences.create(this);
        
        Universal.get().setup(new BungeeMethods());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListenerBungee());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListenerBungee());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new InternalListener());
        ProxyServer.getInstance().registerChannel("advancedban:main");

        cloudSupport = CloudSupportHandler.getCloudSystem();

        if (ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) {
            Universal.setRedis(true);
            ProxyServer.getInstance().getPluginManager().registerListener(this, new PubSubMessageListener());
            RedisBungee.getApi().registerPubSubChannels("advancedban:main", "advancedban:connection");
            Universal.get().getLogger().info("RedisBungee detected, hooking into it!");
        }
    }

    @Override
    public void onDisable() {
        adventure.close();
        adventure = null;
        
        Universal.get().shutdown();
    }
}