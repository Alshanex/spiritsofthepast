package net.hazex.spiritsofthepast.entities.javelin;

import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class FossilizedJavelinEntity extends Projectile {

    public static final double SPEED = 1.6;

    private static final double GRAVITY = 0.05;
    private static final double DRAG = 0.99;
    private static final int MAX_LIFETIME = 200;
    private static final float DAMAGE = 8.0F;

    private static final int STUCK_DURATION = 50;

    private static final double EMBED_OFFSET = 0.9;

    private int life;
    private boolean stuck;
    private int stuckTicks;

    public FossilizedJavelinEntity(EntityType<? extends FossilizedJavelinEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.blocksBuilding = false;
    }

    public FossilizedJavelinEntity(Level level, @Nullable LivingEntity owner, double x, double y, double z) {
        this(SotPEntityRegistry.FOSSILIZED_JAVELIN.get(), level);
        this.setPos(x, y, z);
        this.setOwner(owner);
    }

    public void shoot(Vec3 direction, double speed) {
        Vec3 motion = direction.normalize().scale(speed);
        this.setDeltaMovement(motion);
        faceMotion(motion);
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    private void faceMotion(Vec3 motion) {
        double horizontal = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        this.setYRot((float) (Math.atan2(motion.x, motion.z) * (180.0 / Math.PI)));
        this.setXRot((float) (Math.atan2(motion.y, horizontal) * (180.0 / Math.PI)));
    }

    private boolean isFriendly(Entity target) {
        Entity owner = getOwner();
        return owner != null && (target == owner || owner.isAlliedTo(target));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.stuck) {
            if (++this.stuckTicks >= STUCK_DURATION && !this.level().isClientSide()) {
                this.discard();
            }
            return;
        }

        if (++this.life > MAX_LIFETIME) {
            this.discard();
            return;
        }

        Vec3 motion = this.getDeltaMovement().add(0.0, -GRAVITY, 0.0).scale(DRAG);
        Vec3 from = this.position();
        Vec3 to = from.add(motion);

        Vec3 stop = to;
        boolean impacted = false;

        BlockHitResult blockHit = this.level().clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (blockHit.getType() != HitResult.Type.MISS) {
            stop = blockHit.getLocation();
            impacted = true;
        }

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                this.level(), this, from, stop,
                this.getBoundingBox().expandTowards(motion).inflate(0.3),
                target -> target.isAlive()
                        && target.isPickable()
                        && !target.isSpectator()
                        && !isFriendly(target));
        if (entityHit != null) {
            stop = entityHit.getLocation();
            impacted = true;
        }

        if (!impacted) {
            this.setPos(stop.x, stop.y, stop.z);
            this.setDeltaMovement(motion);
            faceMotion(motion);
            return;
        }

        if (this.level() instanceof ServerLevel server) {
            if (entityHit != null) {
                entityHit.getEntity().hurtServer(server,
                        this.damageSources().thrown(this, this.getOwner()), DAMAGE);
            }
            impactEffects(server);
        }

        this.setDeltaMovement(Vec3.ZERO);
        this.stuck = true;
        this.stuckTicks = 0;

        if (entityHit != null) {
            this.setPos(stop.x, stop.y, stop.z);
            if (this.level() instanceof ServerLevel) {
                this.discard();
            }
            return;
        }

        Vec3 resting = stop.subtract(motion.normalize().scale(EMBED_OFFSET));
        this.setPos(resting.x, resting.y, resting.z);
    }

    private void impactEffects(ServerLevel level) {
        level.sendParticles(ParticleTypes.CRIT,
                this.getX(), this.getY(), this.getZ(), 10, 0.2, 0.2, 0.2, 0.1);
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.TRIDENT_HIT_GROUND, SoundSource.PLAYERS,
                0.8F, 1.1F + this.random.nextFloat() * 0.3F);
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
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
