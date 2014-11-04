package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class TextureRegionResource extends GraphicResource {

	public TextureRegionResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale) {
		super(positionOffset, visible, minRenderScale, maxRenderScale);
	}
	
	public TextureRegionResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height) {
		super(positionOffset, visible, minRenderScale, maxRenderScale, width, height);
	}
	
	public abstract TextureRegion getTextureRegion();
}
