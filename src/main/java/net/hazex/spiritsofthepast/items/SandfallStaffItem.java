package net.hazex.spiritsofthepast.items;

import net.hazex.spiritsofthepast.entities.SandfallEmitterEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SandfallStaffItem extends Item {

    private static final int COOLDOWN = 200;

    public SandfallStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            Vec3 centre = player.position();
            SandfallEmitterEntity emitter = new SandfallEmitterEntity(level, centre.x, centre.y, centre.z);
            emitter.setOwner(player);
            level.addFreshEntity(emitter);

            level.playSound(null, centre.x, centre.y, centre.z, SoundEvents.SAND_PLACE, SoundSource.PLAYERS, 1.5F, 0.5F);
        }

        player.getCooldowns().addCooldown(stack, COOLDOWN);
        return InteractionResult.SUCCESS;
    }
}
