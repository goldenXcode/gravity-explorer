package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TextureRegionBinding extends GraphicResourceBinding {	
	
	private TextureRegion textureRegion;
	
	public TextureRegionBinding(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, TextureRegion textureRegion) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize, width, height, visible, minRenderScale, maxRenderScale);
		this.textureRegion = textureRegion;
	}	

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}
}