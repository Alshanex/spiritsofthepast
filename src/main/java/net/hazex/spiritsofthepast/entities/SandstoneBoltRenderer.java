package net.hazex.spiritsofthepast.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class SandstoneBoltRenderer extends EntityRenderer<SandstoneBoltEntity, SandstoneBoltRenderer.BoltRenderState> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "sandstone_bolt"), "main");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "textures/projectile/sandstone_bolt.png");

    private static final float SCALE = 1.0F;

    private final ModelPart root;
    private final RenderType renderType;

    public SandstoneBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.root = context.bakeLayer(LAYER);
        this.renderType = RenderTypes.entityCutout(TEXTURE);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        CubeListBuilder spike = CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-8.0F, -2.5F, 0.0F, 16, 5, 0);

        root.addOrReplaceChild("spike_a", spike, PartPose.ZERO);
        root.addOrReplaceChild("spike_b", spike,
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) (Math.PI / 2.0), 0.0F, 0.0F));

        CubeListBuilder cap = CubeListBuilder.create()
                .texOffs(0, 5)
                .addBox(-2.5F, -2.5F, 0.0F, 5, 5, 0);

        root.addOrReplaceChild("cap", cap,
                PartPose.offsetAndRotation(-8.0F, 0.0F, 0.0F, 0.0F, (float) (Math.PI / 2.0), 0.0F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public BoltRenderState createRenderState() {
        return new BoltRenderState();
    }

    @Override
    public void extractRenderState(SandstoneBoltEntity entity, BoltRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.boltYRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.boltXRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(BoltRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.boltYRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.boltXRot));
        poseStack.scale(SCALE, SCALE, SCALE);

        collector.submitModelPart(
                this.root,
                poseStack,
                this.renderType,
                state.lightCoords,
                OverlayTexture.NO_OVERLAY,
                null);

        poseStack.popPose();
    }

    public static class BoltRenderState extends EntityRenderState {
        public float boltYRot;
        public float boltXRot;
    }
}
