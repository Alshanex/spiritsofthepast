package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SummonMinionsGoal extends Goal {

    private static final int COOLDOWN = 700;
    private static final int CAST_TICKS = 20;
    private static final int SPAWN_AT_TICK = 10;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private boolean spawned;

    public SummonMinionsGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.SUMMON)) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return this.elapsed < CAST_TICKS
                && this.pharaoh.getAction() == PharaohEntity.ACTION_SUMMONING;
    }

    @Override
    public void start() {
        this.elapsed = 0;
        this.spawned = false;
        this.pharaoh.startAction(PharaohEntity.ACTION_SUMMONING, CAST_TICKS);
        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SotPSoundRegistry.SUMMON_MINIONS.get(), SoundSource.HOSTILE, 0.75F, 1F);
        this.pharaoh.level().playSound(null, this.pharaoh.blockPosition(),
                SotPSoundRegistry.PHARAOH_SUMMON_1.get(), SoundSource.HOSTILE, 1.5F, 1F);
    }

    @Override
    public void tick() {
        this.elapsed++;

        if (this.spawned || this.elapsed < SPAWN_AT_TICK) {
            return;
        }
        this.spawned = true;

        if (!(this.pharaoh.level() instanceof ServerLevel level)) {
            return;
        }

        this.pharaoh.cullMinionsForSummon(level);

        this.pharaoh.summonMinion(level, EntityType.HUSK, this.pharaoh::applySpearHusk);
        this.pharaoh.summonMinion(level, EntityType.HUSK, this.pharaoh::applyShieldHusk);
        this.pharaoh.summonMinion(level, EntityType.HUSK, this.pharaoh::applyKhopeshHusk);
        this.pharaoh.summonMinion(level, EntityType.PARCHED, this.pharaoh::applyParchedBow);
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.SUMMON, COOLDOWN);
        if (this.pharaoh.getAction() == PharaohEntity.ACTION_SUMMONING) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}