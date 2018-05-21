package hell.supersoul.arena.arenas.practice;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.enums.ASetting;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.modules.Team;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.utils.Region;

public class Teamwork extends Race implements Team, Listener {

	public Teamwork(String id) {
		super(id);
	}

	HashMap<Integer, Location> snowballLoc1 = new HashMap<Integer, Location>();
	HashMap<Integer, Location> snowballLoc2 = new HashMap<Integer, Location>();
	HashMap<ChatColor, ArrayList<Location>> stoneButtons = new HashMap<>();
	HashMap<ChatColor, ArrayList<Boolean>> buttonPressed = new HashMap<>();
	HashMap<ChatColor, Integer> rounds = new HashMap<>();
	HashMap<Integer, Location> doorCorner1 = new HashMap<Integer, Location>();
	HashMap<Integer, Location> doorCorner2 = new HashMap<Integer, Location>();
	HashMap<Integer, ArrayList<Location>> plateLocation = new HashMap<Integer, ArrayList<Location>>();
	ArrayList<ArrayList<Integer>> roadPath = new ArrayList<>();
	HashMap<ChatColor, ArrayList<Region>> roadDetection = new HashMap<>();
	HashMap<ChatColor, ArrayList<Location>> roadCorner = new HashMap<>();
	HashMap<ChatColor, ArrayList<Integer>> roadProgress = new HashMap<>();
	HashMap<ChatColor, ArrayList<Location>> roundFourCorners = new HashMap<>();

