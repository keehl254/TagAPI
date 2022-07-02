package com.lkeehl.tagapi.manager;

import com.lkeehl.tagapi.TagAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class TagManager extends JavaPlugin {

    @Override()
    public void onEnable() {
        TagAPI.onEnable(this);
        TagAPI.listenForMovement();
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
