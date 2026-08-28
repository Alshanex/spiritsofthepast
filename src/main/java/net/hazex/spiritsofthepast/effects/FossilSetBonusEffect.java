package net.hazex.spiritsofthepast.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class FossilSetBonusEffect extends MobEffect {
    public static final float CRIT_DAMAGE_PER_LEVEL = 0.05F;
    public static final float MOVEMENT_SPEED_PER_LEVEL = 0.05F;

    public FossilSetBonusEffect(MobEffectCategory mobEffectCategory, int color) {
        super(mobEffectCategory, color);
    }
}