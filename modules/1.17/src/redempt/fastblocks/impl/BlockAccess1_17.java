package redempt.fastblocks.impl;

import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import redempt.fastblocks.BlockAccess;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockAccess1_17 implements BlockAccess {

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
		PacketPlayOutMapChunk packet = new PacketPlayOutMapChunk(chunk);
		((CraftPlayer) player).getHandle().b.sendPacket(packet);
	}
	
	@Override
	public void updateLighting(World world, int x, int z) {
		CraftWorld cw = (CraftWorld) world;

		for (int offX = -1; offX < 2; offX++) {
			for (int offZ = -1; offZ < 2; offZ++) {
				Chunk chunk = cw.getHandle().getChunkAt(x + offX, z + offZ);
				chunk.i.getChunkProvider().a.l.get(ChunkCoordIntPair.pair(x, z)).a(chunk);
			}
		}
	}
}
