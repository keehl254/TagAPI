package com.lkeehl.tagapi.wrappers.versions;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.lkeehl.tagapi.util.VersionFile;
import com.lkeehl.tagapi.wrappers.Wrappers;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.UUID;

public class Wrapper1_18_1 {

    private static VersionFile versionFile;

    public static void init(VersionFile versionFile) {
        Wrapper1_18_1.versionFile = versionFile;

        Wrappers.DESTROY_W_CONTAINER = DestroyWrapper::new;
        Wrappers.DESTROY = DestroyWrapper::new;
        Wrappers.METADATA_W_CONTAINER = MetaDataWrapper::new;
        Wrappers.METADATA = MetaDataWrapper::new;
        Wrappers.MOUNT = MountWrapper::new;
        Wrappers.SPAWN_ENTITY = SpawnEntityWrapper::new;
        Wrappers.SPAWN_ENTITY_LIVING = SpawnEntityLivingWrapper::new;
    }

    public static class DestroyWrapper extends Wrappers.DestroyPacket {

        public DestroyWrapper() {
            super();
        }

        public DestroyWrapper(PacketContainer packet) {
            super(packet);
        }

        @Override
        public int getCount() {
            return handle.getIntLists().read(0).size();
        }

        @Override
        public List<Integer> getEntityIDs() {
            return handle.getIntLists().read(0);
        }

        @Override
        public void addEntityID(int id) {
            List<Integer> ids = this.getEntityIDs();
            ids.add(id);
            handle.getIntLists().write(0, ids);
        }
    }

    public static class MetaDataWrapper extends Wrappers.MetaDataPacket {

        public MetaDataWrapper() {
            super();
        }

        public MetaDataWrapper(PacketContainer packet) {
            super(packet);
        }

        @Override
        public void setEntityID(int value) {
            handle.getIntegers().write(0, value);
        }

        @Override
        public List<WrappedWatchableObject> getMetadata() {
            return handle.getWatchableCollectionModifier().read(0);
        }

        @Override
        public void setMetadata(List<WrappedWatchableObject> value) {
            handle.getWatchableCollectionModifier().write(0, value);
        }
    }

    public static class MountWrapper extends Wrappers.MountPacket {

        @Override
        public void setEntityID(int value) {
            handle.getIntegers().write(0, value);
        }

        @Override
        public int[] getPassengerIds() {
            return handle.getIntegerArrays().read(0);
        }

        @Override
        public void setPassengerIds(int[] value) {
            handle.getIntegerArrays().write(0, value);
        }
    }

    public static class SpawnEntityWrapper extends Wrappers.SpawnEntityPacket {

        @Override
        public void setID(int entityID) {
            handle.getIntegers().write(0, entityID);
        }

        @Override
        public void setUUID(UUID uuid) {
            handle.getUUIDs().write(0, uuid);
        }

        @Override
        public void setType(EntityType type) {
            handle.getEntityTypeModifier().write(0, type);
        }

        @Override
        public void setLocation(Location location) {
            this.setX(location.getX());
            this.setY(location.getY());
            this.setZ(location.getZ());
        }

        @Override
        public void setYaw(float value) {
            handle.getIntegers().write(5, (int) (value * 256.0F / 360.0F));
        }

        @Override
        public void setPitch(float value) {
            handle.getIntegers().write(4, (int) (value * 256.0F / 360.0F));
        }

        @Override
        public void setVelocityX(int value) {
            handle.getIntegers().write(1, (int) (value * 8000.0D));
        }

        @Override
        public void setVelocityY(int value) {
            handle.getIntegers().write(2, (int) (value * 8000.0D));
        }

        @Override
        public void setVelocityZ(int value) {
            handle.getIntegers().write(3, (int) (value * 8000.0D));
        }

        @Override
        public void setX(double value) {
            handle.getDoubles().write(0, value);
        }

        @Override
        public void setY(double value) {
            handle.getDoubles().write(1, value);
        }

        @Override
        public void setZ(double value) {
            handle.getDoubles().write(2, value);
        }

        @Override
        public void setObjectData(int value) {
            handle.getIntegers().write(6, value);
        }
    }

    public static class SpawnEntityLivingWrapper extends Wrappers.SpawnEntityLivingPacket {

        @Override
        public void setID(int entityID) {
            this.getHandle().getIntegers().write(0, entityID);
        }

        @Override
        public void setUUID(UUID uuid) {
            this.getHandle().getUUIDs().write(0, uuid);
        }

        @Override
        public void setType(EntityType type) {
            handle.getIntegers().write(1, versionFile.getEntityID(type));
        }

        @Override
        public void setLocation(Location location) {
            handle.getDoubles().write(0, location.getX());
            handle.getDoubles().write(1, location.getY());
            handle.getDoubles().write(2, location.getZ());
        }

        @Override
        public void setYaw(float value) {
            handle.getBytes().write(0, (byte) (value * 256.0F / 360.0F));
        }

        @Override
        public void setPitch(float value) {
            handle.getBytes().write(1, (byte) (value * 256.0F / 360.0F));
        }

        @Override
        public void setVelocityX(int value) {
            handle.getIntegers().write(2, value);
        }

        @Override
        public void setVelocityY(int value) {
            handle.getIntegers().write(3, value);
        }

        @Override
        public void setVelocityZ(int value) {
            handle.getIntegers().write(4, value);
        }

        @Override
        public void setHeadPitch(float value) {
            handle.getBytes().write(2, (byte) (value * 256.0F / 360.0F));
        }
    }
}
