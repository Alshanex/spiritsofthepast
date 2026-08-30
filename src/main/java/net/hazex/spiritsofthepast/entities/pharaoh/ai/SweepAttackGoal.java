package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class SweepAttackGoal extends Goal {

    private static final int COOLDOWN = 140;
    private static final int IMPACT_TICK = 5;
    private static final int SOUND_TICK = 3;
    private static final double RADIUS = 3.5;
    private static final double KNOCKBACK = 2.0;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private boolean swept;
    private boolean soundPlayed;

    public SweepAttackGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.SWEEP)) {
            return false;
        }
        return !findVictims().isEmpty();
    }

    @Override
    public boolean canContinueToUse() {
        return this.elapsed < PharaohEntity.SWEEP_DURATION
                && this.pharaoh.getAction() == PharaohEntity.ACTION_SWEEP;
    }

    @Override
    public void start() {
        this.elapsed = 0;
        this.swept = false;
        this.soundPlayed = false;
        this.pharaoh.startAction(PharaohEntity.ACTION_SWEEP, PharaohEntity.SWEEP_DURATION);
    }

    @Override
    public void tick() {
        this.elapsed++;

        if (!this.soundPlayed && this.elapsed >= SOUND_TICK) {
            this.soundPlayed = true;
            this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                    SotPSoundRegistry.PHARAOH_SWING.get(), SoundSource.HOSTILE, 1.4F, 0.7F);
        }

        if (this.swept || this.elapsed < IMPACT_TICK) {
            return;
        }
        this.swept = true;

        for (LivingEntity victim : findVictims()) {
            victim.knockback(KNOCKBACK,
                    this.pharaoh.getX() - victim.getX(),
                    this.pharaoh.getZ() - victim.getZ());
            victim.setDeltaMovement(victim.getDeltaMovement().add(0.0, 0.35, 0.0));
            victim.hurtMarked = true;
        }
    }

    private List<LivingEntity> findVictims() {
        Vec3 centre = this.pharaoh.position();
        AABB area = AABB.ofSize(centre, RADIUS * 2.0, 4.0, RADIUS * 2.0);

        return this.pharaoh.level().getEntitiesOfClass(LivingEntity.class, area,
                target -> target != this.pharaoh
                        && target.isAlive()
                        && !target.isSpectator()
                        && !this.pharaoh.isAlliedTo(target)
                        && target.distanceToSqr(centre) <= RADIUS * RADIUS);
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.SWEEP, COOLDOWN);
        if (this.pharaoh.getAction() == PharaohEntity.ACTION_SWEEP) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}