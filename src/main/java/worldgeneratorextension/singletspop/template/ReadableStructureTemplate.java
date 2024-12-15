package worldgeneratorextension.singletspop.template;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.tag.CompoundTag;
import worldgeneratorextension.global.template.StructureTemplate;

import java.util.function.Consumer;

public interface ReadableStructureTemplate extends StructureTemplate {

    ReadableStructureTemplate load(CompoundTag root);

    boolean placeInChunk(FullChunk chunk, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor);

    boolean placeInLevel(ChunkManager level, NukkitRandom random, BlockVector3 position, int integrity, Consumer<CompoundTag> blockActorProcessor);
}
