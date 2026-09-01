package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.sandstone.SandfallEmitterEntity;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.SotPSoundRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SandstormGoal extends Goal {

    private static final int COOLDOWN = 400;
    private static final int MAX_CHANNEL_TICKS = 140;
    private static final double MAX_RANGE = 20.0;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private SandfallEmitterEntity emitter;

    public SandstormGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.STORM)) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        return target != null
                && target.isAlive()
                && this.pharaoh.distanceToSqr(target) <= MAX_RANGE * MAX_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.elapsed >= MAX_CHANNEL_TICKS) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        return this.emitter != null && this.emitter.isAlive();
    }

    @Override
    public void start() {
        this.elapsed = 0;

        if (!(this.pharaoh.level() instanceof ServerLevel level)) {
            return;
        }

        this.emitter = new SandfallEmitterEntity(level, this.pharaoh.getX(), this.pharaoh.getY(), this.pharaoh.getZ());
        this.emitter.setOwner(this.pharaoh);
        level.addFreshEntity(this.emitter);

        this.pharaoh.startAction(PharaohEntity.ACTION_CASTING, -1);
        level.playSound(null, this.pharaoh.blockPosition(),
                SotPSoundRegistry.PHARAOH_SANDSTORM.get(), SoundSource.HOSTILE, 3.5F, 1F);
        level.playSound(null, this.pharaoh.blockPosition(),
                SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 1.5F, 1.2F);
    }

    @Override
    public void tick() {
        this.elapsed++;
    }

    @Override
    public void stop() {
        if (this.emitter != null && this.emitter.isAlive()) {
            this.emitter.discard();
        }
        this.emitter = null;
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.STORM, COOLDOWN);
        this.pharaoh.releaseAction();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}