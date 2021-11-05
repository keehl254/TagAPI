package com.lkeehl.tagapi.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class TagUtil {

    public static Stream<Player> getViewers(Entity entity) {
        return entity.getWorld().getPlayers().stream().filter(p ->
                Math.abs(p.getLocation().getX() - entity.getLocation().getX()) <= 48
                        && Math.abs(p.getLocation().getZ() - entity.getLocation().getZ()) <= 48
        );
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
