package net.hazex.spiritsofthepast.entities;

import net.hazex.spiritsofthepast.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nullable;
import java.util.UUID;

public class SandfallEmitterEntity extends Entity {

    private static final int DURATION = 100;
    private static final double RADIUS = 15.0;
    private static final double SPAWN_HEIGHT = 14.0;
    private static final int SHARDS_PER_TICK = 2;

    private int ticksLeft = DURATION;

    @Nullable
    private UUID ownerUuid;
    @Nullable
    private Entity cachedOwner;

    public SandfallEmitterEntity(EntityType<? extends SandfallEmitterEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public SandfallEmitterEntity(Level level, double x, double y, double z) {
        this(EntityRegistry.SANDFALL_EMITTER.get(), level);
        this.setPos(x, y, z);
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

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            return;
        }

        if (this.ticksLeft-- <= 0) {
            this.discard();
            return;
        }

        Entity owner = this.getOwner();
        if (owner != null && owner.level() == this.level()) {
            this.setPos(owner.getX(), owner.getY(), owner.getZ());
        }

        for (int i = 0; i < SHARDS_PER_TICK; i++) {
            spawnShard();
        }
    }

    private void spawnShard() {
        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double distance = Math.sqrt(this.random.nextDouble()) * RADIUS;

        double x = this.getX() + Math.cos(angle) * distance;
        double z = this.getZ() + Math.sin(angle) * distance;
        double y = this.getY() + SPAWN_HEIGHT;

        if (!this.level().getBlockState(BlockPos.containing(x, y, z)).isAir()) {
            return;
        }

        BlockState shardState = Blocks.SANDSTONE.defaultBlockState();
        SandShardEntity shard = new SandShardEntity(this.level(), x, y, z, shardState);

        shard.setCollisionStartY(this.getY());
        shard.setDeltaMovement(
                (this.random.nextDouble() - 0.5) * 0.04,
                -0.1,
                (this.random.nextDouble() - 0.5) * 0.04
        );
        this.level().addFreshEntity(shard);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.ticksLeft = input.getIntOr("TicksLeft", DURATION);
        String uuid = input.getStringOr("Owner", "");
        this.ownerUuid = uuid.isEmpty() ? null : UUID.fromString(uuid);
        this.cachedOwner = null;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("TicksLeft", this.ticksLeft);
        if (this.ownerUuid != null) {
            output.putString("Owner", this.ownerUuid.toString());
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
