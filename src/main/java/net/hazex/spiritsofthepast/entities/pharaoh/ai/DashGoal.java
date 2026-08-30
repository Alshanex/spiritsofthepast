package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DashGoal extends Goal {

    private static final int COOLDOWN = 220;

    private static final double DASH_SPEED = 0.85;
    private static final int MAX_DASH_TICKS = 14;
    private static final double ARRIVAL_DISTANCE = 2.5;

    private static final double MIN_APPROACH_RANGE = 6.0;
    private static final double MAX_APPROACH_RANGE = 24.0;
    private static final double FLEE_TRIGGER_RANGE = 8.0;

    private enum Phase { PREPARE, TRAVEL, LAND }

    private final PharaohEntity pharaoh;
    private Phase phase = Phase.PREPARE;
    private int phaseTicks;
    private boolean fleeing;
    private Vec3 direction = Vec3.ZERO;

    public DashGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.DASH)) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        double distSqr = this.pharaoh.distanceToSqr(target);

        if (this.pharaoh.isCornered()) {
            return distSqr <= FLEE_TRIGGER_RANGE * FLEE_TRIGGER_RANGE;
        }
        return distSqr >= MIN_APPROACH_RANGE * MIN_APPROACH_RANGE
                && distSqr <= MAX_APPROACH_RANGE * MAX_APPROACH_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        int action = this.pharaoh.getAction();
        return action == PharaohEntity.ACTION_DASH_PREPARE
                || action == PharaohEntity.ACTION_DASHING
                || action == PharaohEntity.ACTION_DASH_LAND;
    }

    @Override
    public void start() {
        this.phase = Phase.PREPARE;
        this.phaseTicks = 0;
        this.fleeing = this.pharaoh.isCornered();

        this.direction = travelDirection();

        this.pharaoh.startAction(PharaohEntity.ACTION_DASH_PREPARE, -1);
        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SoundEvents.BREEZE_INHALE, SoundSource.HOSTILE, 1.0F, 1.4F);
    }

    private Vec3 travelDirection() {
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null) {
            return this.pharaoh.getLookAngle();
        }

        Vec3 toTarget = target.position().subtract(this.pharaoh.position());
        Vec3 flat = new Vec3(toTarget.x, 0.0, toTarget.z);
        if (flat.lengthSqr() < 1.0E-4) {
            return this.pharaoh.getLookAngle();
        }

        Vec3 normalized = flat.normalize();
        return this.fleeing ? normalized.reverse() : normalized;
    }

    @Override
    public void tick() {
        this.phaseTicks++;

        switch (this.phase) {
            case PREPARE -> {
                if (this.phaseTicks >= PharaohEntity.DASH_PREPARE_DURATION) {
                    this.phase = Phase.TRAVEL;
                    this.phaseTicks = 0;
                    this.pharaoh.startAction(PharaohEntity.ACTION_DASHING, -1);
                }
            }
            case TRAVEL -> {
                travel();
                if (shouldStopTravelling()) {
                    this.phase = Phase.LAND;
                    this.phaseTicks = 0;
                    this.pharaoh.setDeltaMovement(0.0, this.pharaoh.getDeltaMovement().y, 0.0);
                    this.pharaoh.startAction(PharaohEntity.ACTION_DASH_LAND, -1);
                    landingEffects();
                }
            }
            case LAND -> {
                if (this.phaseTicks >= PharaohEntity.DASH_LAND_DURATION) {
                    this.pharaoh.releaseAction();
                }
            }
        }
    }

    private void travel() {
        Vec3 current = this.pharaoh.getDeltaMovement();
        this.pharaoh.setDeltaMovement(
                this.direction.x * DASH_SPEED,
                current.y,
                this.direction.z * DASH_SPEED);

        this.pharaoh.hurtMarked = true;

        if (this.pharaoh.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CLOUD,
                    this.pharaoh.getX(), this.pharaoh.getY() + 0.1, this.pharaoh.getZ(),
                    2, 0.2, 0.05, 0.2, 0.01);
        }
    }

    private boolean shouldStopTravelling() {
        if (this.phaseTicks >= MAX_DASH_TICKS) {
            return true;
        }

        if (this.pharaoh.horizontalCollision) {
            return true;
        }

        LivingEntity target = this.pharaoh.getTarget();
        if (!this.fleeing && target != null) {
            return this.pharaoh.distanceToSqr(target) <= ARRIVAL_DISTANCE * ARRIVAL_DISTANCE;
        }
        return false;
    }

    private void landingEffects() {
        if (this.pharaoh.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.CLOUD,
                    this.pharaoh.getX(), this.pharaoh.getY() + 0.1, this.pharaoh.getZ(),
                    20, 0.4, 0.05, 0.4, 0.05);
            level.playSound(null, this.pharaoh.blockPosition(),
                    SoundEvents.GENERIC_BIG_FALL, SoundSource.HOSTILE, 1.0F, 0.8F);
        }
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.DASH, COOLDOWN);
        this.pharaoh.setDeltaMovement(0.0, this.pharaoh.getDeltaMovement().y, 0.0);

        int action = this.pharaoh.getAction();
        if (action == PharaohEntity.ACTION_DASH_PREPARE
                || action == PharaohEntity.ACTION_DASHING
                || action == PharaohEntity.ACTION_DASH_LAND) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
