# TagAPI
<img src="https://i.imgur.com/87cSmdB.png" align="right" alt="A picture of a player with a custom title under their name" style="max-width:280px;width:50%;">
I have frequently come across the question from developers on both the Spigot forums and discord servers that asks how to create customizable text under a players name. A system that provides unlockable "titles" under a players name has been a staple in a server for over two years that I have developed for, and I wished to share the method which I use to create them.
<br>
There have been a couple of methods I had seen to accomplish this:
<br><br>
<ul>
  <li>Using a scoreboard</li>
  <li>Teleporting an invisible armor stand to the players location</li>
  <li>Using an amalgamation of invisible entities and slimes with a -1 size mounting the player</li>
</ul>

<details>
<summary><b>Show Method Differences</b></summary>
<h2>Scoreboards</h2>
<img src = "https://i.imgur.com/sMSo1Pu.png" style="padding-right:15px" align="left" height="180">
<p align="top">
    Scoreboards seem like the easiest solution to this issue, but it has quite a few drawbacks, such as requiring a number next to the text and a maximum of one line is available to work with. You will also constantly being fighting with other plugins over the players current scoreboard.
</p>
<p style="clear:both"></p>
<h2>Armor Stand Teleportation</h2>
This method, as well as the following one, are the go-to methods for creating multiple tags above an entity. You can have an unlimited number of them and, for the most part, will not have to worry about support from other plugins. Compared to the mount technique, you will not have to worry about setting passengers of the entity; however, this method does have it's own drawback: Trailing. The tags will appear to follow the player rather than be attached to them. This can seemingly be fixed for players specifically by listening to the move packets rather, but other entities will trail.
<br>
<h2>Mount</h2>
Finally, we reach the technique that I use in this resource. The Mount method attaches the tags to the entity by having them ride the entity. Minecrafts own client will handle all the movement, so there will be no trailing like with the teleportation method. On top of this benefit, there are a significant less amount of packets sent to the nearby players. While the teleport has to send teleport packets for every entity to every nearby player every time the target moves, the mount method only requires 4 packets:
<br><br>
<ul>
  <li>Spawn Entity - Tell the client to spawn the entity</li>
  <li>Entity Metadata - Set data about the entity, such as invisibility</li>
  <li>Mount entity - Attach the entities together</li>
  <li>Destroy entity - Despawn the entities when the target entity leaves view, dies, or quits</li>
</ul>
<br>
These packets will only be sent to the nearby players whenever needed. The downside of using the Mount method is that another plugin setting the passenger of the target entity will cause the tag to disappear or pop-off.
</details>
<br>This API only supports the Mounting technique.
<br>
<h2>Uses</h2>
There are many great uses for a resource such as this, but I wanted to compile a list of ways that this may help you.
<ul>
  <li>Health bar on entities while attacking</li>
  <li>A mark showing a players party or faction</li>
  <li>A line that states if a player is AFK</li>
  <li>A titles system like mentioned in the first paragraph</li>
</ul>
<h2>Client Side Only</h2>
Yep! It's true! This API does not create any actual entities to work. The entirety of it uses packets to trick the client into thinking an entity exists. What are the benefits, though? Well, there are quite a few. <br><br>First off, no messes. The last thing a server owner would like is for a plugin to incorrectly shutdown, and now there are random floating tags around the world where entities once were. With the entirety of this system using packets and only existing on the clients end, you will not have to worry about this ever!
<br><br>Second, and my personal favorite, is per-player customization. You can have the lines of the tags appear different to each individual player, or even hide specific lines from individual players.
<!--<br><br><p align = "center"><img src="https://i.imgur.com/5vqpHK6.png" style="width:75%;max-width:750px;" --></p>
<h2>How To Install On a Server</h2>
Easy! There are two ways you can use this resource. This resource can either be downloaded from the SpigotMC Plugin Page as an individual plugin to be dropped into the servers plugins directory, or you can directly integrate it into your code.
<br>If you are directly integrating this API into your own code, then the methods to enable and disable it are simple:
In your plugins onEnable, include a line such as
<br>

    TagAPI.onEnable(this);
<br>
and onDisable:
<br>

      TagAPI.onDisable();
<br>
<h2>Creating, Giving, and Updating Tags</h2>
Tags can be created by extending the class Tag and adding TagLine instances like so:
<br>

    public class ExampleTag extends Tag {

        public ExampleTag(Entity target) { // target will be the entity the tag is attached to
            super(target);

            // Adds a new line to the tag.
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
<br>
Each tag line represents a new line above the players head. The constructor for it takes an integer representing the priority towards the top.
<br><br>
In the example above, the first TagLine has a priority of 10, which is greater than the others, so it will show up on top of the others; whereas the last created tag line will be on the bottom.
<br><br>
Following the creation, you can see a method called setGetName. This is a Function that takes a Player, while returning a String. The Player is going to be the individual viewing the target, while the returned String should be what the line will display. 
Similarly, the last TagLine declaration includes a new method called "setKeepSpaceWhenNull." This simply controls whether or not the line should even render for the player if the String returned by the setGetName function is null. This is true by default.
<br><br>
From here, everything can be controlled through the Tag or TagAPI class like so:

    Tag tag = new ExampleTag(entity); // Create a tag for the entity
    tag.giveTag(); // Give the entity the tag.

    tag = TagAPI.getTag(entity); // Grab the tag of an entity. Entity must have been given the tag using above method.
    tag.updateTag(); // Update the tag incase lines should change for viewers
    tag.removeTag(); // Remove the tag from the entity. This tag can still be given back to the entity later.

The above example shows creating a new tag for an entity, spawning the tag in, grabbing an entities tag, updating the tag, as well as removing one.
The same can be accomplished through the TagAPI class.

    TagAPI.giveTag(entity, ExampleTag::new); // Create and give tag to entity
    TagAPI.updateTag(entity); // Update the entities tag
    TagAPI.removeTag(entity); // Remove a tag from an entity

This API also has a "builder" to make tag creation easier. An example would be like:

    TagBuilder.create(entity).withLine(pl->"First Line").withLine(pl->"Second Line").withLine(pl->"Third",pl->true).build().giveTag();

Where TagBuilder#withLine will accept a function accepting a player and returning a string and optionally support a second argument for keeping lines when null.
<br><br>
I lastly wanted to share that it is possible to set default tags for entity types using the method in the following example:

    TagAPI.setDefaultTag(EntityType.PIG, ExampleTag::new);

All viewed entities will be checked only once if they should have a tag. This means that you can easily remove a tag from an entity, and it will not respawn itself.
<h2>Dependencies</h2>
TagAPI requires ProtocolLib to run, and will only run in version 1.17.1 or higher as of current.

