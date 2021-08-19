package redempt.fastblocks.impl;

import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.Chunk;
import net.minecraft.server.v1_16_R3.ChunkSection;
import net.minecraft.server.v1_16_R3.LightEngineThreaded;
import net.minecraft.server.v1_16_R3.PacketPlayOutMapChunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import redempt.fastblocks.BlockAccess;

import java.util.concurrent.ExecutionException;

public class BlockAccess1_16 implements BlockAccess {
	
	@Override
	public void update(BlockState state) {
		Block block = state.getBlock();
		CraftWorld world = (CraftWorld) block.getWorld();
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		Chunk chunk = world.getHandle().getChunkAtWorldCoords(pos);
//		chunk.a().setType(pos.getX() % 16, pos.getY(), pos.getZ() % 16, ((CraftBlockData) type.createBlockData()).getState());
		ChunkSection cs = chunk.getSections()[pos.getY() >> 4];
		if (cs == null) {
			cs = new ChunkSection(pos.getY() >> 4 << 4);
			chunk.getSections()[pos.getY() >> 4] = cs;
		}
		chunk.removeTileEntity(pos);
		cs.setType(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, ((CraftBlockData) state.getBlockData()).getState(), false);

//		chunk.setType(pos, ((CraftBlockData) type.createBlockData()).getState(), true);
	}
	
	@Override
	public void refreshChunk(int x, int z, Player player) {
		CraftWorld world = (CraftWorld) player.getWorld();
		Chunk chunk = world.getHandle().getChunkAt(x, z);
		PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(chunk, Integer.MAX_VALUE);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	@Override
	public void updateLighting(World world, int x, int z) {
		CraftWorld cw = (CraftWorld) world;
		Chunk chunk = cw.getHandle().getChunkAt(x, z);
//		chunk.i.getChunkProvider().getLightEngine().b(new ChunkCoordIntPair(x, z), false);
	}
	
}
