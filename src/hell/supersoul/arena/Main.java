package hell.supersoul.arena;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import hell.supersoul.arena.arenas.ArenaManager;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.config.MyConfigManager;
import hell.supersoul.arena.modules.Arena;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.waitroom.WREventListener;
import hell.supersoul.arena.waitroom.WRManager;
import hell.supersoul.arena.waitroom.WaitRoom;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
	static Main instance;
	public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "SS " + ChatColor.RED + "Arena" + ChatColor.DARK_GRAY + "] ";
	public static ArrayList<Listener> toRegister = new ArrayList<>();
	static MyConfig config;
	static MyConfigManager manager;

	@Override
	public void onEnable() {
		instance = this;
		manager = new MyConfigManager(this);
		config = manager.getNewConfig("config.yml", new String[] { "SS Arena config file" });
		WRManager.getManager().loadMapData();
		MyConfig waitRoomData = Main.manager.getNewConfig("data/waitRoomData.yml", new String[] { "SS Arena waitRoomData file" });
		Bukkit.getPluginManager().registerEvents(new WREventListener(), this);
		for (String worldName : waitRoomData.getConfigurationSection("").getKeys(false)) {
			for (World world : Bukkit.getWorlds()) {
				if (world.getName().equals(worldName))
					continue;
				if (world.getName().startsWith(worldName))
					AUtils.deleteWorld(world.getName());
			}
		}
	}

	@Override
	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player))
			return false;
		Player player = (Player) sender;
		if (cmd.getName().equals("test")) {
			
			WaitRoom wr = WRManager.getManager().createWaitRoom("IceWR");
			WRManager.getManager().changeMap(wr, "IceRealm");
			WRManager.getManager().addPlayer(wr.getRoomName(), player.getName());
			WRManager.getManager().playerReady(null, player.getName());
			WRManager.getManager().countDown(wr);
			
			/*
			WaitRoom wr = WRManager.getManager().createWaitRoom("EventTeamWR");
			WRManager.getManager().changeMap(wr, "TheMathKingE");
			int temp = Bukkit.getOnlinePlayers().size() / 2;
			int count = 1;
			for (Player p : Bukkit.getOnlinePlayers()) {
				WRManager.getManager().addPlayer(wr.getRoomName(), p.getName());
				if (count <= temp)
					WRManager.getManager().playerReady(ChatColor.BLUE, p.getName());
				else
					WRManager.getManager().playerReady(ChatColor.RED, p.getName());
				count++;
			}
			WRManager.getManager().countDown(wr);
			*/
			
			/*
			WaitRoom wr = WRManager.getManager().createWaitRoom("EventTeamWR");
			WRManager.getManager().changeMap(wr, "Teamwork");
			int temp = Bukkit.getOnlinePlayers().size() / 2;
			int count = 1;
			for (Player p : Bukkit.getOnlinePlayers()) {
				WRManager.getManager().addPlayer(wr.getRoomName(), p.getName());
				//if (count <= temp)
				if (count % 2 == 0)
					WRManager.getManager().playerReady(ChatColor.BLUE, p.getName());
				else
					WRManager.getManager().playerReady(ChatColor.RED, p.getName());
				count++;
			}
			WRManager.getManager().countDown(wr);
			*/
			/*
			WaitRoom wr = WRManager.getManager().createWaitRoom("EventTeamWR");
			WRManager.getManager().changeMap(wr, "ZombieApocalypse");
			int temp = Bukkit.getOnlinePlayers().size() / 2;
			int count = 1;
			for (Player p : Bukkit.getOnlinePlayers()) {
				WRManager.getManager().addPlayer(wr.getRoomName(), p.getName());
				//if (count <= temp)
				if (count % 2 == 0)
					WRManager.getManager().playerReady(ChatColor.BLUE, p.getName());
				else
					WRManager.getManager().playerReady(ChatColor.RED, p.getName());
				count++;
			}
			WRManager.getManager().countDown(wr);
			*/
		} else if (cmd.getName().equals("leave")) {
			WaitRoom wr = WRManager.getManager().getPlayerWaitRoom(player);
			if (wr == null)
				return false;
			WRManager.getManager().removePlayer(player.getName());
		}
		
		return false;
	}

	public static Main getInstance() {
		return instance;
	}

	public static MyConfigManager getConfigManager() {
		return manager;
	}

	public static MyConfig getMainConfig() {
		return config;
	}
}
