package com.lkeehl.tagapi.manager;

import com.lkeehl.tagapi.TagAPI;
import com.lkeehl.tagapi.TagBuilder;
import com.lkeehl.tagapi.api.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.plugin.java.JavaPlugin;

public class TagManager extends JavaPlugin {

    @Override()
    public void onEnable() {
        TagAPI.onEnable(this);

        TagAPI.setDefaultTag(EntityType.PLAYER, pl -> {
            Tag tag = Tag.create(pl);
            tag.addTagLine(9).setGetName(HumanEntity::getName);
            tag.addTagLine(8).setGetName(i -> i.getEntityId() + "");
            return tag;
        });
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
