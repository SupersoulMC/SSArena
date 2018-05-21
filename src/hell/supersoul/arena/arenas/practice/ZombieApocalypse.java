package hell.supersoul.arena.arenas.practice;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Dye;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLib;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.enums.ASetting;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.events.PlayerArenaDamageEvent;
import hell.supersoul.arena.events.PlayerArenaDeathEvent;
import hell.supersoul.arena.events.PlayerReachGoalEvent;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.modules.Survive;
import hell.supersoul.arena.modules.Team;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.utils.Region;
import net.minecraft.server.v1_11_R1.EnumParticle;
import net.minecraft.server.v1_11_R1.PacketPlayOutWorldParticles;

public class ZombieApocalypse extends Race implements Listener, Team, Survive {

	static ItemStack helmet = new ItemStack(Material.SKULL_ITEM);
	static ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
	static ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
	static ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
	static ItemStack pumpkin = new ItemStack(Material.PUMPKIN);
	static ItemStack skele = new ItemStack(Material.SKULL_ITEM);
	static ItemStack claw = new ItemStack(Material.DEAD_BUSH);
	static ItemStack speed = new ItemStack(Material.POTION);
	static ItemStack protect = new ItemStack(Material.NETHER_STAR);

	static {
		LeatherArmorMeta meta2 = (LeatherArmorMeta) chestplate.getItemMeta();
		meta2.setColor(Color.GREEN);
		chestplate.setItemMeta(meta2);
		LeatherArmorMeta meta3 = (LeatherArmorMeta) leggings.getItemMeta();
		meta3.setColor(Color.RED);
		leggings.setItemMeta(meta3);
		LeatherArmorMeta meta4 = (LeatherArmorMeta) boots.getItemMeta();
		meta4.setColor(Color.RED);
		boots.setItemMeta(meta4);
		helmet.setDurability((short) SkullType.ZOMBIE.ordinal());
		skele.setDurability((short) SkullType.SKELETON.ordinal());
		ItemMeta clawMeta = claw.getItemMeta();
		clawMeta.setDisplayName(ChatColor.DARK_RED + "CLAW");
		clawMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		claw.setItemMeta(clawMeta);
		claw.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		PotionMeta pMeta = (PotionMeta) speed.getItemMeta();
		pMeta.setBasePotionData(new PotionData(PotionType.SPEED));
		pMeta.setColor(Color.RED);
		pMeta.setDisplayName(ChatColor.RED + "SPEED BOOST");
		speed.setItemMeta(pMeta);
		ItemMeta starMeta = protect.getItemMeta();
		starMeta.setDisplayName(ChatColor.AQUA + "Protection Spell");
		protect.setItemMeta(starMeta);
	}

