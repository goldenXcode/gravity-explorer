package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.math.Rectangle;

public abstract class PeriodicResourceRenderer {

	private Rectangle drawArea;
	private float periodicityWidth;
	private float periodicityHeight;
	
	public PeriodicResourceRenderer(float periodicityWidth, float periodicityHeight) {
		drawArea = new Rectangle();
		this.periodicityWidth = periodicityWidth;
		this.periodicityHeight = periodicityWidth;
	}
		
	public float getPeriodicityWidth() {
		return periodicityWidth;
	}

	protected void setPeriodicityWidth(float periodicityWidth) {
		this.periodicityWidth = periodicityWidth;
	}

	public float getPeriodicityHeight() {
		return periodicityHeight;
	}

	protected void setPeriodicityHeight(float periodicityHeight) {
		this.periodicityHeight = periodicityHeight;
	}

	public void renderObjectPeriodically(MapObjectModel mapObject, GraphicResource resource, GameplayCamera camera) {
		float positionX = resource.getPositionX(mapObject.getPosition().x);
		float positionY = resource.getPositionY(mapObject.getPosition().y);
		float width = resource.getWidth(mapObject.getWidth());
		float height = resource.getHeight(mapObject.getHeight());
		
		drawArea.x = Utility.getScreenCoordinate(positionX - width / 2);
		drawArea.y = Utility.getScreenCoordinate(positionY - height / 2);
		drawArea.width = Utility.getScreenCoordinate(width);
		drawArea.height = Utility.getScreenCoordinate(height);
		
		renderResourcePeriodically(resource, drawArea, mapObject.getRotation(), camera);
	}
	
	public void renderResourcePeriodically(GraphicResource resource, Rectangle drawArea, float rotation, GameplayCamera camera) {
		
		float originX = drawArea.width / 2 - Utility.getScreenCoordinate(resource.positionOffset.x);
		float originY = drawArea.height / 2 - Utility.getScreenCoordinate(resource.positionOffset.y);
		
		float scale = 1f;
		if ((1 / camera.zoom < resource.minRenderScale) || (1 / camera.zoom > resource.maxRenderScale)) {
			scale = resource.minRenderScale * camera.zoom;
		}
		
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
	
		//Render periodicity
		float screenPosX = drawArea.x;
		float screenPosY = drawArea.y;
		
		//Top
		drawArea.x = screenPosX;
		drawArea.y = screenPosY + periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Left
		drawArea.x = screenPosX - periodicityWidth;
		drawArea.y = screenPosY;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Bottom
		drawArea.x = screenPosX;
		drawArea.y = screenPosY - periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Right
		drawArea.x = screenPosX + periodicityWidth;
		drawArea.y = screenPosY;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Left top
		drawArea.x = screenPosX - periodicityWidth;
		drawArea.y = screenPosY + periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Left bottom
		drawArea.x = screenPosX - periodicityWidth;
		drawArea.y = screenPosY - periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Right top
		drawArea.x = screenPosX + periodicityWidth;
		drawArea.y = screenPosY + periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
		
		//Right bottom
		drawArea.x = screenPosX + periodicityWidth;
		drawArea.y = screenPosY - periodicityHeight;
		renderResourceIfInViewport(resource, drawArea, originX, originY, scale, rotation, camera);
	}
	
	protected abstract void renderResource(GraphicResource resource, 
			Rectangle drawArea,
			float originX, float originY, 
			float scale,
			float rotation);

	private void renderResourceIfInViewport(GraphicResource resource, 
			Rectangle drawArea,
			float originX, float originY, 
			float scale,
			float rotation,
			GameplayCamera camera) 
	{
		if (isDrawAreaInViewport(camera)) 
			renderResource(resource, drawArea, originX, originY, scale, rotation);
	}
	
	/**
	 * TODO: Implement for optimization.
	 * @param camera is ignored.
	 * @return true.
	 */
	private boolean isDrawAreaInViewport(GameplayCamera camera) {
		return true;
	}
}
