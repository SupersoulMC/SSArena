package hell.supersoul.arena.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hell.supersoul.arena.enums.PStatus;
import hell.supersoul.arena.modules.Arena;
import hell.supersoul.arena.modules.Race;
import hell.supersoul.arena.waitroom.WaitRoom;

public class ArenaManager {

	private static ArenaManager m = new ArenaManager();
	public static ArenaManager get() {
		return m;
	}
	
	public Arena getPlayerArena(String playerName) {
		for (Arena arena : Arena.arenas) {
			if (arena.getPlayers().contains(playerName)) return arena;
		}
		return null;
	}
	
	public Arena getArenaWithID(String id) {
		for (Arena arena : Arena.arenas) {
			if (arena.getId().equals(id)) return arena;
		}
		return null;
	}
	
	public void removePlayer(String playerName) {
		Arena arena = ArenaManager.get().getPlayerArena(playerName);
		if (arena == null) return;
		arena.getPlayers().remove(playerName);
		arena.getDeadPlayers().remove(playerName);
		arena.getSpectators().remove(playerName);
		arena.getLeftPlayers().add(playerName);
		if (arena.getPlayers().size() <= 0)
			arena.endGame();
		if (arena instanceof Race) {
			((Race)arena).disallowDJ(playerName);
		}
		Player player = Bukkit.getPlayer(playerName);
		if (player != null)
			player.setWalkSpeed(0.2f);
	}
	
	public void createArena(WaitRoom wr) {
		
	}
	
	public void removeArena(WaitRoom wr) {
		
	}
}
