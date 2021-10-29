package keehl.tagapi.wrappers.v1171;

import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import keehl.tagapi.wrappers.Wrappers;

import java.util.List;

public class Mount1171Wrapper extends Wrappers.MountPacket {

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
