package keehl.tagapi;

import keehl.tagapi.tags.Tag;
import keehl.tagapi.tags.TagLine;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class TagBuilder {

    private final Tag tag;
    private int priority = Integer.MAX_VALUE;

    private TagBuilder(Entity entity) {
        this.tag = new Tag(entity);
    }

    public TagBuilder withLine(Function<Player, String> getName) {
        this.tag.addTagLine(new TagLine(priority--).setGetName(getName));
        return this;
    }

    public TagBuilder withLine(Function<Player, String> getName, Function<Player,Boolean> keepSpaceWhenNull) {
        this.tag.addTagLine(new TagLine(priority--).setGetName(getName).setKeepSpaceWhenNull(keepSpaceWhenNull));
        return this;
    }

    public Tag build() {
        return this.tag;
    }

    public static TagBuilder create(Entity entity) {
        assert entity != null;
        return new TagBuilder(entity);
    }

}
