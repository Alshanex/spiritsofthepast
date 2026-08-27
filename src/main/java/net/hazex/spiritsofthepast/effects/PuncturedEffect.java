package net.hazex.spiritsofthepast.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PuncturedEffect extends MobEffect {
    public static final float DEFENSE_LOWERED_PER_LEVEL = -0.1F;
    public static final float REDUCED_TOUGHNESS_PER_LEVEL = -0.1F;

    public PuncturedEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
}