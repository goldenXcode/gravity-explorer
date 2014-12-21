package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a texture. Either a texture region in a TextureAtlas or a stand alone texture.
 * Use TextureLoader to obtain the texture in view.
 * 
 * @author kristofer
 */
public class AtlasResource extends GraphicResource implements TextureRegionRenderable {

	private TextureRegion textureRegion;
	
	public final String textureRegionName;
	
	public AtlasResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, String textureRegionName) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
				height, visible, minRenderScale, maxRenderScale);
		this.textureRegionName = textureRegionName;
		textureRegion = ResourceLoader.getInstance().getTextureRegion(textureRegionName);
	}	

	@Override
	public TextureRegion getTextureRegion() {
		return textureRegion;
	}
}