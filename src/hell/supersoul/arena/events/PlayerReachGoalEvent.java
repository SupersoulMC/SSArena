package hell.supersoul.arena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import hell.supersoul.arena.modules.Arena;

public class PlayerReachGoalEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	private Arena arena;
	private Player player;
	private boolean cancelled;

	public PlayerReachGoalEvent(Arena arena, Player player) {
		this.arena = arena;
		this.player = player;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Arena getArena() {
		return arena;
	}

	public Player getPlayer() {
		return player;
	}
}
