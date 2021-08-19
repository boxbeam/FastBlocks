package redempt.fastblocks;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public interface BlockAccess {
	
	public void update(BlockState state);
	
	public void refreshChunk(int x, int z, Player player);
	
	public void updateLighting(World world, int x, int z);
	
}
