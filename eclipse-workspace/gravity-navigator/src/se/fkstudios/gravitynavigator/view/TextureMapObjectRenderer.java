package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.TextureMapObjectModel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Renders SimpleMapObjects.
 * @author kristofer
 */
public class TextureMapObjectRenderer extends Renderer {

	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private float mapScreenWidth;
	private float mapScreenHeight;
	
	public TextureMapObjectRenderer(ShapeRenderer shapeRenderer,
			SpriteBatch spriteBatch,
			float mapWidth,
			float mapHeight) {
		
		this.shapeRenderer = shapeRenderer;
		this.spriteBatch = spriteBatch;
		this.mapScreenWidth = mapWidth * RenderDefs.PIXELS_PER_UNIT;
		this.mapScreenHeight = mapHeight * RenderDefs.PIXELS_PER_UNIT;
	}
	
	public void render(TextureMapObjectModel mapObject, Rectangle viewport) {
		
		float textureOriginX = (mapObject.getWidth() / 2) * RenderDefs.PIXELS_PER_UNIT;
		float textureOriginY = (mapObject.getHeight() / 2) * RenderDefs.PIXELS_PER_UNIT;
		
		float textureWidth = mapObject.getWidth() * RenderDefs.PIXELS_PER_UNIT;
		float textureHeight = mapObject.getHeight() * RenderDefs.PIXELS_PER_UNIT;

		//since position is the middle of the object but we render the texture from the bottom left corner. 
		float offsetX = mapObject.getWidth() * 0.5f;
		float offsetY = mapObject.getHeight() * 0.5f;
		float mapObjectScreenPositionX = (mapObject.getPosition().x - offsetX) * RenderDefs.PIXELS_PER_UNIT;  
		float mapObjectScreenPositionY = (mapObject.getPosition().y - offsetY) * RenderDefs.PIXELS_PER_UNIT;
		
		TextureRegion textureRegion = TextureLoader.getInstance().getTextureRegion(mapObject.getTextureName());

		spriteBatch.begin();
		
		spriteBatch.draw(textureRegion, 
				mapObjectScreenPositionX, 
				mapObjectScreenPositionY, 
				textureOriginX, 
				textureOriginY, 
				textureWidth, 
				textureHeight, 
				1.0f,
				1.0f,
				mapObject.getRotation());
		
		//TODO refactor to enum in GameplayScreen. 
		// and.. exists 5 more cases, overlap top, bottom and left for example, or all. but whatever...
		boolean overlapBottom = viewport.y < 0;
		boolean overlapTop = (viewport.y + viewport.height) > mapScreenHeight;
		boolean overlapLeft = viewport.x < 0;
		boolean overlapRight = (viewport.x + viewport.width) > mapScreenWidth;
		boolean overlapRightTop = overlapRight && overlapTop;
		boolean overlapRightBottom = overlapRight && overlapBottom;
		boolean overlapLeftTop = overlapLeft && overlapTop;
		boolean overlapLeftBottom = overlapLeft && overlapBottom;
		
		if (overlapRightTop) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX + mapScreenWidth, 
					mapObjectScreenPositionY + mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapRightBottom) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX + mapScreenWidth, 
					mapObjectScreenPositionY - mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapLeftTop) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX - mapScreenWidth, 
					mapObjectScreenPositionY + mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapLeftBottom) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX - mapScreenWidth, 
					mapObjectScreenPositionY - mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapBottom) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX, 
					mapObjectScreenPositionY - mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
			
		}
		if (overlapTop) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX, 
					mapObjectScreenPositionY + mapScreenHeight, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapLeft) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX - mapScreenWidth, 
					mapObjectScreenPositionY, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());
		}
		if (overlapRight) {
			spriteBatch.draw(textureRegion, 
					mapObjectScreenPositionX + mapScreenWidth, 
					mapObjectScreenPositionY, 
					textureOriginX, 
					textureOriginY, 
					textureWidth, 
					textureHeight, 
					1.0f,
					1.0f,
					mapObject.getRotation());	
		}
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), mapObject);
	}
	
	private void debugRender(Matrix4 projectionMatrix, TextureMapObjectModel mapObject) {
		
		shapeRenderer.setProjectionMatrix(projectionMatrix);

		Vector2 position = mapObject.getPosition();
		float mapObjectOffsetX = mapObject.getWidth() * 0.5f;
		float mapObjectOffsetY = mapObject.getHeight() * 0.5f;
			
		shapeRenderer.begin(ShapeType.Line);
		
		shapeRenderer.setColor(RenderDefs.MAP_OBJECT_BORDER_COLOR);
		shapeRenderer.rect(
				(position.x - mapObjectOffsetX) * RenderDefs.PIXELS_PER_UNIT, 
				(position.y - mapObjectOffsetY) * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * RenderDefs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * RenderDefs.PIXELS_PER_UNIT);	
		
		shapeRenderer.setColor(RenderDefs.MAP_OBJECT_CENTER_MARKER_COLOR);
		shapeRenderer.rect(
				position.x * RenderDefs.PIXELS_PER_UNIT - 1, 
				position.y * RenderDefs.PIXELS_PER_UNIT - 1, 
				3, 
				3);	
		
		shapeRenderer.end();
	}
}