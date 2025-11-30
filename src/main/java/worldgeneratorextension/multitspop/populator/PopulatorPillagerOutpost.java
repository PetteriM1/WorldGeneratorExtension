package worldgeneratorextension.multitspop.populator;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityPillager;
import cn.nukkit.entity.passive.EntityIronGolem;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import worldgeneratorextension.Loader;
import worldgeneratorextension.global.block.BlockTypes;
import worldgeneratorextension.global.task.CallbackableChunkGenerationTask;
import worldgeneratorextension.multitspop.loot.PillagerOutpostChest;
import worldgeneratorextension.multitspop.template.ReadOnlyLegacyStructureTemplate;
import worldgeneratorextension.multitspop.template.ReadableStructureTemplate;
import worldgeneratorextension.multitspop.template.StructurePlaceSettings;
import worldgeneratorextension.global.task.ActorSpawnTask;
import worldgeneratorextension.global.task.LootSpawnTask;

import java.util.function.Consumer;

public class PopulatorPillagerOutpost extends Populator {

    protected static final ReadableStructureTemplate WATCHTOWER = new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/watchtower.nbt"));
    protected static final ReadableStructureTemplate WATCHTOWER_OVERGROWN = new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/watchtower_overgrown.nbt"));

    protected static final ReadableStructureTemplate[] FEATURES = new ReadableStructureTemplate[]{
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_cage1.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_cage2.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_logs.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_tent1.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_tent2.nbt")),
            new ReadOnlyLegacyStructureTemplate().load(Loader.loadNBT("structures/pillageroutpost/feature_targets.nbt"))
    };

    protected static final int SPACING = 32;
    protected static final int SEPARATION = 8;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        int biome = chunk.getBiomeId(7, 7);
        if ((biome == EnumBiome.PLAINS.id || biome == EnumBiome.DESERT.id || biome == EnumBiome.BEACH.id || biome == EnumBiome.TAIGA.id || biome == EnumBiome.ICE_PLAINS.id || biome == EnumBiome.SAVANNA.id || biome == EnumBiome.TAIGA_M.id || biome == EnumBiome.FOREST.id || biome == EnumBiome.BIRCH_FOREST.id || biome == EnumBiome.EXTREME_HILLS_PLUS.id)
                && chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            random.setSeed(((chunkX >> 4) ^ (chunkZ >> 4) << 4) ^ level.getSeed());
            random.nextInt();

            if (random.nextBoundedInt(5) == (0x77f73e3a & 3)) {
                ReadableStructureTemplate template = WATCHTOWER;
                int y = chunk.getHighestBlockAt(0, 0);

                int blockId = chunk.getBlockId(0, y, 0);
                while (BlockTypes.isPlant[blockId] && y > 1) {
                    blockId = chunk.getBlockId(0, --y, 0);
                }

                BlockVector3 vec = new BlockVector3(chunkX << 4, y, chunkZ << 4);
                template.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                        .setBlockActorProcessor(getBlockActorProcessor(chunk, random)));
                WATCHTOWER_OVERGROWN.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                        .setIntegrity(5)
                        .setIgnoreAir(true)
                        .setBlockActorProcessor(getBlockActorProcessor(chunk, random)));

                BlockVector3 size = template.getSize();
                fillBase(level.getChunk(chunkX, chunkZ), y, 0, 0, size.getX(), size.getZ());

                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ - 1), random);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX - 1, chunkZ + 1), random);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ - 1), random);
                }
                if (random.nextBoolean()) {
                    this.tryPlaceFeature(level.getChunk(chunkX + 1, chunkZ + 1), random);
                }
            }
        }
    }

    protected void tryPlaceFeature(BaseFullChunk chunk, NukkitRandom random) {
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
        NukkitRandom random = new NukkitRandom(seed);

        BlockVector3 size = template.getSize();
        int x = random.nextBoundedInt(16 - size.getX());
        int z = random.nextBoundedInt(16 - size.getZ());
        int y = chunk.getHighestBlockAt(x, z);

        template.placeInChunk(chunk, random, new BlockVector3((chunk.getX() << 4) + x, y, (chunk.getZ() << 4) + z), new StructurePlaceSettings()
                .setBlockActorProcessor(getBlockActorProcessor(chunk, random)));
        fillBase(chunk, y, x, z, size.getX(), size.getZ());
    }

    protected static void fillBase(FullChunk chunk, int baseY, int startX, int startZ, int sizeX, int sizeZ) {
        for (int x = startX; x < startX + sizeX; x++) {
            for (int z = startZ; z < startZ + sizeZ; z++) {
                int baseId = chunk.getBlockId(x, baseY, z);
                int baseMeta = chunk.getBlockData(x, baseY, z);

                switch (baseId) {
                    case COBBLESTONE:
                    case MOSSY_STONE:
                    case LOG2:
                    case PLANKS:
                    case FENCE:
                        int y = baseY - 1;
                        int id = chunk.getBlockId(x, y, z);

                        while ((BlockTypes.isPlantOrFluid[id]) && y > 1) {
                            chunk.setBlock(x, y, z, baseId, baseMeta);
                            id = chunk.getBlockId(x, --y, z);
                        }
                }
            }
        }
    }

    protected static Consumer<CompoundTag> getBlockActorProcessor(FullChunk chunk, NukkitRandom random) {
        return nbt -> {
            if (nbt.getString("id").equals("StructureBlock")) {
                switch (nbt.getString("metadata")) {
                    case "topChest":
                        ListTag<CompoundTag> itemList = new ListTag<>("Items");
                        PillagerOutpostChest.get().create(itemList, random);

                        Server.getInstance().getScheduler().scheduleDelayedTask(new LootSpawnTask(chunk.getProvider().getLevel(),
                                new BlockVector3(nbt.getInt("x"), nbt.getInt("y") - 1, nbt.getInt("z")), itemList), 2);
                        break;
                    case "pillager":
                        Server.getInstance().getScheduler().scheduleDelayedTask(new ActorSpawnTask(chunk.getProvider().getLevel(),
                                Entity.getDefaultNBT(new Vector3(nbt.getInt("x") + 0.5, nbt.getInt("y"), nbt.getInt("z") + 0.5))
                                        .putString("id", String.valueOf(EntityPillager.NETWORK_ID))), 2);
                        break;
                    case "captain":
                        Server.getInstance().getScheduler().scheduleDelayedTask(new ActorSpawnTask(chunk.getProvider().getLevel(),
                                Entity.getDefaultNBT(new Vector3(nbt.getInt("x") + 0.5, nbt.getInt("y"), nbt.getInt("z") + 0.5))
                                        .putString("id", String.valueOf(EntityPillager.NETWORK_ID))
                                        .putBoolean("PatrolLeader", true)), 2);
                        break;
                    case "cage":
                        Server.getInstance().getScheduler().scheduleDelayedTask(new ActorSpawnTask(chunk.getProvider().getLevel(),
                                Entity.getDefaultNBT(new Vector3(nbt.getInt("x") + 0.5, nbt.getInt("y"), nbt.getInt("z") + 0.5))
                                        .putString("id", String.valueOf(EntityIronGolem.NETWORK_ID))), 2);
                        break;
                }
            }
        };
    }

    public static void init() {
        //NOOP
    }
}
