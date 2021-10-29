package keehl.tagapi.util;

import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class VersionFile {

    private NBTTagCompound mainTag = null;
    private NBTTagCompound versionTag = null;

    private File file;

    public VersionFile(JavaPlugin plugin) {
        File directory = plugin.getDataFolder();
        if (!directory.exists())
            directory.mkdirs();
        this.file = new File(directory, "versions.cult");
        if(!this.file.exists()) {
            InputStream stream = getClass().getResourceAsStream("versions.cult");
            try {
                if (stream != null)
                    Files.copy(stream,this.file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!this.file.exists()) {
            this.mainTag = new NBTTagCompound();
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                try {
                    this.mainTag = NBTCompressedStreamTools.a(new GZIPInputStream(new FileInputStream(this.file)));
                } catch (Exception compressException) {
                    this.mainTag = NBTCompressedStreamTools.a(new FileInputStream(this.file));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.mainTag == null)
                this.mainTag = new NBTTagCompound();
        }

        String version = Bukkit.getBukkitVersion();
        version = version.substring(0, version.indexOf("-"));

        if (!this.mainTag.hasKey(version)) {
            int[] semantic = Arrays.stream(version.split(".")).mapToInt(Integer::parseInt).toArray();
            List<String> versions = new ArrayList<>();
            for (String key : this.mainTag.getKeys()) {
                int major = Integer.parseInt(key.split(".")[0]);
                int minor = Integer.parseInt(key.split(".")[1]);
                int patch = Integer.parseInt(key.split(".")[2]);
                if (major > semantic[0])
                    continue;
                if (major == semantic[0] && minor > semantic[1])
                    continue;
                if (major == semantic[0] && minor == semantic[1] && patch > semantic[2])
                    continue;
                versions.add(key);
            }
            List<String> set = new ArrayList<>(versions);
            set.removeIf(i -> Integer.parseInt(i.split(".")[0]) != semantic[0]);
            if (set.isEmpty())
                set = new ArrayList<>(versions);
            if (set.isEmpty()) {
                this.versionTag = this.mainTag.getCompound(new ArrayList<>(this.mainTag.getKeys()).get(0));
            } else {
                Collections.sort(versions);
                this.versionTag = this.mainTag.getCompound(versions.get(versions.size() - 1));
            }
        } else
            this.versionTag = this.mainTag.getCompound(version);

    }

    public NBTTagCompound getVersionTag() {
        return this.versionTag;
    }

    public int getDataWatcherIndex(EntityType entity, WatcherType watcher) {
        if (!this.versionTag.hasKey(entity.toString()))
            return -1;
        NBTTagCompound entityCompound = this.versionTag.getCompound(entity.toString());
        if (!entityCompound.hasKey(watcher.toString()))
            return -1;
        return entityCompound.getInt(watcher.toString());
    }

    public enum TagType {
        END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY;
    }

}
