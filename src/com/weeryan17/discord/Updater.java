package com.weeryan17.discord;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class Updater {
	Discord instance;
	public Updater(Discord instance){
		this.instance = instance;
	}
	
	public void updatePlayer(Player p){
		String clientId = instance.getLinkedConfig().getString("Players." + p.getUniqueId().toString());
		IUser user = Discord.client.getUserByID(clientId);
		String[] groups = Discord.permission.getPlayerGroups(p);
		List<IGuild> guilds = Discord.client.getGuilds();
		IGuild guild = guilds.get(0);
		List<IRole> role = guild.getRolesByName(groups[0]);
		IRole[] roles = {role.get(0)}; 
		try {
			guild.editUserRoles(user, roles);
		} catch (RateLimitException | MissingPermissionsException | DiscordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateRoles(){
		String[] groups = Discord.permission.getGroups();
		List<IGuild> guilds = Discord.client.getGuilds();
		IGuild guild = guilds.get(0);
		ArrayList<String> roleNames = new ArrayList<String>();
		for(IRole role : guild.getRoles()){
			roleNames.add(role.getName());
		}
		for(String group : groups){
			if(!roleNames.contains(group)){
				IRole role;
				try {
					role = guild.createRole();
					role.changeName(group);
					role.changeColor(Color.CYAN);
				} catch (RateLimitException | MissingPermissionsException | DiscordException e) {
					// TODO Auto-generated catch block
					role = null;
					e.printStackTrace();
				}
			}
		}
	}
}
