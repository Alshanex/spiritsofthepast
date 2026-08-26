package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Items;

public class ShieldParryGoal extends Goal {

    private static final double PARRY_RANGE = 6.0;
    private static final int MIN_HOLD = 20;
    private static final int MAX_HOLD = 50;
    private static final int MIN_GAP = 30;
    private static final int MAX_GAP = 90;
    private static final float PARRY_CHANCE = 0.55F;

    private final Mob mob;
    private int cooldown;
    private int holdTicks;

    public ShieldParryGoal(Mob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }
        if (!this.mob.getOffhandItem().is(Items.SHIELD)) {
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.mob.distanceToSqr(target) > PARRY_RANGE * PARRY_RANGE) {
            return false;
        }
        return this.mob.getRandom().nextFloat() < PARRY_CHANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.holdTicks > 0
                && this.mob.getTarget() != null
                && this.mob.getOffhandItem().is(Items.SHIELD);
    }

    @Override
    public void start() {
        this.holdTicks = MIN_HOLD + this.mob.getRandom().nextInt(MAX_HOLD - MIN_HOLD);
        this.mob.startUsingItem(InteractionHand.OFF_HAND);
    }

    @Override
    public void tick() {
        this.holdTicks--;
    }

    @Override
    public void stop() {
        this.mob.stopUsingItem();
        this.cooldown = MIN_GAP + this.mob.getRandom().nextInt(MAX_GAP - MIN_GAP);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}