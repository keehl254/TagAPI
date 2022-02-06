package com.lkeehl.tagapi.api;

import com.lkeehl.tagapi.tags.BaseTag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Tag {

    /**
     * Creates a new tag for a provided entity.
     *
     * @param target The entity that the tag should belong to.
     * @return A new Tag that can be edited.
     */
    public static Tag create(Entity target) {
        if (target == null)
            return null;
        return BaseTag.create(target);
    }

    /**
     * Adds a new line to the tag with a given priority towards the top.
     *
     * @param importance The priority of the line towards the top.
     */
    public abstract TagLine addTagLine(int importance);

    /**
     * Provides the entity that the tag is focused on.
     *
     * @return The entity that the tag is stuck to.
     */
    public abstract Entity getTarget();

    /**
     * Provides a collection of tag lines associated with this tag.
     *
     * @return This tags lines.
     */
    public abstract List<TagLine> getTagLines();

    /**
     * Returns the bottom-most TagLine. This is often a dummy line to adjust the
     * starting point of the developer-provided lines.
     *
     * @return The bottom-most TagLine.
     */
    public abstract TagLine getBottomTagLine();

    /**
     * Returns the top-most TagLine.
     *
     * @return The top-most TagLine.
     */
    public abstract TagLine getTopTagLine();

    /**
     * Updates the view of the tag for a provided player.
     *
     * @param viewer The player which the tag should update for.
     */
    public abstract void updateTagFor(Player viewer);

    /**
     * Spawns and sets the target entities tag to this.
     */
    public abstract void giveTag();

    /**
     * Removes this tag from the target entity.
     */
    public abstract void removeTag();

    /**
     * Updates the tag for all players who can view it.
     */
    public abstract void updateTag();

}
