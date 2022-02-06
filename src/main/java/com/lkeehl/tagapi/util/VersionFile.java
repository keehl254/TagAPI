package com.lkeehl.tagapi.util;

import com.lkeehl.tagapi.TagAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/*
    The system I use for reading NBT is heavily influenced by code from Querz.
    This version has been heavily modified to read only. On top of that, it only
    reads integers and compounds as that is all I personally needed. Check out their
    full resource here: https://github.com/Querz/NBT

    Yes, MC has a built-in NBT system; however, the package and methods change with NMS
    every-so-often like from 1.17 to 1.18. I would rather not rely on NMS for an API.
 */

@SuppressWarnings("unchecked")
public class VersionFile {

    private final Tag<Map<String, Tag<?>>> versionTag;

    public VersionFile() {
        InputStream fileStream = getClass().getResourceAsStream("/versions.cult");
        assert fileStream != null;

        Tag<Map<String, Tag<?>>> mainTag = null;
        Throwable var3 = null;
        try {
            DataInputStream stream = new DataInputStream(new GZIPInputStream(fileStream));

            byte id = stream.readByte();
            stream.readUTF();
            mainTag = (Tag<Map<String, Tag<?>>>) readTag(id, 512, stream);
        } catch (Exception var13) {
            var3 = var13;
        } finally {
            if (var3 != null) {
                try {
                    fileStream.close();
                } catch (Throwable var12) {
                    var3.addSuppressed(var12);
                }
            } else {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (var3 != null)
            var3.printStackTrace();
        if (mainTag == null)
            mainTag = new Tag<>(new HashMap<>(8));

        Function<String, Short> parse = (version) -> {
            String[] split = version.split("\\.");
            return (short) ((Integer.parseInt(split[0]) << 10) | ((Integer.parseInt(split[1]) << 5) | (split.length == 3 ? Integer.parseInt(split[2]) : 0)));
        };

        short semantic = parse.apply(Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")));
        Comparator<Short> comp = (x, y) -> (x > semantic || y > semantic) ? ((x > semantic && y > semantic) ? Short.compare(x, y) : (x > semantic ? 1 : -1)) : -Short.compare(x, y);
        TreeSet<Short> versions = new TreeSet<>(comp);
        versions.addAll(mainTag.getValue().keySet().stream().map(parse).collect(Collectors.toList()));
        Tag<Map<String, Tag<?>>> tempTag = (Tag<Map<String, Tag<?>>>) mainTag.getValue().get(String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)));
        if (tempTag.getValue().isEmpty()) {
            versions.remove(versions.first());
            while (!versions.isEmpty()) {
                if ((tempTag = (Tag<Map<String, Tag<?>>>) mainTag.getValue().get(String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)))).getValue().isEmpty())
                    versions.remove(versions.first());
                else
                    break;
            }
            if (!versions.isEmpty())
                TagAPI.getPlugin().getLogger().info("TagAPI is using entity metadata from " + String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)) + " for this version of Minecraft.");
            else {
                tempTag = new Tag<>(new HashMap<>(8));
                TagAPI.getPlugin().getLogger().warning("TagAPI is was unable to find a suitable entity metadata set for this Minecraft version! Tags entities may not appear properly! An update might be available for the \"" + TagAPI.getPlugin().getName() + "\" plugin.");
            }
        } else {
            if (semantic != versions.first())
                TagAPI.getPlugin().getLogger().warning("TagAPI is using entity metadata from " + String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)) + " for this version of Minecraft. If issues are present, an update might be required for the \"" + TagAPI.getPlugin().getName() + "\" plugin.");
        }
        this.versionTag = tempTag;
    }

    private Tag<?> readTag(byte type, int maxDepth, DataInputStream stream) throws IOException {
        if (type == 3)
            return new Tag<>(stream.readInt());
        else if (type == 10) {
            Tag<Map<String, Tag<?>>> comp = new Tag<>(new HashMap<>(8));
            for (int id = stream.readByte() & 0xFF; id != 0; id = stream.readByte() & 0xFF) {
                String key = stream.readUTF();
                if (maxDepth < 0)
                    throw new IllegalArgumentException("negative maximum depth is not allowed");
                else if (maxDepth == 0)
                    throw new RuntimeException("reached maximum depth of NBT structure");
                Tag<?> element = this.readTag((byte) id, --maxDepth, stream);
                comp.getValue().put(Objects.requireNonNull(key), Objects.requireNonNull(element));
            }
            return comp;
        } else
            throw new IOException("invalid tag id \"" + type + "\"");
    }

    public int getDataWatcherIndex(EntityType entity, WatcherType watcher) {
        if (!this.versionTag.getValue().containsKey(entity.toString()))
            return -1;
        Tag<Map<String, Tag<?>>> entityCompound = (Tag<Map<String, Tag<?>>>) this.versionTag.getValue().get(entity.toString());
        if (!entityCompound.getValue().containsKey(watcher.toString()))
            return -1;
        return (int) entityCompound.getValue().get(watcher.toString()).getValue();
    }

    public int getEntityID(EntityType entity) {
        if (!this.versionTag.getValue().containsKey(entity.toString()))
            return -1;
        Tag<Map<String, Tag<?>>> entityCompound = (Tag<Map<String, Tag<?>>>) this.versionTag.getValue().get(entity.toString());
        if (!entityCompound.getValue().containsKey("ENTITY-ID"))
            return -1;
        return (int) entityCompound.getValue().get("ENTITY-ID").getValue();
    }

    public record Tag<T>(T value) {

        public Tag(T value) {
            this.value = Objects.requireNonNull(value);
        }

        private T getValue() {
            return value;
        }

    }

}
