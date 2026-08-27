package net.hazex.spiritsofthepast.entities;

import net.hazex.spiritsofthepast.registries.SotPEffectRegistry;
import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class SandstoneBoltEntity extends Projectile {

    public static final double SPEED = 1.4;

    private static final double GRAVITY = 0.0;
    private static final double DRAG = 1.0;
    private static final int MAX_LIFETIME = 60;
    private static final float DAMAGE = 5.0F;

    private int life;
    @Nullable
    private UUID ownerUuid;
    @Nullable
    private Entity cachedOwner;

    public SandstoneBoltEntity(EntityType<? extends SandstoneBoltEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.blocksBuilding = false;
    }

    public SandstoneBoltEntity(Level level, @Nullable LivingEntity owner, double x, double y, double z) {
        this(SotPEntityRegistry.SANDSTONE_BOLT.get(), level);
        this.setPos(x, y, z);
        this.setOwner(owner);
    }

    public void setOwner(@Nullable Entity owner) {
        this.ownerUuid = owner == null ? null : owner.getUUID();
        this.cachedOwner = owner;
    }

    @Nullable
    public Entity getOwner() {
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }
        this.cachedOwner = null;
        if (this.ownerUuid != null && this.level() instanceof ServerLevel server) {
            this.cachedOwner = server.getEntity(this.ownerUuid);
        }
        return this.cachedOwner;
    }

    public void shoot(Vec3 direction) {
        Vec3 motion = direction.normalize().scale(SPEED);
        this.setDeltaMovement(motion);
        this.faceMotion(motion);

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    private void faceMotion(Vec3 motion) {
        double horizontal = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
        this.setYRot((float) (Math.atan2(motion.x, motion.z) * (180.0 / Math.PI)));
        this.setXRot((float) (Math.atan2(motion.y, horizontal) * (180.0 / Math.PI)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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
        Vec3 to = from.add(motion);

        Vec3 stop = to;
        boolean impacted = false;

        BlockHitResult blockHit = this.level().clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (blockHit.getType() != HitResult.Type.MISS) {
            stop = blockHit.getLocation();
            impacted = true;
        }

        Entity owner = this.getOwner();
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                this.level(), this, from, stop,
                this.getBoundingBox().expandTowards(motion).inflate(0.3),
                target -> target.isAlive()
                        && target.isPickable()
                        && !target.isSpectator()
                        && target != owner
                        && !isFriendly(target));
        if (entityHit != null) {
            stop = entityHit.getLocation();
            impacted = true;
        }

        if (!impacted) {
            this.setPos(stop.x, stop.y, stop.z);
            this.setDeltaMovement(motion);
            return;
        }

        if (this.level() instanceof ServerLevel server) {
            if (entityHit != null) {
                hurtTarget(entityHit);
            }
            this.setPos(stop.x, stop.y, stop.z);
            this.setDeltaMovement(Vec3.ZERO);
            shatter(server);
            this.discard();
        } else {
            this.setPos(stop.x, stop.y, stop.z);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    private boolean isFriendly(Entity target) {
        Entity owner = getOwner();
        return owner != null && (target == owner || owner.isAlliedTo(target));
    }

    private void hurtTarget(EntityHitResult entityHit) {
        DamageSource source = this.damageSources().thrown(this, this.getOwner());
        entityHit.getEntity().hurt(source, DAMAGE);
        if(entityHit.getEntity() instanceof LivingEntity livingEntity){
            livingEntity.addEffect(new MobEffectInstance(SotPEffectRegistry.PUNCTURED, 200, 0));
        }
    }

    private void shatter(ServerLevel level) {
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SANDSTONE.defaultBlockState()),
                this.getX(), this.getY(), this.getZ(),
                12, 0.15, 0.15, 0.15, 0.05);

        level.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SAND_BREAK, SoundSource.PLAYERS,
                0.6F, 1.2F + this.random.nextFloat() * 0.3F);
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
