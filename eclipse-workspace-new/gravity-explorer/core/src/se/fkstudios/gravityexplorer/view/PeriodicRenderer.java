package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.controller.GameplayCamera;
import se.fkstudios.gravityexplorer.model.MapObjectModel;
import se.fkstudios.gravityexplorer.model.resources.GraphicResourceBinding;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public abstract class PeriodicRenderer {

	private Vector2 screenPosition;
	private Vector2 screenOrigin;
	private float periodicityWidth;
	private float periodicityHeight;
	
	public PeriodicRenderer(float periodicityWidth, float periodicityHeight) {
		screenPosition = new Vector2();
		screenOrigin = new Vector2();
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
	
	public abstract void updateToCamera(Camera camera);
	
	public void renderObjectPeriodically(MapObjectModel mapObject, GraphicResourceBinding resource, GameplayCamera camera) {
		if (!resource.isVisible()) {
			throw new IllegalStateException("Renderer was asked to render resource with visiblitiy set to false.");
		}
		
		screenPosition.x = Utility.getScreenCoordinate(resource.getPosition().x);
		screenPosition.y =  Utility.getScreenCoordinate(resource.getPosition().y);
	
		float screenWidth =  Utility.getScreenCoordinate(resource.getWidth());
		float screenHeight = Utility.getScreenCoordinate(resource.getHeight());
		
		//Origin should take into account if object has position offset in case several textures/animations should make up the same object 
		//(which in that case must rotate around same origin).
		screenOrigin.x = screenWidth / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().x);
		screenOrigin.y = screenHeight / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().y);
		
		float rotation = mapObject.getRotation();
		
		renderResourcePeriodically(resource, screenPosition, screenWidth, screenHeight, screenOrigin, rotation);
	}
	
	public void renderResourcePeriodically(GraphicResourceBinding resource, 
			Vector2 screenPosition,
			float screenWidth, float screenHeight,
			Vector2 screenOrigin,
			float rotation)
	{
		Array<Vector2> positions = Utility.calculatePerodicPositions(screenPosition, periodicityWidth, periodicityHeight);
		for (Vector2 currentScreenPosition : positions) {
			renderResource(resource, currentScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		}
	}
	
	protected abstract void renderResource(GraphicResourceBinding resource, 
			Vector2 screenPosition,
			float screenWidth, float screnHeight,
			Vector2 screenOrigin,
			float rotation);
}
