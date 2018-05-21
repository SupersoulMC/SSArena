package hell.supersoul.arena.utils;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Region {
	Location firstCorner, secondCorner;

	public Region(Location firstCorner, Location secondCorner) {
		this.firstCorner = firstCorner;
		this.secondCorner = secondCorner;
	}
	
	public boolean isInRegion(Location loc) {
		if (loc == null) return false;
		if (firstCorner == null)
			return false;
		if (firstCorner.getWorld() == null)
			return false;
		if (secondCorner == null)
			return false;
		if (secondCorner.getWorld() == null)
			return false;
		if (firstCorner.getWorld().equals(secondCorner.getWorld())) {
			int x1 = firstCorner.getBlockX();
			int x2 = secondCorner.getBlockX();
			int z1 = firstCorner.getBlockZ();
			int z2 = secondCorner.getBlockZ();
			int y1 = firstCorner.getBlockY();
			int y2 = secondCorner.getBlockY();
			int minx = (int) Math.min(x1, x2);
			int maxx = (int) Math.max(x1, x2);
			int minz = (int) Math.min(z1, z2);
			int maxz = (int) Math.max(z1, z2);
			int miny = (int) Math.min(y1, y2);
			int maxy = (int) Math.max(y1, y2);
			int px = loc.getBlockX();
			int pz = loc.getBlockZ();
			int py = loc.getBlockY();
			if (minx <= px && px <= maxx) {
				if (minz <= pz && pz <= maxz) {
					if (miny <= py && py <= maxy) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isInRegion(Player player) {
		if (player == null)
			return false;
		return isInRegion(player.getLocation());
	}

	public void setBlock(Material replaceMat, Material newMat, Effect effect, Material stepMat, int data) {
		if (!firstCorner.getWorld().equals(secondCorner.getWorld()))
			return;
		World world = firstCorner.getWorld();
		int minX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
		int minY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
		int minZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		int maxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());
		int maxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());
		int maxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					Block block = world.getBlockAt(x, y, z);
					if (replaceMat == null || (replaceMat != null && block.getType().equals(replaceMat))) {
						block.setType(newMat);
						block.setData((byte)data);
						if (effect != null) {
							if (effect.equals(Effect.STEP_SOUND) && replaceMat != null)
								world.playEffect(new Location(world, x, y, z), effect, replaceMat);
							else if (!effect.equals(Effect.STEP_SOUND))
								world.playEffect(new Location(world, x, y, z), effect, data);
						}
					}
				}
			}
		}
	}

	public void setBlock(Material newMat) {
		setBlock(null, newMat, null, null, 0);
	}

	public void setBlock(Material newMat, Effect effect) {
		setBlock(null, newMat, effect, null, 0);
	}
	
	public void setBlock(Material newMat, Effect effect, int data) {
		setBlock(null, newMat, effect, null, data);
	}

	public void setBlock(Material replaceMat, Material newMat) {
		setBlock(replaceMat, newMat, null, null, 0);
	}

	public void setBlock(Material replaceMat, Material newMat, Effect effect) {
		if (effect.equals(Effect.STEP_SOUND))
			setBlock(replaceMat, newMat, effect, newMat, 0);
		else
			setBlock(replaceMat, newMat, effect, null, 0);
	}

	public void playEffect(Effect effect, int data) {
		if (!firstCorner.getWorld().equals(secondCorner.getWorld()))
			return;
		World world = firstCorner.getWorld();
		int minX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
		int minY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
		int minZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		int maxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());
		int maxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());
		int maxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (effect.equals(Effect.STEP_SOUND))
						world.playEffect(new Location(world, x + 0.5, y, z + 0.5), effect, world.getBlockAt(new Location(world, x + 0.5, y + 0.5, z + 0.5)).getType());
					else
						world.playEffect(new Location(world, x + 0.5, y + 0.5, z + 0.5), effect, data);
				}
			}
		}
	}
	
	public void playStepSound(Material mat) {
		if (!firstCorner.getWorld().equals(secondCorner.getWorld()))
			return;
		World world = firstCorner.getWorld();
		int minX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
		int minY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
		int minZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		int maxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());
		int maxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());
		int maxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
						world.playEffect(new Location(world, x + 0.5, y, z + 0.5), Effect.STEP_SOUND, mat);
				}
			}
		}
	}

	public Location getFirstCorner() {
		return firstCorner;
	}

	public Location getSecondCorner() {
		return secondCorner;
	}

	public void setFirstCorner(Location firstCorner) {
		this.firstCorner = firstCorner;
	}

	public void setSecondCorner(Location secondCorner) {
		this.secondCorner = secondCorner;
	}
}
