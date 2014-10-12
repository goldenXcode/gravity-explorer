package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class TextureRegionResource {

	public boolean useParentSize;
	public Vector2 positionOffset;
	public float width;
	public float height;
	public boolean visible;
	public float minRenderScale;
	public float maxRenderScale;
	
	public TextureRegionResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		this.minRenderScale = minRenderScale;
		this.maxRenderScale = maxRenderScale;
		width = -1;
		height = -1;
		useParentSize = true;
	}
	
	public TextureRegionResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		this.minRenderScale = minRenderScale;
		this.maxRenderScale = maxRenderScale;
		this.width = width;
		this.height = height;
		useParentSize = false;
	}
	
	public abstract TextureRegion getTextureRegion();
}
