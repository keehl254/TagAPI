package keehl.tagapi.wrappers.v1171;

import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class SpawnEntity1171Wrapper extends Wrappers.SpawnEntityPacket {

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
