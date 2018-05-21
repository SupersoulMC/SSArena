package hell.supersoul.arena.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.enums.ASetting;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.events.PlayerReachGoalEvent;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.utils.Region;
import hell.supersoul.arena.waitroom.WRManager;
import hell.supersoul.sound.enums.StopSoundMode;
import hell.supersoul.sound.manager.SoundManager;
import net.minecraft.server.v1_11_R1.SoundCategory;

public abstract class Race extends PArena implements Listener {
	public Race(String id) {
		super(id);
		//Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		jumpDye.setColor(DyeColor.LIME);
		jump = jumpDye.toItemStack(1);
		ItemMeta jumpMeta = jump.getItemMeta();
		jumpMeta.setDisplayName(ChatColor.GREEN + "Double Jump");
		jump.setItemMeta(jumpMeta);
		nonjumpDye.setColor(DyeColor.GRAY);
		nonjump = nonjumpDye.toItemStack(1);
		ItemMeta nonjumpMeta = nonjump.getItemMeta();
		nonjumpMeta.setDisplayName(ChatColor.GRAY + "Double Jump");
		nonjump.setItemMeta(nonjumpMeta);
	}

	Region startPlatform, goal;
	ArrayList<Location> luckyBlocks = new ArrayList<>();
	ArrayList<Region> checkPoints = new ArrayList<>();
	HashMap<String, Integer> playerCheckPoints = new HashMap<>();
	HashMap<String, Integer> finishPercent = new HashMap<>();
	HashMap<String, Long> finishTime = new HashMap<>();
	ArrayList<String> ranks = new ArrayList<>();
	ArrayList<String> unfinishedRanks = new ArrayList<>();
	ArrayList<Location> respawnPoints = new ArrayList<>();
	ArrayList<Location> placeLocations = new ArrayList<>();
	Location startingLocation;
	long startTime;
	HashMap<String, Long> endTime = new HashMap<>();
	double rewardMul;
	double timer;
	int deathLevel;
	ArrayList<String> allowDJ = new ArrayList<>();
	ArrayList<String> DJed = new ArrayList<>();
	Dye jumpDye = new Dye();
	Dye nonjumpDye = new Dye();
	ItemStack jump = jumpDye.toItemStack(1);
	ItemStack nonjump = nonjumpDye.toItemStack(1);
	HashMap<ChatColor, ArrayList<Location>> teamRespawnPoints = new HashMap<>();

