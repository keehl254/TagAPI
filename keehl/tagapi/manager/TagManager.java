package keehl.tagapi.manager;

import keehl.tagapi.TagAPI;
import keehl.tagapi.instances.TestTag;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

public class TagManager extends JavaPlugin {

    @Override()
    public void onEnable() {
        TagAPI.onEnable(this);
        TagAPI.setDefaultTag(EntityType.PIG, TestTag::new);
    }

    @Override()
    public void onDisable() {
        TagAPI.onDisable();
    }

}
