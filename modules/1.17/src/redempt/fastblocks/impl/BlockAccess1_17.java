package redempt.fastblocks.impl;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.NibbleArray;
import net.minecraft.world.level.lighting.LightEngineLayer;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import redempt.fastblocks.BlockAccess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

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
		Chunk chunk = cw.getHandle().getChunkAt(x, z);
		LightEngineThreaded engine = chunk.i.getChunkProvider().getLightEngine();
		//engine.a(EnumSkyBlock.b, SectionPosition.a(chunk.getPos(), chunk.getSections()[0].getYPosition()), chunk.i.getChunkProvider()., true);
		engine.a(EnumSkyBlock.b).a(Integer.MAX_VALUE, true, true); // runUpdates(int, boolean, boolean)

		//engine.a(EnumSkyBlock.b);

		/*
		try {
			Method method = engine.getClass().getDeclaredMethod("a", ChunkCoordIntPair.class);
			method.setAccessible(true);
			method.invoke(engine, new ChunkCoordIntPair(x, z));
		} catch (Exception e) {
			e.printStackTrace();
		}
		engine.a(chunk, true);
		engine.queueUpdate();
		engine.a(100);
		 */
//		chunk.i.getChunkProvider().getLightEngine().b(new ChunkCoordIntPair(x, z), false);
	}
	
}