	@Override
	public Scoreboard updateScoreboard() {
		Scoreboard arenaBoard;
		Objective arenaObjective;
		arenaBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		arenaObjective = arenaBoard.registerNewObjective("arena", "dummy");
		arenaObjective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + this.getDisplayName());
		arenaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		double time = this.getTimer();
		if (time > -1) {
			time = time - 0.5d;
			this.setTimer(time);
			if (time > 0)
				arenaObjective.getScore(ChatColor.GREEN + "Time: " + time).setScore(540);
			if (this.getTimer() == 0) {
				for (String playerName : this.getPlayers()) {
					this.playerDamage(Bukkit.getPlayer(playerName), DamageType.TIME);
				}
			}
		}
		arenaObjective.getScore(ChatColor.BOLD.toString()).setScore(500);
		arenaObjective.getScore(ChatColor.DARK_AQUA + "Ranks(%):").setScore(490);
		Map<String, Integer> ranks = new LinkedHashMap<String, Integer>();
		for (String playerName : this.getPlayers()) {
			ranks.put(playerName, AUtils.finishPercent(Bukkit.getPlayer(playerName)));
		}
		ranks = AUtils.sortHashMapByValuesD(ranks);
		for (String playerName : this.getDisconnectedPlayers()) {
			arenaObjective.getScore(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + playerName).setScore(this.getFinishPercent().get(playerName));
		}
		this.getUnfinishedRanks().clear();
		for (String playerName : ranks.keySet()) {
			this.getUnfinishedRanks().add(playerName);
		}
		for (String playerName : this.getPlayers()) {
			if (!Bukkit.getPlayer(playerName).isOnline())
				continue;
			String color = ChatColor.WHITE + "";
			String team = "";
			String nameColor = ChatColor.AQUA + "";
			int score = 0;
			int rankNumber = 99;
			Player player = Bukkit.getPlayer(playerName);
			int size = ranks.keySet().size();
			int pos = this.getUnfinishedRanks().indexOf(playerName);
			if (pos == 0) {
				color = ChatColor.GOLD + "";
			} else if (pos == 1) {
				color = ChatColor.GRAY + "";
			} else if (pos == 2) {
				color = ChatColor.DARK_RED + "";
			}
			if (this instanceof Team)
				team = ChatColor.DARK_GRAY + "[" + this.getTeamPrefix(playerName) + ChatColor.DARK_GRAY + "] ";
			if (this instanceof Survive && this.getDeadPlayers().contains(playerName)) {
				nameColor = ChatColor.RED + "";
			}
			if (this.getRanks().contains(playerName)) {
				nameColor = ChatColor.GREEN + "";
				score = 150 - this.getRanks().indexOf(playerName);
			} else if (this.getStatus().equals(AStatus.over) && !this.getRanks().contains(player.getName())) {
				nameColor = ChatColor.RED + "";
				score = ranks.get(playerName);
			} else {
				score = ranks.get(playerName);
			}
			rankNumber = pos + 1;
			arenaObjective.getScore(ChatColor.DARK_GRAY + "[" + color + rankNumber + ChatColor.DARK_GRAY + "] " + team + nameColor + playerName).setScore(score);
		}
		for (String playerName : this.getPlayers()) {
			Bukkit.getPlayer(playerName).setScoreboard(arenaBoard);
		}
		return arenaBoard;
	}

	@Override
	public void playerRespawn(Player player, DamageType dt, Object... args) {
		if (this.getDeadPlayers().contains(player.getName()))
			return;
		if (this.getPlayerStatus().get(player.getName()).contains(PStatus.INVINCIBLE))
			return;
		int checkpointID = this.getPlayerCheckPoints().get(player.getName());
		Location respawnPoint;
		if (this instanceof Team && this.getSettings().contains(ASetting.useDiffRespawnForTeam))
			respawnPoint = this.getTeamRespawnPoints().get(this.getPlayerTeam(player.getName())).get(checkpointID);
		else
			respawnPoint = this.getRespawnPoints().get(checkpointID);
		this.getPlayerStatus().get(player.getName()).add(PStatus.INVINCIBLE);
		player.teleport(respawnPoint);
		AUtils.freezePlayer(player);
		new BukkitRunnable() {
			double phi = 0;
			Location loc = respawnPoint.clone();
			int y = loc.getBlockY() - 1;

			public void run() {
				loc.setY(y);
				phi += Math.PI / 10;
				for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 40) {
					double r = 1.5;
					double x = r * Math.cos(theta) * Math.sin(phi);
					double y = r * Math.cos(phi) + 1.5;
					double z = r * Math.sin(theta) * Math.sin(phi);
					loc.add(x, y, z);
					loc.getWorld().playEffect(loc, Effect.WATERDRIP, 1, 100);
					loc.subtract(x, y, z);
				}
				if (phi > Math.PI) {
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 1);
		new BukkitRunnable() {
			public void run() {
				Race.this.getPlayerStatus().get(player.getName()).remove(PStatus.INVINCIBLE);
				AUtils.unfreezePlayer(player, false);
			}
		}.runTaskLater(Main.getInstance(), 40);
		player.setFireTicks(0);
		player.setHealth(20);
	}

	@Override
	public void iniArena(String mapName, World world) {
		super.iniArena(mapName, world);
	}

	@Override
	public void startArena() {
		if (this.getSettings().contains(ASetting.useDiffRespawnForTeam)) {
			for (ChatColor team : this.getTeamPlayers().keySet()) {
				for (String playerName : this.getTeamPlayers().get(team)) {
					Bukkit.getPlayer(playerName).teleport(this.getTeamRespawnPoints().get(team).get(0));
				}
			}
		} else
			this.teleport(startingLocation);
		for (String playerName : this.getPlayers()) {
			this.allowDJ(playerName);
			this.getPlayerStatus().put(playerName, new ArrayList<>());
		}
		Region r = this.getStartPlatform();
		Race race = this;
		new BukkitRunnable() {
			public void run() {
				for (String playerName : Race.this.getPlayers()) {
					Player player = Bukkit.getPlayer(playerName);
					SoundManager.get().stopSound(player, "bgm_ssarena_waitroom", StopSoundMode.stopAll);
					SoundManager.get().playIntroThenLoopRest(player, "bgm_ssarena_" + bgmName + "_intro", "bgm_ssarena_" + bgmName);
					player.setHealth(20);
					player.setFoodLevel(20);
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.showPlayer(player);
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 150);
		new BukkitRunnable() {
			public void run() {
				race.setStartTime(System.currentTimeMillis());
				r.setBlock(Material.STAINED_GLASS, Material.AIR, Effect.CLOUD);
				ArenaStartEvent event = new ArenaStartEvent(Race.this);
				Bukkit.getServer().getPluginManager().callEvent(event);
				Race.this.sendTitle(0, 80, 3, "¡±bSTART!", "");
			}
		}.runTaskLater(Main.getInstance(), 250);
		new BukkitRunnable() {
			public void run() {
				if (race.getStatus().equals(AStatus.over) || race.getStatus().equals(AStatus.announcing))
					this.cancel();
				race.updateScoreboard();
			}
		}.runTaskTimer(Main.getInstance(), 0, 10);
	}

	public void alive(Player player) {
		if (this.getDeadPlayers().contains(player.getName()))
			return;
		if (!this.getPlayers().contains(player.getName()))
			return;
		if (this.getRanks().contains(player.getName()))
			return;
		if (this.getFinishTime().containsKey(player.getName()))
			return;
		this.getRanks().add(player.getName());
		this.setTimer(-1);
		TitleAPI.sendTitle(player, 0, 80, 20, "¡±aALIVE!", "");
		AUtils.freezePlayer(player);
		SoundManager.get().stopSound(player, "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
		SoundManager.get().playSound(player, "sfx_ssarena_finish");
		this.sendMessage(Main.prefix + this.getPlayerNameWithTeamColor(player.getName()) + ChatColor.GRAY + " is alive!");
		if (this.getRanks().size() == 1)
			new BukkitRunnable() {
				public void run() {
					Race.this.endGame();
				}
			}.runTaskLater(Main.getInstance(), 100);
	}

	public void reachGoal(Player player) {
		if (this.getFinishTime().containsKey(player.getName()))
			return;
		if (!this.getPlayers().contains(player.getName()))
			return;
		if (this.getDeadPlayers().contains(player.getName()))
			return;
		PlayerReachGoalEvent event = new PlayerReachGoalEvent(this, player);
		Bukkit.getServer().getPluginManager().callEvent(event);
		if (event.isCancelled())
			return;
		this.getFinishTime().put(player.getName(), System.currentTimeMillis());
		this.getRanks().add(player.getName());
		this.setTimer(-1);
		TitleAPI.sendTitle(player, 0, 80, 20, "¡±aFINISH!", "");
		AUtils.freezePlayer(player);
		if (this.getFinishTime().size() == 1) {
			SoundManager.get().stopSound(player, "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
			SoundManager.get().playSound(player, "sfx_ssarena_finish");
			this.sendMessage(Main.prefix + this.getPlayerNameWithTeamColor(player.getName()) + ChatColor.GRAY + " has " + ChatColor.GREEN + "reached the goal " + ChatColor.GRAY + "as the " + ChatColor.GOLD + "1st place" + ChatColor.GRAY + "!");
			for (String p : this.getPlayers()) {
				if (!this.getDeadPlayers().contains(player.getName()) && !this.getFinishTime().containsKey(player.getName()))
					SoundManager.get().stopSound(Bukkit.getPlayer(p), "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
				SoundManager.get().playSound(Bukkit.getPlayer(p), "sfx_ssarena_" + bgmName + "_final");
			}
			Race race = this;
			new BukkitRunnable() {
				int i = 10;

				public void run() {
					if (i <= 0) {
						race.endGame();
						this.cancel();
					}
					ChatColor color = ChatColor.YELLOW;
					if (i <= 3)
						color = ChatColor.RED;
					if (i > 0) {
						if (i <= 5 || i == 10)
							race.sendMessage(Main.prefix + ChatColor.GRAY + "Game ending in " + color + i + ChatColor.GRAY + "!");
						for (String playerName : race.getPlayers()) {
							if (race.getFinishTime().containsKey(playerName))
								continue;
							if (Race.this instanceof Survive && Race.this.getDeadPlayers().contains(playerName))
								continue;
							Player player = Bukkit.getPlayer(playerName);
							TitleAPI.sendTitle(player, 5, 10, 5, color + "" + i, "");
						}
					}
					i--;
				}
			}.runTaskTimer(Main.getInstance(), 0, 22);
		} else {
			this.sendMessage(Main.prefix + this.getPlayerNameWithTeamColor(player.getName()) + ChatColor.GRAY + " has reached the goal!" + ChatColor.GREEN + " Rank: " + this.getFinishTime().size() + ".");
		}
	}

	@Override
	public void endGame() {
		super.endGame();
		this.setStatus(AStatus.over);
		for (String playerName : this.getPlayers()) {
			Player player = Bukkit.getPlayer(playerName);
			if (this instanceof Survive) {
				if (!this.getDeadPlayers().contains(playerName))
					player.sendMessage(Main.prefix + ChatColor.GREEN + "Alive!");
				else
					player.sendMessage(Main.prefix + ChatColor.RED + "GAME OVER!");
			} else if (this.getFinishTime().containsKey(playerName)) {
				player.sendMessage(Main.prefix + ChatColor.GREEN + "Finish!");
			} else {
				AUtils.freezePlayerOnAir(player);
				SoundManager.get().stopSound(player, "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
				SoundManager.get().playSound(player, "sfx_ssarena_gameover");
				player.sendMessage(Main.prefix + ChatColor.RED + "TIME OVER!");
				TitleAPI.sendTitle(player, 0, 80, 20, "¡±cTIME OVER!", "");
			}
		}
		Race race = this;
		new BukkitRunnable() {
			public void run() {
				race.announceResult();
			}
		}.runTaskLater(Main.getInstance(), 135);
	}

	@Override
	public void announceResult() {
		String arenaName = this.getDisplayName();
		if (this.getStatus().equals(AStatus.announcing))
			return;
		Race arena = this;
		arena.setStatus(AStatus.announcing);
		Location fireworkLocation1 = arena.getPlaceLocations().get(0).clone().add(0, 6, 0);
		Location fireworkLocation2 = arena.getPlaceLocations().get(1).clone().add(0, 6, 0);
		Location fireworkLocation3 = arena.getPlaceLocations().get(2).clone().add(0, 6, 0);
		new BukkitRunnable() {
			int count = 12;

			public void run() {
				FireworkEffect.Builder fwB = FireworkEffect.builder();
				Random r = new Random();
				fwB.flicker(r.nextBoolean());
				fwB.trail(r.nextBoolean());
				fwB.with(FireworkEffect.Type.BALL);
				fwB.withColor(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
				fwB.withFade(Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
				FireworkEffect fe = fwB.build();
				Firework f = (Firework) Race.this.getWorld().spawn(fireworkLocation1, Firework.class);
				FireworkMeta fm = f.getFireworkMeta();
				fm.addEffect(fe);
				f.setFireworkMeta(fm);
				new BukkitRunnable() {
					public void run() {
						Firework f = (Firework) Race.this.getWorld().spawn(fireworkLocation2, Firework.class);
						FireworkMeta fm = f.getFireworkMeta();
						fm.addEffect(fe);
						f.setFireworkMeta(fm);
					}
				}.runTaskLater(Main.getInstance(), AUtils.randomInteger(0, 5));
				new BukkitRunnable() {
					public void run() {
						Firework f = (Firework) Race.this.getWorld().spawn(fireworkLocation3, Firework.class);
						FireworkMeta fm = f.getFireworkMeta();
						fm.addEffect(fe);
						f.setFireworkMeta(fm);
					}
				}.runTaskLater(Main.getInstance(), AUtils.randomInteger(0, 5));
				count--;
				if (count == 0)
					cancel();
			}
		}.runTaskTimer(Main.getInstance(), 0, 10);
		if (this instanceof Team) {
			ChatColor team = null;
			if (arena.getRanks().size() > 0)
			team = this.getPlayerTeam(arena.getRanks().get(0));
			for (String playerName : this.getPlayers()) {
				if (team != null && !this.getTeamPlayers().get(team).contains(playerName))
					Bukkit.getPlayer(playerName).teleport(arena.getPlaceLocations().get(1));
				else
					Bukkit.getPlayer(playerName).teleport(arena.getPlaceLocations().get(0));
			}
		} else {
			for (String playerName : arena.getPlayers()) {
				int size = arena.getRanks().size();
				Player player = Bukkit.getPlayer(playerName);
				if (size >= 1 && arena.getRanks().get(0).equals(playerName)) {
					player.teleport(arena.getPlaceLocations().get(0));
				} else if (size >= 2 && arena.getRanks().get(1).equals(playerName)) {
					player.teleport(arena.getPlaceLocations().get(1));
				} else if (size >= 3 && arena.getRanks().get(2).equals(playerName)) {
					player.teleport(arena.getPlaceLocations().get(2));
				} else {
					player.teleport(arena.getPlaceLocations().get(3));
				}

			}
		}
		Scoreboard arenaBoard;
		Objective arenaObjective;
		arenaBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		arenaObjective = arenaBoard.registerNewObjective("arena", "dummy");
		arenaObjective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + arenaName);
		arenaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		String title = "";
		String subTitle = "";
		if (arena.getRanks().size() > 0) {
			if (this instanceof Team) {
				title = this.getTeamFullname(arena.getRanks().get(0)) + " Team";
			} else {
				title = "¡±b" + arena.getRanks().get(0);
			}
			subTitle = "¡±6Won the game!";
		} else {
			title = "¡±7" + "Map failed.";
			subTitle = "¡±8No one reached the goal.";
		}
		for (String playerName : arena.getPlayers()) {
			Player player = Bukkit.getPlayer(playerName);
			SoundManager.get().stopSound(player, "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
			SoundManager.get().playSound(player, "sfx_ssarena_announceResult");
			AUtils.unfreezePlayer(player, true);
			AUtils.normalMode(player);
			TitleAPI.sendTitle(player, 0, 60, 20, title, subTitle);
			int score = 0;
			if (arena.getRanks().contains(playerName))
				score = 150 - arena.getRanks().indexOf(playerName);
			else
				score = arena.getFinishPercent().get(playerName);
			int pos = arena.getRanks().indexOf(playerName) + 1;
			ChatColor color;
			if (pos == 1)
				color = ChatColor.GOLD;
			else if (pos == 2)
				color = ChatColor.GRAY;
			else if (pos == 3)
				color = ChatColor.DARK_RED;
			else
				color = ChatColor.WHITE;
			ChatColor nc;
			String time;
			Bukkit.getLogger().info(arena.getFinishTime().get(player.getName()) + "");
			Bukkit.getLogger().info(arena.getStartTime() + "");
			long finishms = 0;
			if (arena.getRanks().contains(playerName) && arena.getFinishTime().containsKey(player.getName())) {
				nc = ChatColor.GREEN;
				finishms = arena.getFinishTime().get(player.getName()) - arena.getStartTime();
				time = String.format("%02d:%02d.%03d", TimeUnit.MILLISECONDS.toMinutes(finishms), TimeUnit.MILLISECONDS.toSeconds(finishms) % TimeUnit.MINUTES.toSeconds(1), TimeUnit.MILLISECONDS.toMillis(finishms) % TimeUnit.SECONDS.toMillis(1));
			} else {
				nc = ChatColor.RED;
				time = ChatColor.AQUA + "--:--.---";
			}
			String line = ChatColor.DARK_GRAY + "[" + color + pos + ChatColor.DARK_GRAY + "] " + nc + playerName + ChatColor.DARK_GRAY + " - " + time;
			arenaObjective.getScore(line).setScore(score);
			player.setScoreboard(arenaBoard);
			String border = ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------------";
			player.sendMessage(border);
			player.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + arenaName + ChatColor.GRAY + " by " + ChatColor.AQUA + arena.getAuthors());
			player.sendMessage("");
			player.sendMessage(ChatColor.GRAY + "Server Record: " + ChatColor.AQUA + "WIP"/*
																							 * AUtils.convertTicks(recordTime) + ChatColor.GRAY + " by " + ChatColor.AQUA + recordKeeper
																							 */);
			/*if (PlayerData.getPlayerPR(playerName, arenaName) != 0)*/ {
				// int PR = PlayerData.getPlayerPR(playerName, arenaName);
				player.sendMessage(ChatColor.GRAY + "Personal Record: " + "WIP"/* ChatColor.AQUA + AUtils.convertTicks(PR) */);
			} // else
			player.sendMessage("");
			player.sendMessage("");
			if (arena.getFinishTime().containsKey(playerName)) {
				player.sendMessage(ChatColor.GRAY + "Your time: " + ChatColor.AQUA + time);
				/*
				 * if (!PlayerData.getPlayerData(player.getName()).getMapsFinished( ).contains(realName)) { PlayerData.getPlayerData(player.getName()).getMapsFinished(). add(realName); }
				 */
			} else
				player.sendMessage(ChatColor.GRAY + "Your time: " + ChatColor.AQUA + "--:--.---");
			/*if (PlayerData.getPlayerPR(playerName, arenaName) != 0) {
				if (arena.getFinishTime().containsKey(playerName)) {
					int PR = PlayerData.getPlayerPR(playerName, realName);
					if (PR > finishTicks) {
						player.sendMessage(ChatColor.GREEN + "Personal record broke!");
						pd.getPlayerPR().replace(realName, finishTicks);
						pd.setBrokeRecord(true);
						pd.setPersonalRecordsBroken(pd.getPersonalRecordsBroken() + 1);
						if (finishTicks < recordTime) {
							for (Player bPlayer : Bukkit.getOnlinePlayers()) {
								if (!arena.getPlayers().contains(player.getName())) {
									bPlayer.sendMessage(Main.prefix + ChatColor.GOLD + playerName + ChatColor.AQUA + " broke the server record of the map " + ChatColor.GOLD + arena.getName() + ChatColor.AQUA + " with a time of " + ChatColor.GOLD + AUtils.convertTicks(finishTicks) + ChatColor.AQUA + "!!!");
								}
							}
						}
					} else
						player.sendMessage("");
				} else
					player.sendMessage("");
			} else
				player.sendMessage("");*/
			/*if (arena.getFinishTime().containsKey(arena.getRanks().get(0)) && (arena.getFinishTime().get(arena.getRanks().get(0)) - arena.getStartedTime()) < recordTime) {
				String bPlayerName = arena.getRanks().get(0);
				int extraTime = recordTime - arena.getFinishTime().get(arena.getRanks().get(0)) + arena.getStartedTime();
				player.sendMessage(ChatColor.GOLD + bPlayerName + " broke the server record! (- " + AUtils.convertTicks(extraTime) + ")");
			}*/
			final int fScore = score;
			player.sendMessage(border);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
				public void run() {
					player.sendMessage(border);
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Reward Summary");
					player.sendMessage("");
					int count = 0;
					double mul = arena.getRewardMul();
					double totalMoney = 0;
					double totalEXP = 0;
					double oMoney = 0;
					double oEXP = 0;
					int rank = arena.getRanks().indexOf(playerName) + 1;
					int y = arena.getPlayers().size();
					int x = y - rank;
					Player player = Bukkit.getPlayer(playerName);
					if (arena.getPlayers().contains(playerName)) {
						double money = mul * 3 * (x + y / y);
						double exp = mul * 4 * (x + y / y);
						money = AUtils.round(money, 1);
						exp = AUtils.round(exp, 1);
						player.sendMessage(ChatColor.YELLOW + "+ $" + money + ChatColor.DARK_AQUA + " + " + exp + " Exp" + ChatColor.GOLD + " - map rank reward");
						count++;
						totalMoney = totalMoney + money;
						totalEXP = totalEXP + exp;
						oMoney = oMoney + money;
						oEXP = oEXP + exp;
					}
					if (arena.getFinishTime().containsKey(playerName)) {
						double money = mul * 1 * (x + y / y);
						double exp = mul * 2 * (x + y / y);
						money = AUtils.round(money, 1);
						exp = AUtils.round(exp, 1);
						player.sendMessage(ChatColor.YELLOW + "+ $" + money + ChatColor.DARK_AQUA + " + " + exp + " Exp" + ChatColor.GOLD + " - map completion");
						count++;
						totalMoney = totalMoney + money;
						totalEXP = totalEXP + exp;
						oMoney = oMoney + money;
						oEXP = oEXP + exp;
					}
					for (; count < 7; count++) {
						player.sendMessage("");
					}
					totalMoney = AUtils.round(totalMoney, 1);
					totalEXP = AUtils.round(totalEXP, 1);
					//Main.econ.depositPlayer(playerName, totalMoney);
					//long oldExp = SPlayer.get(playerName).getExp();
					//SPlayer.get(playerName).setExp(oldExp + (int) totalEXP);
					final String StotalMoney = Double.toString(totalMoney);
					final String StotalExp = Double.toString((int) totalEXP);
					arenaBoard.resetScores(line);
					arenaObjective.getScore(ChatColor.DARK_GRAY + "[" + color + pos + ChatColor.DARK_GRAY + "] " + nc + playerName + ChatColor.YELLOW + " + $" + totalMoney + ChatColor.AQUA + " " + totalEXP + "EXP").setScore(fScore);
					player.setScoreboard(arenaBoard);
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
						public void run() {
							Player player = Bukkit.getPlayer(playerName);
							player.sendMessage("");
							player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Total:");
							player.sendMessage(ChatColor.YELLOW + "$" + StotalMoney);
							player.sendMessage(ChatColor.DARK_AQUA + StotalExp + " EXP");
							player.sendMessage("");
							/*
							int level = SPlayer.get(playerName).getLevel();
							double exp = SPlayer.get(playerName).getExp();
							double fullExp = Level.getLevel(level).getFullXP();
							int pct = (int) (exp / fullExp * 100);
							String bar = AUtils.percentageBar(35, pct, ChatColor.AQUA);
							player.sendMessage(ChatColor.GRAY + "Level: [" + ChatColor.AQUA + level + ChatColor.GRAY + "] " + bar);
							player.sendMessage(ChatColor.GRAY + "(" + pct + "% - " + (int) exp + "/" + (int) fullExp + ")");
							player.sendMessage(ChatColor.GRAY + "Balance: " + ChatColor.YELLOW + "$" + AUtils.round(hell.supersoul.sprint.Main.econ.getBalance(playerName), 2));
							player.sendMessage(ChatColor.GRAY + "Credits: " + ChatColor.GOLD + "0 (WIP)");
							*/
							player.sendMessage(border);
						}
					}, 80L);
				}
			}, 120L);
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {

			public void run() {
				String target = null;
				if (Race.this.getRanks().isEmpty())
					target = Race.this.getPlayers().get(0);
				else
					target = Race.this.getRanks().get(0);
				WRManager.getManager().gameEnded(arena, Bukkit.getPlayer(target));
				arena.endArena();
			}

		}, 280L);
	}

	@Override
	public void endArena() {
		super.endArena();
		if (this.getFinishTime().size() > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(Main.prefix + ChatColor.AQUA + this.getRanks().get(0) + ChatColor.GRAY + " has won the arena " + ChatColor.AQUA + this.getDisplayName() + ChatColor.GRAY + "!");
			}
		}
	}

	@Override
	public void playerDeath(Player player, DamageType dt, Object... args) {
		super.playerDeath(player, dt, args);
	}

	@Override
	public void playerDisconnected(Player player) {
		this.playerDeath(player, DamageType.UNKNOWN);
	}

	@Override
	public void playerReconnected(Player player) {
		this.getDisconnectedPlayers().remove(player.getName());
		this.getPlayers().add(player.getName());
		AUtils.spectateMode(player);
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (!this.getPlayers().contains(player.getName()))
			return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (this.getPlayers().contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	public void allowDJ(String playerName) {
		if (allowDJ.contains(playerName))
			return;
		if (!this.getPlayers().contains(playerName))
			return;
		allowDJ.add(playerName);
	}

	public void disallowDJ(String playerName) {
		if (!this.getPlayers().contains(playerName))
			return;
		allowDJ.remove(playerName);
		Player player = Bukkit.getPlayer(playerName);
		if (player == null)
			return;
		player.getInventory().setItem(0, null);
	}

	public void doubleJump(String playerName) {

	}

	public Region getStartPlatform() {
		return startPlatform;
	}

	public Region getGoal() {
		return goal;
	}

	public ArrayList<Region> getCheckPoints() {
		return checkPoints;
	}

	public HashMap<String, Integer> getPlayerCheckPoints() {
		return playerCheckPoints;
	}

	public HashMap<String, Integer> getFinishPercent() {
		return finishPercent;
	}

	public ArrayList<Location> getRespawnPoints() {
		return respawnPoints;
	}

	public ArrayList<Location> getPlaceLocations() {
		return placeLocations;
	}

	public long getStartTime() {
		return startTime;
	}

	public HashMap<String, Long> getEndTime() {
		return endTime;
	}

	public double getRewardMul() {
		return rewardMul;
	}

	public double getTimer() {
		return timer;
	}

	public void setStartPlatform(Region startPlatform) {
		this.startPlatform = startPlatform;
	}

	public void setGoal(Region goal) {
		this.goal = goal;
	}

	public void setCheckPoints(ArrayList<Region> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public void setPlayerCheckPoints(HashMap<String, Integer> playerCheckPoints) {
		this.playerCheckPoints = playerCheckPoints;
	}

	public void setFinishPercent(HashMap<String, Integer> finishPercent) {
		this.finishPercent = finishPercent;
	}

	public void setRespawnPoints(ArrayList<Location> respawnPoints) {
		this.respawnPoints = respawnPoints;
	}

	public void setPlaceLocations(ArrayList<Location> placeLocations) {
		this.placeLocations = placeLocations;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(HashMap<String, Long> endTime) {
		this.endTime = endTime;
	}

	public void setRewardMul(double rewardMul) {
		this.rewardMul = rewardMul;
	}

	public void setTimer(double timer) {
		this.timer = timer;
	}

	public Location getStartingLocation() {
		return startingLocation;
	}

	public void setStartingLocation(Location startingLocation) {
		this.startingLocation = startingLocation;
	}

	public int getDeathLevel() {
		return deathLevel;
	}

	public void setDeathLevel(int deathLevel) {
		this.deathLevel = deathLevel;
	}

	public HashMap<String, Long> getFinishTime() {
		return finishTime;
	}

	public ArrayList<String> getRanks() {
		return ranks;
	}

	public HashMap<ChatColor, ArrayList<Location>> getTeamRespawnPoints() {
		return teamRespawnPoints;
	}

	public void setFinishTime(HashMap<String, Long> finishTime) {
		this.finishTime = finishTime;
	}

	public void setRanks(ArrayList<String> ranks) {
		this.ranks = ranks;
	}

	public ArrayList<String> getUnfinishedRanks() {
		return unfinishedRanks;
	}

	public void setUnfinishedRanks(ArrayList<String> unfinishedRanks) {
		this.unfinishedRanks = unfinishedRanks;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
			return;
		}
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (!this.getPlayers().contains(playerName))
			return;
		if (this.getDeadPlayers().contains(playerName))
			return;
		if (player.getGameMode() != GameMode.SPECTATOR) {
			Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();
			if (block.getType().equals(Material.SLIME_BLOCK)) {
				player.setAllowFlight(false);
				if (player.getLocation().subtract(0, 2, 0).getBlock().getType().equals(Material.SOUL_SAND)) {
					player.setVelocity(new Vector(player.getVelocity().getX(), 1.6, player.getVelocity().getZ()));
				} else {
					player.setVelocity(new Vector(player.getVelocity().getX(), 2.5, player.getVelocity().getZ()));
				}
			} else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 1));
			} else if (block.getType().equals(Material.REDSTONE_BLOCK)) {
				player.setAllowFlight(false);
				player.setVelocity(player.getLocation().getDirection().normalize().multiply(-1).setY(0.5));
			}
			if (block == null || block.getType().equals(Material.AIR) || !block.getType().isSolid()) {
				if (!DJed.contains(playerName) && allowDJ.contains(playerName))
					player.getInventory().setItem(0, jump);
			} else if (allowDJ.contains(playerName)) {
				DJed.remove(playerName);
				player.getInventory().setItem(0, nonjump);
			}
			if (event.getTo().getBlockY() <= this.getDeathLevel())
				this.playerDamage(player, DamageType.VOID, (Object[]) null);
			if (this.getGoal().isInRegion(player))
				this.reachGoal(player);
			int checkpointID = this.getPlayerCheckPoints().get(player.getName()) + 1;
			if (checkpointID >= this.getCheckPoints().size())
				return;
			if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR)
				return;
			if (this.getCheckPoints().get(checkpointID).isInRegion(player))
				this.getPlayerCheckPoints().put(player.getName(), checkpointID);
		}
	}

	@EventHandler
	public void onRightClickItem(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;
		Player player = event.getPlayer();
		if (!this.getPlayers().contains(player.getName()))
			return;
		ItemStack clicked = player.getItemInHand();
		if (clicked.getType() == null)
			return;
		if (player.getGameMode().equals(GameMode.SPECTATOR))
			return;
		event.setCancelled(true);
		if (clicked.getType().equals(Material.INK_SACK)) {
			if (clicked.equals(jump)) {
				if (!DJed.contains(player.getName())) {
					if (allowDJ.contains(player.getName())) {
						DJed.add(player.getName());
						player.getInventory().setItem(0, nonjump);
						Location loc = player.getLocation();
						Vector look = loc.getDirection().multiply(1.2D);
						look.setY(0.6);
						player.setVelocity(look);
						player.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_LARGE_BLAST, 1f, 1f);
						player.getWorld().playEffect(loc, Effect.EXPLOSION_LARGE, 1);
					} else {
						player.getInventory().setItem(0, null);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (this.getPlayers().contains(event.getPlayer().getName()))
			if (event.getBlock().getWorld().equals(this.getWorld()))
				event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (this.getPlayers().contains(event.getPlayer().getName()))
			if (event.getBlock().getWorld().equals(this.getWorld()))
				event.setCancelled(true);
	}
}
