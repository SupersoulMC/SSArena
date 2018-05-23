package hell.supersoul.arena.waitroom;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.arenas.ArenaManager;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.enums.WStatus;
import hell.supersoul.arena.modules.Arena;
import hell.supersoul.arena.modules.PArena;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.recovery.RecoverManager;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.sound.enums.StopSoundMode;
import hell.supersoul.sound.manager.SoundManager;

public class WRManager {
	private static WRManager wrm = new WRManager();

	public static WRManager getManager() {
		return wrm;
	}

	public void loadMapData() {
		MyConfig config = Main.getConfigManager().getNewConfig("data/mapData.yml");
		for (String string : config.getConfigurationSection("").getKeys(false)) {
			MapData md = new MapData(config.getString(string + ".className"), config.getInt(string + ".difficulty"),
					(ArrayList<String>) config.getList(string + ".desc"));
			md.setAllowArmorAbilitySwitch(config.getBoolean(string + ".allowArmorAbilitySwitch"));
			md.setAllowChooseTeam(config.getBoolean(string + ".allowChooseTeam"));
			md.setAllowChooseTeamSwitch(config.getBoolean(string + ".allowChooseTeamSwitch"));
			md.setAllowLuckyBlocksSwitch(config.getBoolean(string + ".allowLuckyBlockSwitch"));
			md.setLuckyBlocks(config.getBoolean(string + ".luckyBlocks"));
			md.setDefaultMap(config.getBoolean(string + ".defaultMap"));
			md.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString(string + ".displayname")));
			md.setWorldName(config.getString(string + ".worldName"));
			md.setTitleName(string);
			String worldName = config.getString(string + ".worldName");
			for (World world : Bukkit.getWorlds()) {
				if (world.getName().equals(worldName))
					continue;
				if (world.getName().startsWith(worldName))
					AUtils.deleteWorld(world.getName());
			}
		}
	}

	public MapData getMapData(String titleName) {
		for (MapData md : MapData.maps) {
			if (md.getTitleName().equals(titleName))
				return md;
		}
		return null;
	}

	public WaitRoom getWaitRoom(String name) {
		for (WaitRoom w : WaitRoom.waitRoomObjects) {
			if (w.getRoomName().equals(name)) {
				return w;
			}
		}
		return null;
	}

	public WaitRoom getPlayerWaitRoom(Player player) {
		for (WaitRoom w : WaitRoom.waitRoomObjects) {
			if (w.getPlayers().contains(player)) {
				return w;
			}
		}
		return null;
	}

	public WaitRoom createWaitRoom(String mode) {
		int i = 1;
		String t;
		for (;; i++) {
			t = Integer.toString(i);
			if (i < 10)
				t = "0" + t;
			t = "-" + t;
			if (Bukkit.getWorld(mode + t) == null)
				break;
		}
		String roomName = mode + t;
		AUtils.copyAndImportWorld(mode, roomName);
		WaitRoom wr = loadWaitRoom(mode, roomName);
		return wr;
	}

	public void deleteWaitRoom(WaitRoom wr) {
		if (wr.getStatus().equals(WStatus.INGAME)) {
			Arena arena = null;
			for (Arena a : Arena.arenas) {
				if (a.getWaitRoom().equals(wr)) {
					arena = a;
				}
			}
			if (arena != null) {
				Arena.arenas.remove(arena);
				AUtils.deleteWorld(arena.getId());
			}
		}
		WaitRoom.waitRoomObjects.remove(wr);
		AUtils.deleteWorld(wr.getRoomName());
	}

	public void addPlayer(String roomName, String playerName) {
		WaitRoom wr = WRManager.getManager().getWaitRoom(roomName);
		Player player = Bukkit.getPlayer(playerName);
		if (wr != null) {
			if (!wr.isFull()) {
				if (!wr.getStatus().equals(WStatus.INGAME)) {
					if (!wr.getPlayers().contains(player)) {
						if (wr.isUsingInvRecovery()) {
							RecoverManager.get().saveInvandLoc(player);
							WRManager.getManager().setWRInv(player);
							AUtils.unfreezePlayer(player, true);
						} else {
							RecoverManager.get().saveLoc(player);
						}
						player.teleport(wr.getUnreadyLocation());
						wr.getPlayers().add(player);
						String title = "¡±b" + wr.getRoomName();
						String subTitle = "¡±7Click the ready sign when you're ready!";
						TitleAPI.sendTitle(player, 0, 60, 20, title, subTitle);
						wr.sendMessage(Main.prefix + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " joined "
								+ ChatColor.GRAY + "the game!");
						WRManager.getManager().updateWRScoreboard(wr);
						SoundManager.get().stopSound(player, "bgm_ssnpc_village", StopSoundMode.stopAll);
						SoundManager.get().playSound(player, "bgm_ssarena_waitroom", true);
						player.setScoreboard(wr.getScoreBoard());
						if (wr.isAutoReady() && !wr.getReadyPlayers().contains(player))
							WRManager.getManager().playerReady(null, playerName);
					} else
						player.sendMessage(Main.prefix + ChatColor.RED + " You are already in the wait room!");
				} else
					player.sendMessage(Main.prefix + ChatColor.RED + " The arena is in-game!");
			} else
				player.sendMessage(Main.prefix + ChatColor.RED + " The wait room is full!");
		} else
			player.sendMessage(Main.prefix + ChatColor.RED + " wait room does not exist!");
	}

	public void removePlayer(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		Arena arena = ArenaManager.get().getPlayerArena(playerName);
		if (arena != null) {
			ArenaManager.get().removePlayer(player.getName());
		}
		WaitRoom wr = WRManager.getManager().getPlayerWaitRoom(player);
		wr.getReadyPlayers().remove(player);
		wr.getPlayers().remove(player);
		wr.sendMessage(
				Main.prefix + ChatColor.AQUA + playerName + ChatColor.RED + " left " + ChatColor.GRAY + "the game!");
		player.sendMessage(Main.prefix + ChatColor.RED + "You left the game!");
		WRManager.getManager().updateWRScoreboard(wr);
		if (wr.usingInvRecovery) {
			RecoverManager.get().recoverInvandLoc(player);
			player.setHealth(20);
			player.setFoodLevel(20);
			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
		} else {
			RecoverManager.get().recoverLoc(player);
		}
		if (wr.getPlayers().size() <= 0) {
			WRManager.getManager().deleteWaitRoom(wr);
			return;
		} else if (wr.getRoomOwnerName().equals(playerName)) {
			if (arena == null) {
				Player one = wr.getPlayers().get(new Random().nextInt(wr.getPlayers().size()));
				WRManager.getManager().setRoomOwner(playerName, one.getName());
			}
		}
	}

	public void playerReady(ChatColor team, String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		WaitRoom wr = getPlayerWaitRoom(player);
		if (!wr.isFull()) {
			if (wr.getReadyPlayers().contains(player))
				return;
			wr.getReadyPlayers().add(player);
			if (wr.isTeam()) {
				if (!wr.getTeamReadyPlayers().containsKey(team))
					wr.getTeamReadyPlayers().put(team, new ArrayList<>());
				wr.getTeamReadyPlayers().get(team).add(player);
			}
			player.teleport(wr.getReadyLocation());
			wr.sendMessage(Main.prefix + teamColor(player) + player.getName() + ChatColor.GRAY + " is now "
					+ ChatColor.GREEN + "ready!");
			player.getInventory().setItem(4, null);
			WRManager.getManager().updateWRScoreboard(wr);

		}
	}

	public void playerUnready(String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		WaitRoom wr = WRManager.getManager().getPlayerWaitRoom(player);
		if (wr != null) {
			if (!wr.getStatus().equals(WStatus.INGAME) && !wr.getStatus().equals(WStatus.COUNTING)) {
				/*
				 * if (wr.isTeam()) { if
				 * (ArenaManager.getManager().getPlayerTeam(player).equals("red")) {
				 * wr.getRedReadyPlayers().remove(player); } else if
				 * (ArenaManager.getManager().getPlayerTeam(player).equals("blue")) {
				 * wr.getBlueReadyPlayers().remove(player); } }
				 */
				wr.getReadyPlayers().remove(player);
				player.teleport(wr.getUnreadyLocation());
				WRManager.getManager().updateWRScoreboard(wr);
				WRManager.getManager().setWRInv(player);
				wr.sendMessage(Main.prefix + ChatColor.AQUA + playerName + ChatColor.GRAY + " is " + ChatColor.RED
						+ "not ready " + ChatColor.GRAY + "yet!");
			} else {
				player.sendMessage(Main.prefix + ChatColor.RED + "Game already starting!");
			}
		}
	}

	public void setRoomOwner(String old, String playerName) {
		WaitRoom wr = WRManager.getManager().getPlayerWaitRoom(Bukkit.getPlayer(playerName));
		if (wr == null)
			return;
		if (old != null) {
			Player oplayer = Bukkit.getPlayer(old);
			if (oplayer != null) {
				wr.getReadyPlayers().remove(oplayer);
				for (ChatColor team : wr.getTeamReadyPlayers().keySet())
					wr.getTeamReadyPlayers().get(team).remove(oplayer);
				oplayer.teleport(wr.getUnreadyLocation());
			}
		}
		Player player = Bukkit.getPlayer(playerName);
		// TODO add to random team
		wr.getReadyPlayers().add(player);
		player.teleport(wr.getRoomOwnerLocation());
		wr.setRoomOwnerName(playerName);
		wr.sendMessage(Main.prefix + ChatColor.AQUA + playerName + ChatColor.GRAY + " is now the " + ChatColor.AQUA
				+ "room owner" + ChatColor.GRAY + "!");
	}

	public void preCountDown(WaitRoom wr) {
		if (wr.getStatus().equals(WStatus.COUNTING))
			return;
		wr.setStatus(WStatus.COUNTING);
		if (wr.isTeam()) {
			int size = wr.getAvailableTeams().size();
			for (ChatColor team : wr.getAvailableTeams()) {
				if (wr.getTeamReadyPlayers().get(team).size() >= wr.getMinPlayers() / wr.getAvailableTeams().size())
					size--;
			}
			if (size == 0) {
				WRManager.getManager().countDown(wr);
				return;
			}
		} else {
			int playersLeft = wr.getMinPlayers() - wr.getReadyPlayers().size();
			if (playersLeft <= 0) {
				WRManager.getManager().countDown(wr);
				return;
			}
		}
		wr.setStatus(WStatus.WAITING);
		Bukkit.getPlayer(wr.getRoomOwnerName())
				.sendMessage(Main.prefix + ChatColor.RED + "We don't have enough players to start!");
	}

	public void countDown(WaitRoom wr) {
		wr.setMode("starting");
		for (Player aPlayer : wr.getPlayers()) {
			String title = "¡±eStarting...";
			TitleAPI.sendTitle(aPlayer, 0, 500, 0, title, "");
		}
		new BukkitRunnable() {
			int i = 5;

			public void run() {
				wr.sendMessage(Main.prefix + ChatColor.GRAY + "Game starting in " + ChatColor.AQUA + i + ChatColor.GRAY
						+ " second(s)!");
				i--;
				if (i <= 0) {
					boolean start = true;
					if (!wr.isTeam() && wr.getReadyPlayers().size() < wr.getMinPlayers())
						start = false;
					if (wr.isTeam()) {
						int size = wr.getAvailableTeams().size();
						for (ChatColor team : wr.getAvailableTeams()) {
							if (wr.getTeamReadyPlayers().get(team) == null) {
								// TODO should be continue;
								size--;
								continue;
							}
							if (wr.getTeamReadyPlayers().get(team).size() >= wr.getMinPlayers()
									/ wr.getAvailableTeams().size())
								size--;
						}
						if (size > 0)
							start = false;
					}
					if (!start) {
						wr.sendMessage(Main.prefix + ChatColor.RED + "Not enough players to start!");
						wr.setMode("waiting");
						for (Player aPlayer : wr.getPlayers()) {
							String title = "¡±4Cancelled!";
							String subTitle = "¡±7Not enough players to start!";
							TitleAPI.sendTitle(aPlayer, 0, 60, 5, title, subTitle);
						}
						cancel();
					} else {
						cancel();
						startGame(wr);
					}
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}

	public void startGame(WaitRoom wr) {
		if (wr.getPlayers().size() <= 0) {
			WRManager.getManager().deleteWaitRoom(wr);
			return;
		}
		int i = 1;
		String t;
		String name = wr.getSelectedMap().getWorldName();
		/*
		 * if (name.equals("Random")) { name = wr.getMapList().get(new
		 * Random().nextInt(wr.getMapList().size())); }
		 */
		for (;; i++) {
			t = Integer.toString(i);
			if (i < 10)
				t = "0" + t;
			t = "-" + t;
			if (Bukkit.getWorld(name + t) == null)
				break;
		}
		String worldName = name + t;
		Class<?> clazz;
		try {
			clazz = Class.forName(wr.getSelectedMap().getClassName());
		} catch (ClassNotFoundException e) {
			wr.sendMessage(Main.prefix + ChatColor.RED + "Error: ClassNotFound, please report to HellSS.");
			e.printStackTrace();
			return;
		}
		Constructor<?> constructor;
		try {
			constructor = clazz.getConstructor(String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			wr.sendMessage(Main.prefix + ChatColor.RED + "Error: NoSuchMethod / Security, please report to HellSS.");
			e.printStackTrace();
			return;
		}
		Arena arena = null;
		try {
			arena = (Arena) constructor.newInstance(worldName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			wr.sendMessage(Main.prefix + ChatColor.RED + "Error: ClassCreateFailed, please report to HellSS.");
			e.printStackTrace();
			return;
		}
		if (arena instanceof PArena)
			AUtils.copyAndImportWorld(name, worldName);
		arena.iniArena(wr.getSelectedMap().getTitleName(), Bukkit.getWorld(worldName));
		for (Player player : wr.getPlayers()) {
			if (wr.getReadyPlayers().contains(player))
				continue;
			String playerName = player.getName();
			player.sendMessage(Main.prefix + ChatColor.RED + "Game started but you're not ready!");
			wr.sendMessage(Main.prefix + ChatColor.AQUA + playerName + ChatColor.GRAY
					+ " has been kicked because he is " + ChatColor.RED + "not ready" + ChatColor.GRAY + " yet!");
		}
		if (wr.isTeam()) {
			for (ChatColor team : wr.getAvailableTeams())
				arena.getTeamPlayers().put(team, new ArrayList<>());
			for (ChatColor team : wr.getTeamReadyPlayers().keySet()) {
				for (Player player : wr.getTeamReadyPlayers().get(team)) {
					arena.getTeamPlayers().get(team).add(player.getName());
				}
			}
		}
		arena.setWaitRoom(wr);
		for (Player player : wr.getPlayers()) {
			arena.getPlayers().add(player.getName());
			if (arena instanceof Race)
				((Race) arena).getPlayerCheckPoints().put(player.getName(), 0);
		}
		wr.setStatus(WStatus.INGAME);
		arena.startArena();
	}

	public void changeMap(WaitRoom wr, String titleName) {
		wr.setSelectedMap(WRManager.getManager().getMapData(titleName));
		WRManager.getManager().updateWRScoreboard(wr);
		Inventory inv = wr.getMapMenu();
		inv.clear();
		ArrayList<MapData> maps = wr.getMapList();
		int i = 0;
		for (MapData md : maps) {
			ItemStack is = new ItemStack(Material.WOOL);
			ItemMeta im = is.getItemMeta();
			if (md.getTitleName().equals(titleName)) {
				is.setDurability((short) 5);
				im.setDisplayName(ChatColor.GREEN + md.getTitleName());

			} else {
				is.setDurability((short) 8);
				im.setDisplayName(ChatColor.GRAY + md.getTitleName());
			}
			int d = md.getDifficulty();
			ChatColor color;
			if (d <= 3)
				color = ChatColor.GREEN;
			else if (d <= 6)
				color = ChatColor.GOLD;
			else if (d <= 9)
				color = ChatColor.RED;
			else
				color = ChatColor.DARK_RED;
			String diff = StringUtils.repeat(color + "¡¹ ", d);
			List<String> lore = new ArrayList<String>();
			lore.add(diff);
			im.setLore(lore);
			is.setItemMeta(im);
			wr.getMapMenu().setItem(i, is);
			i++;
		}
	}

	public void setWRInv(Player player) {
		player.getInventory().clear();
		ItemStack quit = new ItemStack(Material.BARRIER);
		ItemMeta quitMeta = quit.getItemMeta();
		quitMeta.setDisplayName(ChatColor.RED + "Quit Game");
		quit.setItemMeta(quitMeta);
		Dye jumpDye = new Dye();
		jumpDye.setColor(DyeColor.GRAY);
		ItemStack jump = jumpDye.toItemStack(1);
		ItemMeta jumpMeta = jump.getItemMeta();
		jumpMeta.setDisplayName(ChatColor.GRAY + "Double Jump");
		jump.setItemMeta(jumpMeta);
		ItemStack chest = new ItemStack(Material.CHEST);
		ItemMeta chestMeta = chest.getItemMeta();
		chestMeta.setDisplayName(ChatColor.AQUA + "Inventory");
		chest.setItemMeta(chestMeta);
		player.getInventory().setItem(0, jump);
		player.getInventory().setItem(8, quit);
		player.getInventory().setItem(4, chest);
	}

	public void updateWRScoreboard(WaitRoom wr) {
		new BukkitRunnable() {
			public void run() {
				Scoreboard wrBoard = wr.getScoreBoard();
				Objective wrObjective = wr.getObjective();
				for (String score : wrBoard.getEntries()) {
					wrBoard.resetScores(score);
				}
				wrObjective.getScore(ChatColor.RESET.toString()).setScore(8);
				wrObjective.getScore(ChatColor.GRAY + "Map: " + ChatColor.AQUA + wr.getSelectedMap().getDisplayName())
						.setScore(7);
				if (wr.getRoomOwnerName() == null)
					wrObjective.getScore(ChatColor.GRAY + "Room Owner: " + ChatColor.RED + "N/A").setScore(6);
				else
					wrObjective.getScore(ChatColor.GRAY + "Room Owner: " + ChatColor.AQUA + wr.getRoomOwnerName())
							.setScore(6);
				wrObjective.getScore(ChatColor.UNDERLINE.toString()).setScore(5);
				wrObjective.getScore(ChatColor.GRAY + "Min Players: " + ChatColor.DARK_GREEN + wr.getMinPlayers())
						.setScore(4);
				wrObjective.getScore(ChatColor.GRAY + "Non-ready Players: " + ChatColor.RED
						+ (wr.getPlayers().size() - wr.getReadyPlayers().size())).setScore(3);
				if (wr.isTeam()) {
					String str = "";
					for (ChatColor c : wr.getAvailableTeams()) {
						str = str + c + "" + wr.getTeamReadyPlayers().get(c).size() + ChatColor.GRAY + " / ";
					}
					str = str.substring(0, str.length() - 3);
					wrObjective.getScore(ChatColor.GRAY + "Ready Players: " + str).setScore(2);
				} else
					wrObjective
							.getScore(
									ChatColor.GRAY + "Ready Players: " + ChatColor.GREEN + wr.getReadyPlayers().size())
							.setScore(2);
				wrObjective.getScore(ChatColor.GRAY + "Max Players: " + ChatColor.DARK_RED + wr.getMaxPlayers())
						.setScore(1);
			}
		}.runTaskLater(Main.getInstance(), 5);
	}

	public String teamColor(Player player) {
		WaitRoom wr = WRManager.getManager().getPlayerWaitRoom(player);
		if (wr.isTeam()) {
			for (ChatColor team : wr.getTeamReadyPlayers().keySet()) {
				if (wr.getTeamReadyPlayers().get(team).contains(player))
					return team.toString();
			}
		}
		return "¡±b";
	}

	public void gameEnded(Arena arena, Player winner) {
		WaitRoom wr = arena.getWaitRoom();
		if (wr.isAutoDisband()) {
			Iterator<String> itr = arena.getPlayers().iterator();
			while (itr.hasNext()) {
				String playerName = itr.next();
				itr.remove();
				WRManager.getManager().removePlayer(playerName);
			}
			WRManager.getManager().deleteWaitRoom(wr);
		} else {
			wr.getPlayers().clear();
			wr.getReadyPlayers().clear();
			wr.setStatus(WStatus.WAITING);
			for (ChatColor c : wr.getAvailableTeams())
				wr.getTeamReadyPlayers().get(c).clear();
			for (String playerName : arena.getPlayers()) {
				if (Bukkit.getPlayer(playerName).isOnline())
					WRManager.getManager().addPlayer(wr.getRoomName(), playerName);
			}
			if (winner == null || !winner.isOnline()) {
				winner = wr.getPlayers().get(new Random().nextInt(wr.getPlayers().size()));
			}
			WRManager.getManager().setRoomOwner(wr.getRoomOwnerName(), winner.getName());
		}
		if (arena instanceof PArena)
			AUtils.deleteWorld(arena.getId());
		Arena.arenas.remove(arena);
	}

	public static WaitRoom loadWaitRoom(String mode, String roomName) {
		int x, y, z, yaw;
		Location loc;
		boolean team = false;
		MyConfig waitRoomData = Main.getConfigManager().getNewConfig("data/waitRoomData.yml",
				new String[] { "SS Arena waitRoomData file" });
		WaitRoom wr = new WaitRoom(mode, roomName);
		if (waitRoomData.getBoolean(mode + ".team")) {
			team = true;
			wr.setTeam(true);
		}
		if (waitRoomData.getBoolean(mode + ".allowMapChoosing"))
			wr.setAllowMapChoosing(true);
		if (waitRoomData.getBoolean(mode + ".autoReady"))
			wr.setAutoReady(true);
		if (waitRoomData.getBoolean(mode + ".allowUnready"))
			wr.setAllowUnready(true);
		if (waitRoomData.getBoolean(mode + ".autoDisband"))
			wr.setAutoDisband(true);
		wr.setUsingInvRecovery(waitRoomData.getBoolean(mode + ".usingInvRecovery"));
		x = waitRoomData.getInt(mode + ".unreadyLocation.x");
		y = waitRoomData.getInt(mode + ".unreadyLocation.y");
		z = waitRoomData.getInt(mode + ".unreadyLocation.z");
		yaw = waitRoomData.getInt(mode + ".unreadyLocation.yaw");
		loc = AUtils.normalizeLocation(new Location(Bukkit.getWorld(roomName), x, y, z, yaw, 0));
		wr.setUnreadyLocation(loc);
		x = waitRoomData.getInt(mode + ".quitLocation.x");
		y = waitRoomData.getInt(mode + ".quitLocation.y");
		z = waitRoomData.getInt(mode + ".quitLocation.z");
		yaw = waitRoomData.getInt(mode + ".quitLocation.yaw");
		loc = AUtils.normalizeLocation(new Location(Bukkit.getWorld(roomName), x, y, z, yaw, 0));
		wr.setQuitLocation(loc);
		x = waitRoomData.getInt(mode + ".roomOwnerLocation.x");
		y = waitRoomData.getInt(mode + ".roomOwnerLocation.y");
		z = waitRoomData.getInt(mode + ".roomOwnerLocation.z");
		yaw = waitRoomData.getInt(mode + ".roomOwnerLocation.yaw");
		loc = AUtils.normalizeLocation(new Location(Bukkit.getWorld(roomName), x, y, z, yaw, 0));
		wr.setRoomOwnerLocation(loc);
		x = waitRoomData.getInt(mode + ".mapCharLocation.x");
		y = waitRoomData.getInt(mode + ".mapCharLocation.y");
		z = waitRoomData.getInt(mode + ".mapCharLocation.z");
		wr.setMapCharLocation(new Location(Bukkit.getWorld(roomName), x, y, z));
		x = waitRoomData.getInt(mode + ".countDownCharLocation.x");
		y = waitRoomData.getInt(mode + ".countDownCharLocation.y");
		z = waitRoomData.getInt(mode + ".countDownCharLocation.z");
		wr.setCountDownCharLocation(new Location(Bukkit.getWorld(roomName), x, y, z));
		x = waitRoomData.getInt(mode + ".readyLocation.x");
		y = waitRoomData.getInt(mode + ".readyLocation.y");
		z = waitRoomData.getInt(mode + ".readyLocation.z");
		yaw = waitRoomData.getInt(mode + ".readyLocation.yaw");
		loc = AUtils.normalizeLocation(new Location(Bukkit.getWorld(roomName), x, y, z, yaw, 0));
		wr.setReadyLocation(loc);

		ArrayList<String> maps = (ArrayList<String>) waitRoomData.getList(mode + ".mapList");
		for (String string : maps) {
			MapData md = WRManager.getManager().getMapData(string);
			wr.getMapList().add(md);
			if (md.isDefaultMap())
				wr.setSelectedMap(md);
		}
		int no;
		no = waitRoomData.getInt(mode + ".minPlayers");
		wr.setMinPlayers(no);
		no = waitRoomData.getInt(mode + ".maxPlayers");
		wr.setMaxPlayers(no);
		no = waitRoomData.getInt(mode + ".charDirection");
		if (wr.isTeam()) {
			ArrayList<String> list = (ArrayList<String>) waitRoomData.getList(mode + ".availableTeams");
			for (String str : list) {
				ChatColor c = ChatColor.valueOf(str);
				if (c == null)
					continue;
				wr.getAvailableTeams().add(c);
				wr.getTeamReadyPlayers().put(c, new ArrayList<>());
			}
		}
		// TODO Chars Direction
		int i = 0;
		for (String map : maps) {
			ItemStack is = new ItemStack(Material.WOOL);
			ItemMeta im = is.getItemMeta();
			if (map.equals("Random")) {
				is.setDurability((short) 5);
				im.setDisplayName(ChatColor.GREEN + map);

			} else {
				is.setDurability((short) 8);
				im.setDisplayName(ChatColor.GRAY + map);
			}

			int d = Main.getMainConfig().getInt("difficulty." + map);
			ChatColor color;
			if (d <= 3)
				color = ChatColor.GREEN;
			else if (d <= 6)
				color = ChatColor.GOLD;
			else if (d <= 9)
				color = ChatColor.RED;
			else
				color = ChatColor.DARK_RED;
			String diff = StringUtils.repeat(color + "¡¹ ", d);
			List<String> lore = new ArrayList<String>();
			lore.add(diff);
			im.setLore(lore);
			is.setItemMeta(im);
			wr.getMapMenu().setItem(i, is);
			i++;
		}
		return wr;
	}
}
