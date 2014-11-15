package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class TextureResource extends GraphicResource implements TextureRegionResource {

	private TextureRegion textureRegion;
	public final String textureName;
	
	public TextureResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, String textureName) {
		super(positionOffset, visible, minRenderScale, maxRenderScale);
		this.textureName = textureName;
		this.textureRegion = new TextureRegion();
	}
	
	public TextureResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height, String textureName) {
		super(positionOffset, visible, minRenderScale, maxRenderScale, width, height);
		this.textureName = textureName;
		this.textureRegion = new TextureRegion();
	}

	@Override
	public TextureRegion getTextureRegion() {
		Texture texture = TextureLoader.getInstance().getTexture(textureName);
		textureRegion.setRegion(texture);
		return textureRegion;
	}
}