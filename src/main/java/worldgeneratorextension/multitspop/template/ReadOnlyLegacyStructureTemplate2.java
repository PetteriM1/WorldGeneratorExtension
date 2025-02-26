package worldgeneratorextension.multitspop.template;

import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.leveldb.structure.LevelDBChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import worldgeneratorextension.Loader;
import worldgeneratorextension.global.task.ActorSpawnTask;
import worldgeneratorextension.global.task.BlockActorSpawnTask;
import worldgeneratorextension.global.template.AbstractLegacyStructureTemplate;
import worldgeneratorextension.global.template.BlockEntry;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReadOnlyLegacyStructureTemplate2 extends AbstractLegacyStructureTemplate implements ReadableStructureTemplate {

    private boolean updated;
    private static final Map<String, BlockEntry> lookup = new HashMap<String, BlockEntry>() {
        {
            put("minecraft:air", new BlockEntry(0, 0));
            put("minecraft:jigsaw", new BlockEntry(0, 0));
            put("minecraft:stone_bricks", new BlockEntry(BlockID.STONE_BRICK, BlockStone.NORMAL));
            put("minecraft:stone_brick_slab", new BlockEntry(BlockID.STONE_SLAB, BlockSlabStone.STONE_BRICK));
        }
    };

    @Override
    public ReadableStructureTemplate load(CompoundTag root) {
        this.clean();

        ListTag<IntTag> size = root.getList("size", IntTag.class);
        this.size = new BlockVector3(size.get(0).data, size.get(1).data, size.get(2).data);

        SimplePalette palette = new SimplePalette();

        ListTag<CompoundTag> paletteTag = root.getList("palette", CompoundTag.class);
        for (int i = 0; i < paletteTag.size(); ++i) {
            CompoundTag tag = paletteTag.get(i);
            if (tag.contains("id")) {
                palette.addMapping(new BlockEntry(tag.getShort("id"), tag.getShort("meta")), i);
            } else {
                String name = tag.getString("Name");
                RuntimeItemMapping.LegacyEntry entry = Loader.getRuntimeItemMapptings().fromIdentifier(name);
                if (entry != null && entry.getLegacyId() > 0) {
                    if (entry.getLegacyId() == BlockID.CHEST) {
                        palette.addMapping(new BlockEntry(entry.getLegacyId(), 4), i);
                    } else {
                        palette.addMapping(new BlockEntry(entry.getLegacyId(), entry.getDamage()), i);
                    }
                } else if (lookup.containsKey(name)) {
                    palette.addMapping(lookup.get(name), i);
                    this.updated = true;
                } else {
                    palette.addMapping(new BlockEntry(0, 0), i);
                    Loader.INSTANCE.getLogger().warning("No block state found for " + name);
                }
            }
        }

        ListTag<CompoundTag> blocks = root.getList("blocks", CompoundTag.class);
        for (int i = 0; i < blocks.size(); ++i) {
            CompoundTag block = blocks.get(i);
            ListTag<IntTag> pos = block.getList("pos", IntTag.class);

            this.blockInfoList.add(new StructureBlockInfo(
                    new BlockVector3(pos.get(0).data, pos.get(1).data, pos.get(2).data),
                    palette.stateFor(block.getInt("state")),
                    block.contains("nbt") ? block.getCompound("nbt") : null));
        }

        this.blockInfoList.sort(Comparator.comparingInt(block -> block.pos.getY()));

        ListTag<CompoundTag> entities = root.getList("entities", CompoundTag.class);
        for (int i = 0; i < entities.size(); ++i) {
            CompoundTag entity = entities.get(i);
            if (entity.contains("nbt")) {
                ListTag<DoubleTag> pos = entity.getList("pos", DoubleTag.class);
                ListTag<IntTag> blockPos = entity.getList("blockPos", IntTag.class);

                this.entityInfoList.add(new StructureEntityInfo(
                        new Vector3(pos.get(0).data, pos.get(1).data, pos.get(2).data),
                        new BlockVector3(blockPos.get(0).data, blockPos.get(1).data, blockPos.get(2).data),
                        entity.getCompound("nbt")));
            }
        }

        return this;
    }

    @Override
    public boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, StructurePlaceSettings settings) {
        if (this.isInvalid() || this.size.getX() > 16 || this.size.getZ() > 16 || !(chunk instanceof LevelDBChunk)) {
            return false;
        }

        boolean isIgnoreAir = settings.isIgnoreAir();
        int integrity = settings.getIntegrity();
        Consumer<CompoundTag> blockActorProcessor = settings.getBlockActorProcessor();
        boolean isIntact = integrity >= 100;

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            int id = entry.getId();

            if (id == Block.AIR && isIgnoreAir || !isIntact && integrity <= random.nextBoundedInt(100) && id != BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            if (id == BlockID.OBSIDIAN && random.nextBoundedInt(100) < 20) {
                id = BlockID.CRYING_OBSIDIAN;
            }

            BlockVector3 vec = blockInfo.pos.add(position);

            if (id != BlockID.STRUCTURE_BLOCK) {
                if (Block.isBlockTransparentById(id) && vec.getY() < 255 && (worldgeneratorextension.global.block.BlockTypes.isWater(chunk.getBlockId(vec.getX() & 0x0f, vec.getY(), vec.getZ() & 0x0f)) || worldgeneratorextension.global.block.BlockTypes.isWater(chunk.getBlockId(vec.getX() & 0x0f, vec.getY() + 1, vec.getZ() & 0x0f)))) {
                    if (id == BlockID.LAVA || id == BlockID.STILL_LAVA) {
                        id = BlockID.MAGMA;
                    } else {
                        chunk.setFullBlockId(vec.getX() & 0x0f, vec.getY(), vec.getZ() & 0x0f, BlockLayer.NORMAL, Block.STILL_WATER << Block.DATA_BITS);
                    }
                }
                chunk.setBlock(vec.getX() & 0x0f, vec.getY(), vec.getZ() & 0x0f, id, entry.getMeta());
            } else if (!isIgnoreAir) {
                chunk.setBlock(vec.getX() & 0xf, vec.getY(), vec.getZ() & 0xf, Block.AIR);
            }

            if (blockInfo.nbt != null) {
                CompoundTag nbt = blockInfo.nbt.clone();

                nbt.putInt("x", vec.getX());
                nbt.putInt("y", vec.getY());
                nbt.putInt("z", vec.getZ());

                if (id != BlockID.STRUCTURE_BLOCK) {
                    if (this.updated) {
                        if (id == BlockID.CHEST) {
                            nbt.putString("id", BlockEntity.CHEST);
                        } else {
                            continue;
                        }
                    }

                    Server.getInstance().getScheduler().scheduleTask(new BlockActorSpawnTask(chunk.getProvider().getLevel(), nbt));
                }

                if (blockActorProcessor != null) {
                    blockActorProcessor.accept(nbt);
                }
            }
        }

        if (!settings.isIgnoreEntities()) {
            this.placeEntities(chunk.getProvider().getLevel(), position);
        }

        return true;
    }

    @Override
    public boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, StructurePlaceSettings settings) {
        throw new UnsupportedOperationException();
    }

    protected void placeEntities(Level level, BlockVector3 position) {
        for (StructureEntityInfo entityInfo : this.entityInfoList) {
            CompoundTag nbt = entityInfo.nbt.clone();

            Vector3 pos = entityInfo.pos.add(position.getX(), position.getY(), position.getZ());

            ListTag<DoubleTag> posTag = new ListTag<>("Pos");
            posTag.add(new DoubleTag("", pos.x));
            posTag.add(new DoubleTag("", pos.y));
            posTag.add(new DoubleTag("", pos.z));
            nbt.putList(posTag);

            Server.getInstance().getScheduler().scheduleTask(new ActorSpawnTask(level, nbt));
        }
    }
}
