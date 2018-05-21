package hell.supersoul.arena.modules;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.connorlinfoot.titleapi.TitleAPI;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.events.ArenaStartEvent;
import hell.supersoul.arena.events.PlayerArenaDamageEvent;
import hell.supersoul.arena.events.PlayerArenaDeathEvent;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.utils.Region;
import hell.supersoul.sound.enums.StopSoundMode;
import hell.supersoul.sound.manager.SoundManager;

public abstract class PArena extends Arena implements Listener {

	ArrayList<Location> toRefresh = new ArrayList<>();
	boolean lastManWin;
	ArrayList<DamageType> disabledDamage = new ArrayList<>();
	ArrayList<DamageType> respawnDamage = new ArrayList<>();
	ArrayList<DamageType> deathDamage = new ArrayList<>();
	HashMap<String, ArrayList<Region>> regions = new HashMap<>();
	String bgmName = "";

	public void playerDamage(Player player, DamageType dt, Object... args) {
		PlayerArenaDamageEvent event = new PlayerArenaDamageEvent(this, player, dt, args);
		if (this.getRespawnDamage().contains(dt)) 
			this.playerRespawn(player, dt, args);
		else if (this.getDeathDamage().contains(dt))
			this.playerDeath(player, dt, args);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	public PArena(String id) {
		super(id);
		//Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@Override
	public void playerDeath(Player player, DamageType dt, Object... args) {
		if (this.getDeadPlayers().contains(player.getName()))
			return;
		this.getDeadPlayers().add(player.getName());
		AUtils.preSpectateMode(player);
		String msg = "";
		if (dt.equals(DamageType.LAVA)) {
			msg = "You were swimming in lava.";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " died when he tried to swim in lava.");
		} else if (dt.equals(DamageType.TIME)) {
			msg = "TIME'S UP!";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " timed out.");
		} else if (dt.equals(DamageType.VOID)) {
			msg = "You fell out of the world.";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " fell out of the world.");
		} else if (dt.equals(DamageType.VOLCALNOERUPTED)) {
			msg = "Volcano erupted.";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " failed to escape from the volcano.");
		} else if (dt.equals(DamageType.UNKNOWN)) {
			msg = "You died.";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " died.");
		} else if (dt.equals(DamageType.ZAHUMANGOAL)) {
			msg = "A human has reached the goal!";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " failed to kill the humans.");
		} else if (dt.equals(DamageType.ZAZOMBIE)) {
			msg = "A human has reached the goal!";
			this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " is killed by zombie.");
		} else if (dt.equals(DamageType.PLAYER)) {
			String kName = (String)args[0];
			msg = "You are killed by " + kName + ".";
			/*
			if (this.getType().equals("teamvs")) {
				this.sendMessage(Main.prefix + ArenaManager.getManager().teamColor(player) + player.getName() + ChatColor.DARK_RED + " is killed by " + ArenaManager.getManager().teamColor(Bukkit.getPlayer(dieway)) + kName + ChatColor.RED + "!");
			} else
			*/
				this.sendMessage(Main.prefix + ChatColor.DARK_RED + player.getName() + " is killed by " + kName + "!");
		}
		this.getPlayerStatus().get(player.getName()).remove(PStatus.INVINCIBLE);
		msg = "¡±8" + msg;
		player.getInventory().setItem(0, null);
		SoundManager.get().stopSound(player, "bgm_ssarena_" + bgmName, StopSoundMode.stopAll);
		SoundManager.get().playSound(player, "sfx_ssarena_gameover");
		String title = "¡±cGAME OVER!";
		String subTitle = msg;
		TitleAPI.sendFullTitle(player, 0, 80, 20, title, subTitle);
		this.getWorld().playEffect(player.getLocation(), Effect.CLOUD, 20);
		new BukkitRunnable() {
			public void run() {
				SoundManager.get().playIntroThenLoopRest(player, "bgm_ssarena_" + bgmName + "_intro", "bgm_ssarena_" + bgmName);
				AUtils.spectateMode(player);
				if (PArena.this.getPlayers().size() - PArena.this.getDeadPlayers().size() > 0) {
					String tPlayer = "";
					for (String playerName : PArena.this.getPlayers()) {
						if (!PArena.this.getDeadPlayers().contains(playerName)) {
							tPlayer = playerName;
							break;
						}
					}
					player.teleport(Bukkit.getPlayer(tPlayer));
				}
			}
		}.runTaskLater(Main.getInstance(), 80);
		/*
		if (arena instanceof ZombieApocalypse) {
			String playerName = player.getName();
			((ZombieApocalypse) arena).getSurvivors().remove(playerName);
			if (((ZombieApocalypse) arena).getSurvivors().size() <= 0) {
				ArenaManager.getManager().alive(arena);
			} else {
				int no = ((ZombieApocalypse) arena).getZombies().size();
				for (String temp : ((ZombieApocalypse) arena).getZombies()) {
					if (arena.getDeadPlayers().contains(temp))
						no--;
				}
				if (no <= 0)
					ArenaManager.getManager().alive(arena);
			}
			if (((ZombieApocalypse) arena).getRound() == 0) {
				if (((ZombieApocalypse) arena).getPressedPlayers().size() == arena.getAlivePlayers().size()) {
					((ZombieApocalypse) arena).nextRound();
				}
			} else if (((ZombieApocalypse) arena).getPressedPlayers().size() == ((ZombieApocalypse) arena).getSurvivors().size() && ((ZombieApocalypse) arena).getSurvivors().size() != 0) {
				((ZombieApocalypse) arena).nextRound();
			}
		}
		*/
		if (this.getPlayers().size() - this.getDeadPlayers().size() <= 0) {
			new BukkitRunnable() {
				public void run() {
					PArena.this.announceResult();
				}
			}.runTaskLater(Main.getInstance(), 100);
		}
		PlayerArenaDeathEvent event = new PlayerArenaDeathEvent(this, player, dt, args);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	public ArrayList<DamageType> getDisabledDamage() {
		return disabledDamage;
	}

	public ArrayList<DamageType> getRespawnDamage() {
		return respawnDamage;
	}

	public ArrayList<DamageType> getDeathDamage() {
		return deathDamage;
	}

	public String getBgmName() {
		return bgmName;
	}

	public void setBgmName(String bgmName) {
		this.bgmName = bgmName;
	}

	public void setDisabledDamage(ArrayList<DamageType> disabledDamage) {
		this.disabledDamage = disabledDamage;
	}

	public void setRespawnDamage(ArrayList<DamageType> respawnDamage) {
		this.respawnDamage = respawnDamage;
	}

	public void setDeathDamage(ArrayList<DamageType> deathDamage) {
		this.deathDamage = deathDamage;
	}

	public HashMap<String, ArrayList<Region>> getRegions() {
		return regions;
	}

	public void setRegions(HashMap<String, ArrayList<Region>> regions) {
		this.regions = regions;
	}

	@Override
	public void endGame() {
		//HandlerList.unregisterAll(this);
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;
		Player player = (Player) event.getEntity();
		if (!this.getPlayers().contains(player.getName()))
			return;
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
			if (e.getDamager() instanceof Player) {
				if (this.getDisabledDamage().contains(DamageType.PLAYER))
					event.setCancelled(true);
				this.playerDamage(player, DamageType.PLAYER, e.getDamager());
			}
		} else {
			DamageType dt = DamageType.toDamageTpye(event.getCause());
			if (dt == null)
				return;
			if (this.getDisabledDamage().contains(dt))
				event.setCancelled(true);
			if (dt.equals(DamageType.FALL))
				this.playerDamage(player, dt, event.getDamage());
			else
				this.playerDamage(player, dt, (Object[]) null);
			if (event.getDamage() >= player.getHealth() && !event.isCancelled()) {
				event.setCancelled(true);
				this.playerDamage(player, DamageType.UNKNOWN, (Object[]) null);
			}
		}
	}
	
	@EventHandler
	public void onPlayerSwapItem(PlayerSwapHandItemsEvent event) {
		if (this.getPlayers().contains(event.getPlayer().getName()))
			event.setCancelled(true);
	}
	
	public ChatColor getPlayerTeam(String playerName) {
		for (ChatColor team : this.getTeamPlayers().keySet()) {
			if (this.getTeamPlayers().get(team).contains(playerName))
				return team;
		}
		return null;
	}
}
