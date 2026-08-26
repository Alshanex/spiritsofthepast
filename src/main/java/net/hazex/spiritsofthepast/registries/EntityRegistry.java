package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.entities.SandShardEntity;
import net.hazex.spiritsofthepast.entities.SandfallEmitterEntity;
import net.hazex.spiritsofthepast.entities.SandstoneBoltEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntityRegistry {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(SpiritsofthePast.MODID);

    public static final Supplier<EntityType<SandfallEmitterEntity>> SANDFALL_EMITTER =
            ENTITY_TYPES.registerEntityType(
                    "sandfall_emitter",
                    SandfallEmitterEntity::new,
                    MobCategory.MISC,
                    builder -> builder
                            .sized(0.1f, 0.1f)
                            .noSummon()
                            .clientTrackingRange(0)
                            .updateInterval(Integer.MAX_VALUE)
            );

    public static final Supplier<EntityType<SandShardEntity>> SAND_SHARD =
            ENTITY_TYPES.registerEntityType(
                    "sand_shard",
                    SandShardEntity::new,
                    MobCategory.MISC,
                    builder -> builder
                            .sized(0.98f, 0.98f)
                            .noSummon()
                            .noSave()
                            .fireImmune()
                            .clientTrackingRange(10)
                            .updateInterval(20)
            );

    public static final Supplier<EntityType<SandstoneBoltEntity>> SANDSTONE_BOLT =
            ENTITY_TYPES.registerEntityType(
                    "sandstone_bolt",
                    SandstoneBoltEntity::new,
                    MobCategory.MISC,
                    builder -> builder
                            .sized(0.3f, 0.3f)
                            .noSummon()
                            .noSave()
                            .fireImmune()
                            .clientTrackingRange(4)
                            .updateInterval(1)
            );

    public static void register(IEventBus modEventBus){
        ENTITY_TYPES.register(modEventBus);
    }
}
