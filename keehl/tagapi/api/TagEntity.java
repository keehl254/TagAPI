package keehl.tagapi.api;

import keehl.tagapi.tags.BaseTagEntity;

import java.util.UUID;

public interface TagEntity {

    int getEntityID();

    UUID getEntityUUID();

    TagLine getTagLine();

    TagEntity getChild();


}
