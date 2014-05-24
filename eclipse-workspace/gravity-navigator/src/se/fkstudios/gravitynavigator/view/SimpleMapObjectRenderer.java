package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.ResourceDefs;
import se.fkstudios.gravitynavigator.model.SimpleMapObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

/**
 * Renders SimpleMapObjects.
 * @author kristofer
 */
public class SimpleMapObjectRenderer extends Renderer {

	private TextureAtlas textureAtlas;	

	/** debug rendering **/
	private ShapeRenderer debugRenderer;
	
	public SimpleMapObjectRenderer() {
		textureAtlas = new TextureAtlas(Gdx.files.internal(ResourceDefs.FILE_PATH_TEXTURE_PACK));
	}
	
	/**
	 * Render a SimpleMapObject.
	 * @param spriteBatch
	 * @param mapObject
	 */
	public void render(SpriteBatch spriteBatch, SimpleMapObject mapObject) {
		
		spriteBatch.begin();
		
		TextureRegion texRegion = textureAtlas.findRegion(mapObject.getTextureName());
		
		float offsetX = mapObject.getWidth() * 0.5f;
		float offsetY = mapObject.getHeight() * 0.5f;
		
		spriteBatch.draw(texRegion, 
				(mapObject.getPosition().x - offsetX) * RenderingDefs.PIXELS_PER_UNIT, 
				(mapObject.getPosition().y - offsetY) * RenderingDefs.PIXELS_PER_UNIT, 
				(mapObject.getWidth() / 2) * RenderingDefs.PIXELS_PER_UNIT, 
				(mapObject.getHeight() / 2) * RenderingDefs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * RenderingDefs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * RenderingDefs.PIXELS_PER_UNIT, 
				1.0f,
				1.0f,
				mapObject.getRotation());
		
		spriteBatch.end();
		
		if (RenderingOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), mapObject);
	}
	
	private void debugRender(Matrix4 projectionMatrix, SimpleMapObject mapObject) {
		
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();
		
		debugRenderer.setProjectionMatrix(projectionMatrix);

		Vector2 position = mapObject.getPosition();
		float mapObjectOffsetX = mapObject.getWidth() * 0.5f;
		float mapObjectOffsetY = mapObject.getHeight() * 0.5f;
			
		debugRenderer.begin(ShapeType.Line);
		
		debugRenderer.setColor(RenderingDefs.MAP_OBJECT_BORDER_COLOR);
		debugRenderer.rect(
				(position.x - mapObjectOffsetX) * RenderingDefs.PIXELS_PER_UNIT, 
				(position.y - mapObjectOffsetY) * RenderingDefs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * RenderingDefs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * RenderingDefs.PIXELS_PER_UNIT);	
		
		debugRenderer.setColor(RenderingDefs.MAP_OBJECT_CENTER_MARKER_COLOR);
		debugRenderer.rect(
				position.x * RenderingDefs.PIXELS_PER_UNIT - 1, 
				position.y * RenderingDefs.PIXELS_PER_UNIT - 1, 
				3, 
				3);	
		
		debugRenderer.end();
	}

}