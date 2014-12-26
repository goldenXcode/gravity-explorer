package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.controller.GameplayCamera;
import se.fkstudios.gravityexplorer.model.MapObjectModel;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public abstract class PeriodicRenderer {

	private Vector2 screenPosition;
	private Vector2 perodicScreenPosition;
	private Vector2 screenOrigin;
	private float periodicityWidth;
	private float periodicityHeight;
	
	public PeriodicRenderer(float periodicityWidth, float periodicityHeight) {
		screenPosition = new Vector2();
		perodicScreenPosition = new Vector2();
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
	
	public void renderObjectPeriodically(MapObjectModel mapObject, GraphicResource resource, GameplayCamera camera) {

		Vector2 modelPosition = resource.getPosition(mapObject.getPosition());
		screenPosition.x = Utility.getScreenCoordinate(modelPosition.x);
		screenPosition.y =  Utility.getScreenCoordinate(resource.getPosition(mapObject.getPosition()).y);
	
		float screenWidth =  Utility.getScreenCoordinate(resource.getWidth(mapObject.getWidth()));
		float screenHeight = Utility.getScreenCoordinate(resource.getHeight(mapObject.getHeight()));
		
		//Origin should take into account if object has position offset in case several textures/animations should make up the same object 
		//(which in that case must rotate around same origin).
		screenOrigin.x = screenWidth / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().x);
		screenOrigin.y = screenHeight / 2 - Utility.getScreenCoordinate(resource.getPositionOffset().y);
		
		float rotation = mapObject.getRotation();
		
		renderResourcePeriodically(resource, screenPosition, screenWidth, screenHeight, screenOrigin, rotation);
	}
	
	public void renderResourcePeriodically(GraphicResource resource, 
			Vector2 screenPosition,
			float screenWidth, float screenHeight,
			Vector2 screenOrigin,
			float rotation)
	{
		renderResource(resource, screenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Render periodicity
		perodicScreenPosition.x = screenPosition.x;
		perodicScreenPosition.y = screenPosition.y;
		
		//Top
		perodicScreenPosition.x = screenPosition.x;
		perodicScreenPosition.y = screenPosition.y + periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Left
		perodicScreenPosition.x = screenPosition.x - periodicityWidth;
		perodicScreenPosition.y = screenPosition.y;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Bottom
		perodicScreenPosition.x = screenPosition.x;
		perodicScreenPosition.y = screenPosition.y - periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Right
		perodicScreenPosition.x = screenPosition.x + periodicityWidth;
		perodicScreenPosition.y = screenPosition.y;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Left top
		perodicScreenPosition.x = screenPosition.x - periodicityWidth;
		perodicScreenPosition.y = screenPosition.y + periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Left bottom
		perodicScreenPosition.x = screenPosition.x - periodicityWidth;
		perodicScreenPosition.y = screenPosition.y - periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Right top
		perodicScreenPosition.x = screenPosition.x + periodicityWidth;
		perodicScreenPosition.y = screenPosition.y + periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
		
		//Right bottom
		perodicScreenPosition.x = screenPosition.x + periodicityWidth;
		perodicScreenPosition.y = screenPosition.y - periodicityHeight;
		renderResource(resource, perodicScreenPosition, screenWidth, screenHeight, screenOrigin, rotation);
	}
	
	protected abstract void renderResource(GraphicResource resource, 
			Vector2 screenPosition,
			float screenWidth, float screnHeight,
			Vector2 screenOrigin,
			float rotation);
}
