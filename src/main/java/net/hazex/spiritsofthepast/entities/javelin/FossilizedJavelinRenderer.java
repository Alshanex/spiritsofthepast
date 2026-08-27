package net.hazex.spiritsofthepast.entities.javelin;

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

public class FossilizedJavelinRenderer extends EntityRenderer<FossilizedJavelinEntity, FossilizedJavelinRenderer.JavelinRenderState> {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "fossilized_javelin"), "main");

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "textures/entity/fossilized_javelin.png");

    private static final float DIAGONAL_ROLL = (float) (Math.PI * 3.0 / 4.0);

    private static final float SCALE = 1.0F;

    private final ModelPart root;
    private final RenderType renderType;

    public FossilizedJavelinRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.root = context.bakeLayer(LAYER);
        this.renderType = RenderTypes.entityCutout(TEXTURE);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        CubeListBuilder plane = CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-16.0F, -16.0F, 0.0F, 32, 32, 0);

        root.addOrReplaceChild("plane_a", plane,
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, DIAGONAL_ROLL));

        PartDefinition tilt = root.addOrReplaceChild("tilt", CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, (float) (Math.PI / 2.0), 0.0F, 0.0F));
        tilt.addOrReplaceChild("plane_b", plane,
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, DIAGONAL_ROLL));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public JavelinRenderState createRenderState() {
        return new JavelinRenderState();
    }

    @Override
    public void extractRenderState(FossilizedJavelinEntity entity, JavelinRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.javelinYRot = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        state.javelinXRot = Mth.rotLerp(partialTick, entity.xRotO, entity.getXRot());
    }

    @Override
    public void submit(JavelinRenderState state,
                       PoseStack poseStack,
                       SubmitNodeCollector collector,
                       CameraRenderState cameraState) {
        super.submit(state, poseStack, collector, cameraState);

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.javelinYRot - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.javelinXRot));
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

    public static class JavelinRenderState extends EntityRenderState {
        public float javelinYRot;
        public float javelinXRot;
    }
}
