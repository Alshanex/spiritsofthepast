package net.hazex.spiritsofthepast.items.Armor.MedjayArmor;

import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class MedjayArmorRenderer<R extends HumanoidRenderState & GeoRenderState> extends GeoArmorRenderer<MedjayArmor, R> {
	public MedjayArmorRenderer() {
		super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "armor/medjay_armor")));
	}
}