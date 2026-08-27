package net.hazex.spiritsofthepast.events;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPEffectRegistry;
import net.hazex.spiritsofthepast.registries.SotPItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = SpiritsofthePast.MODID)
public class AmberSpearHandler {

    private static final Holder<MobEffect> EFFECT = SotPEffectRegistry.PUNCTURED;
    private static final int DURATION_TICKS = 200;
    private static final int AMPLIFIER = 1;

    private static final double RADIUS = 2.0;

    @SubscribeEvent
    private static void onSpearHit(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();

        if (!source.is(DamageTypes.SPEAR)) {
            return;
        }
        if (!(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        if (!attacker.isUsingItem()) {
            return;
        }
        if (!attacker.getUseItem().is(SotPItemRegistry.AMBER_SPEAR.get())) {
            return;
        }
        if (!(attacker.level() instanceof ServerLevel level)) {
            return;
        }

        applyBurst(level, attacker, event.getEntity());
    }

    private static void applyBurst(ServerLevel level, LivingEntity attacker, LivingEntity target) {
        Vec3 centre = target.position();
        AABB area = new AABB(centre, centre).inflate(RADIUS);

        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, area,
                candidate -> candidate != attacker
                        && candidate.isAlive()
                        && !candidate.isSpectator()
                        && !attacker.isAlliedTo(candidate))) {

            if (victim.getBoundingBox().getCenter().distanceToSqr(centre) > RADIUS * RADIUS) {
                continue;
            }
            victim.addEffect(new MobEffectInstance(EFFECT, DURATION_TICKS, AMPLIFIER), attacker);
        }

        level.sendParticles(ParticleTypes.CRIT,
                centre.x, centre.y + 1.0, centre.z, 30, RADIUS * 0.5, 0.5, RADIUS * 0.5, 0.05);
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.8F);
    }
}