package com.lkeehl.tagapi.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

public class TagUtil {

    public static Stream<Player> getViewers(Entity entity) {
        return entity.getWorld().getPlayers().stream().filter(p -> isViewer(entity, p));
    }

    public static boolean isViewer(Entity entity, Player player) {
        return isViewer(player, entity.getLocation().getX(), entity.getLocation().getZ());
    }

    public static boolean isViewer(Player player, double x, double z) {
        return Math.abs(player.getLocation().getX() - x) <= 48 && Math.abs(player.getLocation().getZ() - z) <= 48;
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
