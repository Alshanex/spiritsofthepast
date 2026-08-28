package net.hazex.spiritsofthepast.entities.pharaoh;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.hazex.spiritsofthepast.entities.pharaoh.ai.*;
import net.hazex.spiritsofthepast.registries.SotPItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class PharaohEntity extends Monster implements GeoEntity {
    public static final int ACTION_NONE = 0;
    public static final int ACTION_SUMMONING = 1;
    public static final int ACTION_CASTING = 2;
    public static final int ACTION_INSTANT_CAST = 3;
    public static final int ACTION_SPAWNING = 4;
    public static final int ACTION_SWEEP = 5;

    private static final EntityDataAccessor<Integer> DATA_ACTION = SynchedEntityData.defineId(PharaohEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_DYING = SynchedEntityData.defineId(PharaohEntity.class, EntityDataSerializers.BOOLEAN);

    // Animations
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALKING = RawAnimation.begin().thenLoop("walking");
    private static final RawAnimation CASTING = RawAnimation.begin().thenLoop("casting");
    private static final RawAnimation SUMMONING = RawAnimation.begin().thenPlay("summoning");
    private static final RawAnimation SWING = RawAnimation.begin().thenPlay("swing");
    private static final RawAnimation INSTANT_CAST = RawAnimation.begin().thenPlay("instant_cast");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");
    private static final RawAnimation SPAWN = RawAnimation.begin().thenPlay("spawn");
    private static final RawAnimation SWEEP = RawAnimation.begin().thenPlay("sweep");

    private static final int SWING_DURATION = 8;
    private static final int DEATH_DURATION = 65;
    private static final int SPAWN_DURATION = 45;
    public static final int SWEEP_DURATION = 10;

    private static final float REDUCTION_PER_MINION = 0.10F;
    private static final float MAX_REDUCTION = 0.50F;
    public static final int MINIONS_KEPT_ON_CULL = 1;
    private static final float HEAL_PER_CULLED = 0.10F;

    public static final String MINION_TAG = "sotp_pharaoh_minion";

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            UUID.randomUUID(),
            Component.translatable("entity.spiritsofthepast.pharaoh"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.PROGRESS);

    public enum Ability { SUMMON, STORM, BOLT, SWEEP }

    private static final int GLOBAL_ABILITY_GAP = 60;

    private final int[] abilityCooldowns = new int[Ability.values().length];
    private int globalAbilityGap;

    private final List<UUID> minions = new ArrayList<>();
    private int actionTicks;
    private int spawnTicks = SPAWN_DURATION;

    private int dyingTicks;
    @Nullable
    private DamageSource deathCause;

    public PharaohEntity(EntityType<? extends PharaohEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();

        this.abilityCooldowns[Ability.SUMMON.ordinal()] = 600;
        this.abilityCooldowns[Ability.STORM.ordinal()] = 140;
        this.abilityCooldowns[Ability.BOLT.ordinal()] = 40;
        this.abilityCooldowns[Ability.SWEEP.ordinal()] = 60;
    }

    public boolean abilityReady(Ability ability) {
        return !isBusy()
                && this.globalAbilityGap <= 0
                && this.abilityCooldowns[ability.ordinal()] <= 0;
    }

    public void putAbilityOnCooldown(Ability ability, int ticks) {
        this.abilityCooldowns[ability.ordinal()] = ticks;
        this.globalAbilityGap = GLOBAL_ABILITY_GAP;
    }

    private void tickCooldowns() {
        if (this.globalAbilityGap > 0) {
            this.globalAbilityGap--;
        }
        for (int i = 0; i < this.abilityCooldowns.length; i++) {
            if (this.abilityCooldowns[i] > 0) {
                this.abilityCooldowns[i]--;
            }
        }
    }

    private void faceTarget() {
        LivingEntity target = getTarget();
        if (target == null || isDying()) {
            return;
        }

        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0F / (float) Math.PI)) - 90.0F;

        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
        this.yBodyRotO = yaw;
        this.yHeadRotO = yaw;
    }

    @Override
    public int getMaxHeadYRot() {
        return 180;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 300.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SweepAttackGoal(this));
        this.goalSelector.addGoal(2, new SummonMinionsGoal(this));
        this.goalSelector.addGoal(3, new SandstormGoal(this));
        this.goalSelector.addGoal(4, new SandBoltGoal(this));
        this.goalSelector.addGoal(5, new PharaohMeleeGoal(this, 1.0, true));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8) {
            @Override
            public boolean canUse() {
                return PharaohEntity.this.getTarget() == null && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return PharaohEntity.this.getTarget() == null && super.canContinueToUse();
            }
        });

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public int getAction() {
        return this.entityData.get(DATA_ACTION);
    }

    private void setAction(int action) {
        this.entityData.set(DATA_ACTION, action);
    }

    public void startAction(int action, int durationTicks) {
        setAction(action);
        this.actionTicks = durationTicks;
        this.getNavigation().stop();
    }

    public void releaseAction() {
        setAction(ACTION_NONE);
        this.actionTicks = 0;
    }

    public boolean isBusy() {
        return getAction() != ACTION_NONE;
    }

    public boolean isSpawning() {
        return getAction() == ACTION_SPAWNING;
    }

    // Minions

    public void trackMinion(Entity minion) {
        this.minions.add(minion.getUUID());
    }

    public int aliveMinionCount() {
        if (this.level() instanceof ServerLevel server) {
            this.minions.removeIf(uuid -> {
                Entity e = server.getEntity(uuid);
                return e == null || !e.isAlive();
            });
        }
        return this.minions.size();
    }

    public void cullMinionsForSummon(ServerLevel level) {
        while (aliveMinionCount() > MINIONS_KEPT_ON_CULL) {
            UUID oldest = this.minions.remove(0);
            Entity minion = level.getEntity(oldest);
            if (minion != null) {
                level.sendParticles(ParticleTypes.SOUL, minion.getX(), minion.getY() + 1.0, minion.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
                minion.discard();
                this.heal(this.getMaxHealth() * HEAL_PER_CULLED);
            }
        }
    }

    public <T extends Mob> void summonMinion(ServerLevel level, EntityType<T> type, Consumer<T> setup) {
        T minion = type.create(level, EntitySpawnReason.MOB_SUMMONED);
        if (minion == null) {
            return;
        }

        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double distance = 2.0 + this.random.nextDouble() * 2.0;
        double x = this.getX() + Math.cos(angle) * distance;
        double z = this.getZ() + Math.sin(angle) * distance;

        minion.snapTo(x, this.getY(), z, this.random.nextFloat() * 360.0F, 0.0F);

        minion.addTag(MINION_TAG);

        minion.targetSelector.addGoal(0, new ClearAllyTargetGoal(minion));
        minion.setPersistenceRequired();
        minion.setTarget(this.getTarget());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            minion.setDropChance(slot, 0.0F);
        }
        setup.accept(minion);

        level.addFreshEntity(minion);
        trackMinion(minion);

        level.sendParticles(ParticleTypes.POOF, x, this.getY() + 1.0, z, 16, 0.3, 0.5, 0.3, 0.02);
    }

    public void applySpearHusk(Mob husk) {
        husk.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(SotPItemRegistry.MEDJAY_SPEAR.get()));
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PARCHED_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.PARCHED_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PARCHED_DEATH;
    }

    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.PARCHED_STEP, .25f, 1f);
    }

    public void applyShieldHusk(Mob husk) {
        husk.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        husk.goalSelector.addGoal(1, new ShieldParryGoal(husk));
    }

    public void applyAxeHusk(Mob husk) {
        husk.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(SotPItemRegistry.KHOPESH.asItem()));
        husk.setItemSlot(EquipmentSlot.CHEST, new ItemStack(SotPItemRegistry.MEDJAY_CHESTPLATE.asItem()));
    }

    public void applyParchedBow(Mob parched) {
        parched.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public boolean considersEntityAsAlly(Entity other) {
        if (other == this || other.entityTags().contains(MINION_TAG)) {
            return true;
        }
        return super.considersEntityAsAlly(other);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !isAlliedTo(target) && super.canAttack(target);
    }

    public float damageReduction() {
        return Math.min(MAX_REDUCTION, aliveMinionCount() * REDUCTION_PER_MINION);
    }

    public boolean isDying() {
        return this.entityData.get(DATA_DYING);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (isDying()) {
            return false;
        }

        if(this.isAlliedTo(source.getEntity())){
            return false;
        }

        if (source.getDirectEntity() instanceof Projectile) {
            amount *= 0.5F;
        }

        float reduced = amount * (1.0F - damageReduction());

        if (reduced >= this.getHealth()) {
            beginDeathSequence(source);
            return true;
        }

        return super.hurtServer(level, source, reduced);
    }

    private void beginDeathSequence(DamageSource source) {
        this.entityData.set(DATA_DYING, true);
        this.deathCause = source;
        this.dyingTicks = 0;

        this.setHealth(1.0F);
        this.setInvulnerable(true);
        this.setNoAi(true);
        this.setTarget(null);
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
        releaseAction();

        this.bossEvent.setProgress(0.0F);
    }

    private void finishDeath() {
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.POOF,
                    this.getX(), this.getY() + 1.0, this.getZ(), 60, 0.6, 1.2, 0.6, 0.05);
            level.sendParticles(ParticleTypes.SOUL,
                    this.getX(), this.getY() + 1.0, this.getZ(), 25, 0.5, 1.0, 0.5, 0.03);


            this.setInvulnerable(false);
            this.setHealth(0.0F);
            this.die(this.deathCause == null ? this.damageSources().generic() : this.deathCause);
        }

        this.bossEvent.removeAllPlayers();

        ItemStack stack = new ItemStack(SotPItemRegistry.ANKH_STAFF.get());
        ItemEntity drop = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), stack);
        this.level().addFreshEntity(drop);

        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        super.tick();

        faceTarget();

        if (!this.level().isClientSide() && isDying() && ++this.dyingTicks >= DEATH_DURATION) {
            finishDeath();
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);

        if (isSpawning()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            this.setTarget(null);

            if (--this.spawnTicks <= 0) {
                releaseAction();
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
            return;
        }

        tickCooldowns();

        if (this.actionTicks > 0 && --this.actionTicks == 0) {
            releaseAction();
        }

        if (isBusy()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        }

        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public int getCurrentSwingDuration() {
        return SWING_DURATION;
    }

    // Boss bar

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean isPushable() {
        return !isDying();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTION, ACTION_SPAWNING);
        builder.define(DATA_DYING, false);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        this.spawnTicks = input.getIntOr("SpawnTicks", SPAWN_DURATION);
        if (this.spawnTicks <= 0) {
            setAction(ACTION_NONE);
        }

        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("SpawnTicks", isSpawning() ? this.spawnTicks : 0);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    // GeckoLib

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("locomotion", 5, test -> {
            if (isSpawning()) {
                return test.setAndContinue(SPAWN);
            }
            if (isDying()) {
                return test.setAndContinue(DEATH);
            }
            int action = getAction();
            if (action == ACTION_SWEEP) {
                return test.setAndContinue(SWEEP);
            }
            if (action == ACTION_SUMMONING) {
                return test.setAndContinue(SUMMONING);
            }
            if (action == ACTION_CASTING) {
                return test.setAndContinue(CASTING);
            }
            if (test.isMoving()) {
                return test.setAndContinue(WALKING);
            }
            return test.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>("arms", 3, test -> {
            if (isSpawning() || isDying() || getAction() == ACTION_SWEEP) {
                return PlayState.STOP;
            }
            if (getAction() == ACTION_INSTANT_CAST) {
                return test.setAndContinue(INSTANT_CAST);
            }
            if (this.swinging) {
                return test.setAndContinue(SWING);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}