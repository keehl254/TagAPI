package keehl.tagapi.instances;

import keehl.tagapi.tags.Tag;
import keehl.tagapi.tags.TagLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public class TestTag extends Tag {

    public TestTag(Entity target) {
        super(target);

        TagLine tempLine = new TagLine(this, 10);
        tempLine.setGetName((player, entity) -> ChatColor.GREEN + "" + ChatColor.BOLD + entity.getName());
        this.addTagLine(tempLine);

        tempLine = new TagLine(this, 9);
        tempLine.setGetName((player, entity) -> ChatColor.GOLD + "" + ChatColor.BOLD + "Second Line Test");
        this.addTagLine(tempLine);

        tempLine = new TagLine(this, 8);
        tempLine.setGetName((player, entity) -> ChatColor.GREEN + "" + ChatColor.BOLD + "Third Line Test");
        this.addTagLine(tempLine);

    }

}
