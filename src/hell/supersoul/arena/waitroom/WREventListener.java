package hell.supersoul.arena.waitroom;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import hell.supersoul.arena.recovery.RecoverManager;

public class WREventListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		RecoverManager.get().loginCheck(event.getPlayer());
	}
	
}
