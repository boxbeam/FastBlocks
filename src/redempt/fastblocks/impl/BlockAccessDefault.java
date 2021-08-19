package redempt.fastblocks.impl;

import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import redempt.fastblocks.BlockAccess;

public class BlockAccessDefault implements BlockAccess {
	
	@Override
	public void update(BlockState state) {
		state.update(true, false);
	}
	
	@Override
	public void refreshChunk(int x, int z, Player player) {
	
	}
	
	@Override
	public void updateLighting(World world, int x, int z) {
	
	}
	
}
