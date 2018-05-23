package hell.supersoul.arena.waitroom;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import hell.supersoul.arena.enums.InvName;
import hell.supersoul.arena.enums.WStatus;

public class WaitRoom {
	public static ArrayList<WaitRoom> waitRoomObjects = new ArrayList<>();

	ArrayList<Player> players = new ArrayList<>();
	ArrayList<Player> readyPlayers = new ArrayList<>();
	HashMap<ChatColor, ArrayList<Player>> teamReadyPlayers = new HashMap<>();
	ArrayList<MapData> mapList = new ArrayList<>();
	String roomName, roomOwnerName, mode, boardDirection;
	MapData selectedMap;
	WStatus status = WStatus.WAITING;
	Location quitLocation, unreadyLocation, readyLocation, roomOwnerLocation, mapCharLocation, countDownCharLocation;
	int minPlayers, maxPlayers;
	boolean team, allowMapChoosing, autoReady, allowUnready, autoDisband, usingInvRecovery;
	Inventory mapMenu = Bukkit.createInventory(null, 27, ChatColor.BLACK + "Map Menu");
	Inventory playerMenu = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Room Player Menu");
	Inventory teamMenu = Bukkit.createInventory(null, 9, InvName.TeamMenu.toString());
	Scoreboard scoreBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
	Objective objective = scoreBoard.registerNewObjective("wr", "dummy");
	ArrayList<ChatColor> availableTeams = new ArrayList<>();

	protected WaitRoom(String mode, String roomName) {
		this.mode = mode;
		this.roomName = roomName;
		objective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + this.roomName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		waitRoomObjects.add(this);
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Player> getReadyPlayers() {
		return readyPlayers;
	}

	public String getRoomName() {
		return roomName;
	}

	public MapData getSelectedMap() {
		return selectedMap;
	}

	public String getRoomOwnerName() {
		return roomOwnerName;
	}

	public String getMode() {
		return mode;
	}

	public Location getReadyLocation() {
		return readyLocation;
	}

	public Location getRoomOwnerLocation() {
		return roomOwnerLocation;
	}

	public int getMinPlayers() {
		return minPlayers;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public void setReadyPlayers(ArrayList<Player> readyPlayers) {
		this.readyPlayers = readyPlayers;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void setSelectedMap(MapData selectedMap) {
		this.selectedMap = selectedMap;
	}

	public void setRoomOwnerName(String roomOwnerName) {
		this.roomOwnerName = roomOwnerName;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setReadyLocation(Location readyLocation) {
		this.readyLocation = readyLocation;
	}

	public void setRoomOwnerLocation(Location roomOwnerLocation) {
		this.roomOwnerLocation = roomOwnerLocation;
	}

	public void setMinPlayers(int minPlayers) {
		this.minPlayers = minPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public Location getUnreadyLocation() {
		return unreadyLocation;
	}

	public void setUnreadyLocation(Location unreadyLocation) {
		this.unreadyLocation = unreadyLocation;
	}

	public Inventory getMapMenu() {
		return mapMenu;
	}

	public Inventory getPlayerMenu() {
		return playerMenu;
	}

	public void setMapMenu(Inventory mapMenu) {
		this.mapMenu = mapMenu;
	}

	public void setPlayerMenu(Inventory playerMenu) {
		this.playerMenu = playerMenu;
	}

	public WStatus getStatus() {
		return status;
	}

	public void setStatus(WStatus status) {
		this.status = status;
	}

	public boolean isFull() {
		if (this.getReadyPlayers().size() >= this.maxPlayers)
			return true;
		return false;
	}

	public void sendMessage(String message) {
		for (Player player : this.getPlayers()) {
			player.sendMessage(message);
		}
	}

	public Scoreboard getScoreBoard() {
		return scoreBoard;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setScoreBoard(Scoreboard scoreBoard) {
		this.scoreBoard = scoreBoard;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}

	public boolean isTeam() {
		return team;
	}

	public boolean isAllowMapChoosing() {
		return allowMapChoosing;
	}

	public boolean isAutoReady() {
		return autoReady;
	}

	public boolean isAllowUnready() {
		return allowUnready;
	}

	public void setTeam(boolean team) {
		this.team = team;
		if (team) {
			Inventory inv = this.getTeamMenu();
			inv.clear();
			ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
			ItemStack blue = new ItemStack(Material.WOOL, 1, (short) 11);
			ItemMeta rm = red.getItemMeta();
			ItemMeta bm = blue.getItemMeta();
			rm.setDisplayName(ChatColor.RED + "Red Team");
			bm.setDisplayName(ChatColor.BLUE + "Blue Team");
			red.setItemMeta(rm);
			blue.setItemMeta(bm);
			inv.setItem(0, red);
			inv.setItem(1, blue);
		}
	}

	public void setAllowMapChoosing(boolean allowMapChoosing) {
		this.allowMapChoosing = allowMapChoosing;
	}

	public void setAutoReady(boolean autoReady) {
		this.autoReady = autoReady;
	}

	public void setAllowUnready(boolean allowUnready) {
		this.allowUnready = allowUnready;
	}

	public ArrayList<MapData> getMapList() {
		return mapList;
	}

	public void setMapList(ArrayList<MapData> mapList) {
		this.mapList = mapList;
	}

	public String getBoardDirection() {
		return boardDirection;
	}

	public Location getMapCharLocation() {
		return mapCharLocation;
	}

	public Location getCountDownCharLocation() {
		return countDownCharLocation;
	}

	public void setBoardDirection(String boardDirection) {
		this.boardDirection = boardDirection;
	}

	public void setMapCharLocation(Location mapCharLocation) {
		this.mapCharLocation = mapCharLocation;
	}

	public void setCountDownCharLocation(Location countDownCharLocation) {
		this.countDownCharLocation = countDownCharLocation;
	}

	public Location getQuitLocation() {
		return quitLocation;
	}

	public void setQuitLocation(Location quitLocation) {
		this.quitLocation = quitLocation;
	}

	public Inventory getTeamMenu() {
		return teamMenu;
	}

	public void setTeamMenu(Inventory teamMenu) {
		this.teamMenu = teamMenu;
	}

	public HashMap<ChatColor, ArrayList<Player>> getTeamReadyPlayers() {
		return teamReadyPlayers;
	}

	public ArrayList<ChatColor> getAvailableTeams() {
		return availableTeams;
	}

	public boolean isAutoDisband() {
		return autoDisband;
	}

	public void setAutoDisband(boolean autoDisband) {
		this.autoDisband = autoDisband;
	}

	public boolean isUsingInvRecovery() {
		return usingInvRecovery;
	}

	public void setUsingInvRecovery(boolean usingInvRecovery) {
		this.usingInvRecovery = usingInvRecovery;
	}
}
