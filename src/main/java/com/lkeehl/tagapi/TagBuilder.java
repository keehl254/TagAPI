package com.lkeehl.tagapi;

import com.lkeehl.tagapi.api.Tag;
import com.lkeehl.tagapi.tags.BaseTag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class TagBuilder {

    private final Tag tag;
    private int priority = Integer.MAX_VALUE;

    private TagBuilder(Entity entity) {
        this.tag = BaseTag.create(entity);
    }

    /**
     * Creates a line with a provided Player -> String line function.
     *
     * @param textFunction A Function that determines what the line should say to the provided player.
     * @return This tag builder.
     */
    public TagBuilder withLine(Function<Player, String> textFunction) {
        this.tag.addTagLine(priority--).setText(textFunction);
        return this;
    }

    /**
     * Creates a line with a provided Player -> String line function as well as a Player -> Boolean for null space checks.
     *
     * @param textFunction A Function that determines what the line should say to the provided player.
     * @param keepSpaceWhenNull A Function that determines whether the line should be visible if the line is null to the provided player.
     * @return This tag builder.
     */
    public TagBuilder withLine(Function<Player, String> textFunction, Function<Player, Boolean> keepSpaceWhenNull) {
        this.tag.addTagLine(priority--).setText(textFunction).setKeepSpaceWhenNull(keepSpaceWhenNull);
        return this;
    }

    /**
     * Creates a Tag out of this TagBuilder.
     *
     * @return A tag instance using the data from this Builder.
     */
    public Tag build() {
        return this.tag;
    }

    /**
     * Creates a TagBuilder from a provided entity.
     *
     * @param entity The entity that this tag will belong to.
     * @return A tag builder used to create a tag for the provided entity.
     */
    public static TagBuilder create(Entity entity) {
        assert entity != null;
        return new TagBuilder(entity);
    }

}
