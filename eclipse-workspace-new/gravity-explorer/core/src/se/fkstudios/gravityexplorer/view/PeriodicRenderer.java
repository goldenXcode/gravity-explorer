package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.controller.GameplayCamera;
import se.fkstudios.gravityexplorer.model.MapObjectModel;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;

public abstract class PeriodicRenderer {

	private Rectangle drawArea;
	private float periodicityWidth;
	private float periodicityHeight;
	
	public PeriodicRenderer(float periodicityWidth, float periodicityHeight) {
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
		float positionX = resource.getPosition(mapObject.getPosition()).x;
		float positionY = resource.getPosition(mapObject.getPosition()).y;
		float width = resource.getWidth(mapObject.getWidth());
		float height = resource.getHeight(mapObject.getHeight());
		
		drawArea.x = Utility.getScreenCoordinate(positionX - width / 2);
		drawArea.y = Utility.getScreenCoordinate(positionY - height / 2);
		drawArea.width = Utility.getScreenCoordinate(width);
		drawArea.height = Utility.getScreenCoordinate(height);
		
		renderResourcePeriodically(resource, drawArea, mapObject.getRotation(), camera);
	}
	
	public void renderResourcePeriodically(GraphicResource resource, Rectangle drawArea, float rotation, GameplayCamera camera) {
		
		float originX = drawArea.width / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().x);
		float originY = drawArea.height / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().y);
		
		float scale = 1f;
		if ((1 / camera.zoom < resource.getMinRenderScale()) || (1 / camera.zoom > resource.getMaxRenderScale())) {
			scale = resource.getMinRenderScale() * camera.zoom;
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
	
	public abstract void updateToCamera(Camera camera);
	
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
