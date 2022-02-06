package com.lkeehl.tagapi.tags;

import com.lkeehl.tagapi.TagAPI;
import com.lkeehl.tagapi.api.Tag;
import com.lkeehl.tagapi.api.TagLine;
import com.lkeehl.tagapi.wrappers.AbstractPacket;
import com.lkeehl.tagapi.wrappers.Wrappers;
import com.lkeehl.tagapi.util.TagUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class BaseTag extends Tag {

    public static Tag create(Entity target) {
        return new BaseTag(target);
    }

    private final List<TagLine> tagLines = new ArrayList<>();

    private final Map<Integer, Integer> playerVisionCache = new HashMap<>();

    private final Entity target;

    private BaseTag(Entity target) {
        this.target = target;

        this.addTagLine(new BaseTagLine(Integer.MIN_VALUE, this, true));
        ((BaseTagLine) this.tagLines.get(0)).setInBody();
    }

    private void addTagLine(BaseTagLine line) {
        this.tagLines.add(line);
        this.tagLines.sort(Comparator.comparingInt(TagLine::getImportance));
    }

    public TagLine addTagLine(int importance) {
        BaseTagLine tagLine = new BaseTagLine(importance, this);
        this.addTagLine(tagLine);
        return tagLine;
    }

    public Entity getTarget() {
        return this.target;
    }

    public List<TagLine> getTagLines() {
        return this.tagLines;
    }

    public TagLine getBottomTagLine() {
        return this.tagLines.get(0);
    }

    public TagLine getTopTagLine() {
        return this.tagLines.get(this.tagLines.size() - 1);
    }

    private boolean isTargetVisible() {
        return !(this.target instanceof LivingEntity e) || !e.isInvisible();
    }

    private boolean isTargetSneaking() {
        return this.target instanceof Player e && e.isSneaking();
    }

    public void spawnTagFor(Player viewer) {
        this.spawnTagFor(viewer, this.isTargetVisible(), this.isTargetSneaking());
    }

    public void spawnTagFor(Player viewer, boolean showName, boolean transparentName) {

        List<AbstractPacket> spawnPackets = new ArrayList<>();
        List<AbstractPacket> mountPackets = new ArrayList<>();
        Wrappers.DestroyPacket destroyWrapper = Wrappers.DESTROY.get();
        BaseTagLine lastLine = null;
        int currentVision = this.playerVisionCache.getOrDefault(viewer.getEntityId(), 0);
        int vision = 0;
        for (int i = 0; i < (viewer == this.target ? 1 : this.tagLines.size()); i++) {
            BaseTagLine line = (BaseTagLine) this.tagLines.get(i);
            if (line.shouldHideFrom(viewer)) {
                if (((currentVision >> i) & 1) == 1)
                    line.destroy(destroyWrapper);
                continue;
            }
            Location location = this.target.getLocation().clone();
            spawnPackets.addAll(line.getSpawnPackets(viewer, location, ((currentVision >> i) & 1) == 0, showName, transparentName));
            mountPackets.addAll(line.getMountPackets(viewer, lastLine));
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
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.destroy(wrapper);
        wrapper.sendPacket(viewer);
        this.playerVisionCache.remove(viewer.getEntityId());
    }

    public void unregisterViewer(Player viewer) {
        this.playerVisionCache.remove(viewer.getEntityId());
    }

    public void updateTagFor(Player viewer) {
        this.updateTagFor(viewer, this.isTargetVisible(), this.isTargetSneaking());
    }

    public void updateTagFor(Player viewer, boolean showName, boolean transparentName) {
        int tempVision = 0;
        for (int i = 0; i < (viewer == this.target ? 1 : this.tagLines.size()); i++) {
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
                metaPackets.addAll(((BaseTagLine) line).getMetaPackets(viewer, showName, transparentName));
            }

            metaPackets.forEach(p -> p.sendPacket(viewer));
        }
    }

    public void giveTag() {
        BaseTag oldTag = (BaseTag) TagAPI.getTag(this.target);
        TagAPI.getTagTracker().setEntityTag(this.target.getEntityId(), this);
        if (oldTag != null)
            oldTag.getTagLines().stream().map(i -> (BaseTagLine) i).forEach(BaseTagLine::stopTrackingEntities);
        this.getTagLines().stream().map(i -> (BaseTagLine) i).forEach(BaseTagLine::trackEntities);
        TagUtil.getViewers(this.target).forEach(this::spawnTagFor);
        if (this.target instanceof Player)
            this.spawnTagFor((Player) this.target);
    }

    public void removeTag() {
        TagAPI.getTagTracker().setEntityTag(this.target.getEntityId(), null);
        Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> this.getTagLines().stream().map(i -> (BaseTagLine) i).forEach(BaseTagLine::stopTrackingEntities), 1L);
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
            ((BaseTagLine) line).destroy(wrapper);
    }


}
