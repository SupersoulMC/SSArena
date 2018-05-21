package hell.supersoul.arena.arenas.dungeon;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.modules.DArena;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.Main;
import hell.supersoul.managers.NPCManager;
import hell.supersoul.npc.Conv;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class IceRealm extends DArena implements Listener {

	String elubp = ChatColor.AQUA + "" + ChatColor.BOLD + "Elub " + ChatColor.GRAY + "> " + ChatColor.AQUA;
	String serop = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Sero " + ChatColor.GRAY + "> "
			+ ChatColor.LIGHT_PURPLE;
	String elsap = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + " - ELSA - " + ChatColor.DARK_RED + "> "
			+ ChatColor.DARK_PURPLE;
	String elsagdp = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Elsa" + ChatColor.GRAY + "> "
			+ ChatColor.DARK_PURPLE;
	Location zone1 = new Location(Bukkit.getWorld("battle"), -5.5, 105, 264.5);
	Location z1s1 = new Location(Bukkit.getWorld("battle"), -16.5, 116, 276.5);
	Location z1s2 = new Location(Bukkit.getWorld("battle"), 10.5, 112, 279.5);
	Location z1c1 = new Location(Bukkit.getWorld("battle"), 2, 124, 290);
	Location z1c2 = new Location(Bukkit.getWorld("battle"), -11, 114, 299);
	Location z1b1 = new Location(Bukkit.getWorld("battle"), -5, 121, 299);
	Location z1b2 = new Location(Bukkit.getWorld("battle"), -7, 117, 299);
	Location z1l1 = new Location(Bukkit.getWorld("battle"), -5, 115, 291);
	Location z1l2 = new Location(Bukkit.getWorld("battle"), -5, 107, 291);
	Location z2b1 = new Location(Bukkit.getWorld("battle"), -5, 128, 329);
	Location z2b2 = new Location(Bukkit.getWorld("battle"), -7, 125, 329);
	Location z2s1 = new Location(Bukkit.getWorld("battle"), -9.5, 125, 326.5);
	Location z2s2 = new Location(Bukkit.getWorld("battle"), -1.5, 125, 326.5);
	Location z3s1 = new Location(Bukkit.getWorld("battle"), -17.5, 131, 388.5);
	Location z3s2 = new Location(Bukkit.getWorld("battle"), -0.5, 134, 343.5);
	Location z3s3 = new Location(Bukkit.getWorld("battle"), -17.5, 138, 338.5);
	Location z3s4 = new Location(Bukkit.getWorld("battle"), -5.5, 145, 332.5);
	Location z3b1 = new Location(Bukkit.getWorld("battle"), -5, 139, 331);
	Location z3b2 = new Location(Bukkit.getWorld("battle"), -7, 142, 331);
	Location z3l1 = new Location(Bukkit.getWorld("battle"), -6, 143, 332);
	Location z4s1 = new Location(Bukkit.getWorld("battle"), -5.5, 138, 311.5);
	Location z4s2 = new Location(Bukkit.getWorld("battle"), -10.5, 138, 315.5);
	Location z4s3 = new Location(Bukkit.getWorld("battle"), -5.5, 138, 320.5);
	Location z4s4 = new Location(Bukkit.getWorld("battle"), -0.5, 138, 315.5);
	Location z4b1 = new Location(Bukkit.getWorld("battle"), 6, 137, 327);
	Location z4b2 = new Location(Bukkit.getWorld("battle"), -18, 137, 303);
	Location z4s5 = new Location(Bukkit.getWorld("battle"), -7, 149, 314);
	Location z4s6 = new Location(Bukkit.getWorld("battle"), -5.5, 145.1, 307.5);
	int totalDamageLeft = 50;

	public IceRealm(String id) {
		super(id);
		startloc = new Location(Bukkit.getWorld("battle"), -2, 102, 241.5);
	}

	@Override
	public void startArena() {
		z3l1.getBlock().setData((byte) 3);
		AUtils.setBlock(z1b1, z1b2, Material.BARRIER, (byte) 0, false);
		AUtils.setBlock(z1l1, z1l2, Material.AIR, (byte) 0, false);
		AUtils.replaceBlock(z4b1, z4b2, Material.AIR, Material.ICE, (byte) 0, false);
		ArrayList<String> toRemove = new ArrayList<>();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm mobs kill Ice");
		sendMessage("The mission has " + ChatColor.GREEN + "BEGUN" + ChatColor.GRAY + "!");
		sendTitle(3, 60, 3, ChatColor.GREEN + "Mission Starts!", ChatColor.GRAY + "Defeat the Ice Queen.");
		for (String playerName : this.getPlayers()) {
			if (Bukkit.getPlayer(playerName) == null || !Bukkit.getPlayer(playerName).isOnline()) {
				toRemove.add(playerName);
				continue;
			}
			survivors.add(playerName);
			NPCManager.getManager().activateConv(Bukkit.getPlayer(playerName), Conv.getConv("Battle Intro"),
					ChatColor.DARK_PURPLE.toString(), 0);
			Player player = Bukkit.getPlayer(playerName);
			player.setWalkSpeed(0.2f);
			player.setFoodLevel(20);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
			player.teleport(zone1);
			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
		}
		for (String playerName : toRemove) {
			this.getPlayers().remove(playerName);
		}
		this.updateScoreboard();
		nextZone();
	}

	@Override
	public void announceResult() {
		this.endArena();

	}

	@Override
	public void playerRespawn(Player player, DamageType dt, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerDeath(Player player, DamageType dt, Object... args) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerReconnected(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerDisconnected(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public Scoreboard updateScoreboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void nextZone() {
		currentZone++;
		if (currentZone == 1) {
			this.timer += 90;
			sendTitle(3, 34, 3, ChatColor.AQUA + "Zone I", ChatColor.GRAY + "Climb up to the Ice Castle!");
			new BukkitRunnable() {
				@Override
				public void run() {
					sendTitle(3, 34, 3, ChatColor.GREEN + "+90 Seconds", ChatColor.GRAY + "");
				}
			}.runTaskLater(Main.getInstance(), 40);
			for (int i = 0; i < 7; i++) {
				try {
					MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion1", z1s2);
					MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion1", z1s1);
				} catch (InvalidMobTypeException e) {
					e.printStackTrace();
				}
			}
			this.sendDelayedMessage(
					elubp + "We should get up to the Ice Castle first, but how should we do that without the bridge?",
					40);
			this.sendDelayedMessage(elubp + "Hey how 'bout we use these ice pillars over there?", 100);
			this.sendDelayedMessage(elubp + "But the Anchers are getting in our way!", 160);
			this.sendDelayedMessage(serop + "We should take them down.", 220);
			this.sendDelayedMessage(elubp + "Good idea!", 280);
		} else if (currentZone == 2) {
			sendTitle(3, 34, 3, ChatColor.AQUA + "Zone II", ChatColor.GRAY + "Gather energy by damaging Heartless!");
			new BukkitRunnable() {
				@Override
				public void run() {
					sendTitle(3, 34, 3, ChatColor.GREEN + "+120 Seconds", ChatColor.GRAY + "");
				}
			}.runTaskLater(Main.getInstance(), 40);
			AUtils.setBlock(z1b1, z1b2, Material.AIR, (byte) 0, false);
			AUtils.setBlock(z1l1, z1l2, Material.LADDER, (byte) 0, true);
			AUtils.setBlock(z2b1, z2b2, Material.BEDROCK, (byte) 0, true);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion2", z2s2);
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion4", z2s1);
					} catch (InvalidMobTypeException e) {
						e.printStackTrace();
					}
					if (currentZone != 2 || !IceRealm.this.getStatus().equals(AStatus.inGame))
						this.cancel();
				}
			}.runTaskTimer(Main.getInstance(), 100, 300);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion3", z2s2);
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion5", z2s1);
					} catch (InvalidMobTypeException e) {
						e.printStackTrace();
					}
					if (currentZone != 2 || !IceRealm.this.getStatus().equals(AStatus.inGame))
						this.cancel();
				}
			}.runTaskTimer(Main.getInstance(), 100, 150);
			this.sendDelayedMessage(elubp + "ArrggGGGHH why're there BEDROCKS in our WAY!!!", 60);
			this.sendDelayedMessage(serop + "Calm down, we can easily break them with sufficient energy.", 120);
			this.sendDelayedMessage(serop + "Warriors, we need to collect enough energy by damaging the enemies.", 180);
			this.sendDelayedMessage(serop + "Then we'll be able to break through the bedrock wall.", 240);
		} else if (currentZone == 3) {
			sendTitle(3, 34, 3, ChatColor.AQUA + "Zone III",
					ChatColor.GRAY + "Pull the lever to turn the spawners off!");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mm mobs kill Ice");
			new BukkitRunnable() {
				@Override
				public void run() {
					sendTitle(3, 34, 3, ChatColor.GREEN + "+60 Seconds", ChatColor.GRAY + "");
				}
			}.runTaskLater(Main.getInstance(), 40);
			AUtils.setBlock(z3b1, z3b2, Material.BARRIER, (byte) 0, false);
			AUtils.setBlock(z2b1, z2b2, Material.AIR, (byte) 0, true);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion6", z3s1);
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion6", z3s2);
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion6", z3s3);
						MythicMobs.inst().getAPIHelper().spawnMythicMob("IceMinion6", z3s4);
					} catch (InvalidMobTypeException e) {
						e.printStackTrace();
					}
					if (currentZone != 3)
						this.cancel();
				}
			}.runTaskTimer(Main.getInstance(), 0, 200);
			this.sendDelayedMessage(serop + "!!!", 60);
			this.sendDelayedMessage(serop + "Look at these monsters! How cuuute!", 80);
			this.sendDelayedMessage(elubp + "Careful! They're still aggressive!", 140);
			this.sendDelayedMessage(elubp + "Looks like we can turn the spawners off by switching the lever on top.",
					220);
			this.sendDelayedMessage(elubp + "Warriors, please do that before the monsters are everywhere!!!", 280);
		} else if (currentZone == 4) {
			sendTitle(3, 34, 3, ChatColor.RED + "Boss Zone", ChatColor.GRAY + "Defeat the ice queen.");
			new BukkitRunnable() {
				@Override
				public void run() {
					sendTitle(3, 34, 3, ChatColor.GREEN + "+500 Seconds", ChatColor.GRAY + "");
				}
			}.runTaskLater(Main.getInstance(), 40);
			AUtils.setBlock(z3b1, z3b2, Material.AIR, (byte) 0, false);
			Entity ent = null;
			try {
				MythicMobs.inst().getAPIHelper().spawnMythicMob("IceWitch", z4s1);
			} catch (InvalidMobTypeException e) {
				e.printStackTrace();
			}
			this.sendDelayedMessage(elsap + "hahhah HAHAHAHAHA, well hello!", 20);
			this.sendDelayedMessage(elsap + "Welcome to my ICE CASTLE!", 80);
			this.sendDelayedMessage(elubp + "ELSA?!!", 140);
			this.sendDelayedMessage(elubp + "ELSA?!!", 140);
			this.sendDelayedMessage(elsap + "Isn't it beautful? Or rather amazing you should say?", 200);
			this.sendDelayedMessage(elsap + "This castle is built to absord the darkness in this world...", 260);
			this.sendDelayedMessage(elsap + "So I can return to be all POWERFUL again!!!", 320);
			this.sendDelayedMessage(elsap + "HahahahAHAHAHAHAHAHAHAHA", 380);
			this.sendDelayedMessage(elsap + "I shall absord your power as well.", 420);
			this.sendDelayedMessage(elsap + "BECOME ONE OF MINE!!!!", 480);
			this.sendDelayedMessage(elubp + "You will not succeed!!!", 520);
			this.sendDelayedMessage(serop + "You will not succeed!!!", 520);
		}
	}

	@Override
	public void endZone() {
		if (currentZone == 1) {
			this.timer += 120;
			nextZone();
			sendTitle(3, 34, 3, ChatColor.GOLD + "Zone I Completed", "");
			sendMessage(elubp + "Warriors, climb up using the ladders now!");
		} else if (currentZone == 2) {
			this.timer += 60;
			nextZone();
			sendTitle(3, 34, 3, ChatColor.GOLD + "Zone II Completed", "");
		} else if (currentZone == 3) {
			this.timer += 500;
			nextZone();
			sendTitle(3, 34, 3, ChatColor.GOLD + "Zone III Completed", "");
		} else if (currentZone == 4) {
			sendTitle(3, 34, 3, ChatColor.GOLD + "Boss Zone Completed", "");
			sendMessage(elsagdp + "Than... Thank you for saving me from the darkness...");
			new BukkitRunnable() {
				@Override
				public void run() {
					IceRealm.this.announceResult();
				}
			}.runTaskLater(Main.getInstance(), 40);
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			return;
		if (!(event.getDamager() instanceof Player) && !(event.getDamager() instanceof Projectile))
			return;
		if (this.currentZone != 2)
			return;
		if (!this.getStatus().equals(AStatus.inGame))
			return;
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			String playerName = player.getName();
			if (!this.getPlayers().contains(playerName))
				return;
			int damage = (int) event.getFinalDamage();
			totalDamageLeft -= damage;
			Bukkit.getLogger().info(event.getDamage() + "|" + event.getFinalDamage());
		} else if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (!(proj.getShooter() instanceof Player))
				return;
			Player player = (Player) proj.getShooter();
			String playerName = player.getName();
			if (!this.getPlayers().contains(playerName))
				return;
			int damage = (int) event.getFinalDamage();
			totalDamageLeft -= damage;
		}
		if (totalDamageLeft <= 0)
			endZone();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.currentZone != 1)
			return;
		if (!this.getStatus().equals(AStatus.inGame))
			return;
		Player player = event.getPlayer();
		if (!player.getGameMode().equals(GameMode.SPECTATOR) && AUtils.isInRegion(player, z1c1, z1c2)) {
			endZone();
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (this.currentZone != 3)
			return;
		if (!this.getStatus().equals(AStatus.inGame))
			return;
		if (event.getClickedBlock().getType().equals(Material.LEVER)) {
			if (event.getPlayer().getGameMode().equals(GameMode.SPECTATOR))
				return;
			if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld("battle"), -6, 143, 332))) {
				this.endZone();
			}
		}
	}

	@EventHandler
	public void onMythicMobDeath(MythicMobDeathEvent event) {
		if (this.currentZone != 4)
			return;
		if (!this.getStatus().equals(AStatus.inGame))
			return;
		if (event.getMobType().getInternalName().equals("IceWitch"))
			endZone();
	}

}
