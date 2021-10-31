package keehl.tagapi.manager;

import keehl.tagapi.TagAPI;
import keehl.tagapi.TagBuilder;
import keehl.tagapi.instances.TestTag;
import org.bukkit.Bukkit;
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
