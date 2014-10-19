package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MapObjectTextureRegionRenderer extends TextureRegionRenderer {

	public MapObjectTextureRegionRenderer(SpriteBatch spriteBatch, 
			float periodicityWidth,
			float periodicityHeight) {
		super(spriteBatch, periodicityWidth, periodicityHeight);
	}

	/**
	 * Draws a TextureResource.
	 * @param mapObject MapObjectModel owning the TextureResource.
	 * @param textureResource TextureResource to be drawn.
	 * @param viewport 
	 */
	public void render(MapObjectModel mapObject, TextureRegionResource resource, GameplayCamera camera) {
		float width;
		float height;	
		if (resource.useParentSize) {
			width = Utility.getScreenCoordinate(mapObject.getWidth());
			height = Utility.getScreenCoordinate(mapObject.getHeight());
		}
		else {
			width = Utility.getScreenCoordinate(resource.width);
			height = Utility.getScreenCoordinate(resource.height);
		}
		float textureOriginX = width / 2;
		float textureOriginY = height / 2;
		float positionX = Utility.getScreenCoordinate(mapObject.getPosition().x) - textureOriginX + Utility.getScreenCoordinate(resource.positionOffset.x);  
		float positionY = Utility.getScreenCoordinate(mapObject.getPosition().y) - textureOriginY + Utility.getScreenCoordinate(resource.positionOffset.y);
		this.render(resource, positionX, positionY, width, height, mapObject.getRotation(), camera);	
	}
}
