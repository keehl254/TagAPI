package keehl.tagapi;

import keehl.tagapi.api.Tag;
import keehl.tagapi.api.TagEntity;
import keehl.tagapi.tags.BaseTag;
import keehl.tagapi.tags.BaseTagEntity;
import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TagTracker {

    private final Map<Integer, TagEntity> tagEntities = new HashMap<>();
    private final Map<Integer, Tag> entityTags = new HashMap<>();

    public void trackEntity(BaseTagEntity entity) {
        this.tagEntities.put(entity.getEntityID(), entity);
    }

    public void stopTrackingEntity(BaseTagEntity entity) {
        this.tagEntities.remove(entity.getEntityID());
    }

    public void setEntityTag(Integer entityID, BaseTag tag) {
        if (this.entityTags.containsKey(entityID) || tag == null) {
            Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
            ((BaseTag) this.entityTags.get(entityID)).destroy(wrapper);
            wrapper.broadcastPacket();
        }
        if (tag != null)
            this.entityTags.put(entityID, tag);
        else
            this.entityTags.remove(entityID);
    }

    public Tag getEntityTag(Integer entityID) {
        return this.entityTags.getOrDefault(entityID, null);
    }

    public TagEntity getTagEntity(int entityID) {
        return this.tagEntities.getOrDefault(entityID, null);
    }

    public boolean isTagEntity(int entityID) {
        return this.tagEntities.containsKey(entityID);
    }

    public void deleteTag(Tag tag) {
        entityTags.keySet().removeIf(uuid -> entityTags.get(uuid) == tag);
    }

    public void destroyAll() {
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.entityTags.values().forEach(tag -> ((BaseTag) tag).destroy(wrapper));
        this.entityTags.clear();
        this.tagEntities.clear();

        wrapper.broadcastPacket();
    }

    public void destroyAll(Player viewer) {
        Wrappers.DestroyPacket wrapper = Wrappers.DESTROY.get();
        this.entityTags.values().forEach(tag -> ((BaseTag) tag).destroy(wrapper));

        wrapper.sendPacket(viewer);
    }

    public void unregisterViewer(Player viewer) {
        for (Tag tag : this.entityTags.values())
            ((BaseTag) tag).unregisterViewer(viewer);
    }

}
