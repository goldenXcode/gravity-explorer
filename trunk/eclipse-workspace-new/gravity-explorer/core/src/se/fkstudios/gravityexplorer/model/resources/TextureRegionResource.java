package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class TextureRegionResource extends GraphicResource {	
	
	public TextureRegionResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale) {
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
				height, visible, minRenderScale, maxRenderScale);
	}

	public abstract TextureRegion getTextureRegion();
}
