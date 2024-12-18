package worldgeneratorextension.pm1e.populator;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLayer;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import worldgeneratorextension.Loader;
import worldgeneratorextension.global.task.CallbackableChunkGenerationTask;
import worldgeneratorextension.singletspop.template.ReadOnlyLegacyStructureTemplate3;
import worldgeneratorextension.singletspop.template.ReadableStructureTemplate;

public class PopulatorCoralCrust extends Populator {

    protected static final ReadableStructureTemplate[] FEATURES = {
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/crust1.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/crust2.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/crust3.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/crust4.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/crust5.nbt"))
    };

    protected static final ReadableStructureTemplate[] FEATURES2 = {
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping1.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping2.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping3.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping4.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping5.nbt")),
            new ReadOnlyLegacyStructureTemplate3().load(Loader.loadNBT("structures/coralcrust/outcropping6.nbt"))
    };

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (random.nextBoundedInt(3) != 0) {
            return;
        }

        if (chunk.getBiomeId(7, 7) == EnumBiome.WARM_OCEAN.id) {
            ReadableStructureTemplate template = FEATURES[random.nextBoundedInt(FEATURES.length)];

            BlockVector3 size = template.getSize();
            int x = random.nextBoundedInt(16 - size.getX());
            int z = random.nextBoundedInt(16 - size.getZ());

            int y;
            int previous = -1;
            boolean found = false;

            for (y = Normal.seaHeight; y >= 40; y--) {
                int b = chunk.getBlockId(0, y, 0);

                if ((previous == BlockID.WATER || previous == BlockID.STILL_WATER) && (b == BlockID.GRAVEL || b == BlockID.SAND)) {
                    found = true;
                    break;
                }

                previous = b;
            }

            if (!found) {
                return;
            }

            BlockVector3 vec = new BlockVector3(chunkX + x, y - 1, chunkZ + z);
            template.placeInChunk(chunk, random, vec, 100, null);

            if (random.nextBoundedInt(5) != 0) {
                FEATURES2[random.nextBoundedInt(FEATURES2.length)].placeInChunk(chunk, random, vec, 100, null);
            }
            if (random.nextBoundedInt(5) != 0) {
                this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ - 1), random);
            }
            if (random.nextBoundedInt(5) != 0) {
                this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ + 1), random);
            }
            if (random.nextBoundedInt(5) != 0) {
                this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ - 1), random);
            }
            if (random.nextBoundedInt(5) != 0) {
                this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ + 1), random);
            }

            placeDecoration(random, chunk, random.nextBoundedInt(6) + 6, BlockID.SEA_PICKLE, 4);
            placeDecoration(random, chunk, random.nextBoundedInt(12) + 12, BlockID.CORAL, 5);
            placeDecoration(random, chunk, random.nextBoundedInt(12) + 12, BlockID.CORAL_FAN, 5);
        }
    }

    private static void placeDecoration(NukkitRandom random, FullChunk chunk, int amount, int id, int type) {
        for (int i = 0; i < amount; ++i) {
            int x = random.nextBoundedInt(16);
            int z = random.nextBoundedInt(16);
            int y = getHighestWorkableBlock(chunk, x, z);

            if (y >= 40 && y < Normal.seaHeight && Block.isWater(chunk.getBlockId(x, y, z))) {
                chunk.setBlock(x, y, z, id, random.nextBoundedInt(type));
                chunk.setBlockAtLayer(x, y, z, BlockLayer.WATERLOGGED, BlockID.WATER);
            }
        }
    }

    private static int getHighestWorkableBlock(FullChunk chunk, int x, int z) {
        int y;
        int previous = -1;

        for (y = Normal.seaHeight; y >= 40; y--) {
            int b = chunk.getBlockId(x, y, z);

            if (b == BlockID.CORAL_BLOCK && worldgeneratorextension.global.block.BlockTypes.isWater(previous)) {
                break;
            }

            previous = b;
        }

        return y + 1;
    }

    protected void tryPlaceFeature(BaseFullChunk chunk, NukkitRandom random) {
        if (chunk == null) {
            return;
        }

        ReadableStructureTemplate template = FEATURES[random.nextBoundedInt(FEATURES.length)];
        int seed = random.nextInt();

        if (!chunk.isGenerated()) {
            Server.getInstance().getScheduler().scheduleAsyncTask(null, new CallbackableChunkGenerationTask<>(
                    chunk.getProvider().getLevel(), chunk, this,
                    populator -> populator.placeFeature(template, chunk, seed)));
        } else {
            this.placeFeature(template, chunk, seed);
        }
    }

    protected void placeFeature(ReadableStructureTemplate template, FullChunk chunk, int seed) {
        if (chunk.getBiomeId(7, 7) == EnumBiome.WARM_OCEAN.id) {
            NukkitRandom random = new NukkitRandom(seed);
            BlockVector3 size = template.getSize();
            int x = random.nextBoundedInt(16 - size.getX());
            int z = random.nextBoundedInt(16 - size.getZ());

            int y;
            int previous = -1;

            for (y = Normal.seaHeight; y >= 40; y--) {
                int b = chunk.getBlockId(0, y, 0);

                if ((previous == BlockID.WATER || previous == BlockID.STILL_WATER) && (b == BlockID.GRAVEL || b == BlockID.SAND)) {
                    break;
                }

                previous = b;
            }

            int chunkX = (chunk.getX() << 4);
            int chunkZ = (chunk.getZ() << 4);

            BlockVector3 vec = new BlockVector3(chunkX + x, y - random.nextBoundedInt(8), chunkZ + z);
            template.placeInChunk(chunk, random, vec, 100, null);

            if (random.nextBoundedInt(5) != 0) {
                FEATURES2[random.nextBoundedInt(FEATURES2.length)].placeInChunk(chunk, random, vec, 100, null);
            }

            placeDecoration(random, chunk, random.nextBoundedInt(6) + 6, BlockID.SEA_PICKLE, 4);
            placeDecoration(random, chunk, random.nextBoundedInt(12) + 12, BlockID.CORAL, 5);
            placeDecoration(random, chunk, random.nextBoundedInt(12) + 12, BlockID.CORAL_FAN, 5);
        }
    }

    public static void init() {
        //NOOP
    }
}
