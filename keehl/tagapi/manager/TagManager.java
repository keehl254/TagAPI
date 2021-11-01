package keehl.tagapi.manager;

import keehl.tagapi.TagAPI;
import keehl.tagapi.TagBuilder;
import keehl.tagapi.api.Tag;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TagManager extends JavaPlugin {

    @Override()
    public void onEnable() {
        TagAPI.onEnable(this);

        TagAPI.setDefaultTag(EntityType.PIG, target ->
                TagBuilder.create(target).withLine(pl -> target.getName()).withLine(pl -> ChatColor.YELLOW + "Hello " + pl.getName() + "!").build()
        );
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
