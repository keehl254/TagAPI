package com.lkeehl.tagapi.api;

import java.util.UUID;

public interface TagEntity {

    /**
     * Returns the ID associated with this entity.
     *
     * @return The ID of the fake tag entity.
     */
    int getEntityID();

    /**
     * Returns the UUID associated with this entity.
     *
     * @return The UUID for the fake tag entity.
     */
    UUID getEntityUUID();

    /**
     * Returns the tag line that this entity belongs to.
     *
     * @return The TagLine for the fake tag entity.
     */
    TagLine getTagLine();

    /**
     * Lines are created by mounting many invisible entities. This method here is for recursion purposes so that one can easily grab all the tag entities in a TagLine.
     *
     * @return The TagEntity that this is connected to, or null if none is present.
     */
    TagEntity getChild();


}
