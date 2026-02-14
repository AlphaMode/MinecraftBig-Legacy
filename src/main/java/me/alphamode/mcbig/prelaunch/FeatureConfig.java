package me.alphamode.mcbig.prelaunch;

import java.util.HashMap;
import java.util.Map;

public class FeatureConfig {
    private static final Map<String, Features> options = new HashMap<>();

    static {
        for (Features feature : Features.values()) {
            options.put(feature.getFlag(), feature);
        }
    }

    // Based off of sodium's mixin options
    public static Features getEffectiveFeatureForMixin(String mixinClassName) {
        int lastSplit = 0;
        int nextSplit;

        Features rule = null;

        while ((nextSplit = mixinClassName.indexOf('.', lastSplit)) != -1) {
            String key = "mixin." + mixinClassName.substring(0, nextSplit);

            Features candidate = options.get(key);

            if (candidate != null) {
                rule = candidate;

                if (!rule.isEnabled()) {
                    return rule;
                }
            }

            lastSplit = nextSplit + 1;
        }

        return rule;
    }
}
