package net.hazex.spiritsofthepast.blocks.tomb;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;

public class TombRenderer<T extends BlockEntity & GeoAnimatable, R extends BlockEntityRenderState & GeoRenderState>
        extends GeoBlockRenderer<T, R> {

    public TombRenderer(BlockEntityRendererProvider.Context context, BlockEntityType<T> blockEntityType) {
        super(context, blockEntityType);
    }

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();

        return new AABB(
                pos.getX() - 2, pos.getY(),     pos.getZ() - 2,
                pos.getX() + 3, pos.getY() + 4, pos.getZ() + 3);
    }
}
