package worldgeneratorextension.global.task;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.SimpleChunkManager;
import cn.nukkit.scheduler.AsyncTask;

import java.util.function.Consumer;

public class CallbackableChunkGenerationTask<T> extends AsyncTask {

    public boolean state = true;

    private final Level level;
    private BaseFullChunk chunk;
    private final T structure;
    private final Consumer<T> callback;

    public CallbackableChunkGenerationTask(Level level, BaseFullChunk chunk, T structure, Consumer<T> callback) {
        this.chunk = chunk;
        this.level = level;
        this.structure = structure;
        this.callback = callback;
    }

    @Override
    public void onRun() {
        this.state = false;

        Generator generator = this.level.getGenerator();
        if (generator != null) {
            SimpleChunkManager manager = (SimpleChunkManager) generator.getChunkManager();
            if (manager != null) {
                manager.cleanChunks(this.level.getSeed());
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
                        manager.cleanChunks(this.level.getSeed());
                    }
                }
            }
        }

        if (this.state && this.chunk != null) {
            this.callback.accept(this.structure);
        }
    }
}
