package com.lkeehl.tagapi.api;

import com.lkeehl.tagapi.tags.BaseTag;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface TagLine {

    /**
     * Provides a Function that determines what the line should say to a given player.
     *
     * @return This TagLine so that this class can be used in a builder format.
     */
    TagLine setGetName(Function<Player, String> getName);

    /**
     * Provides a Function that determines whether this line should still be visible to a given player even if the line is null.
     *
     * @return This TagLine so that this class can be used in a builder format.
     */
    TagLine setKeepSpaceWhenNull(Function<Player, Boolean> keepSpaceWhenNull);

    /**
     * Allows you to control the visibility of specific tag lines for individual players.
     *
     * @param viewer The player whose visibility is being altered.
     * @param visible Whether the line should be visible for the provided player.
     */
    void setVisibilityFor(Player viewer, boolean visible);

    /**
     * Returns the priority of this tag line towards the top.
     *
     * @return The priority of the line.
     */
    int getImportance();

    /**
     * Returns the Tag that this TagLine is associated with.
     *
     * @return The Tag this TagLine belongs to.
     */
    Tag getTag();

    /**
     * Returns whether this tag should not be visible to a provided player
     *
     * @param viewer The player that is being checked
     * @return A boolean representing if this line is invisible to a player.
     */
    boolean shouldHideFrom(Player viewer);

    /**
     * Returns whether the entity is within the body of the target entity.
     *
     * @return A boolean representing if this line is within the body of the target entity.
     */
    boolean isInBody();

    /**
     * Returns a line that will be visible to the provided player.
     *
     * @param viewer The player that is being checked.
     * @return The line that should show for the provided player.
     */
    String getNameFor(Player viewer);

}
