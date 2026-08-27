package net.hazex.spiritsofthepast.entities.sandstone;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SandShardRenderer extends EntityRenderer<SandShardEntity, SandShardRenderer.SandShardRenderState> {

    public SandShardRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.4F;
    }

    @Override
    public SandShardRenderState createRenderState() {
        return new SandShardRenderState();
    }

    @Override
    public void extractRenderState(SandShardEntity entity, SandShardRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        BlockState blockState = entity.getBlockState();
        state.hasModel = !blockState.isAir();

        state.spin = (entity.tickCount + partialTick) * 3.0F;

        if (!state.hasModel) {
            return;
        }

        Level level = entity.level();
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());

        MovingBlockRenderState moving = state.movingBlock;
        moving.blockState = blockState;

        moving.blockPos = pos;
        moving.randomSeedPos = pos;
        moving.biome = level.getBiome(pos);
        moving.lightEngine = level.getLightEngine();
    }

    @Override
    public void submit(SandShardRenderState state,
                       PoseStack poseStack,
                       SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);

        if (!state.hasModel) {
            return;
        }

        poseStack.pushPose();

        poseStack.translate(0.0F, 0.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        collector.submitMovingBlock(poseStack, state.movingBlock);

        poseStack.popPose();
    }

    public static class SandShardRenderState extends EntityRenderState {
        public final MovingBlockRenderState movingBlock = new MovingBlockRenderState();
        public boolean hasModel;
        public float spin;
    }
}
