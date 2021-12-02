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
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
