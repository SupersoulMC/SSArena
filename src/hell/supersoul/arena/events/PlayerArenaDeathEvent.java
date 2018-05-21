package hell.supersoul.arena.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import hell.supersoul.arena.enums.DamageType;
import hell.supersoul.arena.modules.Arena;

public class PlayerArenaDeathEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private Arena arena;
    private Player player;
    private DamageType damageType;
    private Object[] arguments;

    public Arena getArena() {
		return arena;
	}

	public PlayerArenaDeathEvent(Arena arena, Player player, DamageType damageType, Object args[]) {
        this.arena = arena;
        this.player = player;
        this.damageType = damageType;
        this.arguments = args;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

	public Player getPlayer() {
		return player;
	}

	public DamageType getDamageType() {
		return damageType;
	}

	public Object[] getArguments() {
		return arguments;
	}
}
