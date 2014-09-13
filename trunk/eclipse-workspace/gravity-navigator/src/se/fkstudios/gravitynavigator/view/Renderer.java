package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public abstract class Renderer {

	protected ShapeRenderer shapeRenderer;
	protected SpriteBatch spriteBatch;
	protected float mapScreenWidth;
	protected float mapScreenHeight;
	
	public Renderer(ShapeRenderer shapeRenderer,
			SpriteBatch spriteBatch,
			float mapWidth,
			float mapHeight) {
		this.shapeRenderer = shapeRenderer;
		this.spriteBatch = spriteBatch;
		this.mapScreenWidth = getScreenCoordinate(mapWidth);
		this.mapScreenHeight = getScreenCoordinate(mapHeight);
	}

	/**
	 * Draws a TextureResource.
	 * @param mapObject MapObjectModel owning the TextureResource.
	 * @param textureResource TextureResource to be drawn.
	 * @param viewport 
	 */
public void render(MapObjectModel mapObject, GraphicResource resource, GameplayCamera camera) {
		float width;
		float height;	
		if (resource.useParentSize) {
			width = getScreenCoordinate(mapObject.getWidth());
			height = getScreenCoordinate(mapObject.getHeight());
		}
		else {
			width = getScreenCoordinate(resource.width);
			height = getScreenCoordinate(resource.height);
		}
		
		float textureOriginX = width / 2;
		float textureOriginY = height / 2;
		
		float positionX = getScreenCoordinate(mapObject.getPosition().x) - textureOriginX + getScreenCoordinate(resource.positionOffset.x);  
		float positionY = getScreenCoordinate(mapObject.getPosition().y ) - textureOriginY + getScreenCoordinate(resource.positionOffset.y);
		
		float originX = textureOriginX - getScreenCoordinate(resource.positionOffset.x);
		float originY = textureOriginY - getScreenCoordinate(resource.positionOffset.y);
				
		float rotation = mapObject.getRotation();
		
		Rectangle drawArea = new Rectangle(positionX, positionY, width, height);
		Rectangle viewport = camera.getViewport();
		
		float renderScale = 1 / camera.zoom;
		float compensatedRenderScale = 1f;
		if (renderScale < mapObject.minRenderScale)
			compensatedRenderScale = mapObject.minRenderScale / renderScale;
		
		TextureRegion textureRegion = getTextureRegion(resource);
		
		spriteBatch.begin();
		
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Render periodicity
		//Top
		drawArea.x = positionX;
		drawArea.y = positionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Left
		drawArea.x = positionX - mapScreenWidth;
		drawArea.y = positionY;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Buttom
		drawArea.x = positionX;
		drawArea.y = positionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Right
		drawArea.x = positionX + mapScreenWidth;
		drawArea.y = positionY;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Left top
		drawArea.x = positionX - mapScreenWidth;
		drawArea.y = positionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Left bottom
		drawArea.x = positionX - mapScreenWidth;
		drawArea.y = positionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Right top
		drawArea.x = positionX + mapScreenWidth;
		drawArea.y = positionY + mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		//Right bottom
		drawArea.x = positionX + mapScreenWidth;
		drawArea.y = positionY - mapScreenHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, compensatedRenderScale, viewport);
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), positionX, positionY, width, height, originX, originY, rotation);
	}

	/**
	 * TODO: write the javadoc.
	 * @param resource
	 * @return
	 */
	protected abstract TextureRegion getTextureRegion(GraphicResource resource);

	private void debugRender(Matrix4 projectionMatrix, 
			float positionX, 
			float positionY, 
			float width, 
			float height,
			float originX,
			float originY,
			float rotation) {
		
		shapeRenderer.setProjectionMatrix(projectionMatrix);
			
		shapeRenderer.begin(ShapeType.Line);
		
		shapeRenderer.setColor(Defs.MAP_OBJECT_BORDER_COLOR);
		shapeRenderer.rect(positionX, positionY, width, height, originX, originY, rotation);
		
		shapeRenderer.setColor(Defs.MAP_OBJECT_CENTER_MARKER_COLOR);
		shapeRenderer.rect(
				positionX + originX - 1, 
				positionY + originY - 1, 
				3, 
				3);	
		
		shapeRenderer.end();
	}
	
	/**
	 * TODO: write the javadoc
	 * @param modelCoordinate
	 * @return
	 */
	protected float getScreenCoordinate(float modelCoordinate) {
		return modelCoordinate * Defs.PIXELS_PER_UNIT;
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
	protected Boolean tryDrawTextureRegion(TextureRegion textureRegion, 
			Rectangle textureArea, 
			float textureOriginX, 
			float textureOriginY, 
			float textureRotation, 
			float scale,
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
				scale, 
				scale, 
				textureRotation);
		}
		return wasDrawn;
	}	
}
