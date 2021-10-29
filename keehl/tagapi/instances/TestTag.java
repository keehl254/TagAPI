package keehl.tagapi.instances;

import keehl.tagapi.tags.Tag;
import keehl.tagapi.tags.TagLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

public class TestTag extends Tag {

    public TestTag(Entity target) {
        super(target);

        TagLine tempLine = new TagLine(10);
        tempLine.setGetName(player -> ChatColor.GREEN + "" + ChatColor.BOLD + target.getName());
        this.addTagLine(tempLine);

        tempLine = new TagLine(9);
        tempLine.setGetName(player -> ChatColor.GOLD + "" + ChatColor.BOLD + "Second Line Test");
        this.addTagLine(tempLine);

        tempLine = new TagLine(8);
        tempLine.setGetName(player -> ChatColor.GREEN + "" + ChatColor.BOLD + "Third Line Test");
        tempLine.setKeepSpaceWhenNull(player -> false);
        this.addTagLine(tempLine);

    }

}
