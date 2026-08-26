package net.hazex.spiritsofthepast.entities.pharaoh;

import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.EntityType;

public class PharaohRenderer<R extends LivingEntityRenderState & GeoRenderState>
        extends GeoEntityRenderer<PharaohEntity, R> {

    public PharaohRenderer(EntityRendererProvider.Context context, EntityType<PharaohEntity> entityType) {
        super(context, entityType);
    }
}
