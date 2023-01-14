package worldgeneratorextension.singletspop.populator;

import cn.nukkit.level.ChunkManager;
import worldgeneratorextension.singletspop.template.ReadableStructureTemplate;

public interface CallbackableTemplateStructurePopulator {

    void generateChunkCallback(ReadableStructureTemplate template, int seed, ChunkManager level, int startChunkX, int startChunkZ, int y, int chunkX, int chunkZ);
}
