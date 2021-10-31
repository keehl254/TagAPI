package keehl.tagapi.tags;

import keehl.tagapi.TagAPI;
import keehl.tagapi.util.TagUtil;
import keehl.tagapi.wrappers.AbstractPacket;
import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Tag {

    private final List<TagLine> tagLines = new ArrayList<>();

    private final Map<Integer, Integer> playerVisionCache = new HashMap<>();

    private final Entity target;

    public Tag(Entity target) {
        this.target = target;

        this.addTagLine(new TagLine(Integer.MIN_VALUE, true));
        this.tagLines.get(0).setInBody();
    }

    public void addTagLine(TagLine tagLine) {
        tagLine.setTag(this);
        this.tagLines.add(tagLine);
        this.tagLines.sort(Comparator.comparingInt(TagLine::getImportance));
    }

    public Entity getTarget() {
        return this.target;
    }

    public List<TagLine> getTagLines() {
        return this.tagLines;
    }

    public void spawnTagFor(Player viewer) {
        if (viewer == this.target)
            return;

        List<AbstractPacket> spawnPackets = new ArrayList<>();
        List<AbstractPacket> mountPackets = new ArrayList<>();
        Wrappers.DestroyPacket destroyWrapper = Wrappers.DESTROY.get();
        TagLine lastLine = null;
        int currentVision = this.playerVisionCache.getOrDefault(viewer.getEntityId(), 0);
        int vision = 0;
        for (int i = 0; i < this.tagLines.size(); i++) {
            TagLine line = this.tagLines.get(i);
            if (line.shouldHideFrom(viewer)) {
                if (((currentVision >> i) & 1) == 1)
                    line.destroy(destroyWrapper);
                continue;
            }
            Location location = this.target.getLocation().clone();
            spawnPackets.addAll(line.getSpawnPackets(viewer, location, ((currentVision >> i) & 1) == 0));
            mountPackets.addAll(line.getMountPackets(lastLine));
            lastLine = line;
            vision = vision | (1 << i);
        }
        Collections.reverse(mountPackets);
        this.playerVisionCache.put(viewer.getEntityId(), vision);

        spawnPackets.forEach(p -> p.sendPacket(viewer));
        Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> mountPackets.forEach(p -> p.sendPacket(viewer)), 1);
        if (destroyWrapper.getCount() > 0)
            Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> destroyWrapper.sendPacket(viewer), 2);
    }

    public void destroyTagFor(Player viewer) {
        if (viewer == this.target)
            return;
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.destroy(wrapper);
        wrapper.sendPacket(viewer);
        this.playerVisionCache.remove(viewer.getEntityId());
    }

    public void unregisterViewer(Player viewer) {
        this.playerVisionCache.remove(viewer.getEntityId());
    }

    public void updateTagFor(Player viewer) {
        if (viewer == this.target)
            return;
        int tempVision = 0;
        for (int i = 0; i < this.tagLines.size(); i++) {
            TagLine line = this.tagLines.get(i);
            if (line.shouldHideFrom(viewer))
                continue;
            tempVision = tempVision | (1 << i);
        }
        if (tempVision != this.playerVisionCache.getOrDefault(viewer.getEntityId(), 0)) {
            this.spawnTagFor(viewer);
        } else {
            List<AbstractPacket> metaPackets = new ArrayList<>();
            for (TagLine line : tagLines) {
                if (line.shouldHideFrom(viewer))
                    continue;
                metaPackets.addAll(line.getMetaPackets(viewer));
            }

            metaPackets.forEach(p -> p.sendPacket(viewer));
        }
    }

    public void giveTag() {
        Tag oldTag = TagAPI.getTag(this.target);
        TagAPI.getTagTracker().setEntityTag(this.target.getEntityId(), this);
        if(oldTag != null)
            oldTag.getTagLines().forEach(TagLine::stopTrackingEntities);
        this.getTagLines().forEach(TagLine::trackEntities);
        TagUtil.getViewers(this.target, 1).forEach(this::spawnTagFor);
    }

    public void removeTag() {
        TagAPI.getTagTracker().setEntityTag(this.target.getEntityId(), null);
        Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> this.getTagLines().forEach(TagLine::stopTrackingEntities), 1L);
    }

    public void updateTag() {
        for (Player viewer : Bukkit.getOnlinePlayers())
            this.updateTagFor(viewer);
    }

    public void destroy(boolean delete) {
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.destroy(wrapper);
        wrapper.broadcastPacket();

        if (delete)
            TagAPI.getTagTracker().deleteTag(this);
    }

    public void destroy(Wrappers.DestroyPacket wrapper) {
        for (TagLine line : tagLines)
            line.destroy(wrapper);
    }


}
