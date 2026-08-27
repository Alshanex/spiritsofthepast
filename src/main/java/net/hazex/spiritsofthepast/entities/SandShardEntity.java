package net.hazex.spiritsofthepast.entities;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SandShardEntity extends Projectile {

    private static final EntityDataAccessor<Integer> DATA_BLOCK_STATE =
            SynchedEntityData.defineId(SandShardEntity.class, EntityDataSerializers.INT);

    private static final int MAX_LIFETIME = 200;
    private static final double GRAVITY = 0.045;
    private static final double DRAG = 0.98;
    private static final float IMPACT_DAMAGE = 4.0F;
    private static final double SPLASH_RADIUS = 3.0;
    private static final double COLLISION_HEADROOM = 3.0;
    private static final int COLLISION_STEPS = 4;

    private static final boolean DEBUG_SPLASH = false;

    private int life;

    private double collisionStartY = Double.POSITIVE_INFINITY;

    public SandShardEntity(EntityType<? extends SandShardEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.blocksBuilding = false;
    }

    public SandShardEntity(Level level, double x, double y, double z, BlockState state) {
        this(SotPEntityRegistry.SAND_SHARD.get(), level);
        this.setPos(x, y, z);
        this.setBlockState(state);
    }

    private boolean isFriendly(Entity target) {
        Entity owner = getOwner();
        return owner != null && (target == owner || owner.isAlliedTo(target));
    }

    public void setBlockState(BlockState state) {
        this.entityData.set(DATA_BLOCK_STATE, Block.getId(state));
    }

    public BlockState getBlockState() {
        return Block.stateById(this.entityData.get(DATA_BLOCK_STATE));
    }

    public void setCollisionStartY(double y) {
        this.collisionStartY = y;
    }

    @Override
    public void tick() {
        super.tick();

        if (++this.life > MAX_LIFETIME) {
            this.discard();
            return;
        }

        Vec3 motion = this.getDeltaMovement().add(0.0, -GRAVITY, 0.0).scale(DRAG);
        Vec3 from = this.position();

        Vec3 stop = from.add(motion);
        boolean impacted = false;
        EntityHitResult entityHit = null;

        if (stop.y <= this.collisionStartY + COLLISION_HEADROOM) {
            double clear = freeFraction(motion);
            if (clear < 1.0) {
                stop = from.add(motion.scale(clear));
                impacted = true;
            }
        }

        Entity struck = findStruckEntity(motion);
        if (struck != null) {
            entityHit = new EntityHitResult(struck);
            impacted = true;
        }

        this.setPos(stop.x, stop.y, stop.z);
        this.setDeltaMovement(impacted ? Vec3.ZERO : motion);

        if (impacted && this.level() instanceof ServerLevel server) {
            onImpact(server, entityHit);
        }
    }

    private double freeFraction(Vec3 motion) {
        AABB box = this.getBoundingBox();
        for (int step = COLLISION_STEPS; step >= 1; step--) {
            double fraction = (double) step / COLLISION_STEPS;
            if (this.level().noCollision(this, box.move(motion.scale(fraction)))) {
                return fraction;
            }
        }
        return 0.0;
    }

    @Nullable
    private Entity findStruckEntity(Vec3 motion) {
        AABB sweep = this.getBoundingBox().expandTowards(motion).inflate(0.05);

        Entity nearest = null;
        double nearestDist = Double.MAX_VALUE;

        for (Entity candidate : this.level().getEntities(this, sweep,
                target -> target.isAlive()
                        && target.isPickable()
                        && !target.isSpectator()
                        && !isFriendly(target))) {
            double dist = candidate.distanceToSqr(this);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private void onImpact(ServerLevel level, @Nullable EntityHitResult entityHit) {
        BlockState state = this.getBlockState();

        splashDamage(level);

        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                this.getX(), this.getY() + 0.25, this.getZ(),
                40, SPLASH_RADIUS * 0.4, 0.3, SPLASH_RADIUS * 0.4, 0.15);

        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SAND_BREAK, SoundSource.BLOCKS,
                0.7F, 0.8F + this.random.nextFloat() * 0.4F);

        this.discard();
    }

    private void splashDamage(ServerLevel level) {
        if (IMPACT_DAMAGE <= 0.0F) {
            return;
        }

        Vec3 centre = this.position();
        AABB area = new AABB(centre, centre).inflate(SPLASH_RADIUS);
        DamageSource source = this.damageSources().fallingBlock(this);

        if (DEBUG_SPLASH) {
            SpiritsofthePast.LOGGER.debug("[shard] impact at " + centre
                    + " radius=" + SPLASH_RADIUS
                    + " box=" + area
                    + " candidates=" + level.getEntitiesOfClass(LivingEntity.class, area, t -> true).size());
        }

        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, area,
                target -> target.isAlive() && !target.isSpectator() && !isFriendly(target))) {

            double distSqr = victim.getBoundingBox().getCenter().distanceToSqr(centre);
            if (DEBUG_SPLASH) {
                SpiritsofthePast.LOGGER.debug("[shard]   candidate " + victim.getName().getString()
                        + " dist=" + Math.sqrt(distSqr)
                        + " friendly=" + isFriendly(victim));
            }
            if (distSqr > SPLASH_RADIUS * SPLASH_RADIUS) {
                continue;
            }

            victim.invulnerableTime = 0;
            victim.hurtServer(level, source, IMPACT_DAMAGE);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_BLOCK_STATE, Block.getId(Blocks.SANDSTONE.defaultBlockState()));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.life = input.getIntOr("Life", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Life", this.life);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return false;
    }
}