package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.TextureMapObjectModel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Renders SimpleMapObjects.
 * @author kristofer
 */
public class TextureMapObjectRenderer extends Renderer {

	/** debug rendering **/
	private ShapeRenderer debugRenderer;
	
	/**
	 * Render a SimpleMapObject.
	 * @param spriteBatch
	 * @param mapObject
	 */
	public void render(SpriteBatch spriteBatch, TextureMapObjectModel mapObject) {
		
		spriteBatch.begin();
		
		TextureRegion texRegion = TextureLoader.getInstance().getTextureRegion(mapObject.getTextureName());
		
		float offsetX = mapObject.getWidth() * 0.5f;
		float offsetY = mapObject.getHeight() * 0.5f;
		
		spriteBatch.draw(texRegion, 
				(mapObject.getPosition().x - offsetX) * RenderDefs.PIXELS_PER_UNIT, 
				(mapObject.getPosition().y - offsetY) * RenderDefs.PIXELS_PER_UNIT, 
				(mapObject.getWidth() / 2) * RenderDefs.PIXELS_PER_UNIT, 
				(mapObject.getHeight() / 2) * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * RenderDefs.PIXELS_PER_UNIT, 
				1.0f,
				1.0f,
				mapObject.getRotation());
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), mapObject);
	}
	
	private void debugRender(Matrix4 projectionMatrix, TextureMapObjectModel mapObject) {
		
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();
		
		debugRenderer.setProjectionMatrix(projectionMatrix);

		Vector2 position = mapObject.getPosition();
		float mapObjectOffsetX = mapObject.getWidth() * 0.5f;
		float mapObjectOffsetY = mapObject.getHeight() * 0.5f;
			
		debugRenderer.begin(ShapeType.Line);
		
		debugRenderer.setColor(RenderDefs.MAP_OBJECT_BORDER_COLOR);
		debugRenderer.rect(
				(position.x - mapObjectOffsetX) * RenderDefs.PIXELS_PER_UNIT, 
				(position.y - mapObjectOffsetY) * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * RenderDefs.PIXELS_PER_UNIT);	
		
		debugRenderer.setColor(RenderDefs.MAP_OBJECT_CENTER_MARKER_COLOR);
		debugRenderer.rect(
				position.x * RenderDefs.PIXELS_PER_UNIT - 1, 
				position.y * RenderDefs.PIXELS_PER_UNIT - 1, 
				3, 
				3);	
		
		debugRenderer.end();
	}

}