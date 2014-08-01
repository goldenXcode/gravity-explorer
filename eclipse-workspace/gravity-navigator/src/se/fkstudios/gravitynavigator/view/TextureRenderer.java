package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.PhysicsEngine;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Renders SimpleMapObjects.
 * @author kristofer
 */
public class TextureRenderer {

	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private float mapScreenWidth;
	private float mapScreenHeight;
	
	public TextureRenderer(ShapeRenderer shapeRenderer,
			SpriteBatch spriteBatch,
			float mapWidth,
			float mapHeight) {
		
		this.shapeRenderer = shapeRenderer;
		this.spriteBatch = spriteBatch;
		this.mapScreenWidth = mapWidth * Defs.PIXELS_PER_UNIT;
		this.mapScreenHeight = mapHeight * Defs.PIXELS_PER_UNIT;
	}
	
	public void render(MapObjectModel mapObject, TextureResource textureResource, Rectangle viewport) {
		
		if (isObjectInViewport(mapObject, viewport))
		{
			float textureOriginX = (mapObject.getWidth() / 2) * Defs.PIXELS_PER_UNIT;
			float textureOriginY = (mapObject.getHeight() / 2) * Defs.PIXELS_PER_UNIT;
			
			float textureWidth = mapObject.getWidth() * Defs.PIXELS_PER_UNIT;
			float textureHeight = mapObject.getHeight() * Defs.PIXELS_PER_UNIT;

			//since position is the middle of the object but we render the texture from the bottom left corner. 
			float offsetX = mapObject.getWidth() * 0.5f;
			float offsetY = mapObject.getHeight() * 0.5f;
			float mapObjectScreenPositionX = (mapObject.getPosition().x - offsetX) * Defs.PIXELS_PER_UNIT;  
			float mapObjectScreenPositionY = (mapObject.getPosition().y - offsetY) * Defs.PIXELS_PER_UNIT;
			
//			TODO: REMOVE - OLD CODE
//			TextureRegion textureRegion = TextureLoader.getInstance().getTextureRegion(mapObject.getTextureName());
			
//			TODO: AND THE NEW CODE
			TextureRegion textureRegion = TextureLoader.getInstance().getTextureRegion(textureResource.textureName);
			
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
	}
	
	private void debugRender(Matrix4 projectionMatrix, MapObjectModel mapObject) {
		
		shapeRenderer.setProjectionMatrix(projectionMatrix);

		Vector2 position = mapObject.getPosition();
		float mapObjectOffsetX = mapObject.getWidth() * 0.5f;
		float mapObjectOffsetY = mapObject.getHeight() * 0.5f;
			
		shapeRenderer.begin(ShapeType.Line);
		
		shapeRenderer.setColor(Defs.MAP_OBJECT_BORDER_COLOR);
		shapeRenderer.rect(
				(position.x - mapObjectOffsetX) * Defs.PIXELS_PER_UNIT, 
				(position.y - mapObjectOffsetY) * Defs.PIXELS_PER_UNIT, 
				mapObject.getWidth() * Defs.PIXELS_PER_UNIT, 
				mapObject.getHeight() * Defs.PIXELS_PER_UNIT);	
		
		shapeRenderer.setColor(Defs.MAP_OBJECT_CENTER_MARKER_COLOR);
		shapeRenderer.rect(
				position.x * Defs.PIXELS_PER_UNIT - 1, 
				position.y * Defs.PIXELS_PER_UNIT - 1, 
				3, 
				3);	
		
		shapeRenderer.end();
	}
	
	private boolean isObjectInViewport(MapObjectModel mapObject, Rectangle viewport) {
		
		Rectangle modelViewport = new Rectangle(viewport.x / Defs.PIXELS_PER_UNIT, 
				viewport.y / Defs.PIXELS_PER_UNIT, 
				viewport.width / Defs.PIXELS_PER_UNIT, 
				viewport.height / Defs.PIXELS_PER_UNIT);
		
		float height = modelViewport.height / 2 + mapObject.getHeight() / 2;
		float width = modelViewport.width / 2 + mapObject.getWidth() / 2;
		double maxViewportWidthHeight = Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));
		
		float shortestDistance = PhysicsEngine.shortestDistanceVector(
				new Vector2(modelViewport.x + modelViewport.width / 2, 
						modelViewport.y + modelViewport.height / 2),
				mapObject.getPosition()).len();
		
		return shortestDistance < maxViewportWidthHeight;
	}
}