	public void startGame() {
		this.setRound(0);
		this.setTimer(20);
		if (AUtils.random(50)) {
			for (String playerName : this.getTeamPlayers().get(ChatColor.BLUE)) {
				this.getZombies().add(playerName);
			}
			for (String playerName : this.getTeamPlayers().get(ChatColor.RED)) {
				this.getHumans().add(playerName);
				this.getSurvivors().add(playerName);
			}
		} else {
			for (String playerName : this.getTeamPlayers().get(ChatColor.BLUE)) {
				this.getHumans().add(playerName);
				this.getSurvivors().add(playerName);
			}
			for (String playerName : this.getTeamPlayers().get(ChatColor.RED)) {
				this.getZombies().add(playerName);
			}
		}
		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j <= 3; j++) {
				this.getAvailablePlates().add(this.getRoundZeroPlatesLocation().get("red").clone().add(-2 * j, 0, -2 * i));
			}
		}
		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j <= 3; j++) {
				this.getAvailablePlates().add(this.getRoundZeroPlatesLocation().get("blue").clone().add(-2 * j, 0, -2 * i));
			}
		}
		new BukkitRunnable() {
			public void run() {
				String title = "¡±bHumans";
				String subTitle = "¡±6Run away from the zombies!";
				for (String playerName : ZombieApocalypse.this.getHumans()) {
					Player player = Bukkit.getPlayer(playerName);
					TitleAPI.sendFullTitle(player, 5, 100, 5, title, subTitle);
				}
				title = "¡±cZombies";
				subTitle = "¡±6Kill all humans!";
				for (String playerName : ZombieApocalypse.this.getZombies()) {
					Player player = Bukkit.getPlayer(playerName);
					TitleAPI.sendFullTitle(player, 5, 100, 5, title, subTitle);
					player.getInventory().setChestplate(chestplate);
					player.getInventory().setLeggings(leggings);
					player.getInventory().setBoots(boots);
					player.getInventory().setHelmet(pumpkin);
					new BukkitRunnable() {
						public void run() {
							player.getInventory().setHelmet(helmet);
						}
					}.runTaskLater(Main.getInstance(), 40);
				}
			}
		}.runTaskLater(Main.getInstance(), 80);
		new BukkitRunnable() {
			Location loc1 = ZombieApocalypse.this.getFallingBlockLocation().get(0);
			Location loc2 = ZombieApocalypse.this.getFallingBlockLocation().get(1);
			int minx = Math.min(loc1.getBlockX(), loc2.getBlockX());
			int maxx = Math.max(loc1.getBlockX(), loc2.getBlockX());
			int minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
			int maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
			int y = Math.min(loc1.getBlockY(), loc2.getBlockY());
			World world = ZombieApocalypse.this.getWorld();

			public void run() {
				int x = AUtils.randomNumber(minx, maxx);
				int z = AUtils.randomNumber(minz, maxz);
				int count = 0;
				Location loc = new Location(world, x, y, z);
				while (count < 4) {
					count++;
					if (count == 2)
						loc.add(0, 0, 1);
					else if (count == 3)
						loc.add(1, 0, 0);
					else if (count == 4)
						loc.add(0, 0, -1);
					Location loc2 = AUtils.descExcludeLiquid(loc);
					Location loc3 = loc2.clone().add(0.5, 1, 0.5);
					Block block = loc2.getBlock();
					if (!ZombieApocalypse.this.getClipboard().containsKey(loc2))
						ZombieApocalypse.this.getClipboard().put(loc2, block);
					for (String playerName : ZombieApocalypse.this.getPlayers()) {
						Bukkit.getPlayer(playerName).sendBlockChange(loc2, Material.WOOL, DyeColor.RED.getWoolData());
					}
					FallingBlock fb = loc1.getWorld().spawnFallingBlock(loc, Material.SLIME_BLOCK, (byte) 0);
					fb.setDropItem(false);
				}
				if (ZombieApocalypse.this.getStatus().equals(AStatus.over))
					cancel();
			}
		}.runTaskTimer(Main.getInstance(), 500, 3);
	}

	public ZombieApocalypse(String id) {
		super(id);
	}

	@Override
	public void iniArena(String mapName, World world) {
		super.iniArena(mapName, world);
		this.getRoundZeroDoorsLocation().put("red", new ArrayList<Location>());
		this.getRoundZeroDoorsLocation().put("blue", new ArrayList<Location>());
		this.getRoundZeroDoorsLocation().get("red").add(new Location(world, 11, 4, 40));
		this.getRoundZeroDoorsLocation().get("red").add(new Location(world, 5, 12, 40));
		this.getRoundZeroDoorsLocation().get("blue").add(new Location(world, -3, 4, 40));
		this.getRoundZeroDoorsLocation().get("blue").add(new Location(world, -9, 12, 40));
		this.getDoorLocation().put(1, new ArrayList<Location>());
		this.getDoorLocation().put(2, new ArrayList<Location>());
		this.getDoorLocation().put(3, new ArrayList<Location>());
		this.getDoorLocation().put(4, new ArrayList<Location>());
		this.getDoorLocation().get(1).add(new Location(world, 7, 4, 166));
		this.getDoorLocation().get(1).add(new Location(world, -5, 12, 166));
		this.getDoorLocation().get(2).add(new Location(world, 7, 4, 328));
		this.getDoorLocation().get(2).add(new Location(world, -5, 12, 328));
		this.getDoorLocation().get(3).add(new Location(world, 7, 4, 523));
		this.getDoorLocation().get(3).add(new Location(world, -5, 12, 523));
		this.getRoundZeroPlatesLocation().put("red", new Location(world, 11, 5, 36));
		this.getRoundZeroPlatesLocation().put("blue", new Location(world, -3, 5, 36));
		this.getPlateLocation().put(1, new Location(world, 4, 5, 160));
		this.getPlateLocation().put(2, new Location(world, 4, 5, 321));
		this.getPlateLocation().put(3, new Location(world, 4, 5, 519));
		this.getTpLocation().put(1, new Location(world, 0, 4, 162));
		this.getTpLocation().put(2, new Location(world, 0, 4, 323));
		this.getTpLocation().put(3, new Location(world, 1, 4, 521));
		this.getFallingBlockLocation().add(new Location(world, 9, 50, 755));
		this.getFallingBlockLocation().add(new Location(world, -7, 50, 854));
		this.getIronBarLocation().add(new Location(world, 5, 1, 550));
		this.getIronBarLocation().add(new Location(world, 4, 2, 550));
		this.setBgmName("zombieapocalypse");
		this.setStartPlatform(new Region(new Location(world, -6, 45, -11), new Location(world, 8, 45, -5)));
		this.getTeamRespawnPoints().put(ChatColor.RED, new ArrayList<>());
		this.getTeamRespawnPoints().put(ChatColor.BLUE, new ArrayList<>());
		this.getTeamRespawnPoints().get(ChatColor.RED).add(new Location(world, 5, 46, -8));
		this.getTeamRespawnPoints().get(ChatColor.BLUE).add(new Location(world, -3, 46, -8));
		this.setGoal(new Region(new Location(world, -4, 5, 880), new Location(world, 6, 5, 870)));
		this.getPlaceLocations().add(new Location(world, 1, 5, 891, -180, 0));
		this.getPlaceLocations().add(new Location(world, 1, 5, 885));
		this.getPlaceLocations().add(new Location(world, 1, 5, 885));
		this.getPlaceLocations().add(new Location(world, 1, 5, 885));
		this.getDeathDamage().add(DamageType.VOID);
		this.getSettings().add(ASetting.useDiffRespawnForTeam);
		this.setDisplayName(ChatColor.RED + "Zombie Apocalypse");
		this.setRound(0);
		this.setDeathLevel(-1);
		this.getDisabledDamage().add(DamageType.FIRE);
		this.getDisabledDamage().add(DamageType.FIRE_TICK);
		this.getDisabledDamage().add(DamageType.LAVA);
		this.getDisabledDamage().add(DamageType.FALL);
	}

	public void nextRound() {
		if (nextRounding) return;
		nextRounding = true;
		ZombieApocalypse arena = this;
		int round = arena.getRound();
		this.getAvailablePlates().clear();
		round++;
		arena.setRound(round);
		arena.setTimer(-1);
		World world = this.getWorld();
		int delay = 0;
		if (round == 1)
			delay = 80;
		else if (round == 2)
			delay = 60;
		else if (round == 3)
			delay = 40;
		else if (round == 4)
			delay = 35;
		if (round != 1) {
			for (String playerName : arena.getZombies()) {
				if (!this.getDeadPlayers().contains(playerName)) {
					Player player = Bukkit.getPlayer(playerName);
					AUtils.freezePlayer(player);
					player.getInventory().setHelmet(skele);
					Location loc = arena.getTpLocation().get(round - 1).clone();
					double random = AUtils.randomNumberDouble(0, 3);
					if (AUtils.random(50)) {
						random = random * (-1);
					}
					loc.setX(loc.getX() + random);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 2, 1, false, false));
					player.teleport(loc);
				}
			}
		}
		if (round < 4)
			for (int i = 0; i <= 1; i++) {
				for (int j = 0; j <= 3; j++) {
					this.getAvailablePlates().add(this.getPlateLocation().get(round).clone().add(-2 * j, 0, -2 * i));
				}
			}
		for (Location loc : this.getAvailablePlates()) {
			Bukkit.getLogger().info(loc + "");
		}
		String title;
		if (round <= 3)
			title = "¡±7Round ¡±b" + round;
		else
			title = "¡±4Last ¡±7Round";
		if (round == 4) {
			new BukkitRunnable() {
				public void run() {
					Location loc1 = arena.getIronBarLocation().get(0);
					Location loc2 = arena.getIronBarLocation().get(1);
					int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
					int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
					int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
					int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
					int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
					int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							for (int z = minZ; z <= maxZ; z++) {
								Block block = world.getBlockAt(x, y, z);
								block.setType(Material.AIR);
								world.playEffect(new Location(world, x, y, z), Effect.STEP_SOUND, Material.IRON_FENCE);
							}
						}
					}
				}
			}.runTaskLater(Main.getInstance(), 220);
		}
		new BukkitRunnable() {
			int temp = arena.getRound();

			public void run() {
				for (String playerName : ZombieApocalypse.this.getPlayers()) {
					Player player = Bukkit.getPlayer(playerName);
					TitleAPI.sendTitle(player, 10, 100, 10, title);
				}
				if (temp > 1) {
					temp--;
					Location loc1 = arena.getDoorLocation().get(temp).get(0);
					Location loc2 = arena.getDoorLocation().get(temp).get(1);
					int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
					int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
					int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
					int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
					int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
					int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							for (int z = minZ; z <= maxZ; z++) {
								Block block = world.getBlockAt(x, y, z);
								block.setType(Material.AIR);
								Location loc = block.getLocation();
								world.playEffect(loc, Effect.STEP_SOUND, Material.OBSIDIAN);
							}
						}
					}
				} else {
					Location loc1 = arena.getRoundZeroDoorsLocation().get("blue").get(0);
					Location loc2 = arena.getRoundZeroDoorsLocation().get("blue").get(1);
					int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
					int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
					int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
					int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
					int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
					int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							for (int z = minZ; z <= maxZ; z++) {
								Block block = world.getBlockAt(x, y, z);
								block.setType(Material.AIR);
								Location loc = block.getLocation();
								world.playEffect(loc, Effect.STEP_SOUND, Material.LAPIS_BLOCK);
							}
						}
					}
					loc1 = arena.getRoundZeroDoorsLocation().get("red").get(0);
					loc2 = arena.getRoundZeroDoorsLocation().get("red").get(1);
					minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
					minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
					minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
					maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
					maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
					maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
					for (int x = minX; x <= maxX; x++) {
						for (int y = minY; y <= maxY; y++) {
							for (int z = minZ; z <= maxZ; z++) {
								Block block = world.getBlockAt(x, y, z);
								block.setType(Material.AIR);
								Location loc = block.getLocation();
								world.playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
							}
						}
					}
					for (String playerName : arena.getZombies()) {
						ZombieApocalypse.this.disallowDJ(playerName);
						Bukkit.getPlayer(playerName).getInventory().setItem(1, claw.clone());
						Bukkit.getPlayer(playerName).getInventory().setItem(2, speed.clone());
					}
					for (String playerName : arena.getHumans()) {
						Bukkit.getPlayer(playerName).getInventory().setItem(1, protect.clone());
					}
				}
			}
		}.runTaskLater(Main.getInstance(), 60);
		new BukkitRunnable() {
			int counter = 3;

			public void run() {
				if (!arena.getStatus().equals(AStatus.over)) {
					if (counter > 0) {
						String title = "¡±b" + counter;
						for (String playerName : arena.getHumans()) {
							Player player = Bukkit.getPlayer(playerName);
							TitleAPI.sendTitle(player, 0, 20, 0, title);
						}
					} else {
						String title = "¡±bRUN!!!";
						for (String playerName : arena.getSurvivors()) {
							Player player = Bukkit.getPlayer(playerName);
							AUtils.unfreezePlayer(player, true);
							ZombieApocalypse.this.allowDJ(playerName);
							TitleAPI.sendTitle(player, 0, 40, 3, title);
							arena.setTimer(30 + 10* ZombieApocalypse.this.getRound());
							cancel();
						}
					}
					counter--;
				}
			}
		}.runTaskTimer(Main.getInstance(), 100, 13);
		final int r = round;
		new BukkitRunnable() {
			int counter = 3;
			public void run() {
				if (!arena.getStatus().equals(AStatus.over)) {
					if (counter > 0) {
						String title = "¡±c" + counter;
						for (String playerName : arena.getZombies()) {
							Player player = Bukkit.getPlayer(playerName);
							TitleAPI.sendTitle(player, 0, 20, 0, title);
						}
					} else {
						String title = "¡±4KILL THEM ALL";
						for (String playerName : arena.getZombies()) {
							if (!ZombieApocalypse.this.getDeadPlayers().contains(playerName)) {
								Player player = Bukkit.getPlayer(playerName);
								AUtils.unfreezePlayer(player, true);
								player.getInventory().setHelmet(helmet);
								TitleAPI.sendTitle(player, 0, 40, 3, title);
								if (r == 4)
									ZombieApocalypse.this.allowDJ(playerName);
							}
						}
						nextRounding = false;
						cancel();
						arena.getPressedPlayers().clear();
					}
					counter--;
				}
			}
		}.runTaskTimer(Main.getInstance(), 100 + delay, 13);
	}

	public void cooldown(Player player, ItemStack is) {
		Bukkit.getLogger().info("1");
		ItemStack item = is.clone();
		int counter = 0;
		Material mat = Material.SULPHUR;
		if (is.equals(protect)) {
			counter = 16;
			mat = Material.FLINT;
		} else if (is.equals(claw)) {
			counter = 1;
			mat = Material.SULPHUR;
		} else if (is.getType().equals(Material.POTION)) {
			counter = 5;
			mat = Material.STRING;
		} else
			return;
		Bukkit.getLogger().info("2");
		int slot = 2;
		if (is.equals(claw))
			slot = 1;
		else if (is.equals(protect))
			slot = 1;
		ItemStack cd = new ItemStack(mat);
		ItemMeta cdMeta = cd.getItemMeta();
		cdMeta.setDisplayName(ChatColor.GRAY + ChatColor.stripColor(is.getItemMeta().getDisplayName()));
		cd.setItemMeta(cdMeta);
		cd.setAmount(counter);
		player.getInventory().setItem(slot, cd);
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_COOLDOWN);
		packet.getIntegers().write(0, 20 * (counter - 1));
		packet.getModifier().write(0, CraftItemStack.asNMSCopy(cd).getItem());
		try {
			ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		//WTF IS THIS
		final int co = counter;
		final int s = slot;
		new BukkitRunnable() {
			int c = co;

			public void run() {
				c--;
				if (c <= 0 || ZombieApocalypse.this.getDeadPlayers().contains(player.getName()) || !ZombieApocalypse.this.getPlayers().contains(player.getName())) {
					player.getInventory().setItem(s, item);
					this.cancel();
				} else {
					cd.setAmount(c);
					player.getInventory().setItem(s, cd);
				}
			}
		}.runTaskTimer(Main.getInstance(), 0, 20);
	}

	int round;
	ArrayList<String> zombies = new ArrayList<String>();
	ArrayList<String> humans = new ArrayList<String>();
	HashMap<String, ArrayList<Location>> roundZeroDoorsLocation = new HashMap<String, ArrayList<Location>>();
	HashMap<String, Location> roundZeroPlatesLocation = new HashMap<>();
	HashMap<Integer, Location> plateLocation = new HashMap<>();
	HashMap<Integer, ArrayList<Location>> doorLocation = new HashMap<Integer, ArrayList<Location>>();
	ArrayList<Location> ironBarLocation = new ArrayList<Location>();
	ArrayList<Location> fallingBlockLocation = new ArrayList<Location>();
	ArrayList<Location> pressedPlates = new ArrayList<Location>();
	ArrayList<String> survivors = new ArrayList<String>();
	ArrayList<String> pressedPlayers = new ArrayList<String>();
	HashMap<Integer, Location> tpLocation = new HashMap<Integer, Location>();
	ArrayList<Location> availablePlates = new ArrayList<>();
	HashMap<Location, Block> clipboard = new HashMap<>();
	boolean nextRounding = false;

	public HashMap<Integer, Location> getTpLocation() {
		return tpLocation;
	}

	public void setTpLocation(HashMap<Integer, Location> tpLocation) {
		this.tpLocation = tpLocation;
	}

	public ArrayList<String> getPressedPlayers() {
		return pressedPlayers;
	}

	public void setPressedPlayers(ArrayList<String> pressedPlayers) {
		this.pressedPlayers = pressedPlayers;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public ArrayList<String> getZombies() {
		return zombies;
	}

	public void setZombies(ArrayList<String> zombies) {
		this.zombies = zombies;
	}

	public ArrayList<String> getHumans() {
		return humans;
	}

	public void setHumans(ArrayList<String> humans) {
		this.humans = humans;
	}

	public HashMap<Integer, Location> getPlateLocation() {
		return plateLocation;
	}

	public HashMap<Integer, ArrayList<Location>> getDoorLocation() {
		return doorLocation;
	}

	public void setDoorLocation(HashMap<Integer, ArrayList<Location>> doorLocation) {
		this.doorLocation = doorLocation;
	}

	public ArrayList<Location> getFallingBlockLocation() {
		return fallingBlockLocation;
	}

	public void setFallingBlockLocation(ArrayList<Location> fallingBlockLocation) {
		this.fallingBlockLocation = fallingBlockLocation;
	}

	public ArrayList<Location> getPressedPlates() {
		return pressedPlates;
	}

	public void setPressedPlates(ArrayList<Location> pressedPlates) {
		this.pressedPlates = pressedPlates;
	}

	public ArrayList<String> getSurvivors() {
		return survivors;
	}

	public void setSurvivors(ArrayList<String> survivors) {
		this.survivors = survivors;
	}

	public HashMap<String, ArrayList<Location>> getRoundZeroDoorsLocation() {
		return roundZeroDoorsLocation;
	}

	public void setRoundZeroDoorsLocation(HashMap<String, ArrayList<Location>> roundZeroDoorsLocation) {
		this.roundZeroDoorsLocation = roundZeroDoorsLocation;
	}

	public HashMap<String, Location> getRoundZeroPlatesLocation() {
		return roundZeroPlatesLocation;
	}

	public ArrayList<Location> getIronBarLocation() {
		return ironBarLocation;
	}

	public void setIronBarLocation(ArrayList<Location> ironBarLocation) {
		this.ironBarLocation = ironBarLocation;
	}

	public ArrayList<Location> getAvailablePlates() {
		return availablePlates;
	}

	public HashMap<Location, Block> getClipboard() {
		return clipboard;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction().equals(Action.PHYSICAL)) {
			Block block = event.getClickedBlock();
			if (block.getType().equals(Material.STONE_PLATE)) {
				String playerName = player.getName();
				if (this.getDeadPlayers().contains(playerName)) {
					event.setCancelled(true);
					return;
				}
				Location loc = block.getLocation();
				int round = this.getRound();
				if (round == 0) {
					if (!this.getPressedPlayers().contains(playerName) && !this.getPressedPlates().contains(loc)) {
						this.getPressedPlayers().add(playerName);
						this.getPressedPlates().add(loc);
						AUtils.freezePlayer(player);
						Location tpLocation = loc.clone();
						tpLocation.setX(tpLocation.getX() + 0.5d);
						tpLocation.setY(tpLocation.getY() + 0.01d);
						tpLocation.setZ(tpLocation.getZ() + 0.5d);
						player.teleport(tpLocation);
						this.disallowDJ(playerName);
						PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL, false, tpLocation.getBlockX(), tpLocation.getBlockY(), tpLocation.getBlockZ(), 1, 2, 1, 0, 30, null);
						for (Player p : Bukkit.getServer().getOnlinePlayers())
							((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
						player.setVelocity(new Vector(0, 0, 0));
						if (this.getPlayers().size() <= this.getPressedPlayers().size()) {
							this.nextRound();
						}
					}
				} else {
					if (this.getSurvivors().contains(playerName)) {
						if (!this.getPressedPlayers().contains(playerName) && !this.getPressedPlates().contains(loc)) {
							if (this.getAvailablePlates().contains(loc)) {
								this.getPressedPlayers().add(playerName);
								this.getPressedPlates().add(loc);
								AUtils.freezePlayer(player);
								Location tpLocation = loc.clone();
								tpLocation.setX(tpLocation.getX() + 0.5d);
								tpLocation.setY(tpLocation.getY() + 0.01d);
								tpLocation.setZ(tpLocation.getZ() + 0.5d);
								player.teleport(tpLocation);
								this.disallowDJ(playerName);
								PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.SPELL, false, tpLocation.getBlockX(), tpLocation.getBlockY(), tpLocation.getBlockZ(), 1, 2, 1, 0, 30, null);
								for (Player p : Bukkit.getServer().getOnlinePlayers())
									((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
							}
							player.setVelocity(new Vector(0, 0, 0));
							int temp = 0;
							if (this.getSurvivors().size() <= this.getPressedPlayers().size()) {
								this.nextRound();
							}
						}

					}
				}
			}
		} else {
			String playerName = player.getName();
			if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if (!this.getDeadPlayers().contains(playerName)) {
					if (this.getPlayerStatus().get(playerName).contains(PStatus.FREEZED))
						return;
					ItemStack hand = player.getInventory().getItemInMainHand();
					if (hand.equals(claw) && (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
						this.cooldown(player, hand);
					} else if (!hand.getType().equals(Material.INK_SACK) && !hand.getType().equals(Material.AIR)) {
						this.cooldown(player, hand);
						if (hand.getType().equals(Material.POTION))
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 3, false, false));
						else if (hand.equals(protect)) {
							this.getPlayerStatus().get(playerName).add(PStatus.INVINCIBLE);
							new BukkitRunnable() {
								int counter = 80 / 2;

								public void run() {
									double radius = 1, angle = 90, red = Float.MIN_VALUE, green = 127 / 255, blue = 255 / 255;
									Location loc = player.getLocation();
									double yaw = 360 - Math.abs(loc.getYaw());
									Particle particle = Particle.REDSTONE;
									// The ring
									for (double i = 0; i < 360; i += 5) {
										// Removing part of the ring in front of the player.
										if (yaw - angle / 2 >= 0 && yaw + angle / 2 < 360) {
											if (i >= yaw - angle / 2 && i <= yaw + angle / 2)
												continue;
										} else if (yaw - angle / 2 < 0) {
											if (i >= yaw - angle / 2 + 360 || i <= yaw + angle / 2)
												continue;
										} else if (yaw + angle / 2 >= 360) {
											if (i >= yaw - angle / 2 || i <= yaw + angle / 2 - 360)
												continue;
										}
										double z = loc.getZ() + Math.cos(Math.toRadians(i)) * radius;
										double x = loc.getX() + Math.sin(Math.toRadians(i)) * radius;
										// Spawning the ring.
										for (double y = loc.getY() + 0.7; y <= loc.getY() + 1.7; y += 0.5)
											ZombieApocalypse.this.getWorld().spawnParticle(particle, x, y, z, 0, red, green, blue, 1);
									}
									// The line at the back
									for (double y = loc.getY() + 0.7; y <= loc.getY() + 1.7; y += 0.1) {
										double back = yaw < 180 ? yaw + 180 : yaw - 180;
										double z = loc.getZ() + Math.cos(Math.toRadians(back)) * radius;
										double x = loc.getX() + Math.sin(Math.toRadians(back)) * radius;
										ZombieApocalypse.this.getWorld().spawnParticle(particle, x, y, z, 0, red, green, blue, 1);
									}
									counter--;
									if (counter < 0)
										this.cancel();
								}
							}.runTaskTimer(Main.getInstance(), 0, 2);
							new BukkitRunnable() {
								public void run() {
									ZombieApocalypse.this.getPlayerStatus().get(playerName).remove(PStatus.INVINCIBLE);
								}
							}.runTaskLater(Main.getInstance(), 80);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (event.getDamager() instanceof Player) {
			String playerName = player.getName();
			if (event.getEntity() instanceof Player) {
				event.setCancelled(true);
				Player attacker = (Player) event.getDamager();
				if (!this.getDeadPlayers().contains(attacker.getName())) {
					if (!this.getPressedPlayers().contains(playerName)) {
						if (this.getSurvivors().contains(playerName) && this.getZombies().contains(attacker.getName())) {
							if (!this.getDeadPlayers().contains(playerName) && !this.getDeadPlayers().contains(attacker.getName())) {
								if (attacker.getInventory().getItemInMainHand().getType().equals(Material.DEAD_BUSH))
									if (!this.getPlayerStatus().get(player.getName()).contains(PStatus.INVINCIBLE))
										this.playerDeath(player, DamageType.PLAYER, attacker.getName());
							}
						}
					}
				}
			}
		}

	}

	@EventHandler
	public void onArenaStart(ArenaStartEvent event) {
		this.startGame();
	}

	@EventHandler
	public void onPlayerArenaDeath(PlayerArenaDeathEvent event) {
		if (!(event.getArena() == this))
			return;
		if (event.getDamageType().equals(DamageType.ZAHUMANGOAL))
			return;
		if (this.getHumans().contains(event.getPlayer().getName()))
			this.getSurvivors().remove(event.getPlayer().getName());
		if (this.getPressedPlayers().contains(event.getPlayer().getName()))
			this.getPressedPlayers().remove(event.getPlayer().getName());
		if (this.getRound() == 0 && this.getPressedPlayers().size() <= 0)
			return;
		if (this.getSurvivors().size() <= 0)
			for (String playerName : this.getZombies())
				this.alive(Bukkit.getPlayer(playerName));
		else {
			int size = this.getZombies().size();
			for (String playerName : this.getZombies()) {
				if (this.getDeadPlayers().contains(playerName))
					size--;
			}
			if (size <= 0)
				for (String playerName : this.getHumans())
					this.alive(Bukkit.getPlayer(playerName));
		}
	}

	@EventHandler
	public void onPlayerReachGoal(PlayerReachGoalEvent event) {
		if (!(event.getArena() == this))
			return;
		if (this.getZombies().contains(event.getPlayer().getName()))
			event.setCancelled(true);
		else {
			if (this.getRanks().size() <= 0)
				for (String playerName : this.getZombies())
					this.playerDeath(Bukkit.getPlayer(playerName), DamageType.ZAHUMANGOAL);
		}
	}

	@EventHandler
	public void onPlayerArenaDamage(PlayerArenaDamageEvent event) {
		if (!(event.getArena() == this))
			return;
		Player player = event.getPlayer();
		if (event.getDamageType().equals(DamageType.LAVA)) {
			if (this.getHumans().contains(player.getName())) {
				this.playerDeath(player, event.getDamageType());
			}
		} else if (event.getDamageType().equals(DamageType.TIME)) {
			if (this.getPressedPlayers().contains(player.getName()))
				return;
			if (this.getRound() == 0)
				this.playerDeath(player, event.getDamageType());
			else if (this.getHumans().contains(player.getName())) {
				this.playerDeath(player, event.getDamageType());
			}
			if (this.getPressedPlayers().size() > 0)
				this.nextRound();
		}
	}

	@EventHandler
	public void onBlockFall(EntityChangeBlockEvent event) {
		if (!event.getEntity().getWorld().equals(this.getWorld()))
			return;
		if ((event.getEntityType().equals(EntityType.FALLING_BLOCK))) {
			event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, Material.EMERALD_BLOCK);
			event.setCancelled(true);
			Block block = AUtils.descExcludeLiquid(event.getBlock().getLocation()).getBlock();
			for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 2, 2, 2)) {
				if (entity instanceof Player) {
					entity.setVelocity(entity.getLocation().getDirection().normalize().multiply(-1.5).setY(0.5));
				}
			}
			if (this.getClipboard().containsKey(block.getLocation())) {
				for (String playerName : this.getPlayers()) {
					Block b = this.getClipboard().get(block.getLocation());
					Bukkit.getPlayer(playerName).sendBlockChange(b.getLocation(), b.getTypeId(), b.getData());
				}
				this.getClipboard().remove(block.getLocation());
			}
		}
	}
}
