package worldgeneratorextension.global.task;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import worldgeneratorextension.Loader;

public class BlockActorSpawnTask extends PluginTask<Plugin> {

    private final Level level;
    private final CompoundTag nbt;

    public BlockActorSpawnTask(Level level, CompoundTag nbt) {
        super(Loader.INSTANCE);
        this.level = level;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        BlockEntity.createBlockEntity(this.nbt.getString("id"),
                this.level.getChunk(this.nbt.getInt("x") >> 4, this.nbt.getInt("z") >> 4), this.nbt);
    }
}
