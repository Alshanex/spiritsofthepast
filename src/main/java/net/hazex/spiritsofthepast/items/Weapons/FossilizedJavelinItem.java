package net.hazex.spiritsofthepast.items.Weapons;

import net.hazex.spiritsofthepast.entities.javelin.FossilizedJavelinEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FossilizedJavelinItem extends Item {

    private static final int COOLDOWN = 60;

    public FossilizedJavelinItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            FossilizedJavelinEntity javelin = new FossilizedJavelinEntity(
                    level, player, player.getX(), player.getEyeY() - 0.1, player.getZ());
            javelin.shoot(player.getLookAngle(), FossilizedJavelinEntity.SPEED);
            level.addFreshEntity(javelin);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        player.getCooldowns().addCooldown(stack, COOLDOWN);
        player.swing(hand);
        return InteractionResult.SUCCESS;
    }
}
