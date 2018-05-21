package hell.supersoul.arena.enums;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public enum DamageType {
	VOID(DamageCause.VOID), FIRE(DamageCause.FIRE), FIRE_TICK(DamageCause.FIRE_TICK),TIME(null), LAVA(DamageCause.LAVA), PLAYER(null), UNKNOWN(null), FALL(DamageCause.FALL), VOLCALNOERUPTED(null), ZAZOMBIE(null), ZAHUMANGOAL(null);
	private DamageType(DamageCause dc) {
		this.damageCause = dc;
	}
	public DamageCause damageCause;
	public static DamageType toDamageTpye(DamageCause dc) {
		for (DamageType dt : DamageType.values())
			if (dt.damageCause == dc) return dt;
		return null;
	}
}
