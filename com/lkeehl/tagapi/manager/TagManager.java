package com.lkeehl.tagapi.manager;

import com.lkeehl.tagapi.TagAPI;
import com.lkeehl.tagapi.TagBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class TagManager extends JavaPlugin {

    @Override()
    public void onEnable() {
        TagAPI.onEnable(this);

        TagAPI.setDefaultTag(EntityType.PIG,entity -> {
            TagBuilder builder = TagBuilder.create(entity); // Create a new TagBuilder
            builder.withLine(pl->"First Line").withLine(pl->"Second Line"); // Add a first and second line
            builder.withLine(pl->"Third Line", pl->false); // Add another line, because why not?
            return builder.build();
        });
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
