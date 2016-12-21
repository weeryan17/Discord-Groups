package com.weeryan17.discord;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

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
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

public class DiscordEventHandler {
	//TODO Remove most of the try and catch cases
	Discord instance;
	public DiscordEventHandler(Discord instance){
		this.instance = instance;
	}
	
	@EventSubscriber
	public void onReadyEvent(ReadyEvent e) throws MissingPermissionsException, DiscordException, RateLimitException {
		instance.getLogger().info("bot ready");
		Collection<IChannel> channels = Discord.client.getChannels();
		IChannel mainChannel;
		for(IChannel channel : channels){
			instance.getLogger().info("Current channel checking: " + channel.getName());
			if(channel.getName().equals(instance.getMainConfig().getString("Channel"))){
				mainChannel = channel;
				mainChannel.sendMessage("Bot ready to do stuff!");
			}
			
		}
		Updater update = new Updater(instance);
		update.updateRoles();
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(instance, new Runnable(){
			@Override
			public void run() {
				instance.getLogger().info("Updating server roles");
				for(Player p : Bukkit.getServer().getOnlinePlayers()){
					update.updatePlayer(p);
				}
				Discord.client.changeStatus(Status.empty());
			}
			
		}, 0L, 1000L);
	}

	static HashMap<Integer, Player> ids = new HashMap<Integer, Player>();
	@EventSubscriber
	public void onMessgaeEvent(MessageReceivedEvent e) throws MissingPermissionsException, DiscordException, RateLimitException {
		IMessage message = e.getMessage();
		String[] command = message.getContent().split(" ");
		if(command[0].equalsIgnoreCase("!register")){
			if(command.length == 2){
				Player p = Bukkit.getPlayer(command[1]);
				if(p!=null){
					message.getChannel().sendMessage(message.getAuthor().getName() + " A message has been sent to you in minecraft with the id you need to link discord and minecraft");
					p.sendMessage(ChatColor.YELLOW + "Your discord registration id is " + ids.size() + 1);
					ids.put(ids.size() + 1, p);
				} else {
					message.getChannel().sendMessage(message.getAuthor().getName() + " The user name you specifyed is invalid. Make sure you're loged onto the server and your user name is correct");
				}
			} else if(command.length == 3 && command[1].equals("confirm")){
				int id;
				try {
					id = Integer.valueOf(command[2]);
				} catch (NumberFormatException e1){
					id = -1;
				}
				if(id <= -1){
					message.getChannel().sendMessage(message.getAuthor().getName() + " You used an invalid number");
				} else {
					if(ids.containsKey(id)){
						Player p = ids.get(id);
						instance.getLinkedConfig().set("Players." + p.getUniqueId().toString(), message.getAuthor().getID());
						instance.getLinkedConfig().set("Discord Users." + message.getAuthor().getID(), p.getUniqueId().toString());
						instance.saveLinkedConfig();
						ids.remove(id);
						message.getChannel().sendMessage(message.getAuthor().getName() + " You minecraft and discord are now linked on this server");
					} else {
						message.getChannel().sendMessage(message.getAuthor().getName() + " You used an invalid number");
					}
				}
			} else {
				message.getChannel().sendMessage(message.getAuthor().getName() + " Your usage of the register command is incorect");
			}
		}
	}
	
	@EventSubscriber
	public void onJoin(UserJoinEvent e) throws MissingPermissionsException, DiscordException, RateLimitException {
		if(instance.getMainConfig().getBoolean("IGNSync")){
			IUser user = e.getUser();
			if(instance.getLinkedConfig().contains(user.getID())){
				Player p = Bukkit.getPlayer((UUID) instance.getLinkedConfig().get(user.getID()));
				if(!p.getName().equals(user.getName())){
					Discord.client.getGuilds().get(0).setUserNickname(user, p.getName());
				}
			}
		}
	}
}
