package hell.supersoul.arena.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import hell.supersoul.arena.modules.Arena;

public class ArenaEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Arena arena;

    public Arena getArena() {
		return arena;
	}

	public ArenaEndEvent(Arena arena) {
        this.arena = arena;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
