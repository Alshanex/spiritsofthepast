package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.SandstoneBoltEntity;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SandBoltGoal extends Goal {

    private static final int COOLDOWN = 50;
    private static final int CAST_TICKS = 5;
    private static final int FIRE_AT_TICK = 4;
    private static final double MIN_RANGE = 4.0;
    private static final double MAX_RANGE = 28.0;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private boolean fired;

    public SandBoltGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.BOLT)) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || !target.isAlive() || !this.pharaoh.hasLineOfSight(target)) {
            return false;
        }
        double distSqr = this.pharaoh.distanceToSqr(target);
        return distSqr >= MIN_RANGE * MIN_RANGE && distSqr <= MAX_RANGE * MAX_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.elapsed < CAST_TICKS
                && this.pharaoh.getAction() == PharaohEntity.ACTION_INSTANT_CAST;
    }

    @Override
    public void start() {
        this.elapsed = 0;
        this.fired = false;
        this.pharaoh.startAction(PharaohEntity.ACTION_INSTANT_CAST, CAST_TICKS);
    }

    @Override
    public void tick() {
        this.elapsed++;

        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || this.fired || this.elapsed < FIRE_AT_TICK) {
            return;
        }
        this.fired = true;

        Vec3 origin = new Vec3(this.pharaoh.getX(), this.pharaoh.getEyeY() - 0.2, this.pharaoh.getZ());
        Vec3 aim = target.position().add(0.0, target.getBbHeight() * 0.5, 0.0).subtract(origin);

        SandstoneBoltEntity bolt = new SandstoneBoltEntity(this.pharaoh.level(), this.pharaoh, origin.x, origin.y, origin.z);
        bolt.shoot(aim);
        this.pharaoh.level().addFreshEntity(bolt);

        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SoundEvents.SAND_HIT, SoundSource.HOSTILE, 1.2F, 0.8F);
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.BOLT, COOLDOWN);
        if (this.pharaoh.getAction() == PharaohEntity.ACTION_INSTANT_CAST) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}