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
import java.util.function.Supplier;

public class BlockAccess1_17 implements BlockAccess {
  private static final Supplier<Method> UPDATE_CHUNK_STATUS_METHOD =
			Suppliers.memoize(() -> {
				try {
					final Method method = LightEngineThreaded.class.getDeclaredMethod("a", ChunkCoordIntPair.class);
					method.setAccessible(true);

					return method;
				} catch (NoSuchMethodException e) {
					throw new RuntimeException(e);
				}
			});

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

		try {
			UPDATE_CHUNK_STATUS_METHOD.get().invoke(engine, chunk.getPos());
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		chunk.b(false);
		engine.a(chunk.getPos(), true);
		engine.queueUpdate();
	}
	
}
