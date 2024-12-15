package worldgeneratorextension.pm1e.populator;

import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import worldgeneratorextension.global.task.BlockActorSpawnTask;
import worldgeneratorextension.pm1e.loot.TreasureChest;

public class PopulatorTreasureChest extends Populator {

    protected static final int SPACING = 20;
    protected static final int SEPARATION = 8;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {

            int biome = chunk.getBiomeId(8, 8);
            if (biome == EnumBiome.BEACH.id || biome == EnumBiome.COLD_BEACH.id) {
                int y = chunk.getHighestBlockAt(8, 8);
                if (y > Normal.seaHeight + 5 || y < Normal.seaHeight - 5) {
                    return;
                }

                y -= 2;

                int block;
                while (y > Normal.seaHeight - 10 && ((block = chunk.getBlockId(8, y, 8)) != SANDSTONE && block != STONE)) {
                    y--;
                }

                int topBlock = chunk.getBlockId(8, y + 1, 8);
                if (topBlock != SAND && topBlock != GRAVEL) {
                    return;
                }

                chunk.setBlock(8, y, 8, CHEST, 0);

                CompoundTag nbt = BlockEntity.getDefaultCompound(new Vector3((chunkX << 4) + 8, y, (chunkZ << 4) + 8), BlockEntity.CHEST);
                ListTag<CompoundTag> itemList = new ListTag<>("Items");
                TreasureChest.get().create(itemList, random);
                nbt.putList(itemList);
                Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
            }
        }
    }
}
