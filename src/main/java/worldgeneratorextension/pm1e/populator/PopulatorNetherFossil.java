package worldgeneratorextension.pm1e.populator;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import worldgeneratorextension.Loader;
import worldgeneratorextension.singletspop.template.ReadOnlyLegacyStructureTemplate2;
import worldgeneratorextension.singletspop.template.ReadableStructureTemplate;

public class PopulatorNetherFossil extends Populator {

    protected static final ReadableStructureTemplate[] FOSSILS = {
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_1.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_2.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_3.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_4.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_5.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_6.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_7.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_8.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_9.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_10.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_11.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_12.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_13.nbt")),
            new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/nether_fossils/fossil_14.nbt"))
    };

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(16) == 0 && chunk.getBiomeId(3, 3) == EnumBiome.SOULSAND_VALLEY.id) {
            int y;
            int previous = -1;

            for (y = 100; y > 0; y--) {
                int b = chunk.getBlockId(0, y, 0);

                if (previous == BlockID.AIR && (b == BlockID.SOUL_SAND || b == BlockID.SOUL_SOIL)) {
                    break;
                }

                previous = b;
            }

            if (y < 1) {
                return;
            }

            BlockVector3 vec = new BlockVector3(chunkX << 4, y, chunkZ << 4);
            FOSSILS[random.nextBoundedInt(FOSSILS.length)].placeInChunk(chunk, random, vec, 100, null);
        }
    }

    public static void init() {
        //NOOP
    }
}
