package hell.supersoul.arena.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.arenas.ArenaManager;
import hell.supersoul.arena.enums.ASetting;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.modules.Arena;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.modules.Survive;
import hell.supersoul.arena.modules.Team;
import net.minecraft.server.v1_12_R1.EntityPlayer;

public class AUtils {
	public static ArrayList<Material> blockList = new ArrayList<Material>();
	public static HashMap<Block, Material> clipBoard = new HashMap<Block, Material>();
	public static HashMap<Block, Byte> clipBoardData = new HashMap<Block, Byte>();
	public static ArrayList<String> worldWhitelist = new ArrayList<>();

	static {
		worldWhitelist.add("SMP2");
		worldWhitelist.add("smp3");
		blockList.add(Material.WATER);
		blockList.add(Material.STATIONARY_WATER);
		blockList.add(Material.LAVA);
		blockList.add(Material.STATIONARY_LAVA);
		blockList.add(Material.LONG_GRASS);
		blockList.add(Material.YELLOW_FLOWER);
		blockList.add(Material.RED_ROSE);
		blockList.add(Material.TRIPWIRE);
		blockList.add(Material.SIGN);
		blockList.add(Material.TORCH);
		blockList.add(Material.REDSTONE_TORCH_OFF);
		blockList.add(Material.REDSTONE_TORCH_ON);
		blockList.add(Material.RAILS);
		blockList.add(Material.ACTIVATOR_RAIL);
		blockList.add(Material.DETECTOR_RAIL);
		blockList.add(Material.POWERED_RAIL);
		blockList.add(Material.LEVER);
		blockList.add(Material.STONE_BUTTON);
		blockList.add(Material.WOOD_BUTTON);
		blockList.add(Material.LADDER);
		blockList.add(Material.VINE);
		blockList.add(Material.DEAD_BUSH);
		blockList.add(Material.SAPLING);
		blockList.add(Material.REDSTONE_WIRE);
		blockList.add(Material.CROPS);
		blockList.add(Material.CARROT);
		blockList.add(Material.WALL_SIGN);
		blockList.add(Material.BANNER);
		blockList.add(Material.WALL_BANNER);
		blockList.add(Material.POTATO);
		blockList.add(Material.STONE_PLATE);
		blockList.add(Material.WOOD_PLATE);
		blockList.add(Material.GOLD_PLATE);
		blockList.add(Material.IRON_PLATE);
		blockList.add(Material.FIRE);
		blockList.add(Material.DOUBLE_PLANT);
		blockList.add(Material.SUGAR_CANE);
		blockList.add(Material.PUMPKIN_STEM);
		blockList.add(Material.MELON_STEM);
		blockList.add(Material.NETHER_STALK);
		blockList.add(Material.WATER_LILY);
	}

	public static boolean random(int percentage) {
		Random random = new Random();
		int chance = random.nextInt(100);
		if (chance < percentage) {
			return true;
		} else {
			return false;
		}
	}

