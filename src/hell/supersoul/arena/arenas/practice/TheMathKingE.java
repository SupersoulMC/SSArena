package hell.supersoul.arena.arenas.practice;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.modules.Survive;
import hell.supersoul.arena.modules.Team;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.utils.Region;
import hell.supersoul.sound.enums.StopSoundMode;
import hell.supersoul.sound.manager.SoundManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;

public class TheMathKingE extends Race implements Survive, Listener, Team {

	private int round = 0;
	private int answer;
	private ArrayList<String> steppedPlayers = new ArrayList<String>();
	private HashMap<Integer, HashMap<Integer, ArrayList<Location>>> judgementDoor = new HashMap<Integer, HashMap<Integer, ArrayList<Location>>>();
	private HashMap<Integer, Location> plateLocation = new HashMap<Integer, Location>();
	private HashMap<Integer, Region> continueLocation = new HashMap<Integer, Region>();
	private HashMap<Integer, Region> deathLocation = new HashMap<Integer, Region>();
	private HashMap<Integer, ArrayList<Integer>> choices = new HashMap<Integer, ArrayList<Integer>>();
	private HashMap<Integer, Integer> answers = new HashMap<Integer, Integer>();
	private HashMap<Integer, HashMap<Integer, Location>> choiceLocation = new HashMap<Integer, HashMap<Integer, Location>>();
	private ArrayList<Location> activatedPlate = new ArrayList<Location>();
	private ArrayList<Location> availablePlates = new ArrayList<Location>();

	public TheMathKingE(String id) {
		super(id);
	}

