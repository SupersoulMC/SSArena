package hell.supersoul.arena.recovery;

import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import hell.supersoul.arena.Main;
import hell.supersoul.arena.arenas.ArenaManager;
import hell.supersoul.arena.config.MyConfig;
import hell.supersoul.arena.config.MyConfigManager;
import hell.supersoul.arena.enums.AStatus;
import hell.supersoul.arena.modules.Arena;
import hell.supersoul.managers.BackupManager;

public class RecoverManager {

	private static RecoverManager rm = new RecoverManager();

	public static RecoverManager get() {
		return rm;
	}

	MyConfig recover;
	{
		Main.getConfigManager().getNewConfig("recovery.yml", new String[] { "SS Arena config file" });
	}

	public void loginCheck(Player player) {
		if (!Main.getInstance().getConfig().contains("locinv." + player.getName()))
			return;
		Arena arena = ArenaManager.get().getPlayerArena(player.getName());
		if (arena == null) {
			recoverInvandLoc(player);
			return;
		}
		if (!arena.getStatus().equals(AStatus.inGame)) {
			recoverInvandLoc(player);
			return;
		}
		arena.playerReconnected(player);
	}
	
	public void recoverLoc(Player player) {
		if (player == null)
			return;
		if (!player.isOnline())
			return;
		if (!Main.getInstance().getConfig().contains("locinv." + player.getName()))
			return;
		player.teleport((Location) Main.getInstance().getConfig().get("locinv." + player.getName() + ".loc"));
		Main.getInstance().getConfig().set("locinv." + player.getName(), null);
		Main.getInstance().saveConfig();
	}

	public void recoverInvandLoc(Player player) {
		if (player == null)
			return;
		if (!player.isOnline())
			return;
		if (!Main.getInstance().getConfig().contains("locinv." + player.getName()))
			return;
		Bukkit.getLogger().info("recovering " + player.getName());
		Main.getInstance().reloadConfig();
		player.setGameMode(GameMode.SURVIVAL);
		ItemStack[] items = ((ArrayList<ItemStack>) Main.getInstance().getConfig()
				.get("locinv." + player.getName() + ".items")).toArray(new ItemStack[0]);
		player.getInventory().setContents(items);
		player.getInventory().setArmorContents(
				((ArrayList<ItemStack>) Main.getInstance().getConfig().get("locinv." + player.getName() + ".armor"))
						.toArray(new ItemStack[0]));
		player.setExp((float) Main.getInstance().getConfig().getDouble("locinv." + player.getName() + ".exp"));
		Main.getInstance().getConfig().set("locinv." + player.getName(), null);
		Main.getInstance().saveConfig();
	}

	public void saveInvandLoc(Player player) {
		ItemStack[] items = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();
		Location loc = player.getLocation();
		Main.getInstance().getConfig().set("locinv." + player.getName() + ".items", items);
		Main.getInstance().getConfig().set("locinv." + player.getName() + ".armor", armor);
		Main.getInstance().getConfig().set("locinv." + player.getName() + ".loc", loc);
		Main.getInstance().getConfig().set("data.locinv." + player.getName() + ".exp", player.getExp());
		Main.getInstance().saveConfig();
		ArrayList<ItemStack> list = new ArrayList<>();
		for (ItemStack is : items)
			list.add(is);
		for (ItemStack is : armor)
			list.add(is);
		BackupManager.getManager().saveInventoryBackup(player, items,
				"SSARENA - " + hell.supersoul.npc.Main.dateFormat.format(Calendar.getInstance().getTime()));
	}
	
	public void saveLoc(Player player) {
		Location loc = player.getLocation();
		Main.getInstance().getConfig().set("locinv." + player.getName() + ".loc", loc);
		Main.getInstance().saveConfig();
	}

}