	// min and max are inclusive.
	public static int randomNumber(int min, int max) {
		Random random = new Random();
		int randomNum = random.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public static double randomNumberDouble(double min, double max) {
		Random random = new Random();
		double decimal = random.nextDouble();
		double randomNum = min + (max - min) * decimal;
		return randomNum;
	}

	public static void freezePlayer(Player player) {
		player.setFoodLevel(2);
		player.setWalkSpeed(0.0f);
		player.removePotionEffect(PotionEffectType.JUMP);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, -10, false, false));
		player.setCollidable(false);
		Arena arena = ArenaManager.get().getPlayerArena(player.getName());
		if (arena != null)
			arena.getPlayerStatus().get(player.getName()).add(PStatus.FREEZED);
	}

	public static void freezePlayerOnAir(Player player) {
		player.setFlySpeed(0.0f);
		player.setFoodLevel(2);
		player.setAllowFlight(true);
		player.setFlying(true);
		player.setWalkSpeed(0.0f);
		player.setCollidable(false);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 9999, -10, false, false));
		Arena arena = ArenaManager.get().getPlayerArena(player.getName());
		if (arena != null)
			arena.getPlayerStatus().get(player.getName()).add(PStatus.FREEZED);
	}

	public static void unfreezePlayer(Player player, boolean removePotion) {
		player.setFoodLevel(20);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(0.2f);
		player.setWalkSpeed(0.5f);
		player.setCollidable(true);
		if (removePotion)
			for (PotionEffect effect : player.getActivePotionEffects()) {
				player.removePotionEffect(effect.getType());
			}
		player.removePotionEffect(PotionEffectType.JUMP);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 1, false, false));
		Arena arena = ArenaManager.get().getPlayerArena(player.getName());
		if (arena != null)
			arena.getPlayerStatus().get(player.getName()).remove(PStatus.FREEZED);
	}
	
	public static void spectateMode(Player target) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.hidePlayer(target);
		}
		for (PotionEffect effect : target.getActivePotionEffects()) {
			target.removePotionEffect(effect.getType());
		}
		target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 5, false, false));
		target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 9999, 0, false, false));
		target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 1, false, false));
		target.setWalkSpeed(0.0f);
		target.setGameMode(GameMode.ADVENTURE);
		target.setAllowFlight(true);
		target.setFlying(true);
		target.setFlySpeed(0.3f);
		target.setFireTicks(0);
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName(ChatColor.RED + "Teleporter");
		compass.setItemMeta(compassMeta);
		target.getInventory().setItem(0, compass);
	}

	public static void preSpectateMode(Player target) {
		target.getInventory().setItem(0, null);
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.hidePlayer(target);
		}
		for (PotionEffect effect : target.getActivePotionEffects()) {
			target.removePotionEffect(effect.getType());
		}
		target.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 1, false, false));
		target.setVelocity(new Vector());
		target.setWalkSpeed(0.0f);
		target.setGameMode(GameMode.ADVENTURE);
		target.setAllowFlight(true);
		target.setFlying(true);
		target.setFlySpeed(0.0f);
		target.setFireTicks(0);
	}

	public static void normalMode(Player player) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			target.showPlayer(player);
		}
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlySpeed(0.2f);
		player.setWalkSpeed(0.2f);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 1, false, false));
	}

	public static ItemStack playerSkullForName(String playerName) {
		ItemStack is = new ItemStack(Material.SKULL_ITEM, 1);
		is.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(playerName);
		meta.setDisplayName(playerName);
		is.setItemMeta(meta);
		return is;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static boolean isInRegion(Player player, Location loc1, Location loc2) {
		if (loc1.getWorld().equals(loc2.getWorld())) {
			Location playerLocation = player.getLocation();
			int x1 = loc1.getBlockX();
			int x2 = loc2.getBlockX();
			int z1 = loc1.getBlockZ();
			int z2 = loc2.getBlockZ();
			int y1 = loc1.getBlockY();
			int y2 = loc2.getBlockY();
			int minx = (int) Math.min(x1, x2);
			int maxx = (int) Math.max(x1, x2);
			int minz = (int) Math.min(z1, z2);
			int maxz = (int) Math.max(z1, z2);
			int miny = (int) Math.min(y1, y2);
			int maxy = (int) Math.max(y1, y2);
			int px = playerLocation.getBlockX();
			int pz = playerLocation.getBlockZ();
			int py = playerLocation.getBlockY();
			if (minx <= px && px <= maxx) {
				if (minz <= pz && pz <= maxz) {
					if (miny <= py && py <= maxy) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isOnGround(Player player) {
		Block block = player.getWorld().getBlockAt(player.getLocation().clone().subtract(0, 1, 0));
		if (block.getType() != Material.AIR) {
			if (!blockList.contains(block.getType())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInWater(Player player) {
		Block block = player.getLocation().getBlock();
		if (block.getType().equals(Material.WATER) || block.getType().equals(Material.STATIONARY_WATER)) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Integer> sortHashMapByValuesD(Map<String, Integer> map) {
		/*List mapKeys = new ArrayList(passedMap.keySet());
		List mapValues = new ArrayList(passedMap.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);
		
		LinkedHashMap sortedMap = new LinkedHashMap();
		
		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
			Object val = valueIt.next();
			Iterator keyIt = mapKeys.iterator();
		
			while (keyIt.hasNext()) {
				Object key = keyIt.next();
				String comp1 = passedMap.get(key).toString();
				String comp2 = val.toString();
		
				if (comp1.equals(comp2)) {
					passedMap.remove(key);
					mapKeys.remove(key);
					sortedMap.put((String) key, (Integer) val);
					break;
				}
		
			}
		
		}
		return sortedMap;*/
		Map<String, Integer> result = new LinkedHashMap<>();
		Stream<Map.Entry<String, Integer>> st = map.entrySet().stream();
		st.sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}

	public static int getPing(Player p) {
		CraftPlayer cp = (CraftPlayer) p;
		EntityPlayer ep = cp.getHandle();
		return ep.ping;
	}

	public static Location descExcludeLiquid(Location loc) {
		int y = loc.getBlockY();
		while (y > 0) {
			Block block = loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ());
			if (!block.getType().equals(Material.AIR) && !blockList.contains(block.getType())) {
				break;
			}
			y--;
		}
		return new Location(loc.getWorld(), loc.getBlockX(), y, loc.getBlockZ());
	}

	public static void copyAndImportWorld(String source, String target) {
		String command;
		command = "mv load " + source;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mv clone " + source + "  " + target;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mvconfirm";
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mv import " + target + " normal -g EmptyWorldGenerator";
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mvm set monsters false " + target;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mv unload " + source;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static void deleteWorld(String worldName) {
		if (worldWhitelist.contains(worldName)) return;
		String command;
		command = "mv delete " + worldName;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
		command = "mvconfirm";
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static int finishPercent(Player player) {
		Arena arena = ArenaManager.get().getPlayerArena(player.getName());
		if (!(arena instanceof Race))
			return 0;
		Race race = (Race) arena;
		if (race.getRanks().contains(player.getName()))
			return 100;
		if (race.getDisconnectedPlayers().contains(player.getName()))
			return race.getFinishPercent().get(player.getName());
		double totalLength = 0;
		double playerDistance = 0;
		Vector playerloc = player.getLocation().toVector();
		if (race instanceof Survive && race.getDeadPlayers().contains(player.getName())) {
			return (race.getFinishPercent().get(player.getName()));
		}
		int playerID = race.getPlayerCheckPoints().get(player.getName());
		if (race instanceof Team && race.getSettings().contains(ASetting.useDiffRespawnForTeam)) {
			ChatColor team = race.getPlayerTeam(player.getName());
			for (int id = 0; id + 1 <= race.getTeamRespawnPoints().get(team).size(); id++) {
				Vector respawnloc = race.getTeamRespawnPoints().get(team).get(id).toVector();
				double distance;
				Vector respawnloc2;
				double pDistance;
				if (id + 2 > race.getTeamRespawnPoints().get(team).size())
					respawnloc2 = race.getGoal().getFirstCorner().toVector();
				else
					respawnloc2 = race.getTeamRespawnPoints().get(team).get(id + 1).toVector();
				distance = respawnloc.distanceSquared(respawnloc2);
				totalLength = distance + totalLength;
				if (id == playerID) {
					pDistance = playerloc.distanceSquared(respawnloc2);
					playerDistance = playerDistance + pDistance;
				} else if (id > playerID) {
					playerDistance = playerDistance + distance;
				}
			}
		} else {
			for (int id = 0; id + 1 <= race.getRespawnPoints().size(); id++) {
				Vector respawnloc = race.getRespawnPoints().get(id).toVector();
				double distance;
				Vector respawnloc2;
				double pDistance;
				if (id + 1 == race.getRespawnPoints().size()) {
					respawnloc2 = race.getGoal().getFirstCorner().toVector();
				} else {
					respawnloc2 = race.getRespawnPoints().get(id + 1).toVector();
				}
				distance = respawnloc.distanceSquared(respawnloc2);
				totalLength = distance + totalLength;
				if (id == playerID) {
					pDistance = playerloc.distanceSquared(respawnloc2);
					playerDistance = playerDistance + pDistance;
				} else if (id > playerID) {
					playerDistance = playerDistance + distance;
				}
			}
		}
		double percentage = (totalLength - playerDistance) / totalLength * 100;
		race.getFinishPercent().put(player.getName(), (int) percentage);
		return (int) percentage;
	}

	public static ArrayList<String> sortRaceRanks(Race arena) {
		ArrayList<String> playersToSort = new ArrayList<>();
		ArrayList<String> ranks = new ArrayList<String>();
		for (String playerName : arena.getPlayers()) {
			if (arena instanceof Survive && arena.getDeadPlayers().contains(playerName))
				continue;
			if (arena.getRanks().contains(playerName))
				continue;
			if (arena.getDisconnectedPlayers().contains(playerName))
				continue;
			playersToSort.add(playerName);
		}
		ranks.addAll(arena.getRanks());
		Collections.sort(playersToSort, new RanksComparator());
		ranks.addAll(playersToSort);
		return ranks;
	}

	public static void unloadWorld(String worldName) {
		String command;
		command = "mv unload " + worldName;
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
	}

	public static String percentageBar(int bars, int percentage, ChatColor color) {
		int colored = (int) (bars * ((double) percentage / 100d));
		int grey = bars - colored;
		String bar = color.toString();
		bar = bar + StringUtils.repeat("|", colored);
		bar = bar + ChatColor.GRAY;
		bar = bar + StringUtils.repeat("|", grey);
		return bar;
	}

	public static Location normalizeLocation(Location loc) {
		loc.add(0.5d, 0, 0.5d);
		return loc;
	}public static void setBlock(Location loc1, Location loc2, Material mat, byte data, boolean effect) {
		if (loc1.getWorld() == null || loc2.getWorld() == null) return;
		if (mat == null) return;
		if (!loc1.getWorld().equals(loc2.getWorld())) return;
		int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		World world = loc1.getWorld();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (effect)
						world.playEffect(new Location(world, x, y, z), Effect.STEP_SOUND, block.getTypeId());
					block.setType(mat);
					block.setData(data);
				}
			}
		}
	}	
	
	
	public static void replaceBlock(Location loc1, Location loc2, Material replacement, Material mat, byte data, boolean effect) {
		if (loc1.getWorld() == null || loc2.getWorld() == null) return;
		if (mat == null) return;
		if (!loc1.getWorld().equals(loc2.getWorld())) return;
		int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		World world = loc1.getWorld();
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (!block.getType().equals(replacement)) continue;
					if (effect)
						world.playEffect(new Location(world, x, y, z), Effect.STEP_SOUND, block.getTypeId());
					block.setType(mat);
					block.setData(data);
				}
			}
		}
	}
	
	public static Location randomLocationInRegion(Location loc1, Location loc2) {
		if (!loc1.getWorld().equals(loc2.getWorld())) return null;
		return new Location(loc1.getWorld(), randomInteger(loc1.getBlockX(), loc2.getBlockX()), randomInteger(loc1.getBlockY(), loc2.getBlockY()),randomInteger(loc1.getBlockZ(), loc2.getBlockZ()));
	}public static boolean randomChance(int percentage) {
		Random random = new Random();
		return (random.nextInt(100) + 1 <= percentage);
	}
	
	public static String convertCC(String s) {
		return ChatColor.translateAlternateColorCodes((char)'&', new String(s));
	}
	
	public static int randomInteger(int no1, int no2) {
		int min = 0;
		int max = 0;
		if (no1 > no2) {
			min = no2;
			max = no1;
		} else {
			min = no1;
			max = no2;
		}
		if(min >= 0 && max >= 0) {
		    Random rand = new Random();
		    int randomNum = rand.nextInt((max - min) + 1) + min;
		    return randomNum;
		} else if (min < 0 && max >= 0) {
			max = max - min;
			return (randomInteger(0, max) + min);
		} else {
			try {
			return (randomInteger(min * -1, max * -1) * -1);
			} catch (StackOverflowError error) {
				Bukkit.getLogger().info(min + " " + max);
			}
		}
		return 0;
	}
	
	public static BlockFace randomDir() {
		List<BlockFace> bfl = new ArrayList<BlockFace>();
		for(BlockFace bf : BlockFace.values()) {
			if(!(bf == BlockFace.DOWN || bf == BlockFace.UP || bf == BlockFace.SELF)) {
				bfl.add(bf);
			}
		}
		return bfl.get(randomInteger(1, 15));
    }
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
}
