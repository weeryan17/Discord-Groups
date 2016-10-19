package com.weeryan17.discord;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordEventHandler {
	Discord instance;
	public DiscordEventHandler(Discord instance){
		this.instance = instance;
	}
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent e){
		instance.getLogger().info("bot ready");
		Collection<IChannel> channels = Discord.client.getChannels(true);
		IChannel mainChannel = null;
		for(IChannel channel : channels){
			if(channel.getName().equals(instance.getMainConfig().getString("Channel"))){
				mainChannel = channel;
			}
			
		}
		try {
			mainChannel.sendMessage("Bot ready to do stuff!");
		} catch (MissingPermissionsException e1) {
			// TODO Auto-generated catch block
			instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
			e1.printStackTrace();
		} catch (DiscordException e1) {
			// TODO Auto-generated catch block
			instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
			e1.printStackTrace();
		} catch (RateLimitException e1) {
			// TODO Auto-generated catch block
			instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
			e1.printStackTrace();
		}
		Updater update = new Updater(instance);
		update.updateRoles();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable(){
			@Override
			public void run() {
				for(Player p : Bukkit.getServer().getOnlinePlayers()){
					update.updatePlayer(p);
					
				}
				
			}
			
		}, 0L, 1000L);
	}

	static HashMap<Integer, Player> ids = new HashMap<Integer, Player>();
	@EventSubscriber
	public void onMessgaeEvent(MessageReceivedEvent e){
		IMessage message = e.getMessage();
		String[] command = message.getContent().split(" ");
		if(command[0].equalsIgnoreCase("!register")){
			if(command.length == 2){
				Player p = Bukkit.getPlayer(command[1]);
				if(p!=null){
					try {
						message.getChannel().sendMessage(message.getAuthor().getName() + " A message has been sent to you in minecraft with the id you need to link discord and minecraft");
					} catch (MissingPermissionsException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
						e1.printStackTrace();
					} catch (DiscordException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
						e1.printStackTrace();
					} catch (RateLimitException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
						e1.printStackTrace();
					}
					p.sendMessage(ChatColor.YELLOW + "Your discord registration id is " + ids.size() + 1);
					ids.put(ids.size() + 1, p);
				} else {
					try {
						message.getChannel().sendMessage(message.getAuthor().getName() + " The user name you specifyed is invalid. Make sure you're loged onto the server and your user name is correct");
					} catch (MissingPermissionsException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
						e1.printStackTrace();
					} catch (DiscordException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
						e1.printStackTrace();
					} catch (RateLimitException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
						e1.printStackTrace();
					}
				}
			} else if(command.length == 3 && command[1].equals("confirm")){
				int id;
				try {
					id = Integer.valueOf(command[2]);
				} catch (NumberFormatException e1){
					id = -1;
				}
				if(id <= -1){
					try {
						message.getChannel().sendMessage(message.getAuthor().getName() + " You used an invalid number");
					} catch (MissingPermissionsException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
						e1.printStackTrace();
					} catch (DiscordException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
						e1.printStackTrace();
					} catch (RateLimitException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
						e1.printStackTrace();
					}
				} else {
					if(ids.containsKey(id)){
						Player p = ids.get(id);
						instance.getLinkedConfig().set("Players." + p.getUniqueId().toString(), message.getAuthor().getID());
						instance.getLinkedConfig().set("Discord Users." + message.getAuthor().getID(), p.getUniqueId().toString());
						instance.saveLinkedConfig();
						ids.remove(id);
						try {
							message.getChannel().sendMessage(message.getAuthor().getName() + " You minecraft and discord are now linked on this server");
						} catch (MissingPermissionsException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
							e1.printStackTrace();
						} catch (DiscordException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
							e1.printStackTrace();
						} catch (RateLimitException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
							e1.printStackTrace();
						}
					} else {
						try {
							message.getChannel().sendMessage(message.getAuthor().getName() + " You used an invalid number");
						} catch (MissingPermissionsException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
							e1.printStackTrace();
						} catch (DiscordException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
							e1.printStackTrace();
						} catch (RateLimitException e1) {
							// TODO Auto-generated catch block
							instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
							e1.printStackTrace();
						}
					}
				}
			} else {
				try {
					message.getChannel().sendMessage(message.getAuthor().getName() + " Your usage of the register command is incorect");
				} catch (MissingPermissionsException e1) {
					// TODO Auto-generated catch block
					instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
					e1.printStackTrace();
				} catch (DiscordException e1) {
					// TODO Auto-generated catch block
					instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
					e1.printStackTrace();
				} catch (RateLimitException e1) {
					// TODO Auto-generated catch block
					instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
					e1.printStackTrace();
				}
			}
		}
	}
	
	@EventSubscriber
	public void onJoin(UserJoinEvent e){
		if(instance.getMainConfig().getBoolean("IGNSync")){
			IUser user = e.getUser();
			if(instance.getLinkedConfig().contains(user.getID())){
				Player p = Bukkit.getPlayer((UUID) instance.getLinkedConfig().get(user.getID()));
				if(!p.getName().equals(user.getName())){
					try {
						Discord.client.getGuilds().get(0).setUserNickname(user, p.getName());
					} catch (MissingPermissionsException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot has invalid permisions");
						e1.printStackTrace();
					} catch (DiscordException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Plugin got a disscord exception");
						e1.printStackTrace();
					} catch (RateLimitException e1) {
						// TODO Auto-generated catch block
						instance.getLogger().log(Level.SEVERE, "Bot exceded server rate limit");
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
