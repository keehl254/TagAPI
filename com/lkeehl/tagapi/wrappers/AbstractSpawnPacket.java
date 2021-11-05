package com.lkeehl.tagapi.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public abstract class AbstractSpawnPacket extends AbstractPacket{

    protected AbstractSpawnPacket(PacketType type) {
        super(new PacketContainer(type), type);
        handle.getModifier().writeDefaults();
    }

    protected AbstractSpawnPacket(PacketContainer container, PacketType type) {
        super(container, type);
    }

    public abstract void setID(int entityID);

    public abstract void setUUID(UUID uuid);

    public abstract void setType(EntityType type);

    public abstract void setLocation(Location location);

    public abstract void setYaw(float value);

    public abstract void setPitch(float value);

    public abstract void setVelocityX(int value);

    public abstract void setVelocityY(int value);

    public abstract void setVelocityZ(int value);

}
