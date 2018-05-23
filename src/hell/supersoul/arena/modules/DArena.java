package hell.supersoul.arena.modules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.utils.AUtils;
import hell.supersoul.arena.waitroom.WRManager;
import hell.supersoul.magic.rpg.PlayerM;

public abstract class DArena extends Arena{

	protected int currentZone = 0;
	protected double timer = 0;
	protected Location startloc;
	boolean scoreboardLooping = false;

	public DArena(String id) {
		super(id);
	}
	
	@Override
	public void startArena() {
		new BukkitRunnable() {
			public void run() {
				if (DArena.this.getStatus().equals(AStatus.over) || DArena.this.getStatus().equals(AStatus.announcing))
					this.cancel();
				DArena.this.updateScoreboard();
			}
		}.runTaskTimer(Main.getInstance(), 0, 10);
	}
	
	public abstract void nextZone();
	
	public abstract void endZone();
	
	@Override
	public void endArena() {
		WRManager.getManager().gameEnded(this, null);
		super.endArena();
	}
	
	@Override
	public void announceResult() {
		this.sendTitle(10, 100, 10, ChatColor.AQUA + "VICTORY", "");
		new BukkitRunnable() {
			@Override
			public void run() {
				DArena.this.endArena();
			}
		}.runTaskLater(Main.getInstance(), 100);
	}
	
	@Override
	public Scoreboard updateScoreboard() {
		Scoreboard arenaBoard;
		Objective arenaObjective;
		arenaBoard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		arenaObjective = arenaBoard.registerNewObjective("arena", "dummy");
		arenaObjective.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + this.getDisplayName());
		arenaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		double time = this.timer;
		if (time > -1) {
			time = time - 0.5d;
			timer = time;
			if (time > 0)
				arenaObjective.getScore(ChatColor.RED + "Time: " + time).setScore(540);
			if (this.timer == 0) {
				for (String playerName : this.getPlayers()) {
					if (!this.getDeadPlayers().contains(playerName)) {
						Player player = Bukkit.getPlayer(playerName);
						if (player != null)
							player.damage(999999);
					}
				}
			}
		}
		arenaObjective.getScore(ChatColor.BOLD.toString()).setScore(500);
		arenaObjective.getScore(ChatColor.DARK_AQUA + "Status:").setScore(490);
		for (String playerName : this.getPlayers()) {
			Player player = Bukkit.getPlayer(playerName);
			PlayerM playerM = PlayerM.getPlayerM(player);
			String playerCompo = ChatColor.GRAY + "- " + ChatColor.AQUA + playerName;
			String statusCompo = "";
			int level = 0;
			
			if (this.getDisconnectedPlayers().contains(playerName))
				playerCompo = ChatColor.DARK_RED + "- " + ChatColor.STRIKETHROUGH + playerName + " -";
			else if (this.getDeadPlayers().contains(playerName))
				playerCompo = ChatColor.RED + "- " + ChatColor.STRIKETHROUGH + playerName + " -";
			else if (playerM != null && player != null) {
				level = playerM.getLevel();
				statusCompo = ChatColor.GRAY + " " + ChatColor.GREEN + (int) player.getHealth() + " ";
				statusCompo += ChatColor.LIGHT_PURPLE + "" + playerM.getMP() + ChatColor.GRAY + " -";
			}
			
			String output = playerCompo + statusCompo;
			Bukkit.getLogger().info(output.length() + "");
			arenaObjective.getScore(output).setScore(level);
		}
		for (String playerName : this.getPlayers()) {
			Bukkit.getPlayer(playerName).setScoreboard(arenaBoard);
		}
		return arenaBoard;
	}
}
