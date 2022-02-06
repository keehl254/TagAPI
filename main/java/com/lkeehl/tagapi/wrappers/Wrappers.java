package com.lkeehl.tagapi.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Wrappers {

    public static Supplier<SpawnEntityPacket> SPAWN_ENTITY;
    public static Supplier<SpawnEntityLivingPacket> SPAWN_ENTITY_LIVING;
    public static Supplier<MountPacket> MOUNT;
    public static Supplier<MetaDataPacket> METADATA;
    public static Function<PacketContainer, MetaDataPacket> METADATA_W_CONTAINER;
    public static Supplier<DestroyPacket> DESTROY;
    public static Function<PacketContainer,DestroyPacket> DESTROY_W_CONTAINER;

    public abstract static class SpawnEntityPacket extends AbstractSpawnPacket {

        protected SpawnEntityPacket() {
            super(PacketType.Play.Server.SPAWN_ENTITY);
            handle.getModifier().writeDefaults();
        }

        public abstract void setX(double value);

        public abstract void setY(double value);

        public abstract void setZ(double value);

        public abstract void setObjectData(int value);


    }

    public abstract static class SpawnEntityLivingPacket extends AbstractSpawnPacket {

        protected SpawnEntityLivingPacket() {
            super(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            handle.getModifier().writeDefaults();
        }

        public abstract void setHeadPitch(float value);

    }

    public abstract static class MountPacket extends AbstractPacket {

        protected MountPacket() {
            super(new PacketContainer( PacketType.Play.Server.MOUNT));
            handle.getModifier().writeDefaults();
        }

        public abstract void setEntityID(int value);

        public abstract int[] getPassengerIds();

        public abstract void setPassengerIds(int[] value);


    }

    public abstract static class MetaDataPacket extends AbstractPacket {

        protected MetaDataPacket() {
            super(new PacketContainer( PacketType.Play.Server.ENTITY_METADATA));
            handle.getModifier().writeDefaults();
        }

        public MetaDataPacket(PacketContainer packet) {
            super(packet);
        }

        public abstract void setEntityID(int value);

        public abstract List<WrappedWatchableObject> getMetadata();

        public abstract void setMetadata(List<WrappedWatchableObject> value);


    }

    public abstract static class DestroyPacket extends AbstractPacket {

        protected DestroyPacket() {
            super(new PacketContainer( PacketType.Play.Server.ENTITY_DESTROY));
            handle.getModifier().writeDefaults();
        }

        public DestroyPacket(PacketContainer packet) {
            super(packet);
        }

        public abstract int getCount();

        public abstract List<Integer> getEntityIDs();

        public abstract void addEntityID(int id);

    }
}
