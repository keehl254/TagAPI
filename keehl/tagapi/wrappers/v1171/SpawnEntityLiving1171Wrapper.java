package keehl.tagapi.wrappers.v1171;

import keehl.tagapi.wrappers.Wrappers;
import net.minecraft.core.IRegistry;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class SpawnEntityLiving1171Wrapper extends Wrappers.SpawnEntityLivingPacket {

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
        handle.getIntegers().write(1, IRegistry.Y.getId(IRegistry.Y.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
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
