package keehl.tagapi.wrappers.v1171;

import keehl.tagapi.wrappers.Wrappers;

public class Wrapper1771 {

    public static void init() {
        Wrappers.DESTROY_W_CONTAINER = Destroy1171Wrapper::new;
        Wrappers.DESTROY = Destroy1171Wrapper::new;
        Wrappers.METADATA_W_CONTAINER = MetaData1171Wrapper::new;
        Wrappers.METADATA = MetaData1171Wrapper::new;
        Wrappers.MOUNT = Mount1171Wrapper::new;
        Wrappers.SPAWN_ENTITY = SpawnEntity1171Wrapper::new;
        Wrappers.SPAWN_ENTITY_LIVING = SpawnEntityLiving1171Wrapper::new;
    }

}