	@Override
	public void iniArena(String mapName, World world) {
		super.iniArena(mapName, world);
		this.getSnowballLoc1().put(1, new Location(world, -19, 42, 52, 270, 0));
		this.getSnowballLoc1().put(2, new Location(world, -19, 42, -44, 270, 0));
		this.getSnowballLoc1().put(3, new Location(world, 11, 42, 52, 90, 0));
		this.getSnowballLoc1().put(4, new Location(world, 11, 42, -44, 90, 0));
		this.getSnowballLoc2().put(1, new Location(world, -19, 42, -21, 270, 0));
		this.getSnowballLoc2().put(2, new Location(world, -19, 42, -133, 270, 0));
		this.getSnowballLoc2().put(3, new Location(world, 11, 42, -21, 90, 0));
		this.getSnowballLoc2().put(4, new Location(world, 11, 42, -133, 90, 0));
		this.getDoorCorner1().put(1, new Location(world, -14, 36, 240));
		this.getDoorCorner1().put(2, new Location(world, -14, 49, 64));
		this.getDoorCorner1().put(3, new Location(world, -14, 49, -150));
		this.getDoorCorner1().put(4, new Location(world, -14, 52, -301));
		this.getDoorCorner1().put(5, new Location(world, 0, 36, 240));
		this.getDoorCorner1().put(6, new Location(world, 0, 49, 64));
		this.getDoorCorner1().put(7, new Location(world, 0, 49, -150));
		this.getDoorCorner1().put(8, new Location(world, 0, 52, -301));
		this.getDoorCorner2().put(1, new Location(world, -8, 29, 240));
		this.getDoorCorner2().put(2, new Location(world, -8, 42, 64));
		this.getDoorCorner2().put(3, new Location(world, -8, 42, -150));
		this.getDoorCorner2().put(4, new Location(world, -8, 45, -301));
		this.getDoorCorner2().put(5, new Location(world, 6, 29, 240));
		this.getDoorCorner2().put(6, new Location(world, 6, 42, 64));
		this.getDoorCorner2().put(7, new Location(world, 6, 42, -150));
		this.getDoorCorner2().put(8, new Location(world, 6, 45, -301));
		ArrayList<Location> temp = new ArrayList<>();
		temp.add(new Location(world, -11, 29, 244));
		this.getPlateLocation().put(1, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, -13, 42, 68));
		temp.add(new Location(world, -9, 42, 68));
		this.getPlateLocation().put(2, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, -13, 42, -146));
		temp.add(new Location(world, -9, 42, -146));
		this.getPlateLocation().put(3, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, -13, 45, -297));
		temp.add(new Location(world, -9, 45, -297));
		this.getPlateLocation().put(4, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, 3, 29, 244));
		this.getPlateLocation().put(5, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, 1, 42, 68));
		temp.add(new Location(world, 5, 42, 68));
		this.getPlateLocation().put(6, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, 1, 42, -146));
		temp.add(new Location(world, 5, 42, -146));
		this.getPlateLocation().put(7, (ArrayList<Location>) temp.clone());
		temp.clear();
		temp.add(new Location(world, 1, 45, -297));
		temp.add(new Location(world, 5, 45, -297));
		this.getPlateLocation().put(8, (ArrayList<Location>) temp.clone());
		temp.clear();
		this.getTeamRespawnPoints().put(ChatColor.RED, new ArrayList<>());
		this.getTeamRespawnPoints().put(ChatColor.BLUE, new ArrayList<>());
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -7, 126, 351, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, -1, 126, 351, 180, 0));
		this.getCheckPoints().add(null);
		this.getCheckPoints().add(new Region(new Location(world, -17, 58, 364), new Location(world, 8, 14, 284)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -9, 30, 350, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 1, 30, 350, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 8, 9, 239), new Location(world, -17, 35, 197)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -12, 28, 228, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 28, 228, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 37, 169), new Location(world, -19, 18, 139)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -12, 28, 165, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 28, 165, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 50, 63), new Location(world, -19, 31, 22)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -11, 41, 58, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 41, 58, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 52, -23), new Location(world, -19, 30, -94)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -11, 41, -39, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 41, -39, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 50, -151), new Location(world, -19, 15, -207)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -11, 41, -159, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 41, -159, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 71, -198), new Location(world, -19, 33, -252)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -11, 49, -212, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, 3, 49, -212, 180, 0));
		this.getCheckPoints().add(new Region(new Location(world, 11, 53, -302), new Location(world, -19, 33, -360)));
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, -4, 44, -308, 180, 0));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, -4, 44, -308, 180, 0));
		this.getButtonPressed().put(ChatColor.RED, new ArrayList<>());
		this.getButtonPressed().put(ChatColor.BLUE, new ArrayList<>());
		this.getButtonPressed().get(ChatColor.RED).add(false);
		this.getButtonPressed().get(ChatColor.RED).add(false);
		this.getButtonPressed().get(ChatColor.RED).add(false);
		this.getButtonPressed().get(ChatColor.RED).add(false);
		this.getButtonPressed().get(ChatColor.BLUE).add(false);
		this.getButtonPressed().get(ChatColor.BLUE).add(false);
		this.getButtonPressed().get(ChatColor.BLUE).add(false);
		this.getButtonPressed().get(ChatColor.BLUE).add(false);
		this.getRounds().put(ChatColor.RED, 0);
		this.getRounds().put(ChatColor.BLUE, 0);
		this.getRoadPath().add(new ArrayList<>());
		this.getRoadPath().add(new ArrayList<>());
		this.getRoadPath().get(0).add(1);
		this.getRoadPath().get(1).add(1);
		for (int i = 1; i < 18; i++) {
			this.getRoadPath().get(0).add(AUtils.randomInteger(0, 2));
		}
		for (int i = 1; i < 27; i++) {
			this.getRoadPath().get(1).add(AUtils.randomInteger(0, 2));
		}
		ArrayList<Integer> tempi = new ArrayList<>();
		tempi.add(0);
		tempi.add(0);
		this.getRoadProgress().put(ChatColor.RED, (ArrayList<Integer>) tempi.clone());
		this.getRoadProgress().put(ChatColor.BLUE, (ArrayList<Integer>) tempi.clone());
		this.getRoadCorner().put(ChatColor.RED, new ArrayList<>());
		this.getRoadCorner().put(ChatColor.BLUE, new ArrayList<>());
		this.getRoadCorner().get(ChatColor.RED).add(new Location(world, -14, 27, 223));
		this.getRoadCorner().get(ChatColor.BLUE).add(new Location(world, 1, 27, 223));
		this.getRoadCorner().get(ChatColor.RED).add(new Location(world, -14, 27, 158));
		this.getRoadCorner().get(ChatColor.BLUE).add(new Location(world, 1, 27, 158));
		Region r1 = new Region(new Location(world, -12, 27, 223), new Location(world, -11, 27, 221));
		r1.setBlock(Material.STEP, Effect.STEP_SOUND, 8);
		Region r2 = new Region(new Location(world, 3, 27, 223), new Location(world, 4, 27, 221));
		r2.setBlock(Material.STEP, Effect.STEP_SOUND, 8);
		Region r3 = new Region(new Location(world, -12, 27, 158), new Location(world, -11, 27, 156));
		r3.setBlock(Material.STEP, Effect.STEP_SOUND, 8);
		Region r4 = new Region(new Location(world, 3, 27, 158), new Location(world, 4, 27, 156));
		r4.setBlock(Material.STEP, Effect.STEP_SOUND, 8);
		this.getRoadDetection().put(ChatColor.RED, new ArrayList<>());
		this.getRoadDetection().put(ChatColor.BLUE, new ArrayList<>());
		this.getRoadDetection().get(ChatColor.RED).add(new Region(new Location(world, -11, 27, 223), new Location(world, -12, 27, 221)));
		this.getRoadDetection().get(ChatColor.RED).add(new Region(new Location(world, -12, 27, 158), new Location(world, -11, 27, 156)));
		this.getRoadDetection().get(ChatColor.BLUE).add(new Region(new Location(world, 3, 27, 223), new Location(world, 4, 27, 221)));
		this.getRoadDetection().get(ChatColor.BLUE).add(new Region(new Location(world, 4, 27, 156), new Location(world, 3, 27, 158)));
		this.getSettings().add(ASetting.useDiffRespawnForTeam);
		this.setStartPlatform(new Region(new Location(world, 1, 125, 349), new Location(world, -9, 125, 353)));
		this.setGoal(new Region(new Location(world, -6, 43, -357), new Location(world, -2, 55, -359)));
		this.getPlaceLocations().add(new Location(world, -4, 46, -366, 180, 0));
		this.getPlaceLocations().add(new Location(world, -4, 44, -371));
		this.getPlaceLocations().add(new Location(world, -4, 44, -371));
		this.getPlaceLocations().add(new Location(world, -4, 44, -371));
		this.getDisabledDamage().add(DamageType.FALL);
		this.setDeathLevel(20);
		this.getStoneButtons().put(ChatColor.RED, new ArrayList<>());
		this.getStoneButtons().put(ChatColor.BLUE, new ArrayList<>());
		this.getStoneButtons().get(ChatColor.RED).add(new Location(world, -11, 42, -37));
		this.getStoneButtons().get(ChatColor.RED).add(new Location(world, -11, 42, -136));
		this.getStoneButtons().get(ChatColor.RED).add(new Location(world, -11, 50, -209));
		this.getStoneButtons().get(ChatColor.RED).add(new Location(world, -11, 45, -290));
		this.getStoneButtons().get(ChatColor.BLUE).add(new Location(world, 3, 42, -37));
		this.getStoneButtons().get(ChatColor.BLUE).add(new Location(world, 3, 42, -136));
		this.getStoneButtons().get(ChatColor.BLUE).add(new Location(world, 3, 50, -209));
		this.getStoneButtons().get(ChatColor.BLUE).add(new Location(world, 3, 45, -290));
		this.getRespawnDamage().add(DamageType.VOID);
		this.getRoundFourCorners().put(ChatColor.RED, new ArrayList<>());
		this.getRoundFourCorners().put(ChatColor.BLUE, new ArrayList<>());
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -14, 40, -166));
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -8, 40, -197));
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -14, 48, -197));
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -14, 40, -221));
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -8, 40, -285));
		this.getRoundFourCorners().get(ChatColor.RED).add(new Location(world, -14, 43, -285));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 0, 40, -166));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 6, 40, -197));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 0, 48, -197));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 0, 40, -221));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 6, 40, -285));
		this.getRoundFourCorners().get(ChatColor.BLUE).add(new Location(world, 0, 43, -285));
		this.setBgmName("teamwork");
		this.getDisabledDamage().add(DamageType.PLAYER);
		this.getDisabledDamage().add(DamageType.FIRE);
		this.getDisabledDamage().add(DamageType.FIRE_TICK);
		this.getRespawnDamage().add(DamageType.UNKNOWN);
		this.setDisplayName(ChatColor.BLUE + "Teamwork");
	}

	public HashMap<Integer, Location> getSnowballLoc1() {
		return snowballLoc1;
	}

	public HashMap<Integer, Location> getSnowballLoc2() {
		return snowballLoc2;
	}

	public HashMap<Integer, Location> getDoorCorner1() {
		return doorCorner1;
	}

	public HashMap<Integer, Location> getDoorCorner2() {
		return doorCorner2;
	}

	public HashMap<Integer, ArrayList<Location>> getPlateLocation() {
		return plateLocation;
	}

	public HashMap<ChatColor, ArrayList<Boolean>> getButtonPressed() {
		return buttonPressed;
	}

	public HashMap<ChatColor, ArrayList<Location>> getStoneButtons() {
		return stoneButtons;
	}

	public HashMap<ChatColor, Integer> getRounds() {
		return rounds;
	}

	public ArrayList<ArrayList<Integer>> getRoadPath() {
		return roadPath;
	}

	public HashMap<ChatColor, ArrayList<Region>> getRoadDetection() {
		return roadDetection;
	}

	public HashMap<ChatColor, ArrayList<Location>> getRoadCorner() {
		return roadCorner;
	}

	public HashMap<ChatColor, ArrayList<Integer>> getRoadProgress() {
		return roadProgress;
	}

	public HashMap<ChatColor, ArrayList<Location>> getRoundFourCorners() {
		return roundFourCorners;
	}

	public void InfinityLoop() {
		for (int id : this.getSnowballLoc1().keySet()) {
			Location snowballLoc1 = this.getSnowballLoc1().get(id);
			Location snowballLoc2 = this.getSnowballLoc2().get(id);
			int x1 = snowballLoc1.getBlockX();
			int y1 = snowballLoc1.getBlockY() + 1;
			int z1 = snowballLoc1.getBlockZ();
			int z2 = snowballLoc2.getBlockZ();
			int minz = Math.min(z1, z2);
			int maxz = Math.max(z1, z2);
			Vector v = snowballLoc1.getDirection();
			new BukkitRunnable() {
				public void run() {
					if (!Teamwork.this.getStatus().equals(AStatus.inGame))
						cancel();
					if (id <= 2 && Teamwork.this.getButtonPressed().get(ChatColor.RED).get(id - 1))
						cancel();
					else if (id > 2 && Teamwork.this.getButtonPressed().get(ChatColor.BLUE).get(id - 3))
						cancel();
					int newZ = AUtils.randomNumber(minz, maxz);
					Location spawnLocation = new Location(Teamwork.this.getWorld(), x1, y1, newZ);
					Snowball snowball = Teamwork.this.getWorld().spawn(spawnLocation, Snowball.class);
					snowball.setVelocity(v);
					newZ = AUtils.randomNumber(minz, maxz);
					spawnLocation = new Location(Teamwork.this.getWorld(), x1, y1, newZ);
					Snowball snowball1 = Teamwork.this.getWorld().spawn(spawnLocation, Snowball.class);
					snowball1.setVelocity(v);
					newZ = AUtils.randomNumber(minz, maxz);
					spawnLocation = new Location(Teamwork.this.getWorld(), x1, y1, newZ);
					Snowball snowball2 = Teamwork.this.getWorld().spawn(spawnLocation, Snowball.class);
					snowball2.setVelocity(v);
					newZ = AUtils.randomNumber(minz, maxz);
					spawnLocation = new Location(Teamwork.this.getWorld(), x1, y1, newZ);
					Snowball snowball3 = Teamwork.this.getWorld().spawn(spawnLocation, Snowball.class);
					snowball3.setVelocity(v);
					newZ = AUtils.randomNumber(minz, maxz);
					spawnLocation = new Location(Teamwork.this.getWorld(), x1, y1, newZ);
					Snowball snowball4 = Teamwork.this.getWorld().spawn(spawnLocation, Snowball.class);
					snowball4.setVelocity(v);
				}
			}.runTaskTimer(Main.getInstance(), 0, 5);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!this.getPlayers().contains(player.getName()))
			return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block clicked = event.getClickedBlock();
			if (clicked.getType().equals(Material.STONE_BUTTON)) {
				Location buttonLocation = clicked.getLocation();
				for (ChatColor team : this.getStoneButtons().keySet()) {
					for (Location loc : this.getStoneButtons().get(team)) {
						if (loc.equals(buttonLocation)) {
							int id = this.getStoneButtons().get(team).indexOf(loc);
							if (this.getButtonPressed().get(team).get(id))
								return;
							this.getButtonPressed().get(team).set(id, true);
							Block block = this.getWorld().getBlockAt(buttonLocation.subtract(0, 0, 1));
							block.setType(Material.WOOL);
							block.setData(DyeColor.LIME.getWoolData());
							if (id > 1) {
								Location loc1 = this.getRoundFourCorners().get(team).get(0 + 3 * (id - 2));
								Location loc2 = this.getRoundFourCorners().get(team).get(1 + 3 * (id - 2));
								Location loc3 = this.getRoundFourCorners().get(team).get(2 + 3 * (id - 2));
								Region r1 = new Region(loc1, loc2);
								Region r2 = new Region(loc2.clone().add(0, 1, 0), loc3);
								r1.setBlock(Material.STEP, null, 8);
								r2.setBlock(Material.LADDER, null, 3);
							}
						}
					}
				}
			}
		} else if (event.getAction().equals(Action.PHYSICAL)) {
			if (event.getClickedBlock().getType().equals(Material.GOLD_PLATE)) {
				Location plateLocation = event.getClickedBlock().getLocation();
				for (int id : this.getPlateLocation().keySet()) {
					if (this.getPlateLocation().get(id).contains(plateLocation)) {
						ChatColor team = null;
						if (id <= 4) {
							if (this.getRounds().get(ChatColor.RED) != id - 1)
								return;
							team = ChatColor.RED;
						}
						if (id > 4) {
							if (this.getRounds().get(ChatColor.BLUE) != id - 5)
								return;
							team = ChatColor.BLUE;
						}
						int steppedPlayer = 0;
						for (Location l : this.getPlateLocation().get(id)) {
							Location lamp = new Location(l.getWorld(), l.getX(), l.getY() - 1, l.getZ());
							if (lamp.getBlock().getType().equals(Material.REDSTONE_LAMP_ON)) {
								steppedPlayer++;
							}
						}
						if (steppedPlayer == this.getPlateLocation().get(id).size()) {
							int round = this.getRounds().get(team);
							this.getRounds().put(team, round + 1);
							final ChatColor t = team;
							new BukkitRunnable() {
								Location doorCorner1 = Teamwork.this.getDoorCorner1().get(id);
								Location doorCorner2 = Teamwork.this.getDoorCorner2().get(id);
								int x1 = doorCorner1.getBlockX();
								int y1 = doorCorner1.getBlockY();
								int z1 = doorCorner1.getBlockZ();
								int x2 = doorCorner2.getBlockX();
								int y2 = doorCorner2.getBlockY();
								int z2 = doorCorner2.getBlockZ();
								int minX = Math.min(x1, x2);
								int minY = Math.min(y1, y2);
								int minZ = Math.min(z1, z2);
								int maxX = Math.max(x1, x2);
								int maxY = Math.max(y1, y2);
								int maxZ = Math.max(z1, z2);
								int count = 0;

								public void run() {
									int temp = 0;
									for (Location l : Teamwork.this.getPlateLocation().get(id)) {
										Location lamp = new Location(l.getWorld(), l.getX(), l.getY() - 1, l.getZ());
										if (lamp.getBlock().getType() == Material.REDSTONE_LAMP_ON) {
											temp++;
										}
									}
									if (temp == Teamwork.this.getPlateLocation().get(id).size()) {
										count++;
									} else {
										cancel();
										Teamwork.this.getRounds().put(t, round);
									}
									for (int x = minX; x <= maxX; x++) {
										for (int y = minY; y <= maxY; y++) {
											for (int z = minZ; z <= maxZ; z++) {
												if (AUtils.random(20)) {
													Block block = Teamwork.this.getWorld().getBlockAt(x, y, z);
													player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.COAL_BLOCK.getId());
												}
											}
										}
									}
									if (count == 10) {
										for (int x = minX; x <= maxX; x++) {
											for (int y = minY; y <= maxY; y++) {
												for (int z = minZ; z <= maxZ; z++) {
													Block block = Teamwork.this.getWorld().getBlockAt(x, y, z);
													block.setType(Material.AIR);
													player.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.EMERALD_BLOCK.getId());
													cancel();
												}
											}
										}
									}
								}
							}.runTaskTimer(Main.getInstance(), 2, 5);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onArenaStart(ArenaStartEvent event) {
		if (event.getArena() == this) {
			this.InfinityLoop();
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getBlockY() == event.getTo().getBlockY()) {
			return;
		}
		Player player = event.getPlayer();
		if (!this.getPlayers().contains(player.getName()))
			return;
		Block block = event.getTo().getBlock();
		if (!block.getType().equals(Material.STEP)) {
			 block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
			 if (!block.getType().equals(Material.STEP)) {
					return;
			 }
		}
		Location loc = block.getLocation();
		ChatColor team = this.getPlayerTeam(player.getName());
		if (team == null)
			return;
		for (Region region : this.getRoadDetection().get(team)) {
			if (region.isInRegion(loc)) {
				int id = this.getRoadDetection().get(team).indexOf(region);
				int progress = this.getRoadProgress().get(team).get(id);
				if (progress >= this.getRoadPath().get(id).size() - 1)
					return;
				this.getRoadProgress().get(team).set(id, progress + 1);
				int x = this.getRoadCorner().get(team).get(id).getBlockX();
				x = x + (2 * this.getRoadPath().get(id).get(progress + 1));
				int y = this.getRoadCorner().get(team).get(id).getBlockY();
				if (id == 1) {
					y = y + (progress + 2) / 2;
				}
				int z = this.getRoadCorner().get(team).get(id).getBlockZ();
				z = z - (3 * (progress + 1));
				Region next = new Region(new Location(this.getWorld(), x, y, z), new Location(this.getWorld(), x + 1, y, z - 2));
				this.getRoadDetection().get(team).set(id, next);
				if (id == 0) {
					next.setBlock(Material.STEP, null, 8);
					next.playStepSound(Material.STEP);
				} else if (id == 1) {
					if (progress % 2 != 0)
						next.setBlock(Material.STEP, null, 8);
					else
						next.setBlock(Material.STEP, Effect.STEP_SOUND);
					next.playStepSound(Material.STEP);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			if (!(event.getEntity() instanceof Player))
				return;
			Player player = (Player) event.getEntity();
			if (!this.getPlayers().contains(player.getName()))
				return;
			if (damager instanceof Snowball) {
				event.setDamage(0);
				event.getEntity().setVelocity(damager.getVelocity().multiply(1.4));
				event.setCancelled(false);
			}
		}
	}
}
