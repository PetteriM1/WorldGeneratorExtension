package worldgeneratorextension.theend.scheduler;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.PluginTask;
import worldgeneratorextension.Loader;

public abstract class EntitySpawnTask extends PluginTask<Loader> {

    protected final FullChunk chunk;
    protected final CompoundTag nbt;

    public EntitySpawnTask(FullChunk chunk, CompoundTag nbt) {
        super(Loader.INSTANCE);
        this.chunk = chunk;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        Entity entity = Entity.createEntity(this.getType(), this.chunk, this.nbt);

        this.onCreated(entity);

        if (entity != null) {
            entity.spawnToAll();
        }
    }

    protected void onCreated(Entity entity) {

    }

    protected abstract String getType();
}
