package me.alphamode.mcbig.mixin;

import me.alphamode.mcbig.prelaunch.FeatureConfig;
import me.alphamode.mcbig.prelaunch.Features;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class McBigMixinPlugin implements IMixinConfigPlugin {
    private static final String MIXIN_PACKAGE = "me.alphamode.mcbig.mixin.";

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(MIXIN_PACKAGE.length());
        Features option = FeatureConfig.getEffectiveFeatureForMixin(mixin);
        if (option != null) {
            return option.isEnabled();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
