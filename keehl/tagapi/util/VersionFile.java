package keehl.tagapi.util;

import keehl.tagapi.TagAPI;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class VersionFile {

    private NBTTagCompound versionTag;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public VersionFile(JavaPlugin plugin) {
        File directory = plugin.getDataFolder();
        if (!directory.exists())
            directory.mkdirs();
        File file = new File(directory, "versions.cult");
        if (!file.exists()) {
            InputStream stream = getClass().getResourceAsStream("versions.cult");
            try {
                if (stream != null)
                    Files.copy(stream, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NBTTagCompound mainTag = null;
        if (!file.exists()) {
            mainTag = new NBTTagCompound();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                try {
                    mainTag = NBTCompressedStreamTools.a(new GZIPInputStream(new FileInputStream(file)));
                } catch (Exception compressException) {
                    mainTag = NBTCompressedStreamTools.a(new FileInputStream(file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mainTag == null)
                mainTag = new NBTTagCompound();
        }

        Function<String, Short> parse = (version) -> {
            String[] split = version.split("\\.");
            return (short) ((Integer.parseInt(split[0]) << 10) | ((Integer.parseInt(split[1]) << 5) | Integer.parseInt(split[2])));
        };

        short semantic = parse.apply(Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")));
        Comparator<Short> comp = (x, y) -> (x > semantic || y > semantic) ? ((x > semantic && y > semantic) ? Short.compare(x, y) : (x > semantic ? 1 : -1)) : -Short.compare(x, y);
        TreeSet<Short> versions = new TreeSet<>(comp);
        versions.addAll(mainTag.getKeys().stream().map(parse).collect(Collectors.toList()));
        this.versionTag = mainTag.getCompound(String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)));

        if (semantic != versions.first())
            TagAPI.getPlugin().getLogger().warning("TagAPI is using entity metadata from " + String.format("%s.%s.%s", ((versions.first() >> 10) & 31), ((versions.first() >> 5) & 31), (versions.first() & 31)) + " for this version of Minecraft. If issues are present, please update the versions.cult file in the " + TagAPI.getPlugin().getName() + " plugin data folder.");

    }

    public int getDataWatcherIndex(EntityType entity, WatcherType watcher) {
        if (!this.versionTag.hasKey(entity.toString()))
            return -1;
        NBTTagCompound entityCompound = this.versionTag.getCompound(entity.toString());
        if (!entityCompound.hasKey(watcher.toString()))
            return -1;
        return entityCompound.getInt(watcher.toString());
    }

}
