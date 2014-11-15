package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a texture. Either a texture region in a TextureAtlas or a stand alone texture.
 * Use TextureLoader to obtain the texture in view.
 * 
 * @author kristofer
 */
public class TextureAtlasResource extends GraphicResource implements TextureRegionResource {

	public final String textureRegionName;
	
	public TextureAtlasResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, String textureRegionName) {
		super(positionOffset, visible, minRenderScale, maxRenderScale);
		this.textureRegionName = textureRegionName;
	}
	
	public TextureAtlasResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height, String textureRegionName) {
		super(positionOffset, visible, minRenderScale, maxRenderScale, width, height);
		this.textureRegionName = textureRegionName;
	}

	@Override
	public TextureRegion getTextureRegion() {
		return TextureLoader.getInstance().getTextureRegion(textureRegionName);
	}
}