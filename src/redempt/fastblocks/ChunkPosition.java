package redempt.fastblocks;

import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class ChunkPosition {
	
	public World world;
	public int x;
	public int z;
	
	public ChunkPosition(World world, int x, int z) {
		this.world = world;
		this.x = x;
		this.z = z;
	}
	
	public ChunkPosition(Block block) {
		this(block.getWorld(), block.getX() >> 4, block.getZ() >> 4);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ChunkPosition)) return false;
		ChunkPosition that = (ChunkPosition) o;
		return x == that.x && z == that.z && world.equals(that.world);
	}
	
	public int distance(ChunkPosition other) {
		return Math.max(Math.abs(other.x - x), Math.abs(other.z - z));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(world.getName(), x, z);
	}
	
}