	public void updateBanner(Banner banner, Integer number) {
		if (number == 1) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.SQUARE_TOP_LEFT);
			Pattern pattern2 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_CENTER);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern4 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
		} else if (number == 2) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern2 = new Pattern(DyeColor.ORANGE, PatternType.RHOMBUS_MIDDLE);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_DOWNLEFT);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern5 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
		} else if (number == 3) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_MIDDLE);
			Pattern pattern2 = new Pattern(DyeColor.ORANGE, PatternType.STRIPE_LEFT);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_RIGHT);
			Pattern pattern5 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern6 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
			banner.addPattern(pattern6);
		} else if (number == 4) {
			Pattern pattern1 = new Pattern(DyeColor.ORANGE, PatternType.HALF_HORIZONTAL);
			Pattern pattern2 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_LEFT);
			Pattern pattern3 = new Pattern(DyeColor.ORANGE, PatternType.STRIPE_BOTTOM);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_RIGHT);
			Pattern pattern5 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_MIDDLE);
			Pattern pattern6 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.GRAY);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
			banner.addPattern(pattern6);
		} else if (number == 5) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern2 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_DOWNRIGHT);
			Pattern pattern3 = new Pattern(DyeColor.ORANGE, PatternType.CURLY_BORDER);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.SQUARE_BOTTOM_LEFT);
			Pattern pattern5 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern6 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
			banner.addPattern(pattern6);
		} else if (number == 6) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_RIGHT);
			Pattern pattern2 = new Pattern(DyeColor.ORANGE, PatternType.HALF_HORIZONTAL);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_MIDDLE);
			Pattern pattern5 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_LEFT);
			Pattern pattern6 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern7 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
			banner.addPattern(pattern6);
			banner.addPattern(pattern7);
		} else if (number == 7) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern2 = new Pattern(DyeColor.ORANGE, PatternType.DIAGONAL_RIGHT);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_DOWNLEFT);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.SQUARE_BOTTOM_LEFT);
			Pattern pattern5 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
		} else if (number == 8) {
			Pattern pattern1 = new Pattern(DyeColor.ORANGE, PatternType.STRIPE_CENTER);
			Pattern pattern2 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_MIDDLE);
			Pattern pattern5 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.GRAY);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
		} else if (number == 9) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_LEFT);
			Pattern pattern2 = new Pattern(DyeColor.ORANGE, PatternType.HALF_HORIZONTAL_MIRROR);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_MIDDLE);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern5 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_RIGHT);
			Pattern pattern6 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern7 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
			banner.addPattern(pattern6);
			banner.addPattern(pattern7);
		} else if (number == 0) {
			Pattern pattern1 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_TOP);
			Pattern pattern2 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_RIGHT);
			Pattern pattern3 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_BOTTOM);
			Pattern pattern4 = new Pattern(DyeColor.GRAY, PatternType.STRIPE_LEFT);
			Pattern pattern5 = new Pattern(DyeColor.ORANGE, PatternType.BORDER);
			banner.setBaseColor(DyeColor.ORANGE);
			banner.addPattern(pattern1);
			banner.addPattern(pattern2);
			banner.addPattern(pattern3);
			banner.addPattern(pattern4);
			banner.addPattern(pattern5);
		}
		banner.update();
	}

	@Override
	public void iniArena(String mapName, World world) {
		super.iniArena(mapName, world);
		this.getChoiceLocation().put(1, new HashMap<Integer, Location>());
		this.getChoiceLocation().put(2, new HashMap<Integer, Location>());
		this.getChoiceLocation().put(3, new HashMap<Integer, Location>());
		this.getChoiceLocation().put(4, new HashMap<Integer, Location>());
		this.getChoiceLocation().put(5, new HashMap<Integer, Location>());
		this.getChoiceLocation().get(1).put(1, new Location(world, -38, 160, -332));
		this.getChoiceLocation().get(1).put(2, new Location(world, -44, 160, -332));
		this.getChoiceLocation().get(1).put(3, new Location(world, -50, 160, -332));
		this.getChoiceLocation().get(2).put(1, new Location(world, -38, 141, -91));
		this.getChoiceLocation().get(2).put(2, new Location(world, -44, 141, -91));
		this.getChoiceLocation().get(2).put(3, new Location(world, -50, 141, -91));
		this.getChoiceLocation().get(3).put(1, new Location(world, -38, 122, 150));
		this.getChoiceLocation().get(3).put(2, new Location(world, -44, 122, 150));
		this.getChoiceLocation().get(3).put(3, new Location(world, -50, 122, 150));
		this.getChoiceLocation().get(4).put(1, new Location(world, -38, 103, 391));
		this.getChoiceLocation().get(4).put(2, new Location(world, -44, 103, 391));
		this.getChoiceLocation().get(4).put(3, new Location(world, -50, 103, 391));
		this.getChoiceLocation().get(5).put(1, new Location(world, -38, 109, 533));
		this.getChoiceLocation().get(5).put(2, new Location(world, -44, 109, 533));
		this.getChoiceLocation().get(5).put(3, new Location(world, -50, 109, 533));
		this.getJudgementDoor().put(1, new HashMap<>());
		this.getJudgementDoor().put(2, new HashMap<>());
		this.getJudgementDoor().put(3, new HashMap<>());
		this.getJudgementDoor().put(4, new HashMap<>());
		this.getJudgementDoor().put(5, new HashMap<>());
		this.getJudgementDoor().get(1).put(1, new ArrayList<>());
		this.getJudgementDoor().get(1).put(2, new ArrayList<>());
		this.getJudgementDoor().get(1).put(3, new ArrayList<>());
		this.getJudgementDoor().get(1).get(1).add(new Location(world, -38, 164, -302));
		this.getJudgementDoor().get(1).get(1).add(new Location(world, -40, 161, -302));
		this.getJudgementDoor().get(1).get(2).add(new Location(world, -44, 164, -302));
		this.getJudgementDoor().get(1).get(2).add(new Location(world, -46, 161, -302));
		this.getJudgementDoor().get(1).get(3).add(new Location(world, -50, 164, -302));
		this.getJudgementDoor().get(1).get(3).add(new Location(world, -52, 161, -302));
		this.getJudgementDoor().get(2).put(1, new ArrayList<>());
		this.getJudgementDoor().get(2).put(2, new ArrayList<>());
		this.getJudgementDoor().get(2).put(3, new ArrayList<>());
		this.getJudgementDoor().get(2).get(1).add(new Location(world, -38, 145, -61));
		this.getJudgementDoor().get(2).get(1).add(new Location(world, -40, 142, -61));
		this.getJudgementDoor().get(2).get(2).add(new Location(world, -44, 145, -61));
		this.getJudgementDoor().get(2).get(2).add(new Location(world, -46, 142, -61));
		this.getJudgementDoor().get(2).get(3).add(new Location(world, -50, 145, -61));
		this.getJudgementDoor().get(2).get(3).add(new Location(world, -52, 142, -61));
		this.getJudgementDoor().get(3).put(1, new ArrayList<>());
		this.getJudgementDoor().get(3).put(2, new ArrayList<>());
		this.getJudgementDoor().get(3).put(3, new ArrayList<>());
		this.getJudgementDoor().get(3).get(1).add(new Location(world, -38, 126, 180));
		this.getJudgementDoor().get(3).get(1).add(new Location(world, -40, 123, 180));
		this.getJudgementDoor().get(3).get(2).add(new Location(world, -44, 126, 180));
		this.getJudgementDoor().get(3).get(2).add(new Location(world, -46, 123, 180));
		this.getJudgementDoor().get(3).get(3).add(new Location(world, -50, 126, 180));
		this.getJudgementDoor().get(3).get(3).add(new Location(world, -52, 123, 180));
		this.getJudgementDoor().get(4).put(1, new ArrayList<>());
		this.getJudgementDoor().get(4).put(2, new ArrayList<>());
		this.getJudgementDoor().get(4).put(3, new ArrayList<>());
		this.getJudgementDoor().get(4).get(1).add(new Location(world, -38, 107, 421));
		this.getJudgementDoor().get(4).get(1).add(new Location(world, -40, 104, 421));
		this.getJudgementDoor().get(4).get(2).add(new Location(world, -44, 107, 421));
		this.getJudgementDoor().get(4).get(2).add(new Location(world, -46, 104, 421));
		this.getJudgementDoor().get(4).get(3).add(new Location(world, -50, 107, 421));
		this.getJudgementDoor().get(4).get(3).add(new Location(world, -52, 104, 421));
		this.getJudgementDoor().get(5).put(1, new ArrayList<>());
		this.getJudgementDoor().get(5).put(2, new ArrayList<>());
		this.getJudgementDoor().get(5).put(3, new ArrayList<>());
		this.getJudgementDoor().get(5).get(1).add(new Location(world, -38, 113, 563));
		this.getJudgementDoor().get(5).get(1).add(new Location(world, -40, 110, 563));
		this.getJudgementDoor().get(5).get(2).add(new Location(world, -44, 113, 563));
		this.getJudgementDoor().get(5).get(2).add(new Location(world, -46, 110, 563));
		this.getJudgementDoor().get(5).get(3).add(new Location(world, -50, 113, 563));
		this.getJudgementDoor().get(5).get(3).add(new Location(world, -52, 110, 563));
		this.getPlateLocation().put(1, new Location(world, -42, 161, -275));
		this.getPlateLocation().put(2, new Location(world, -42, 142, -34));
		this.getPlateLocation().put(3, new Location(world, -42, 123, 207));
		this.getPlateLocation().put(4, new Location(world, -45, 104, 448));
		this.getDeathLocation().put(1, new Region(new Location(world, -42, 159, -268), new Location(world, -48, 159, -271)));
		this.getDeathLocation().put(2, new Region(new Location(world, -42, 140, -27), new Location(world, -48, 140, -30)));
		this.getDeathLocation().put(3, new Region(new Location(world, -42, 121, 214), new Location(world, -48, 121, 211)));
		this.getDeathLocation().put(4, new Region(new Location(world, -42, 102, 455), new Location(world, -48, 102, 452)));
		this.getContinueLocation().put(1, new Region(new Location(world, -40, 164, -264), new Location(world, -50, 160, -264)));
		this.getContinueLocation().put(2, new Region(new Location(world, -40, 145, -23), new Location(world, -50, 141, -23)));
		this.getContinueLocation().put(3, new Region(new Location(world, -40, 126, 218), new Location(world, -50, 122, 218)));
		this.getContinueLocation().put(4, new Region(new Location(world, -40, 107, 459), new Location(world, -50, 103, 459)));
		this.setStartingLocation(new Location(world, -45, 249, -504));
		this.setStartPlatform(new Region(new Location(world, -41, 248, -501), new Location(world, -49, 248, -506)));
		this.setGoal(new Region(new Location(world, -38, 108, 578), new Location(world, -52, 130, 576)));
		this.getPlaceLocations().add(new Location(world, -45, 109, 591));
		this.getPlaceLocations().add(new Location(world, -44, 108, 596, 180, -10));
		this.getPlaceLocations().add(new Location(world, -44, 108, 596, 180, -10));
		this.getPlaceLocations().add(new Location(world, -44, 108, 596, 180, -10));
		this.getCheckPoints().add(null);
		this.getCheckPoints().add(new Region(new Location(world, -33, 150, -458), new Location(world, -59, 164, -418)));
		this.getCheckPoints().add(new Region(new Location(world, -31, 129, -52), new Location(world, -52, 143, -185)));
		this.getCheckPoints().add(new Region(new Location(world, -32, 112, 24), new Location(world, -54, 125, 56)));
		this.getCheckPoints().add(new Region(new Location(world, -39, 112, 72), new Location(world, -56, 125, 133)));
		this.getCheckPoints().add(new Region(new Location(world, -36, 113, 80), new Location(world, -56, 125, 133)));
		this.getCheckPoints().add(new Region(new Location(world, -38, 114, 90), new Location(world, -56, 125, 133)));
		this.getCheckPoints().add(new Region(new Location(world, -34, 91, 265), new Location(world, -52, 104, 295)));
		this.getCheckPoints().add(new Region(new Location(world, -55, 92, 316), new Location(world, -36, 104, 381)));
		this.getCheckPoints().add(new Region(new Location(world, -50, 92, 329), new Location(world, -36, 104, 381)));
		this.getCheckPoints().add(new Region(new Location(world, -59, 87, 349), new Location(world, -36, 104, 381)));
		this.getRespawnPoints().add(new Location(world, -45, 179, -503));
		this.getRespawnPoints().add(new Location(world, -45, 155, -422));
		this.getRespawnPoints().add(new Location(world, -45, 136, -181));
		this.getRespawnPoints().add(new Location(world, -45, 117, 60));
		this.getRespawnPoints().add(new Location(world, -45, 117, 76));
		this.getRespawnPoints().add(new Location(world, -46, 117, 95));
		this.getRespawnPoints().add(new Location(world, -45, 117, 109));
		this.getRespawnPoints().add(new Location(world, -45, 98, 303));
		this.getRespawnPoints().add(new Location(world, -43, 98, 322));
		this.getRespawnPoints().add(new Location(world, -42, 98, 333));
		this.getRespawnPoints().add(new Location(world, -44, 98, 351));
		this.getDeathDamage().add(DamageType.LAVA);
		this.getRespawnDamage().add(DamageType.FIRE);
		this.getDisabledDamage().add(DamageType.FALL);
		this.getDisabledDamage().add(DamageType.PLAYER);
		this.getDisabledDamage().add(DamageType.FIRE_TICK);
		this.getDisabledDamage().add(DamageType.LAVA);
		this.getDisabledDamage().add(DamageType.FIRE);
		this.setBgmName("themathking");
		this.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "The Math King (E)");
	}

	public void nextRound() {
		Bukkit.getLogger().info("1");
		if (this.getPlayers().size() - this.getDeadPlayers().size() <= 0)
			return;
		int round = this.getRound();
		round++;
		this.setRound(round);
		final int temp = round;
		this.getAnswers().clear();
		this.getSteppedPlayers().clear();
		this.getActivatedPlate().clear();
		this.getAvailablePlates().clear();
		int delay1 = 40;
		int delay2 = 80;
		if (round == 5) {
			delay1 = 0;
			delay2 = 40;
		}
		if (round != 5) {
			int plates = 1;
			if (round == 1) {
				plates = (int) (this.getPlayers().size() * 0.8);
				plates++;
				if (plates > 12)
					plates = 12;
			} else if (round == 2) {
				plates = (int) (this.getPlayers().size() * 0.6);
				plates++;
				if (plates > 9)
					plates = 9;
			} else if (round == 3) {
				plates = (int) (this.getPlayers().size() * 0.4);
				plates++;
				if (plates > 6)
					plates = 6;
			} else if (round == 4) {
				plates = 1;
			}
			if (plates > this.getPlayers().size())
				plates = this.getPlayers().size();
			int minusz = 0;
			int minusx = 0;
			for (int i = 1; i <= plates; i++) {
				this.getWorld().getBlockAt(this.getPlateLocation().get(round).clone().add(minusx, -1, minusz)).setType(Material.REDSTONE_LAMP_OFF);
				Location loc = this.getPlateLocation().get(round).clone().add(minusx, 0, minusz);
				this.getWorld().getBlockAt(loc).setType(Material.STONE_PLATE);
				this.getAvailablePlates().add(loc);
				minusx = minusx - 3;
				if (i % 3 == 0) {
					minusz = minusz - 3;
					minusx = 0;
				}
			}
		}
		new BukkitRunnable() {
			public void run() {
				String title;
				if (temp != 5) {
					title = "¡±7Round ¡±6" + temp;
				} else {
					title = "¡±4Last ¡±7Round!";
					for (String playerName : TheMathKingE.this.getPlayers()) {
						Player player = Bukkit.getPlayer(playerName);
						SoundManager.get().stopSound(player, "bgm_ssarena_themathking", StopSoundMode.stopAll);
						SoundManager.get().playSound(player, "bgm_ssarena_themathking_final");
					}
				}
				for (String playerName : TheMathKingE.this.getPlayers()) {
					Player player = Bukkit.getPlayer(playerName);
					TitleAPI.sendTitle(player, 3, 30, 3, title);
				}
			}
		}.runTaskLater(Main.getInstance(), delay1);
		new BukkitRunnable() {
			public void run() {
				int answer = 0;
				String question = "";
				if (temp == 1) {
					int no1 = AUtils.randomNumber(0, 9);
					int no2 = AUtils.randomInteger(0, 9);
					answer = no1 + no2;
					question = no1 + " + " + no2;
					TheMathKingE.this.setTimer(45);
				} else if (temp == 2) {
					int no1 = AUtils.randomInteger(0, 50);
					int no2 = AUtils.randomInteger(0, 50);
					int max = Math.max(no1, no2);
					int min = Math.min(no1, no2);
					answer = max - min;
					question = max + " - " + min;
					TheMathKingE.this.setTimer(45);
				} else if (temp == 3) {
					int no1 = AUtils.randomInteger(0, 20);
					int no2 = AUtils.randomInteger(1, 9);
					answer = no1 * no2;
					question = no1 + " * " + no2;
					TheMathKingE.this.setTimer(45);
				} else if (temp == 4) {
					int no1 = AUtils.randomInteger(0, 20);
					int no2 = AUtils.randomInteger(1, 10);
					answer = no1 * no2;
					question = no1 + " * " + no2;
					TheMathKingE.this.setTimer(32);
				} else if (temp == 5) {
					int no1 = AUtils.randomInteger(0, 20);
					int no2 = AUtils.randomInteger(1, 10);
					int no3 = AUtils.randomInteger(0, 100);
					answer = no1 * no2 + no3;
					question = no1 + " * " + no2 + " + " + no3;
					TheMathKingE.this.setTimer(17);
				}
				/*
				} else if (arena.getDifficulty().equals("normal")) {
				if (temp == 1) {
					int no1 = AUtils.randomInteger(10, 99);
					int no2 = AUtils.randomInteger(10, 99);
					answer = no1 + no2;
					question = no1 + " + " + no2;
					arena.setTimer(30);
				} else if (temp == 2) {
					int no1 = AUtils.randomInteger(100, 999);
					int no2 = AUtils.randomInteger(100, 999);
					int max = Math.max(no1, no2);
					int min = Math.min(no1, no2);
					answer = max - min;
					question = max + " - " + min;
					arena.setTimer(30);
				} else if (temp == 3) {
					int no1 = AUtils.randomInteger(1, 30);
					int no2 = AUtils.randomInteger(1, 30);
					answer = no1 * no2;
					question = no1 + " * " + no2;
					arena.setTimer(30);
				} else if (temp == 4) {
					int no1 = AUtils.randomInteger(1, 30);
					int no2 = AUtils.randomInteger(1, 30);
					int no3 = AUtils.randomInteger(0, 99);
					answer = no1 * no2 + no3;
					question = no1 + " * " + no2 + " + " + no3;
					arena.setTimer(25);
				} else if (temp == 5) {
					int no1 = AUtils.randomInteger(1, 20);
					int no2 = AUtils.randomInteger(1, 20);
					int no3 = AUtils.randomInteger(1, 599);
					int sum = no1 * no2 + no3;
					int no4 = AUtils.randomInteger(1, sum);
					answer = sum - no4;
					question = no1 + " * " + no2 + " + " + no3 + " - " + no4;
					arena.setTimer(13);
				}
				} else if (arena.getDifficulty().equals("hard")) {
				if (temp == 1) {
					int no1 = AUtils.randomInteger(1, 30);
					int no2 = AUtils.randomInteger(1, 30);
					answer = no1 * no2;
					question = no1 + " * " + no2;
					arena.setTimer(30);
				} else if (temp == 2) {
					int no1 = AUtils.randomInteger(1, 10);
					int no2 = AUtils.randomInteger(1, 10);
					int no3 = AUtils.randomInteger(1, 9);
					answer = no1 * no2 * no3;
					question = no1 + " * " + no2 + " * " + no3;
					arena.setTimer(25);
				} else if (temp == 3) {
					int no1 = AUtils.randomInteger(1, 50);
					int no2 = AUtils.randomInteger(1, 50);
					int min = Math.min(no1, no2);
					int max = Math.max(no1, no2);
					int no3 = AUtils.randomInteger(1, 9);
					answer = (max - min) * no3;
					question = "(" + max + " - " + min + ") * " + no3;
					arena.setTimer(25);
				} else if (temp == 4) {
					int no1 = AUtils.randomInteger(1, 15);
					int no2 = AUtils.randomInteger(1, 16);
					int no3 = AUtils.randomInteger(1, 15);
					int no4 = AUtils.randomInteger(1, 17);
					answer = (no1 + no2) * (no3 + no4);
					question = "(" + no1 + " + " + no2 + ") * (" + no3 + " + " + no4 + ")";
					arena.setTimer(23);
				} else if (temp == 5) {
					int no1 = AUtils.randomInteger(1, 999);
					int no2 = AUtils.randomInteger(1, 999);
					int min = Math.min(no1, no2);
					int max = Math.max(no1, no2);
					answer = max - min;
					question = max + " - " + min;
					arena.setTimer(7.5);
				}
				}
				*/
				int downRange = AUtils.randomInteger(-(answer / 2), answer / 2);
				int upRange = AUtils.randomInteger(-(answer / 2), answer / 2);
				ArrayList<Integer> choices = new ArrayList<Integer>();
				int fakeAnswer1 = answer + downRange;
				int fakeAnswer2 = answer + upRange;
				choices.add(answer);
				choices.add(fakeAnswer1);
				choices.add(fakeAnswer2);
				TheMathKingE.this.getChoices().put(temp, new ArrayList<Integer>());
				for (int choice : choices) {
					int fNo;
					int sNo;
					int tNo;
					if (choice < 100) {
						fNo = 0;
						if (choice < 10) {
							sNo = 0;
							if (choice < 1) {
								tNo = 0;
							} else {
								tNo = Integer.parseInt(Integer.toString(choice).substring(0, 1));
							}
						} else {
							sNo = Integer.parseInt(Integer.toString(choice).substring(0, 1));
							tNo = Integer.parseInt(Integer.toString(choice).substring(1, 2));
						}
					} else {
						fNo = Integer.parseInt(Integer.toString(choice).substring(0, 1));
						sNo = Integer.parseInt(Integer.toString(choice).substring(1, 2));
						tNo = Integer.parseInt(Integer.toString(choice).substring(2, 3));
					}
					int choiceNo;
					do {
						choiceNo = AUtils.randomInteger(1, 3);
					} while (TheMathKingE.this.getChoices().get(temp).contains(choiceNo));
					TheMathKingE.this.getAnswers().put(choiceNo, choice);
					TheMathKingE.this.getChoices().get(temp).add(choiceNo);
					Location fLoc = TheMathKingE.this.getChoiceLocation().get(temp).get(choiceNo);
					Location sLoc = fLoc.clone().add(-1, 0, 0);
					Location tLoc = sLoc.clone().add(-1, 0, 0);
					Block fBlock = fLoc.getBlock();
					Block sBlock = sLoc.getBlock();
					Block tBlock = tLoc.getBlock();
					fBlock.setType(Material.AIR);
					sBlock.setType(Material.AIR);
					tBlock.setType(Material.AIR);
					fBlock.setType(Material.WALL_BANNER);
					sBlock.setType(Material.WALL_BANNER);
					tBlock.setType(Material.WALL_BANNER);
					BlockState fBS = fBlock.getState();
					BlockState sBS = sBlock.getState();
					BlockState tBS = tBlock.getState();
					Banner fBanner = (Banner) fBS;
					Banner sBanner = (Banner) sBS;
					Banner tBanner = (Banner) tBS;
					TheMathKingE.this.updateBanner(fBanner, fNo);
					TheMathKingE.this.updateBanner(sBanner, sNo);
					TheMathKingE.this.updateBanner(tBanner, tNo);
					org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) fBanner.getData();
					bannerData.setFacingDirection(BlockFace.NORTH);
					fBanner.setData(bannerData);
					sBanner.setData(bannerData);
					tBanner.setData(bannerData);
				}
				question = "¡±6" + question;
				question = question + "¡±a = ?";
				TheMathKingE.this.setAnswer(answer);
				for (String playerName : TheMathKingE.this.getPlayers()) {
					Player player = Bukkit.getPlayer(playerName);
					TitleAPI.sendTitle(player, 3, 60, 3, question);
				}
			}
		}.runTaskLater(Main.getInstance(), delay2);

	}

	public void execute() {
		int round = this.getRound();
		if (round < 1) return;
		this.setTimer(-1);
		ArrayList<String> deathList = new ArrayList<String>();
		for (String playerName : this.getPlayers()) {
			if (this.getDeadPlayers().contains(playerName))
				continue;
			if (round == 5) {
				this.playerDeath(Bukkit.getPlayer(playerName), DamageType.TIME);
			}
			if (!this.getSteppedPlayers().contains(playerName)) {
				deathList.add(playerName);
			}
		}
		if (deathList.size() > 0) {
			Location corner1 = this.getDeathLocation().get(round).getFirstCorner();
			Location corner2 = this.getDeathLocation().get(round).getSecondCorner();
			double x = ((corner1.getBlockX() + corner2.getBlockX()) / 2) + (AUtils.randomInteger(-2, 2) / 10d);
			double y = corner1.getY() + 1.01;
			double z = ((corner1.getBlockZ() + corner2.getBlockZ()) / 2) + (AUtils.randomInteger(-2, 2) / 10d);
			Location tpLocation = new Location(this.getWorld(), x, y, z);
			tpLocation.setYaw(180);
			for (String playerName : deathList) {
				Player player = Bukkit.getPlayer(playerName);
				this.disallowDJ(playerName);
				AUtils.freezePlayer(player);
				player.setVelocity(new Vector(0, 0, 0));
				player.teleport(tpLocation);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					public void run() {
						tpLocation.setYaw(player.getLocation().getYaw());
						tpLocation.setPitch(player.getLocation().getPitch());
						TheMathKingE.this.getPlayerStatus().get(playerName).add(PStatus.INVINCIBLE);
						player.teleport(tpLocation);
					}
				}, 60L);
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
				public void run() {
					for (String playerName : deathList) {
						Player player = Bukkit.getPlayer(playerName);
						TheMathKingE.this.playerDeath(player, DamageType.LAVA);
					}
				}
			}, 100L);
		}
		new BukkitRunnable() {
			public void run() {
				Location loc1 = TheMathKingE.this.getDeathLocation().get(round).getFirstCorner();
				Location loc2 = TheMathKingE.this.getDeathLocation().get(round).getSecondCorner();
				int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
				int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
				int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
				int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
				int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
				int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
				World world = TheMathKingE.this.getWorld();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					public void run() {
						for (int x = minX; x <= maxX; x++) {
							for (int y = minY; y <= maxY; y++) {
								for (int z = minZ; z <= maxZ; z++) {
									Block block = world.getBlockAt(x, y, z);
									block.setType(Material.STAINED_GLASS);
									block.setData(DyeColor.RED.getWoolData());
									Location loc = block.getLocation();
									PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SMOKE_LARGE, false, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) 0.5, (float) 0.5, (float) 0.5, 0, 30, null);
									for (String playerName : TheMathKingE.this.getPlayers()) {
										Player player = Bukkit.getPlayer(playerName);
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
									}
								}
							}
						}
					}
				}, 40L);
				for (int x = minX; x <= maxX; x++) {
					for (int y = minY; y <= maxY; y++) {
						for (int z = minZ; z <= maxZ; z++) {
							Block block = world.getBlockAt(x, y, z);
							block.setType(Material.AIR);
							FallingBlock fb = world.spawnFallingBlock(block.getLocation(), Material.STAINED_GLASS, (byte) 14);
							fb.setDropItem(false);
							world.playEffect(block.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
						}
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 60);
		new BukkitRunnable() {
			public void run() {
				Location loc1 = TheMathKingE.this.getContinueLocation().get(round).getFirstCorner();
				Location loc2 = TheMathKingE.this.getContinueLocation().get(round).getSecondCorner();
				int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
				int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
				int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
				int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
				int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
				int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
				World world = TheMathKingE.this.getWorld();
				for (int x = minX; x <= maxX; x++) {
					for (int y = minY; y <= maxY; y++) {
						for (int z = minZ; z <= maxZ; z++) {
							Block block = world.getBlockAt(x, y, z);
							block.setType(Material.AIR);
							Location loc = block.getLocation();
							PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, false, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), (float) 0.5, (float) 0.5, (float) 0.5, 0, 30, null);
							for (String playerName : TheMathKingE.this.getPlayers()) {
								Player player = Bukkit.getPlayer(playerName);
								((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
							}
						}
					}
				}
				TheMathKingE.this.sendTitle(3, 20, 0, "¡±e3", "");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					public void run() {
						TheMathKingE.this.sendTitle(0, 20, 0, "¡±e2", "");
					}
				}, 12L);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					public void run() {
						TheMathKingE.this.sendTitle(0, 20, 0, "¡±e1", "");
					}
				}, 24L);
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					public void run() {
						TheMathKingE.this.nextRound();
						for (String playerName : TheMathKingE.this.getPlayers()) {
							Player player = Bukkit.getPlayer(playerName);
							if (!TheMathKingE.this.getDeadPlayers().contains(playerName)) {
								AUtils.unfreezePlayer(player, true);
								TheMathKingE.this.allowDJ(playerName);
							}
						}
						TheMathKingE.this.sendTitle(0, 20, 3, "¡±aGO!", "");
					}
				}, 36L);
			}
		}.runTaskLater(Main.getInstance(), 150);
	}

	/*
	public void resetArena() {
		TheMathKing arena = this;
		arena.setRound(0);
		arena.getAnswers().clear();
		arena.getSteppedPlayers().clear();
		arena.getActivatedPlate().clear();
		for (int round : arena.getJudgementDoor().keySet()) {
			for (int temp : arena.getJudgementDoor().get(round).keySet()) {
				Location loc1 = arena.getJudgementDoor().get(round).get(temp).get(0);
				Location loc2 = arena.getJudgementDoor().get(round).get(temp).get(1);
				int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
				int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
				int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
				int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
				int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
				int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
				World world = Bukkit.getWorld(arena.getWorld());
				for (int x = minX; x <= maxX; x++) {
					for (int y = minY; y <= maxY; y++) {
						for (int z = minZ; z <= maxZ; z++) {
							Block block = world.getBlockAt(x, y, z);
							block.setType(Material.STAINED_GLASS);
							block.setData(DyeColor.YELLOW.getData());
						}
					}
				}
			}
		}
		for (int round : arena.getDeathLocation().keySet()) {
			Location loc1 = arena.getDeathLocation().get(round).get(0);
			Location loc2 = arena.getDeathLocation().get(round).get(1);
			int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
			int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
			int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
			int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
			int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			World world = Bukkit.getWorld(arena.getWorld());
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						Block block = world.getBlockAt(x, y, z);
						block.setType(Material.STAINED_GLASS);
						block.setData(DyeColor.RED.getData());
					}
				}
			}
		}
		for (int round : arena.getContinueLocation().keySet()) {
			Location loc1 = arena.getContinueLocation().get(round).get(0);
			Location loc2 = arena.getContinueLocation().get(round).get(1);
			int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
			int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
			int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
			int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
			int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			World world = Bukkit.getWorld(arena.getWorld());
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						Block block = world.getBlockAt(x, y, z);
						block.setType(Material.STAINED_GLASS);
						block.setData(DyeColor.BLACK.getData());
					}
				}
			}
		}
	}
	*/

	public ArrayList<String> getSteppedPlayers() {
		return steppedPlayers;
	}

	public void setSteppedPlayers(ArrayList<String> steppedPlayers) {
		this.steppedPlayers = steppedPlayers;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Location>>> getJudgementDoor() {
		return judgementDoor;
	}

	public HashMap<Integer, Location> getPlateLocation() {
		return plateLocation;
	}

	public HashMap<Integer, Region> getDeathLocation() {
		return deathLocation;
	}

	public HashMap<Integer, Region> getContinueLocation() {
		return continueLocation;
	}

	public HashMap<Integer, ArrayList<Integer>> getChoices() {
		return choices;
	}

	public void setChoices(HashMap<Integer, ArrayList<Integer>> choices) {
		this.choices = choices;
	}

	public HashMap<Integer, HashMap<Integer, Location>> getChoiceLocation() {
		return choiceLocation;
	}

	public ArrayList<Location> getActivatedPlate() {
		return activatedPlate;
	}

	public void setActivatedPlate(ArrayList<Location> activatedPlate) {
		this.activatedPlate = activatedPlate;
	}

	public HashMap<Integer, Integer> getAnswers() {
		return answers;
	}

	public ArrayList<Location> getAvailablePlates() {
		return availablePlates;
	}

	public void timesUp() {
		if (this.round == 5) {
			for (String playerName : this.getPlayers()) {
				if (!this.getDeadPlayers().contains(playerName))
					this.playerDeath(Bukkit.getPlayer(playerName), DamageType.TIME);
			}
		} else {
			this.execute();
		}
	}

	@EventHandler
	public void onArenaStart(ArenaStartEvent event) {
		if (event.getArena().equals(this)) {
			this.nextRound();
		}
	}

	@Override
	public void endGame() {
		super.endGame();
	}

	@Override
	public void playerDamage(Player player, DamageType dt, Object... args) {
		super.playerDamage(player, dt, args);
		if (dt.equals(DamageType.TIME)) {
			this.execute();
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!this.getPlayers().contains(player.getName()))
			return;
		if (event.getAction().equals(Action.PHYSICAL)) {
			Material mat = event.getClickedBlock().getType();
			if (mat.equals(Material.STONE_PLATE)) {
				if (!this.getDeadPlayers().contains(player.getName())) {
					Location location = event.getClickedBlock().getLocation();
					if (!this.getActivatedPlate().contains(location) && this.getAvailablePlates().contains(location)) {
						int round = this.getRound();
						this.getSteppedPlayers().add(player.getName());
						this.getActivatedPlate().add(location);
						AUtils.freezePlayer(player);
						Location tpLocation = location.clone();
						tpLocation.setX(tpLocation.getX() + 0.5d);
						tpLocation.setY(tpLocation.getY() + 0.01d);
						tpLocation.setZ(tpLocation.getZ() + 0.5d);
						player.teleport(tpLocation);
						this.disallowDJ(player.getName());
						player.setVelocity(new Vector(0, 0, 0));
						if ((this.getActivatedPlate().size() >= this.getAvailablePlates().size()) || (this.getActivatedPlate().size() >= this.getPlayers().size() - this.getDeadPlayers().size())) {
							this.execute();

						}
					}
				} else
					event.setCancelled(true);
			} else if (mat.equals(Material.WOOD_PLATE) || mat.equals(Material.IRON_PLATE) || mat.equals(Material.GOLD_PLATE)) {
				int choice = 0;
				if (mat.equals(Material.WOOD_PLATE))
					choice = 1;
				else if (mat.equals(Material.GOLD_PLATE))
					choice = 2;
				else if (mat.equals(Material.IRON_PLATE))
					choice = 3;
				if (!this.getDeadPlayers().contains(player.getName())) {
					int attempt = this.getAnswers().get(choice);
					Location loc1 = this.getJudgementDoor().get(round).get(choice).get(0);
					Location loc2 = this.getJudgementDoor().get(round).get(choice).get(1);
					Region region = new Region(loc1, loc2);
					if (attempt == this.getAnswer()) {
						if (loc1.getBlock() != null && loc1.getBlock().getType().equals(Material.STAINED_GLASS)) {
							region.setBlock(Material.AIR, Effect.STEP_SOUND);
						}
						region.playEffect(Effect.HAPPY_VILLAGER, 0);
					} else {
						region.setBlock(Material.STAINED_CLAY, null, DyeColor.RED.getWoolData());
						region.playStepSound(Material.REDSTONE_BLOCK);
					}
				} else
					event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.getEntityType().equals(EntityType.FALLING_BLOCK)) {
			if (event.getEntity().getWorld().equals(this.getWorld()))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getBlock().getWorld().equals(this.getWorld()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getBlock().getWorld().equals(this.getWorld()))
			event.setCancelled(true);
	}
}
