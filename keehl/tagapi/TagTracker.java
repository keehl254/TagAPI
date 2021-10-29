package keehl.tagapi;

import keehl.tagapi.tags.Tag;
import keehl.tagapi.tags.TagEntity;
import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TagTracker {

    private final Map<Integer, TagEntity> tagEntities = new HashMap<>();
    private final Map<Integer, Tag> entityTags = new HashMap<>();

    public void trackEntity(TagEntity entity) {
        this.tagEntities.put(entity.getEntityID(), entity);
    }

    public void setEntityTag(Integer entityID, Tag tag) {
        if (this.entityTags.containsKey(entityID)) {
            Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
            this.entityTags.get(entityID).destroy(wrapper);
            wrapper.broadcastPacket();
        }
        this.entityTags.put(entityID, tag);
    }

    public Tag getEntityTag(Integer entityID) {
        return this.entityTags.getOrDefault(entityID, null);
    }

    public TagEntity getTagEntity(int entityID) {
        return this.tagEntities.getOrDefault(entityID,null);
    }

    public boolean isTagEntity(int entityID) {
        return this.tagEntities.containsKey(entityID);
    }

    public void deleteTag(Tag tag) {
        entityTags.keySet().removeIf(uuid -> entityTags.get(uuid) == tag);
    }

    public void destroyAll() {
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.entityTags.values().forEach(tag -> tag.destroy(wrapper));
        this.entityTags.clear();
        this.tagEntities.clear();

        wrapper.broadcastPacket();
    }

    public void destroyAll(Player viewer) {
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.entityTags.values().forEach(tag -> tag.destroy(wrapper));
        this.entityTags.clear();
        this.tagEntities.clear();

        wrapper.sendPacket(viewer);
    }

    public void unregisterViewer(Player viewer) {
        for(Tag tag : this.entityTags.values())
            tag.unregisterViewer(viewer);
    }

}
