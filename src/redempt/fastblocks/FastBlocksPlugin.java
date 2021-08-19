package redempt.fastblocks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.region.CuboidRegion;

public class FastBlocksPlugin extends JavaPlugin implements Listener {
	
	public static FastBlocksPlugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		Bukkit.getPluginManager().registerEvents(this, this);
		new CommandParser(this.getResource("command.rdcml"))
				.setArgTypes(ArgType.of("material", Material.class))
				.parse().register("fastblocks", this);
	}
	
	@CommandHook("test")
	public void test(Player sender, Material type) {
		Location loc = sender.getLocation().getBlock().getLocation();
		long time = System.currentTimeMillis();
		CuboidRegion.cubeRadius(loc, 50).forEachBlock(b -> {
			FastBlocks.setBlock(b, type);
		});
		FastBlocks.refreshChunks(true);
		System.out.println("Setting 1 million blocks took " + (System.currentTimeMillis() - time) + "ms");
	}
	
	@CommandHook("light")
	public void light(Player player) {
		FastBlocks.updateLighting(player.getLocation().getChunk());
	}
	
}
