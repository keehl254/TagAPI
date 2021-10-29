package keehl.tagapi.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class TagUtil {

    private static boolean isNear(double max, Location l1, Location l2) {
        return !(Math.abs(l1.getX() - l2.getX()) > max) && !(Math.abs(l1.getZ() - l2.getZ()) > max);
    }

    public static Stream<Player> getViewers(Entity entity, double err) {
        double finalVal = 48 * err;
        return entity.getWorld().getPlayers().stream().filter(p -> isNear(finalVal, p.getLocation(), entity.getLocation()));
    }

    public static <H> void applyData(WrappedDataWatcher watcher, int index, Class<H> clazz, H value) {
        WrappedDataWatcher.WrappedDataWatcherObject object = new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(clazz));
        watcher.setObject(object, value);
    }

    public static <H> void applyData(WrappedDataWatcher watcher, int index, WrappedDataWatcher.Serializer serializer, H value) {
        WrappedDataWatcher.WrappedDataWatcherObject object = new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer);
        watcher.setObject(object, value);
    }

}
