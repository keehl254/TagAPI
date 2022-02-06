package com.lkeehl.tagapi.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class DevPacket extends AbstractPacket {
    /**
     * Constructs a new strongly typed wrapper for the given packet.
     *
     * @param handle - handle to the raw packet data.
     */
    public DevPacket(PacketContainer handle) {
        super(handle);
    }
}
