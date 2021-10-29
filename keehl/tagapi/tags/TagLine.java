package keehl.tagapi.tags;

import keehl.tagapi.wrappers.AbstractPacket;
import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TagLine {

    private Tag tag;

    private final TagEntity bottomEntity;
    private final TagEntity topEntity;

    private final Map<UUID, Boolean> visibilityMap = new HashMap<>();

    private Function<Player, String> getName;
    private Function<Player, Boolean> keepSpaceWhenNull;

    private final int importance;

    private boolean isInBody;

    public TagLine(int importance) {
        this(importance, false);
    }

    protected TagLine(int importance, boolean removeFish) {
        this.importance = importance;

        TagEntity tempEntity;
        if (removeFish) {
            tempEntity = bottomEntity = new TagEntity(this, null, EntityType.TROPICAL_FISH);
            tempEntity = new TagEntity(this, tempEntity, EntityType.SLIME);
            tempEntity = new TagEntity(this, tempEntity, EntityType.TROPICAL_FISH);
            tempEntity = new TagEntity(this, tempEntity, EntityType.TURTLE);
        } else {
            bottomEntity = new TagEntity(this, null, EntityType.SILVERFISH);
            tempEntity = new TagEntity(this, bottomEntity, EntityType.SILVERFISH);
        }

        tempEntity = new TagEntity(this, tempEntity, EntityType.SLIME);
        topEntity = new TagEntity(this, tempEntity, EntityType.ARMOR_STAND,true);

        getName = (x) -> null;
        keepSpaceWhenNull = (x) -> true;
    }

    protected void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setInBody() {
        this.isInBody = true;
    }

    public void setGetName(Function<Player, String> getName) {
        this.getName = getName;
    }

    public void setKeepSpaceWhenNull(Function<Player, Boolean> keepSpaceWhenNull) {
        this.keepSpaceWhenNull = keepSpaceWhenNull;
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

    public TagEntity getTopEntity() {
        return this.topEntity;
    }

    public Tag getTag() {
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

    public List<AbstractPacket> getMountPackets(TagLine parent) {
        List<AbstractPacket> packets = new ArrayList<>();
        this.bottomEntity.getMountPackets(packets, parent == null ? this.tag.getTarget().getEntityId() : parent.getTopEntity().getEntityID());
        Collections.reverse(packets);
        return packets;
    }

    public String getNameFor(Player viewer) {
        return this.getName.apply(viewer);
    }


}
