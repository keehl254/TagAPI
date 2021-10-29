package keehl.tagapi.wrappers.v1171;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import keehl.tagapi.wrappers.Wrappers;

import java.util.List;

public class MetaData1171Wrapper extends Wrappers.MetaDataPacket {

    public MetaData1171Wrapper() {
        super();
    }

    public MetaData1171Wrapper(PacketContainer packet) {
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
