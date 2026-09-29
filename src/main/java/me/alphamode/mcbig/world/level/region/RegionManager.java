package me.alphamode.mcbig.world.level.region;

import net.minecraft.world.level.Level;

// Make the world into regions between
public class RegionManager {
    private final Level level;
    // Only 4 regions could ever be active at once
    private final LocalLevelRegion[] regions = new LocalLevelRegion[4];

    public RegionManager(Level level) {
        this.level = level;
    }


    public interface RegionLocator {

    }
}
