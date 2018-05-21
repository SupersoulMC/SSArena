package hell.supersoul.arena.modules;

import java.util.ArrayList;

import org.bukkit.Location;

public abstract class DArena extends Arena{

	protected int currentZone = 0;
	protected double timer = 0;
	protected ArrayList<String> survivors = new ArrayList<>();
	protected Location startloc;

	public DArena(String id) {
		super(id);
	}
	
	public abstract void nextZone();
	
	public abstract void endZone();

}
