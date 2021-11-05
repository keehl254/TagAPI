package com.lkeehl.tagapi.tags;

import com.lkeehl.tagapi.api.TagEntity;
import com.lkeehl.tagapi.api.TagLine;
import com.lkeehl.tagapi.wrappers.AbstractPacket;
import com.lkeehl.tagapi.wrappers.Wrappers;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Function;

public class BaseTagLine implements TagLine {

    private final BaseTag tag;

    private final BaseTagEntity bottomEntity;
    private final BaseTagEntity topEntity;

    private final Map<UUID, Boolean> visibilityMap = new HashMap<>();

    private Function<Player, String> getName;
    private Function<Player, Boolean> keepSpaceWhenNull;

    private final int importance;

    private boolean isInBody;

    protected BaseTagLine(int importance, BaseTag tag) {
        this(importance, tag, false);
    }

    protected BaseTagLine(int importance, BaseTag tag, boolean removeFish) {
        this.importance = importance;
        this.tag = tag;

        BaseTagEntity tempEntity;
        if (removeFish) {
            tempEntity = bottomEntity = new BaseTagEntity(this, null, EntityType.TROPICAL_FISH);
            tempEntity = new BaseTagEntity(this, tempEntity, EntityType.SLIME);
            tempEntity = new BaseTagEntity(this, tempEntity, EntityType.TROPICAL_FISH);
            tempEntity = new BaseTagEntity(this, tempEntity, EntityType.TURTLE);
        } else {
            bottomEntity = new BaseTagEntity(this, null, EntityType.SILVERFISH);
            tempEntity = new BaseTagEntity(this, bottomEntity, EntityType.SILVERFISH);
        }

        tempEntity = new BaseTagEntity(this, tempEntity, EntityType.SLIME);
        topEntity = new BaseTagEntity(this, tempEntity, EntityType.ARMOR_STAND, true);

        getName = (x) -> null;
        keepSpaceWhenNull = (x) -> true;
    }

    protected void setInBody() {
        this.isInBody = true;
    }

    protected void trackEntities() {
        this.bottomEntity.trackLine();
    }

    protected void stopTrackingEntities() {
        this.bottomEntity.stopTrackingLine();
    }

    public BaseTagLine setGetName(Function<Player, String> getName) {
        this.getName = getName;
        return this;
    }

    public BaseTagLine setKeepSpaceWhenNull(Function<Player, Boolean> keepSpaceWhenNull) {
        this.keepSpaceWhenNull = keepSpaceWhenNull;
        return this;
    }

    private boolean isVisibleTo(Player player) {
        return this.visibilityMap.getOrDefault(player.getUniqueId(), true);
    }

    public void setVisibilityFor(Player viewer, boolean visible) {
        if (!visible)
            this.visibilityMap.put(viewer.getUniqueId(), false);

        this.tag.destroyTagFor(viewer);
        this.tag.spawnTagFor(viewer);
    }

    public void destroy(Wrappers.DestroyPacket wrapper) {
        this.bottomEntity.destroy(wrapper);
    }

    public int getImportance() {
        return this.importance;
    }

    public BaseTagEntity getTopEntity() {
        return this.topEntity;
    }

    public BaseTag getTag() {
        return this.tag;
    }

    public boolean shouldHideFrom(Player viewer) {
        String name = getName.apply(viewer);
        return !this.isVisibleTo(viewer) || (name == null && !keepSpaceWhenNull.apply(viewer));
    }

    public boolean isInBody() {
        return this.isInBody;
    }

    public List<AbstractPacket> getSpawnPackets(Player viewer, Location location, boolean spawnNew) {
        List<AbstractPacket> packets = new ArrayList<>();
        this.bottomEntity.getSpawnPackets(viewer, packets, location, spawnNew);
        Collections.reverse(packets);
        return packets;
    }

    public List<AbstractPacket> getMetaPackets(Player viewer) {
        List<AbstractPacket> packets = new ArrayList<>();
        this.bottomEntity.getMetaPackets(viewer, packets);
        Collections.reverse(packets);
        return packets;
    }

    public List<AbstractPacket> getMountPackets(BaseTagLine parent) {
        List<AbstractPacket> packets = new ArrayList<>();
        this.bottomEntity.getMountPackets(packets, parent == null ? this.tag.getTarget().getEntityId() : parent.getTopEntity().getEntityID());
        Collections.reverse(packets);
        return packets;
    }

    public List<TagEntity> getTagEntities() {
        List<TagEntity> entities = new ArrayList<>();
        TagEntity entity = this.bottomEntity;
        while (entity != null) {
            entities.add(entity);
            entity = entity.getChild();
        }
        return entities;
    }

    public String getNameFor(Player viewer) {
        return this.getName.apply(viewer);
    }


}
