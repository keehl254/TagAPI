package keehl.tagapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import keehl.tagapi.api.TagLine;
import keehl.tagapi.tags.BaseTag;
import keehl.tagapi.wrappers.Wrappers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public class TagListener implements Listener {

    private final PacketAdapter sendAdapter;
    private final PacketAdapter receiveAdapter;

    public TagListener() {

        /*
         The send adapter listens for entity spawning and destroying packets being sent to a player
         so that TagAPI can properly create or destroy tags related to the created/destroyed entity.

         It would be frustrating having tags left over for a player who died or left the server. It
         would be equally frustrating having tags not reappear if a player leaves view distance and
         comes back.
         */
        this.sendAdapter = new PacketAdapter(TagAPI.getPlugin(), ListenerPriority.MONITOR, SPAWN_ENTITY, SPAWN_ENTITY_LIVING, ENTITY_DESTROY, NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketType packetType = event.getPacketType();
                if (packetType == SPAWN_ENTITY || packetType == SPAWN_ENTITY_LIVING || packetType == NAMED_ENTITY_SPAWN) {
                    int entityID = event.getPacket().getIntegers().read(0);
                    if (TagAPI.getTagTracker().isTagEntity(entityID))
                        return;

                    BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                    if (tag == null) {
                        Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(event.getPlayer().getWorld(), entityID);
                        if (entity == null || !TagAPI.entityDefaultTags.containsKey(entity.getType()) || (entity.hasMetadata("had-default-tag")))
                            return;

                        entity.setMetadata("had-default-tag", new FixedMetadataValue(TagAPI.getPlugin(), true));
                        Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> TagAPI.entityDefaultTags.get(entity.getType()).apply(entity).giveTag(), 1L);
                        return;
                    }

                    if (packetType == NAMED_ENTITY_SPAWN)
                        tag.unregisterViewer(event.getPlayer());

                    tag.spawnTagFor(event.getPlayer());
                } else if (packetType.equals(ENTITY_DESTROY)) {
                    Wrappers.DestroyPacket wrapper = Wrappers.DESTROY_W_CONTAINER.apply(event.getPacket());
                    for (int entityID : wrapper.getEntityIDs()) {
                        BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                        if (tag == null)
                            continue;
                        tag.destroyTagFor(event.getPlayer());
                    }
                }
            }

        };
        /*
            This receive adapter is to get rid of a single annoyance: Interaction with the fake entities.
            Without intercepting this packet, any player trying to interact with an entity with a tag would
            be clicking the tag instead. This adapter simply changes the interacted-with entity to the entity
            with the tag rather than the tag itself. If the fake tag-entity is not within the entities body,
            we will simply ignore it.
         */
        this.receiveAdapter = new PacketAdapter(TagAPI.getPlugin(), ListenerPriority.LOWEST, PacketType.Play.Client.USE_ENTITY) {

            @Override()
            public void onPacketReceiving(PacketEvent event) {
                PacketType packetType = event.getPacketType();
                if (packetType.equals(PacketType.Play.Client.USE_ENTITY)) {
                    int entityID = event.getPacket().getIntegers().read(0);
                    if (!TagAPI.getTagTracker().isTagEntity(entityID))
                        return;

                    TagLine tagLine = TagAPI.getTagTracker().getTagEntity(entityID).getTagLine();
                    if (!tagLine.isInBody())
                        return;

                    Entity entity = tagLine.getTag().getTarget();
                    if (!(entity instanceof LivingEntity))
                        return;

                    if (event.getPlayer().hasLineOfSight(entity))
                        event.getPacket().getIntegers().write(0, entity.getEntityId());

                }
            }
        };
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(this.sendAdapter);
        protocolManager.addPacketListener(this.receiveAdapter);
    }

    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListener(this.sendAdapter);
        ProtocolLibrary.getProtocolManager().removePacketListener(this.receiveAdapter);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent e) {
        this.onDespawn(e.getEntity());
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities())
            this.onDespawn(entity);
    }

    @EventHandler()
    public void onLeave(PlayerQuitEvent e) {
        TagAPI.getTagTracker().unregisterViewer(e.getPlayer());
    }

    private void onDespawn(Entity e) {
        if (TagAPI.getTagTracker().getEntityTag(e.getEntityId()) == null)
            return;
        ((BaseTag) TagAPI.getTagTracker().getEntityTag(e.getEntityId())).destroy(true);
    }

}
