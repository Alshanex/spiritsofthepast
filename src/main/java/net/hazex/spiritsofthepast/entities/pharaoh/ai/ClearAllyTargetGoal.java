package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class ClearAllyTargetGoal extends Goal {

    private final Mob mob;

    public ClearAllyTargetGoal(Mob mob) {
        this.mob = mob;
    }

    private static boolean isAlly(LivingEntity target) {
        return target.entityTags().contains(PharaohEntity.MINION_TAG) || target instanceof PharaohEntity;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && isAlly(target);
    }

    @Override
    public void start() {
        this.mob.setTarget(null);
        this.mob.setLastHurtByMob(null);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}
