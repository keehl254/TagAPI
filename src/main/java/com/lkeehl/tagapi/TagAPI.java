package com.lkeehl.tagapi;

import com.lkeehl.tagapi.api.Tag;
import com.lkeehl.tagapi.tags.BaseTagEntity;
import com.lkeehl.tagapi.util.VersionFile;
import com.lkeehl.tagapi.wrappers.versions.Wrapper1_17_1;
import com.lkeehl.tagapi.wrappers.versions.Wrapper1_18_1;
import com.lkeehl.tagapi.wrappers.versions.Wrapper1_19_1;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TagAPI {

    protected static JavaPlugin plugin;

    private static TagTracker tagTracker;
    private static TagListener listener;

    protected static Map<EntityType, Function<Entity, Tag>> entityDefaultTags = new HashMap<>();

    /**
     * Initiates the TagAPI
     *
     * @param javaPlugin A plugin is required to activate TagAPI so that registers and packet wrappers may work.
     */
    public static void onEnable(JavaPlugin javaPlugin) {
        TagAPI.plugin = javaPlugin;

        VersionFile versionFile = new VersionFile();
        BaseTagEntity.init(versionFile);

        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);

        if ("v1_17_R1".equals(version))
            Wrapper1_17_1.init(versionFile);
        else if (version.startsWith("v1_18"))
            Wrapper1_18_1.init(versionFile);
        else
            Wrapper1_19_1.init(versionFile);

        tagTracker = new TagTracker();
        Bukkit.getPluginManager().registerEvents(listener = new TagListener(), javaPlugin);
    }

    /**
     * Disables the listener and destroys all active tags
     */
    public static void onDisable() {
        listener.onDisable();
        HandlerList.unregisterAll(listener);
        try {
            tagTracker.destroyAll();
        } catch (IllegalPluginAccessException ignored) {
        }
    }

    /**
     * Returns the plugin used to activate TagAPI
     *
     * @return the plugin used to activate TagAPI
     */
    public static JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Returns a class used by TagAPI to track existing tags and their target entities.
     *
     * @return TagAPI's tag tracker instance
     */
    public static TagTracker getTagTracker() {
        return tagTracker;
    }

    /**
     * Returns whether or not an entity has a tag
     *
     * @param entity The entity that will be checked for a tag
     * @return a boolean depicting if an entity has a tag
     */
    public static boolean hasTag(Entity entity) {
        return tagTracker.getEntityTag(entity.getEntityId()) != null;
    }

    /**
     * Returns the tag, if any, that an entity has. Returns null if
     * the entity does not have a tag.
     *
     * @param entity The entity that will be checked for a tag
     * @return the tag belonging to the entity
     */
    public static Tag getTag(Entity entity) {
        return tagTracker.getEntityTag(entity.getEntityId());
    }

    /**
     * Adds or replaces the tag for the tags target
     *
     * @param entity         The entity that the tag should be applied to
     * @param tagConstructor The constructor for a tag to be added to an entity
     */
    public static void giveTag(Entity entity, Function<Entity, Tag> tagConstructor) {
        tagConstructor.apply(entity).giveTag();
    }

    /**
     * Removes the tag for a provided entity
     *
     * @param entity The entity that the tag should be provided for
     */
    public static void removeTag(Entity entity) {
        Tag tag = tagTracker.getEntityTag(entity.getEntityId());
        if (tag != null)
            tag.removeTag();
    }

    /**
     * Updates the tag of an entity for all players. This will
     * refresh lines for players, as well as remove/add any lines that
     * should or should not be visible for individual players.
     *
     * @param entity The entity whose tag will be updated
     */
    public static void updateTag(Entity entity) {
        Tag tag = getTag(entity);
        if (tag == null)
            return;
        tag.updateTag();
    }

    /**
     * Updates the tag of an entity for a specified player. This will
     * refresh lines for the player, as well as remove/add any lines that
     * should or should not be visible for the specified player.
     *
     * @param entity The entity whose tag will be updated
     * @param viewer The player that the tag should be updated for
     */
    public static void updateTagFor(Entity entity, Player viewer) {
        Tag tag = getTag(entity);
        if (tag == null)
            return;
        tag.updateTagFor(viewer);
    }

    /**
     * Adding a default tag for an entity type will provide all entities
     * of the given type a tag upon becoming visible in the world to a
     * player. If the entity has already had a tag, it will not re-add
     * the tag.
     *
     * @param type           The entity type the tag will be applied to
     * @param tagConstructor A function that will create a tag for a provided entity.
     */
    public static void setDefaultTag(EntityType type, Function<Entity, Tag> tagConstructor) {
        entityDefaultTags.put(type, tagConstructor);
    }

    /**
     * Calling this method will allow the packet listener to listen for player and entity movement to ensure
     * a tag is hidden if a player is outside an appropriate distance from the viewer.
     */
    public static void listenForMovement() {
        listener.listenForMovement();
    }

}
