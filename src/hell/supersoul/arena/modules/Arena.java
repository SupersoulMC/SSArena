package hell.supersoul.arena.modules;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.enums.ASetting;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.events.ArenaEndEvent;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.utils.Region;
import hell.supersoul.arena.waitroom.WRManager;
import hell.supersoul.arena.waitroom.WaitRoom;

public abstract class Arena implements Listener{

	public static ArrayList<Arena> arenas = new ArrayList<>();

	ArrayList<String> deadPlayers = new ArrayList<>();
	String id, displayName;
	WaitRoom waitRoom;
	ArrayList<String> players = new ArrayList<>(), disconnectedPlayers = new ArrayList<>(), spectators = new ArrayList<>(), leftPlayers = new ArrayList<>();
	ArrayList<ASetting> settings = new ArrayList<>();
	AStatus status = AStatus.inGame;
	Boolean hidden = false;
	ArrayList<String> authors = new ArrayList<>();
	HashMap<String, ArrayList<PStatus>> playerStatus = new HashMap<>();
	World world;
	HashMap<ChatColor, ArrayList<String>> teamPlayers = new HashMap<>();

	public Arena(String id) {
		this.id = id;
		arenas.add(this);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	public abstract void startArena();

	public void endArena() {
		ArenaEndEvent event = new ArenaEndEvent(this);
		Bukkit.getServer().getPluginManager().callEvent(event);
		this.sendMessage(Main.prefix + ChatColor.GRAY + "The arena has ended.");
	}

	public abstract void announceResult();

	public abstract void playerRespawn(Player player, DamageType dt, Object... args);

	public abstract void playerDeath(Player player, DamageType dt, Object... args);
	
	public abstract void playerReconnected(Player player);
	
	public abstract void playerDisconnected(Player player);

	public void endGame() {
		HandlerList.unregisterAll(this);
	}
	
	public abstract Scoreboard updateScoreboard();

	// TODO Damage, settings, regions
	public void iniArena(String mapName, World world) {
		this.setDisplayName("TEST");
		//this.setDisplayName(WaitRoomManager.getManager().getMapData(mapName).getDisplayName());
		MyConfig config = Main.getConfigManager().getNewConfig("data/arenas/" + mapName + ".yml");
		this.setAuthors((ArrayList<String>) config.getList("Arena.authors"));
		this.world = world;
		/*
		Boolean team = config.getBoolean("Arena.team");
		Boolean survive = config.getBoolean("Arena.survive");
		if (team)
			this.setTeam(new Team(mapName, world, this));
		if (survive)
			this.setSurvive(new Survive(mapName, world, this));
		*/
	}

	public void sendMessage(String msg) {
		for (String playerName : this.getPlayers()) {
			Bukkit.getPlayer(playerName).sendMessage(msg);
		}
		for (String playerName : this.getSpectators()) {
			Bukkit.getPlayer(playerName).sendMessage(msg);
		}
	}
	
	public void sendTitle(int fadein, int stay, int fadeout, String title, String subTitle) {
		for (String playerName : players) {
			Player player = Bukkit.getPlayer(playerName);
			TitleAPI.sendTitle(player, fadein, stay, fadeout, title, subTitle);
		}
	}

	public void teleport(Location loc) {
		for (String playerName : this.getPlayers()) {
			Bukkit.getPlayer(playerName).teleport(loc);
		}
	}

	public String getPlayerNameWithTeamColor(String playerName) {
		if (!(this instanceof Team))
			return ChatColor.AQUA + playerName;
		else
			for (ChatColor c : this.getTeamPlayers().keySet()) {
				if (this.getTeamPlayers().get(c).contains(playerName))
					return c + playerName;
			}
		return ChatColor.WHITE + playerName;
	}

	public String getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public WaitRoom getWaitRoom() {
		return waitRoom;
	}

	public ArrayList<String> getPlayers() {
		return players;
	}

	public ArrayList<String> getDisconnectedPlayers() {
		return disconnectedPlayers;
	}

	public ArrayList<String> getSpectators() {
		return spectators;
	}

	public ArrayList<ASetting> getSettings() {
		return settings;
	}

	public AStatus getStatus() {
		return status;
	}

	public Boolean getHidden() {
		return hidden;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setWaitRoom(WaitRoom waitRoom) {
		this.waitRoom = waitRoom;
	}

	public void setPlayers(ArrayList<String> players) {
		this.players = players;
	}

	public void setDisconnectedPlayers(ArrayList<String> disconnectedPlayers) {
		this.disconnectedPlayers = disconnectedPlayers;
	}

	public void setStatus(AStatus status) {
		this.status = status;
	}

	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public ArrayList<String> getAuthors() {
		return authors;
	}

	public void setAuthors(ArrayList<String> authors) {
		this.authors = authors;
	}

	public HashMap<String, ArrayList<PStatus>> getPlayerStatus() {
		return playerStatus;
	}
	
	public ArrayList<String> getDeadPlayers() {
		return deadPlayers;
	}
	
	public void setDeadPlayers(ArrayList<String> deadPlayers) {
		this.deadPlayers = deadPlayers;
	}

	public World getWorld() {
		return world;
	}

	public ArrayList<String> getLeftPlayers() {
		return leftPlayers;
	}
	
	public HashMap<ChatColor, ArrayList<String>> getTeamPlayers() {
		return teamPlayers;
	}
	
	public String getTeamPrefix(String playerName) {
		ChatColor team = ChatColor.GRAY;
		for (ChatColor c : this.getTeamPlayers().keySet()) {
			if (this.getTeamPlayers().get(c).contains(playerName)) {
				team = c;
				break;
			}
		}
		if (team.equals(ChatColor.RED))
			return ChatColor.RED + "R";
		else if (team.equals(ChatColor.BLUE))
			return ChatColor.BLUE + "B";
		return ChatColor.GRAY + "-";
	}
	
	public String getTeamFullname(String playerName) {
		ChatColor team = ChatColor.GRAY;
		for (ChatColor c : this.getTeamPlayers().keySet()) {
			if (this.getTeamPlayers().get(c).contains(playerName)) {
				team = c;
				break;
			}
		}
		if (team.equals(ChatColor.RED))
			return ChatColor.RED + "Red";
		else if (team.equals(ChatColor.BLUE))
			return ChatColor.BLUE + "Blue";
		return ChatColor.GRAY + "-";
	}
}
