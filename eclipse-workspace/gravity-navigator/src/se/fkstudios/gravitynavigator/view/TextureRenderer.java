package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.resources.GraphicResource;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Object drawing MapObjectModel's TextureResources.
 * @author kristofer
 */
public class TextureRenderer extends Renderer {
	
	public TextureRenderer(ShapeRenderer shapeRenderer,
			SpriteBatch spriteBatch, 
			float mapWidth, 
			float mapHeight) {
		super(shapeRenderer, spriteBatch, mapWidth, mapHeight);
	}

	@Override
	protected TextureRegion getTextureRegion(GraphicResource resource) {
		TextureResource textureResource = (TextureResource)resource;
		return TextureLoader.getInstance().getTextureRegion(textureResource.textureName);
	}
}