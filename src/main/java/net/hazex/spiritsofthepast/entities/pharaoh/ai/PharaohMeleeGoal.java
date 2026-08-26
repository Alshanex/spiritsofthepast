package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class PharaohMeleeGoal extends Goal {
    private static final int IMPACT_TICK = 7;
    private static final int ATTACK_INTERVAL = 25;
    private static final int IDLE = -1;

    private final PharaohEntity pharaoh;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;

    private int cooldown;
    private int swingTimer = IDLE;
    private int repathDelay;

    public PharaohMeleeGoal(PharaohEntity pharaoh, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.pharaoh = pharaoh;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pharaoh.isBusy()) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }

        if (this.pharaoh.isBusy() && this.swingTimer == IDLE) {
            return false;
        }
        return this.followingTargetEvenIfNotSeen || this.pharaoh.getSensing().hasLineOfSight(target);
    }

    @Override
    public void start() {
        this.swingTimer = IDLE;
        this.repathDelay = 0;
    }

    @Override
    public void stop() {
        this.swingTimer = IDLE;
    }

    @Override
    public void tick() {
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null) {
            return;
        }

        if (this.cooldown > 0) {
            this.cooldown--;
        }

        if (this.swingTimer != IDLE) {
            this.swingTimer++;
            this.pharaoh.getNavigation().stop();
            if (this.swingTimer >= IMPACT_TICK) {
                this.swingTimer = IDLE;
                landHit(target);
            }
            return;
        }

        approach(target);

        if (this.cooldown <= 0 && this.pharaoh.isWithinMeleeAttackRange(target)) {
            this.cooldown = ATTACK_INTERVAL;
            this.swingTimer = 0;
            this.pharaoh.swing(InteractionHand.MAIN_HAND);
        }
    }

    private void approach(LivingEntity target) {
        if (this.pharaoh.isWithinMeleeAttackRange(target)) {
            return;
        }

        if (--this.repathDelay <= 0) {
            this.repathDelay = 4 + this.pharaoh.getRandom().nextInt(7);
            this.pharaoh.getNavigation().moveTo(target, this.speedModifier);
        }

        if (this.pharaoh.getNavigation().isDone()) {
            this.pharaoh.getMoveControl().setWantedPosition(
                    target.getX(), target.getY(), target.getZ(), this.speedModifier);
        }
    }

    private void landHit(LivingEntity target) {
        if (!target.isAlive() || !this.pharaoh.isWithinMeleeAttackRange(target)) {
            return;
        }
        if (this.pharaoh.level() instanceof ServerLevel level) {
            this.pharaoh.doHurtTarget(level, target);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}