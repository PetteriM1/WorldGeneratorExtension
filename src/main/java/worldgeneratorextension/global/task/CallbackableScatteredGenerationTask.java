package worldgeneratorextension.global.task;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import cn.nukkit.scheduler.AsyncTask;
import worldgeneratorextension.scatteredbuilding.populator.PopulatorScatteredStructure;
import worldgeneratorextension.scatteredbuilding.structure.piece.ScatteredStructurePiece;

public class CallbackableScatteredGenerationTask extends AsyncTask {

    public boolean state = true;

    private final Level world;
    private BaseFullChunk chunk;
    private final PopulatorScatteredStructure structure;
    private final ScatteredStructurePiece piece;
    private final ChunkManager level;
    private final int startChunkX;
    private final int startChunkZ;

    public CallbackableScatteredGenerationTask(Level world, BaseFullChunk chunk, PopulatorScatteredStructure structure, ScatteredStructurePiece piece, ChunkManager level, int startChunkX, int startChunkZ) {
        this.chunk = chunk;
        this.world = world;
        this.structure = structure;
        this.piece = piece;
        this.level = level;
        this.startChunkX = startChunkX;
        this.startChunkZ = startChunkZ;
    }

    @Override
    public void onRun() {
        this.state = false;
        Generator generator = this.world.getGenerator();
        if (generator != null) {
            SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();
            if (manager != null) {
                manager.cleanChunks(this.world.getSeed());
                synchronized (manager) {
                    try {
                        BaseFullChunk chunk = this.chunk;
                        if (chunk != null) {
                            synchronized (chunk) {
                                if (!chunk.isGenerated()) {
                                    manager.setChunk(chunk.getX(), chunk.getZ(), chunk);
                                    generator.generateChunk(chunk.getX(), chunk.getZ());
                                    chunk = manager.getChunk(chunk.getX(), chunk.getZ());
                                    chunk.setGenerated();
                                }
                            }
                            this.chunk = chunk;
                            this.state = true;
                        }
                    } finally {
                        manager.cleanChunks(this.world.getSeed());
                    }
                }
            }
        }
    //}

    //@Override
    //public void onCompletion(Server server) {
        if (this.state && this.world != null) {
            if (this.chunk != null) {
                this.structure.generateChunkCallback(this.level, this.startChunkX, this.startChunkZ, this.piece, this.chunk.getX(), this.chunk.getZ());
            }
        }
    }
}
