package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public abstract class TextureRegionRenderer {
	
	protected SpriteBatch spriteBatch;
	protected ShapeRenderer shapeRenderer;
	protected float periodicityWidth;
	protected float periodicityHeight;
	
	public TextureRegionRenderer(SpriteBatch spriteBatch,
			float periodicityWidth,
			float periodicityHeight) {
		this.shapeRenderer = new ShapeRenderer();
		this.spriteBatch = spriteBatch;
		this.periodicityWidth = periodicityWidth;
		this.periodicityHeight = periodicityHeight;
	}
	
	protected void render(TextureRegionResource resource, 
			float screenPositionX, 
			float screenPositionY, 
			float textureWidth, 
			float textureHeight, 
			float rotation, 
			GameplayCamera camera) {
		Rectangle drawArea = new Rectangle(screenPositionX, screenPositionY, textureWidth, textureHeight);
		float originX = textureWidth / 2 - Utility.getScreenCoordinate(resource.positionOffset.x);
		float originY = textureHeight / 2 - Utility.getScreenCoordinate(resource.positionOffset.y);
		Rectangle viewport = camera.getViewport();
		
		float renderScale = 1f;
		if ((1 / camera.zoom < resource.minRenderScale) || (1 / camera.zoom > resource.maxRenderScale)) {
			renderScale = resource.minRenderScale * camera.zoom;
		}
		
		TextureRegion textureRegion = resource.getTextureRegion();
		
		spriteBatch.begin();
				
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
	
		//Render periodicity
		//Top
		drawArea.x = screenPositionX;
		drawArea.y = screenPositionY + periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Left
		drawArea.x = screenPositionX - periodicityWidth;
		drawArea.y = screenPositionY;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Buttom
		drawArea.x = screenPositionX;
		drawArea.y = screenPositionY - periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Right
		drawArea.x = screenPositionX + periodicityWidth;
		drawArea.y = screenPositionY;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Left top
		drawArea.x = screenPositionX - periodicityWidth;
		drawArea.y = screenPositionY + periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Left bottom
		drawArea.x = screenPositionX - periodicityWidth;
		drawArea.y = screenPositionY - periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Right top
		drawArea.x = screenPositionX + periodicityWidth;
		drawArea.y = screenPositionY + periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		//Right bottom
		drawArea.x = screenPositionX + periodicityWidth;
		drawArea.y = screenPositionY - periodicityHeight;
		tryDrawTextureRegion(textureRegion, drawArea, originX, originY, rotation, renderScale, viewport);
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), screenPositionX, screenPositionY, textureWidth, textureHeight, originX, originY, rotation);
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
		
//		Boolean wasDrawn = textureArea.overlaps(viewport);
		Boolean wasDrawn = true;
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
	
	protected void debugRender(Matrix4 projectionMatrix, 
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
}
