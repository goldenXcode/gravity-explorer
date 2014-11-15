package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TextureResource extends GraphicResource implements TextureRegionResource {

	private TextureRegion textureRegion;
	public final String textureName;
	
	public TextureResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, String textureName) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
				height, visible, minRenderScale, maxRenderScale);
		this.textureName = textureName;
		textureRegion = new TextureRegion();
	}	

	@Override
	public TextureRegion getTextureRegion() {
		Texture texture = TextureLoader.getInstance().getTexture(textureName);
		textureRegion.setRegion(texture);
		return textureRegion;
	}
}