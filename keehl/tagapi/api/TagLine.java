package keehl.tagapi.api;

import keehl.tagapi.tags.BaseTag;
import keehl.tagapi.tags.BaseTagLine;
import org.bukkit.entity.Player;

import java.util.function.Function;

public interface TagLine {

    TagLine setGetName(Function<Player, String> getName);

    TagLine setKeepSpaceWhenNull(Function<Player, Boolean> keepSpaceWhenNull);

    void setVisibilityFor(Player viewer, boolean visible);

    int getImportance();

    BaseTag getTag();

    boolean shouldHideFrom(Player viewer);

    boolean isInBody();

    String getNameFor(Player viewer);

}
