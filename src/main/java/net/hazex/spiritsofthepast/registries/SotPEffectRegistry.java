package net.hazex.spiritsofthepast.registries;

import io.redspace.ironslib.registry.IronsLibRegistries;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.effects.FossilSetBonusEffect;
import net.hazex.spiritsofthepast.effects.PuncturedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SotPEffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SpiritsofthePast.MODID);

    public static final Holder<MobEffect> PUNCTURED = MOB_EFFECTS.register("punctured",
            () -> new PuncturedEffect(MobEffectCategory.HARMFUL, 11101546)
                    .addAttributeModifier(Attributes.ARMOR,
                            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "punctured_armor"),
                            PuncturedEffect.DEFENSE_LOWERED_PER_LEVEL,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)

                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "punctured_toughness"),
                            PuncturedEffect.REDUCED_TOUGHNESS_PER_LEVEL,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final Holder<MobEffect> FOSSIL_SET_BONUS = MOB_EFFECTS.register("fossil_set_bonus",
            () -> new FossilSetBonusEffect(MobEffectCategory.HARMFUL, 11101546)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "fossil_set_bonus"),
                            FossilSetBonusEffect.MOVEMENT_SPEED_PER_LEVEL,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)

                    .addAttributeModifier(IronsLibRegistries.AttributeRegistry.CRIT_DAMAGE,
                            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "fossil_set_bonus"),
                            FossilSetBonusEffect.CRIT_DAMAGE_PER_LEVEL,
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}