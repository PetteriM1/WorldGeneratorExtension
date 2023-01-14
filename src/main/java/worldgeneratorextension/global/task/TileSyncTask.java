package worldgeneratorextension.global.task;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import worldgeneratorextension.Loader;

public class TileSyncTask extends PluginTask<Plugin> {

    public final String type;
    public final FullChunk chunk;
    public final CompoundTag nbt;

    public TileSyncTask(String type, FullChunk chunk, CompoundTag nbt) {
        super(Loader.INSTANCE);
        this.type = type;
        this.chunk = chunk;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        BlockEntity.createBlockEntity(this.type, this.chunk, this.nbt);
    }
}
