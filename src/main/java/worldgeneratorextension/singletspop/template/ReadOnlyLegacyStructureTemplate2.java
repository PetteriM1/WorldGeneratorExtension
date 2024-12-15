package worldgeneratorextension.singletspop.template;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.RuntimeItemMapping;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import worldgeneratorextension.Loader;
import worldgeneratorextension.global.template.AbstractLegacyStructureTemplate;
import worldgeneratorextension.global.template.BlockEntry;

import java.util.Comparator;
import java.util.function.Consumer;

public class ReadOnlyLegacyStructureTemplate2 extends AbstractLegacyStructureTemplate implements ReadableStructureTemplate {

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
                } else {
                    palette.addMapping(new BlockEntry(0, 0), i);
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
    public boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor) {
        if (this.isInvalid() || this.size.getX() > 16 || this.size.getZ() > 16) {
            return false;
        }

        for (StructureBlockInfo blockInfo : this.blockInfoList) {
            BlockEntry entry = blockInfo.state;

            if (entry.getId() == Block.AIR || entry.getId() == BlockID.STRUCTURE_BLOCK) {
                continue;
            }

            BlockVector3 vec = blockInfo.pos.add(position);

            chunk.setBlock(vec.getX() & 0x0f, vec.getY(), vec.getZ() & 0x0f, entry.getId(), entry.getMeta());
        }

        return true;
    }

    @Override
    public boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor) {
        throw new UnsupportedOperationException();
    }
}
