package keehl.tagapi.api;

import keehl.tagapi.tags.BaseTag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Tag {

    public static Tag create(Entity target) {
        if (target == null)
            return null;
        return BaseTag.create(target);
    }

    public abstract TagLine addTagLine(int importance);

    public abstract Entity getTarget();

    public abstract List<TagLine> getTagLines();

    public abstract void updateTagFor(Player viewer);

    public abstract void giveTag();

    public abstract void removeTag();

    public abstract void updateTag();

}
