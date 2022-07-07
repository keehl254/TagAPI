package com.lkeehl.tagapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.lkeehl.tagapi.api.TagLine;
import com.lkeehl.tagapi.tags.BaseTag;
import com.lkeehl.tagapi.tags.BaseTagEntity;
import com.lkeehl.tagapi.util.TagUtil;
import com.lkeehl.tagapi.wrappers.AbstractPacket;
import com.lkeehl.tagapi.wrappers.Wrappers;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public class TagListener implements Listener {

    private final PacketAdapter sendAdapter;
    private final PacketAdapter receiveAdapter;

    private boolean listenForMovement = false;

    private int taskID = -1;

    private final List<Integer> initiatingEntitiesToIgnore = new ArrayList<>();

    public TagListener() {

        /*
         The send adapter listens for entity spawning and destroying packets being sent to a player
         so that TagAPI can properly create or destroy tags related to the created/destroyed entity.

         It would be frustrating having tags left over for a player who died or left the server. It
         would be equally frustrating having tags not reappear if a player leaves view distance and
         comes back.
         */
        this.sendAdapter = new PacketAdapter(TagAPI.getPlugin(), ListenerPriority.MONITOR, Wrappers.packetTypes) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketType packetType = event.getPacketType();
                if (packetType == SPAWN_ENTITY || packetType == SPAWN_ENTITY_LIVING || packetType == NAMED_ENTITY_SPAWN) {
                    int entityID = event.getPacket().getIntegers().read(0);
                    if (TagAPI.getTagTracker().isTagEntity(entityID))
                        return;

                    BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                    if (tag == null) {
                        if (initiatingEntitiesToIgnore.contains(entityID))
                            return;
                        initiatingEntitiesToIgnore.add(entityID);
                        Bukkit.getScheduler().runTaskLater(TagAPI.getPlugin(), () -> createTagForSpawnedEntity(entityID, event.getPlayer().getWorld()), 1L);
                        return;
                    }

                    if (packetType == NAMED_ENTITY_SPAWN)
                        tag.unregisterViewer(event.getPlayer());

                    tag.spawnTagFor(event.getPlayer());
                } else if (packetType.equals(ENTITY_METADATA)) {
                    Wrappers.MetaDataPacket wrapper = Wrappers.METADATA_W_CONTAINER.apply(event.getPacket());
                    int entityID = event.getPacket().getIntegers().read(0);
                    if (TagAPI.getTagTracker().isTagEntity(entityID))
                        return;
                    BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                    if (tag == null)
                        return;
                    Optional<WrappedWatchableObject> baseEntityData = wrapper.getMetadata().stream().filter(i -> i.getIndex() == 0).findFirst();
                    if (baseEntityData.isEmpty())
                        return;
                    byte value = (byte) baseEntityData.get().getValue();
                    if ((value & 34) == 0) {
                        tag.updateTagFor(event.getPlayer(), true, false);
                        return;
                    }
                    tag.updateTagFor(event.getPlayer(), (value & 32) == 0, (value & 2) != 0);
                } else if (packetType.equals(PLAYER_INFO)) {
                    EnumWrappers.PlayerInfoAction action = event.getPacket().getPlayerInfoAction().read(0);
                    if (action == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER || action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        List<PlayerInfoData> dataList = event.getPacket().getPlayerInfoDataLists().read(0);
                        for (PlayerInfoData data : dataList) {
                            Player player = Bukkit.getPlayer(data.getProfile().getUUID());
                            if (player == null || !player.isOnline())
                                continue;
                            BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(player.getEntityId());
                            if (tag == null)
                                continue;
                            if (action == EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)
                                tag.destroyTagFor(event.getPlayer());
                            else
                                tag.updateTagFor(event.getPlayer());
                        }
                    }
                } else if (packetType.equals(ENTITY_DESTROY)) {
                    Wrappers.DestroyPacket wrapper = Wrappers.DESTROY_W_CONTAINER.apply(event.getPacket());
                    for (int entityID : wrapper.getEntityIDs()) {
                        BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                        if (tag == null)
                            continue;
                        tag.destroyTagFor(event.getPlayer());
                    }
                } else if (packetType.equals(REL_ENTITY_MOVE) || packetType.equals(REL_ENTITY_MOVE_LOOK) || packetType.equals(ENTITY_TELEPORT) || packetType.equals(ENTITY_LOOK)) {
                    if (!TagListener.this.listenForMovement)
                        return;
                    int entityID = event.getPacket().getIntegers().read(0);
                    if (TagAPI.getTagTracker().isTagEntity(entityID))
                        return;

                    BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                    if (tag == null)
                        return;

                    if (packetType.equals(ENTITY_TELEPORT)) {
                        if (!TagUtil.isViewer(event.getPlayer(), event.getPacket().getDoubles().read(0), event.getPacket().getDoubles().read(2))) {
                            tag.destroyTagFor(event.getPlayer());
                            return;
                        }
                    } else if (!packetType.equals(ENTITY_LOOK)) {
                        if (!TagUtil.isViewer(event.getPacket().getEntityModifier(event.getPlayer().getWorld()).read(0), event.getPlayer())) {
                            tag.destroyTagFor(event.getPlayer());
                            return;
                        }
                    }

                    if (packetType.equals(ENTITY_LOOK) || packetType.equals(REL_ENTITY_MOVE_LOOK)) {
                        List<AbstractPacket> packets = new ArrayList<>();
                        ((BaseTagEntity) tag.getBottomTagLine().getBottomEntity()).getMetaPackets(event.getPlayer(), packets, !event.getPlayer().isInvisible(), event.getPlayer().isSneaking());
                        packets.forEach(p -> p.sendPacket(event.getPlayer()));
                    }

                }
            }

        };
        /*
            This receiving adapter is to get rid of a single annoyance: Interaction with the fake entities.
            Without intercepting this packet, any player trying to interact with an entity with a tag would
            be clicking the tag instead. This adapter simply changes the interacted-with entity to the entity
            with the tag rather than the tag itself. If the fake tag-entity is not within the entities body,
            we will simply ignore it.
         */
        this.receiveAdapter = new PacketAdapter(TagAPI.getPlugin(), ListenerPriority.LOWEST, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK, PacketType.Play.Client.POSITION) {

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
                    if (entity == event.getPlayer())
                        return;

                    if (event.getPlayer().hasLineOfSight(entity))
                        event.getPacket().getIntegers().write(0, entity.getEntityId());

                } else if (listenForMovement) {
                    if (!packetType.equals(PacketType.Play.Client.POSITION_LOOK) || packetType.equals(PacketType.Play.Client.LOOK) || packetType.equals(PacketType.Play.Client.POSITION))
                        return;
                    int entityID = event.getPlayer().getEntityId();
                    if (TagAPI.getTagTracker().isTagEntity(entityID))
                        return;
                    BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(entityID);
                    if (tag == null)
                        return;
                    tag.updateBottomStand();
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

        Bukkit.getScheduler().cancelTask(this.taskID);
    }

    public void createTagForSpawnedEntity(int entityID, World world) {
        Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityID);
        if (entity == null || !TagAPI.entityDefaultTags.containsKey(entity.getType()) || (entity.hasMetadata("had-default-tag")))
            return;

        entity.setMetadata("had-default-tag", new FixedMetadataValue(TagAPI.getPlugin(), true));
        TagAPI.entityDefaultTags.get(entity.getType()).apply(entity).giveTag();
        initiatingEntitiesToIgnore.remove(Integer.valueOf(entityID));
    }

    public void setupTask() {
        if (this.taskID != -1)
            Bukkit.getScheduler().cancelTask(this.taskID);
        this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(TagAPI.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!TagAPI.hasTag(player))
                    continue;
                ((BaseTag) TagAPI.getTag(player)).updateBottomStand(player);
            }
        }, 0L, 1L);
    }

    public void listenForMovement() {
        this.listenForMovement = true;
        this.setupTask();
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
        e.getPlayer().removeMetadata("had-default-tag", TagAPI.getPlugin());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        BaseTag tag = (BaseTag) TagAPI.getTagTracker().getEntityTag(e.getPlayer().getEntityId());
        if (tag == null) {
            if (!TagAPI.entityDefaultTags.containsKey(EntityType.PLAYER) || (e.getPlayer().hasMetadata("had-default-tag")))
                return;

            e.getPlayer().setMetadata("had-default-tag", new FixedMetadataValue(TagAPI.getPlugin(), true));
            TagAPI.entityDefaultTags.get(EntityType.PLAYER).apply(e.getPlayer()).giveTag();
        }
    }

    private void onDespawn(Entity e) {
        if (TagAPI.getTagTracker().getEntityTag(e.getEntityId()) == null)
            return;
        ((BaseTag) TagAPI.getTagTracker().getEntityTag(e.getEntityId())).destroy(!(e instanceof Player));
    }

}
