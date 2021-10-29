package keehl.tagapi.wrappers.v1171;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import keehl.tagapi.wrappers.Wrappers;

import java.util.List;

public class Destroy1171Wrapper extends Wrappers.DestroyPacket {

    public Destroy1171Wrapper() {
        super();
    }

    public Destroy1171Wrapper(PacketContainer packet) {
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
