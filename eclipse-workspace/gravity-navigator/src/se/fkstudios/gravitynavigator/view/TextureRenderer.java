package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Object drawing MapObjectModel's TextureResources.
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
	
	/**
	 * Draws a TextureResource.
	 * @param mapObject MapObjectModel owning the TextureResource.
	 * @param textureResource TextureResource to be drawn.
	 * @param viewport 
	 */
	public void render(MapObjectModel mapObject, TextureResource textureResource, Rectangle viewport) {

		float textureOriginX = (mapObject.getWidth() / 2) * Defs.PIXELS_PER_UNIT;
		float textureOriginY = (mapObject.getHeight() / 2) * Defs.PIXELS_PER_UNIT;
		float texturePositionX = mapObject.getPosition().x * Defs.PIXELS_PER_UNIT - textureOriginX;  
		float texturePositionY = mapObject.getPosition().y * Defs.PIXELS_PER_UNIT - textureOriginY;
		float textureWidth = mapObject.getWidth() * Defs.PIXELS_PER_UNIT;
		float textureHeight = mapObject.getHeight() * Defs.PIXELS_PER_UNIT;
		float textureRotation = mapObject.getRotation();
		Rectangle textureArea = new Rectangle(texturePositionX, texturePositionY, textureWidth, textureHeight);
		
		TextureRegion textureRegion = TextureLoader.getInstance().getTextureRegion(textureResource.textureName);
		
		spriteBatch.begin();
		
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Render periodicity
		//Top
		textureArea.x = texturePositionX;
		textureArea.y = texturePositionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Left
		textureArea.x = texturePositionX - mapScreenWidth;
		textureArea.y = texturePositionY;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Buttom
		textureArea.x = texturePositionX;
		textureArea.y = texturePositionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Right
		textureArea.x = texturePositionX + mapScreenWidth;
		textureArea.y = texturePositionY;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Left top
		textureArea.x = texturePositionX - mapScreenWidth;
		textureArea.y = texturePositionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Left bottom
		textureArea.x = texturePositionX - mapScreenWidth;
		textureArea.y = texturePositionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Right top
		textureArea.x = texturePositionX + mapScreenWidth;
		textureArea.y = texturePositionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		//Right bottom
		textureArea.x = texturePositionX + mapScreenWidth;
		textureArea.y = texturePositionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, textureArea, textureOriginX, textureOriginY, textureRotation, viewport);
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), mapObject);
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
	
	/**
	 * Draws a TextureRegion stretched in a Rectangle if the Rectangle overlaps given viewport Rectangle.
	 * @param textureRegion Texture region to be drawn.
	 * @param textureArea Area to draw the texture region in.
	 * @param textureOriginX X-wise origin to determine rotation etc.
	 * @param textureOriginY Y-wise origin to determine rotation etc.
	 * @param textureRotation Texture's rotation.
	 * @param viewport Rectangle to test overlapping against.
	 * @return True if the TextureRegion was drawn.
	 */
	private Boolean tryDrawTextureRegion(TextureRegion textureRegion, 
			Rectangle textureArea, 
			float textureOriginX, 
			float textureOriginY, 
			float textureRotation, 
			Rectangle viewport) {
		
		Boolean wasDrawn = textureArea.overlaps(viewport);
		if (wasDrawn) {
			spriteBatch.draw(textureRegion, 
				textureArea.x, 
				textureArea.y, 
				textureOriginX, 
				textureOriginY,
				textureArea.width, 
				textureArea.height, 
				1.0f, 
				1.0f, 
				textureRotation);
		}
		return wasDrawn;
	}
}
