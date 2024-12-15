package worldgeneratorextension.global.task;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.scheduler.AsyncTask;

import java.util.Collection;

public class ChunkPopulateTask extends AsyncTask {

    private final ChunkManager level;
    private final FullChunk chunk;
    private final Collection<Populator> populators;

    public ChunkPopulateTask(ChunkManager level, FullChunk chunk, Collection<Populator> populators) {
        this.level = level;
        this.chunk = chunk;
        this.populators = populators;
    }

    @Override
    public void onRun() {
        int chunkX = this.chunk.getX();
        int chunkZ = this.chunk.getZ();
        long seed = this.level.getSeed();
        this.populators.forEach(populator -> populator.populate(this.level, chunkX, chunkZ, new NukkitRandom(0xdeadbeef ^ (chunkX << 8) ^ chunkZ ^ seed), this.chunk));
    }
}
