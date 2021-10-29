package keehl.tagapi.tags;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import keehl.tagapi.TagAPI;
import keehl.tagapi.util.TagUtil;
import keehl.tagapi.util.VersionFile;
import keehl.tagapi.util.WatcherType;
import keehl.tagapi.util.TreeSet;
import keehl.tagapi.wrappers.AbstractPacket;
import keehl.tagapi.wrappers.AbstractSpawnPacket;
import keehl.tagapi.wrappers.Wrappers;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TagEntity {

    private final TagLine tagLine;
    private final TagEntity parent;
    private TagEntity child;

    private final int entityID;
    private final UUID entityUUID;

    private final EntityType entityType;

    private final boolean nameEntity;

    private static final TreeSet<EntityType, DataEntry> entityWatchers = new TreeSet<>();

    private static VersionFile versionFile;

    private static AtomicInteger atomicEntityID;

    public static void init(VersionFile file) {
        versionFile = file;
        List<EntityType> mobs = Arrays.asList(EntityType.ARMOR_STAND, EntityType.SILVERFISH, EntityType.SLIME, EntityType.TROPICAL_FISH, EntityType.TURTLE);

        for (EntityType mob : mobs) {
            entityWatchers.add(mob, new DataEntry(file.getDataWatcherIndex(mob, WatcherType.INVISIBLE), Byte.class, (byte) (1 << 5)));
            entityWatchers.add(mob, new DataEntry(file.getDataWatcherIndex(mob, WatcherType.CUSTOM_NAME), WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.ofNullable(IChatBaseComponent.ChatSerializer.a(""))));
            entityWatchers.add(mob, new DataEntry(file.getDataWatcherIndex(mob, WatcherType.NAME_VISIBLE), Boolean.class, false));
            entityWatchers.add(mob, new DataEntry(file.getDataWatcherIndex(mob, WatcherType.SILENT), Boolean.class, true));
        }

        Function<EntityType, DataEntry> entry = i -> new DataEntry(file.getDataWatcherIndex(i, WatcherType.NO_AI), Byte.class, (byte) 1);
        entityWatchers.add(EntityType.SILVERFISH, entry.apply(EntityType.SILVERFISH));
        entityWatchers.add(EntityType.SLIME, entry.apply(EntityType.SLIME));
        entityWatchers.add(EntityType.TROPICAL_FISH, entry.apply(EntityType.TROPICAL_FISH));
        entityWatchers.add(EntityType.TURTLE, entry.apply(EntityType.TURTLE));

        entityWatchers.add(EntityType.ARMOR_STAND, new DataEntry(file.getDataWatcherIndex(EntityType.ARMOR_STAND, WatcherType.IS_SMALL), Byte.class, (byte) 16));
        entityWatchers.add(EntityType.SLIME, new DataEntry(file.getDataWatcherIndex(EntityType.SLIME, WatcherType.SIZE), Integer.class, -1));
        entityWatchers.add(EntityType.TURTLE, new DataEntry(file.getDataWatcherIndex(EntityType.TURTLE, WatcherType.IS_BABY), Boolean.class, true));

        Field[] fields  = Entity.class.getDeclaredFields();
        for(Field f : fields) {
            if(f.getType().equals(AtomicInteger.class)) {
                f.setAccessible(true);
                try {
                    atomicEntityID = (AtomicInteger) f.get(null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public TagEntity(TagLine tagLine, TagEntity parent, EntityType entityType) {
        this(tagLine, parent, entityType, false);
    }

    public TagEntity(TagLine tagLine, TagEntity parent, EntityType entityType, boolean nameEntity) {
        this.tagLine = tagLine;
        this.parent = parent;
        this.entityID = atomicEntityID.incrementAndGet();
        this.entityUUID = UUID.randomUUID();
        this.entityType = entityType;
        this.nameEntity = nameEntity;
        if (parent != null)
            parent.setChild(this);
        TagAPI.getTagTracker().trackEntity(this);
    }

    public int getEntityID() {
        return this.entityID;
    }

    public UUID getEntityUUID() {
        return this.entityUUID;
    }

    public TagLine getTagLine() {
        return this.tagLine;
    }

    protected void setChild(TagEntity child) {
        this.child = child;
    }

    protected AbstractPacket getSpawnPacket(Location location) {
        AbstractSpawnPacket wrapper = this.entityType.isAlive() ? Wrappers.SPAWN_ENTITY_LIVING.get() : Wrappers.SPAWN_ENTITY.get();
        wrapper.setID(this.entityID);
        wrapper.setUUID(this.entityUUID);
        wrapper.setLocation(location);
        wrapper.setType(this.entityType);
        wrapper.setVelocityX(0);
        wrapper.setVelocityY(0);
        wrapper.setVelocityZ(0);
        wrapper.setPitch(0.0F);
        wrapper.setYaw(0.0F);

        if (wrapper instanceof Wrappers.SpawnEntityPacket objectWrapper)
            objectWrapper.setObjectData(0);
        else {
            ((Wrappers.SpawnEntityLivingPacket) wrapper).setHeadPitch(0.0F);
        }

        return wrapper;
    }

    protected AbstractPacket getMetaPacket(Player viewer) {
        Wrappers.MetaDataPacket wrapper = Wrappers.METADATA.get();
        wrapper.setEntityID(this.getEntityID());

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        entityWatchers.get(this.entityType).forEach(entry -> entry.apply(watcher));
        String name;
        if (this.nameEntity && (name = this.tagLine.getNameFor(viewer)) != null) {
            TagUtil.applyData(watcher, versionFile.getDataWatcherIndex(this.entityType,WatcherType.NAME_VISIBLE), Boolean.class, true); // Name Visible
            TagUtil.applyData(watcher, versionFile.getDataWatcherIndex(this.entityType,WatcherType.CUSTOM_NAME), WrappedDataWatcher.Registry.getChatComponentSerializer(true), Optional.ofNullable(IChatBaseComponent.ChatSerializer.a(ComponentSerializer.toString(TextComponent.fromLegacyText(name)))));
        }

        wrapper.setMetadata(watcher.getWatchableObjects());
        return wrapper;
    }

    public void getSpawnPackets(Player viewer, List<AbstractPacket> packets, Location location, boolean spawnNew) {
        if (this.child != null)
            this.child.getSpawnPackets(viewer, packets, location, spawnNew);
        packets.add(this.getMetaPacket(viewer));
        if (spawnNew)
            packets.add(this.getSpawnPacket(location));
    }

    public void getMountPackets(List<AbstractPacket> packets, int defaultParentID) {
        if (this.child != null)
            this.child.getMountPackets(packets, defaultParentID);

        packets.add(this.getMountPacket(defaultParentID));
    }

    public void getMetaPackets(Player viewer, List<AbstractPacket> packets) {
        if (this.child != null)
            this.child.getMetaPackets(viewer, packets);

        packets.add(this.getMetaPacket(viewer));
    }

    public AbstractPacket getMountPacket(int defaultParentID) {
        Wrappers.MountPacket wrapper = Wrappers.MOUNT.get();
        wrapper.setEntityID(this.parent == null ? defaultParentID : this.parent.getEntityID());
        wrapper.setPassengerIds(new int[]{this.entityID});
        return wrapper;
    }

    public void destroy(Wrappers.DestroyPacket wrapper) {
        if (this.child != null)
            this.child.destroy(wrapper);

        wrapper.addEntityID(this.entityID);
    }

    private static class DataEntry {

        private final Object value;

        protected WrappedDataWatcher.WrappedDataWatcherObject watcher;

        public DataEntry(int index, WrappedDataWatcher.Serializer serializer, Object value) {
            this.watcher = new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer);
            this.value = value;
        }

        public DataEntry(int index, Class<?> clazz, Object value) {
            this.watcher = new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(clazz));
            this.value = value;
        }

        public void apply(WrappedDataWatcher watcher) {
            watcher.setObject(this.watcher, this.value);
        }


    }

}
