package keehl.tagapi.instances;

import keehl.tagapi.tags.Tag;
import keehl.tagapi.tags.TagLine;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;

public class TestTag extends Tag implements Listener {

    public TestTag(Entity target) {
        super(target);

        this.addTagLine(new TagLine(10).setGetName(player ->
                ChatColor.GREEN + "" + ChatColor.BOLD + target.getName()
        ));

        this.addTagLine(new TagLine(9).setGetName(player ->
                ChatColor.GOLD + "" + ChatColor.BOLD + "Second Line Test"
        ));

        this.addTagLine(new TagLine(8).setGetName(player ->
                ChatColor.GREEN + "" + ChatColor.BOLD + "Third Line Test"
        ).setKeepSpaceWhenNull(player -> false));


    }

}
