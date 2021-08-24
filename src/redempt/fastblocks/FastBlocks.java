package redempt.fastblocks;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import redempt.fastblocks.impl.BlockAccessDefault;
import redempt.redlib.RedLib;

import java.util.HashSet;
import java.util.Set;

public class FastBlocks {
	
	private static BlockAccess access;
	private static Set<ChunkPosition> modified = new HashSet<>();
	
	static {
		String name = "redempt.fastblocks.impl.BlockAccess1_" + RedLib.MID_VERSION;
		try {
			Class<?> clazz = Class.forName(name);
			access = (BlockAccess) clazz.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().warning("Could not load BlockAccess for version 1_" + RedLib.MID_VERSION + ", falling back to Bukkit");
			access = new BlockAccessDefault();
		}
	}
	
	private static boolean isInBounds(Block block) {
		return block.getY() >= 0 && block.getY() <= block.getWorld().getMaxHeight();
	}
	
	public static void setBlock(Block block, Material type, byte data) {
		BlockState state = block.getState();
		state.setData(new MaterialData(type, data));
		updateState(state);
	}
	
	public static void setBlock(Block block, Material type) {
		BlockState state = block.getState();
		state.setType(type);
		updateState(state);
	}
	
	public static void updateState(BlockState state) {
		if (!isInBounds(state.getBlock())) {
			return;
		}
		modified.add(new ChunkPosition(state.getBlock()));
		access.update(state);
	}
	
	public static void setBlockData(Block block, BlockData data) {
		BlockState state = block.getState();
		state.setBlockData(data);
		updateState(state);
	}
	
	public static void updateLighting(Chunk chunk) {
		access.updateLighting(chunk.getWorld(), chunk.getX(), chunk.getZ());
	}
	
	public static void refreshChunks(boolean updateLighting) {
		modified.forEach(c -> {
			for (Player player : c.world.getPlayers()) {
				ChunkPosition cpos = new ChunkPosition(player.getLocation().getBlock());
				if (cpos.distance(c) > Bukkit.getViewDistance()) {
					continue;
				}
				access.refreshChunk(c.x, c.z, player);
			}
			if (updateLighting) {
				access.updateLighting(c.world, c.x, c.z);
			}
		});
		modified.clear();
	}
	
}
