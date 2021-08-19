package redempt.fastblocks.impl;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.ChunkSection;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import redempt.fastblocks.BlockAccess;

public class BlockAccess1_12 implements BlockAccess {
	
	@Override
	public void update(BlockState state) {
		Block block = state.getBlock();
		CraftWorld world = (CraftWorld) block.getWorld();
		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		Chunk chunk = world.getHandle().getChunkAtWorldCoords(pos);
		chunk.tileEntities.remove(pos);
		ChunkSection cs = chunk.getSections()[pos.getY() >> 4];
		if (cs == null) {
			cs = new ChunkSection(pos.getY() >> 4 << 4, true);
			chunk.getSections()[pos.getY() >> 4] = cs;
		}
		MaterialData data = state.getData();
		int id = data.getItemType().getId() + (data.getData() << 12);
		IBlockData d = net.minecraft.server.v1_12_R1.Block.getByCombinedId(id);
		cs.setType(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, d);
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
		chunk.initLighting();
	}
	
}
