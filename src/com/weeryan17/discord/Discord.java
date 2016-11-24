package com.weeryan17.discord;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.Permission;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

public class Discord extends JavaPlugin {
	
	String token = "";
	
	public static IDiscordClient client = null;
	ConfigApi api = null;
	
	public static Permission permission = null;
	
	public void onEnable(){
		Discord4J.disableAudio();
		api = new ConfigApi(this);
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
		if(this.getMainConfig().getString("Bot.token").equals("")){
			getLogger().log(Level.WARNING, "Config not found or is configured incorectly. Please configure your config for use with your bot");
			api.saveDefaultConfigs("config", "", false);
			Bukkit.getPluginManager().disablePlugin(this);
			
		} else {
			this.token = this.getMainConfig().getString("Bot.token");
			client = this.getClient(token);
			client.getDispatcher().registerListener(new DiscordEventHandler(this));
		}
		
	}
	
	public IDiscordClient getClient(String token){
		try {
			return new ClientBuilder().withToken(token).login();
		} catch (DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public FileConfiguration getLinkedConfig(){
		return api.config("Linked Accounts", "");
	}
	
	public void saveLinkedConfig(){
		api.saveConfigs("Linked Accounts", "");
	}
	
	public FileConfiguration getMainConfig(){
		return api.config("config", "");
	}
	
	public void saveMainConfig(){
		api.saveConfigs("config", "");
	}
}
