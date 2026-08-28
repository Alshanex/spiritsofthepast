package net.hazex.spiritsofthepast.items.Armor.FossilArmor;

import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class FossilArmorRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<FossilArmor, R> {
	public FossilArmorRenderer() {
		super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "armor/fossil_armor")));

		withRenderLayer(AutoGlowingGeoLayer::new);
	}
}