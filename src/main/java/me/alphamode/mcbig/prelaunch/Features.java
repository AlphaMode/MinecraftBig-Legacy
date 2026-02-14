package me.alphamode.mcbig.prelaunch;

public enum Features {
    BIG_DECIMAL_MOVEMENT("mixin.features.big_movement", "Big Decimal Movement"),
    BIG_DECIMAL_WORLD_GEN("mixin.features.big_worldgen", "Big Decimal World Generation"),
    FIX_STRIPELANDS("mixin.features.fix_stripelands", "Fix stripelands");

    private final String flag;
    private final String text;

    Features(String flag, String text) {
        this.flag = flag;
        this.text = text;
    }

    public String getFlag() {
        return flag;
    }

    public String getText () {
        return text;
    }

    public boolean isEnabled() {
        return Boolean.getBoolean(flag);
    }
}
