package eaglemixins.compat;

import net.minecraftforge.fml.common.Loader;

public class ModLoadedUtil {
    public static final LoadedContainer forgottenitems = new LoadedContainer("forgottenitems");
    public static final LoadedContainer nuclearcraft = new LoadedContainer("nuclearcraft");
    public static final LoadedContainer rlcombat = new LoadedContainer("bettercombatmod");
    public static final LoadedContainer spartanweaponry = new LoadedContainer("spartanweaponry");
    public static final LoadedContainer xat = new LoadedContainer("xat");

    public static class LoadedContainer{
        private Boolean isLoaded = null;
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
    }
}
