package hell.supersoul.arena.utils;

import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import hell.supersoul.arena.arenas.ArenaManager;
import hell.supersoul.arena.modules.Race;

public class RanksComparator implements Comparator<Object> {
	@Override
	public int compare(Object obj1, Object obj2) {
		Player player1 = Bukkit.getPlayer((String) obj1);
		Player player2 = Bukkit.getPlayer((String) obj2);
		Race race1 = (Race) ArenaManager.get().getPlayerArena(player1.getName());
		if (race1 == null) return 0;
		Race race2 = (Race) ArenaManager.get().getPlayerArena(player2.getName());
		if (race2 == null) return 0;
		if (!race1.equals(race2)) return 0;
		if (race1.getFinishPercent().get(player1.getName()) > race1.getFinishPercent().get(player2.getName())) {
			return 1;
		} else if (race1.getFinishPercent().get(player1.getName()) < race1.getFinishPercent().get(player2.getName())) {
			return -1;
		} else {
			return 0;
		}
	}
}
