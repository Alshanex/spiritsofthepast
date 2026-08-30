package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FireballVolleyGoal extends Goal {

    private static final int COOLDOWN = 160;
    private static final int FIRE_AT_TICK = 5;
    private static final double MIN_RANGE = 5.0;
    private static final double MAX_RANGE = 24.0;

    private static final double SPREAD_DEGREES = 10.0;
    private static final double POWER = 0.9;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private boolean fired;

    public FireballVolleyGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.FIREBALL)) {
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
        return this.elapsed < PharaohEntity.FIREBALL_DURATION
                && this.pharaoh.getAction() == PharaohEntity.ACTION_FIREBALL;
    }

    @Override
    public void start() {
        this.elapsed = 0;
        this.fired = false;
        this.pharaoh.startAction(PharaohEntity.ACTION_FIREBALL, PharaohEntity.FIREBALL_DURATION);
        this.pharaoh.swing(InteractionHand.MAIN_HAND);
    }

    @Override
    public void tick() {
        this.elapsed++;

        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || this.fired || this.elapsed < FIRE_AT_TICK) {
            return;
        }
        this.fired = true;

        Vec3 origin = new Vec3(this.pharaoh.getX(), this.pharaoh.getEyeY() - 0.1, this.pharaoh.getZ());
        Vec3 aim = target.position()
                .add(0.0, target.getBbHeight() * 0.5, 0.0)
                .subtract(origin)
                .normalize();

        for (int i = -1; i <= 1; i++) {
            Vec3 direction = rotateAroundY(aim, Math.toRadians(SPREAD_DEGREES * i));

            SmallFireball fireball = new SmallFireball(
                    this.pharaoh.level(), this.pharaoh, direction.scale(POWER));
            fireball.setPos(origin.x, origin.y, origin.z);
            this.pharaoh.level().addFreshEntity(fireball);
        }

        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SotPSoundRegistry.PHARAOH_FIREBALL.get(), SoundSource.HOSTILE, 1.5F, 0.7F);
        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.5F, 0.7F);
    }

    private static Vec3 rotateAroundY(Vec3 vec, double radians) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        return new Vec3(
                vec.x * cos - vec.z * sin,
                vec.y,
                vec.x * sin + vec.z * cos);
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.FIREBALL, COOLDOWN);
        if (this.pharaoh.getAction() == PharaohEntity.ACTION_FIREBALL) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
