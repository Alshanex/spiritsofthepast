package net.hazex.spiritsofthepast.entities.pharaoh.ai;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class SlamAttackGoal extends Goal {

    private static final int COOLDOWN = 200;
    private static final int IMPACT_TICK = 14;
    private static final double RADIUS = 8.0;
    private static final float DAMAGE = 12.0F;
    private static final double KNOCKBACK = 0.8;

    private final PharaohEntity pharaoh;
    private int elapsed;
    private boolean slammed;

    public SlamAttackGoal(PharaohEntity pharaoh) {
        this.pharaoh = pharaoh;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!this.pharaoh.abilityReady(PharaohEntity.Ability.SLAM)) {
            return false;
        }
        LivingEntity target = this.pharaoh.getTarget();
        return target != null
                && target.isAlive()
                && this.pharaoh.distanceToSqr(target) <= RADIUS * RADIUS;
    }

    @Override
    public boolean canContinueToUse() {
        return this.elapsed < PharaohEntity.SLAM_DURATION
                && this.pharaoh.getAction() == PharaohEntity.ACTION_SLAM;
    }

    @Override
    public void start() {
        this.elapsed = 0;
        this.slammed = false;
        this.pharaoh.startAction(PharaohEntity.ACTION_SLAM, PharaohEntity.SLAM_DURATION);
    }

    @Override
    public void tick() {
        this.elapsed++;

        if (this.slammed || this.elapsed < IMPACT_TICK) {
            return;
        }
        this.slammed = true;

        if (!(this.pharaoh.level() instanceof ServerLevel level)) {
            return;
        }

        Vec3 centre = this.pharaoh.position();
        AABB area = new AABB(centre, centre).inflate(RADIUS);

        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, area,
                candidate -> candidate != this.pharaoh
                        && candidate.isAlive()
                        && !candidate.isSpectator()
                        && !this.pharaoh.isAlliedTo(candidate))) {

            if (victim.getBoundingBox().getCenter().distanceToSqr(centre) > RADIUS * RADIUS) {
                continue;
            }

            victim.hurtServer(level, this.pharaoh.damageSources().mobAttack(this.pharaoh), DAMAGE);
            victim.knockback(KNOCKBACK,
                    this.pharaoh.getX() - victim.getX(),
                    this.pharaoh.getZ() - victim.getZ());
            victim.hurtMarked = true;
        }

        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK,
                        Blocks.SANDSTONE.defaultBlockState()),
                centre.x, centre.y + 0.2, centre.z, 60, RADIUS * 0.5, 0.1, RADIUS * 0.5, 0.25);
        level.playSound(null, this.pharaoh.blockPosition(),
                SoundEvents.MACE_SMASH_GROUND, SoundSource.HOSTILE, 1.2F, 0.9F);
    }

    @Override
    public void stop() {
        this.pharaoh.putAbilityOnCooldown(PharaohEntity.Ability.SLAM, COOLDOWN);
        if (this.pharaoh.getAction() == PharaohEntity.ACTION_SLAM) {
            this.pharaoh.releaseAction();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
