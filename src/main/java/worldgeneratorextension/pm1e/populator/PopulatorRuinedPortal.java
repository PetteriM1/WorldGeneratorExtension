package worldgeneratorextension.pm1e.populator;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import worldgeneratorextension.Loader;
import worldgeneratorextension.global.task.LootSpawnTask;
import worldgeneratorextension.pm1e.loot.PortalChest;
import worldgeneratorextension.multitspop.template.ReadOnlyLegacyStructureTemplate2;
import worldgeneratorextension.multitspop.template.ReadableStructureTemplate;
import worldgeneratorextension.multitspop.template.StructurePlaceSettings;
import worldgeneratorextension.singletspop.populator.PopulatorShipwreck;

import java.util.function.Consumer;

public class PopulatorRuinedPortal extends Populator {

    protected static final ReadableStructureTemplate PORTAL3 = new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/ruined_portal/portal_3.nbt"));
    protected static final ReadableStructureTemplate PORTAL4 = new ReadOnlyLegacyStructureTemplate2().load(Loader.loadNBT("structures/ruined_portal/portal_4.nbt"));

    protected static final int SPACING = 24;
    protected static final int SEPARATION = 8;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, FullChunk chunk) {
        if (chunkX == (((chunkX < 0 ? (chunkX - SPACING + 1) : chunkX) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)
                && chunkZ == (((chunkZ < 0 ? (chunkZ - SPACING + 1) : chunkZ) / SPACING) * SPACING) + random.nextBoundedInt(SPACING - SEPARATION)) {
            ReadableStructureTemplate template = random.nextBoolean() ? PORTAL3 : PORTAL4;

            BlockVector3 size = template.getSize();
            int sumY = 0;
            int blockCount = 0;

            for (int x = 0; x < size.getX() && x < 16; x++) {
                for (int z = 0; z < size.getZ() && z < 16; z++) {
                    int y = chunk.getHighestBlockAt(x, z);

                    if (y > 80) {
                        return;
                    }

                    int id = chunk.getBlockId(x, y, z);
                    while (PopulatorShipwreck.FILTER[id] && y > 0) {
                        id = chunk.getBlockId(x, --y, z);
                    }

                    if (y + 2 < (sumY / (blockCount + 1))) {
                        return;
                    }

                    sumY += y;
                    blockCount++;
                }
            }

            int y = sumY / blockCount;

            BlockVector3 vec = new BlockVector3(chunkX << 4, y, chunkZ << 4);

            if (y > 2) {
                for (int x = 0; x < size.getX() && x < 16; x++) {
                    for (int z = 0; z < size.getZ() && z < 16; z++) {
                        chunk.setBlock(x, y - 1, z, Block.NETHERRACK, 0);
                        chunk.setBlock(x, y - 2, z, Block.NETHERRACK, 0);
                    }
                }
            }

            template.placeInChunk(chunk, random, vec, new StructurePlaceSettings()
                    .setIgnoreAir(true).setBlockActorProcessor(getBlockActorProcessor(chunk, random)));
        }
    }

    public static void init() {
        //NOOP
    }

    protected static Consumer<CompoundTag> getBlockActorProcessor(FullChunk chunk, NukkitRandom random) {
        return nbt -> {
            if (nbt.getString("id").equals(BlockEntity.CHEST)) {
                ListTag<CompoundTag> itemList = new ListTag<>("Items");
                PortalChest.get().create(itemList, random);

                Server.getInstance().getScheduler().scheduleDelayedTask(new LootSpawnTask(chunk.getProvider().getLevel(),
                        new BlockVector3(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), itemList), 2);
            }
        };
    }
}
