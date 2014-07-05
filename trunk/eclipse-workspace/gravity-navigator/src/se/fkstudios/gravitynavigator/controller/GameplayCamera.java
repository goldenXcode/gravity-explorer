package se.fkstudios.gravitynavigator.controller;

import se.fkstudios.gravitynavigator.view.RenderDefs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class GameplayCamera extends OrthographicCamera {

	public enum CameraMode { TIGHT, LOOSE };
	
	private CameraMode cameraMode;
	
	public GameplayCamera(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
		cameraMode = CameraMode.LOOSE;
	}
	
	public GameplayCamera(float viewportWidth, float viewportHeight, CameraMode cameraMode) {
		this(viewportWidth, viewportHeight);
		this.cameraMode = cameraMode;
	}
	
	public void setCameraMode(CameraMode value) {
		cameraMode = value; 
	}
	
	public CameraMode getCameraMode () {
		return cameraMode; 	
	}
	
	public void zoomIn() {
	    zoom += -0.1f;
	}
	
	public void zoomOut() {
		zoom += 0.1f; 
	}
	
	public void zoom (float amount) {
		zoom += amount; 	
	}

	/**
	 * Update camera's position and zoom based on spaceship's (player's) position and speed.
	 */
	public void updatePosition(float delta, Vector2 targetPosition, float mapWidth, float mapHeight) {	
		//update position
		Vector2 targetScreenPosition = targetPosition.cpy().scl(RenderDefs.PIXELS_PER_UNIT);
		Vector2 cameraPosition = new Vector2(position.x, position.y);
		 
		float jumpThresholdX = mapWidth * RenderDefs.PIXELS_PER_UNIT * 0.5f;
		float jumpThresholdY = mapHeight * RenderDefs.PIXELS_PER_UNIT * 0.5f;
		
		boolean jumpLeft = cameraPosition.x - targetScreenPosition.x < -jumpThresholdX;
		boolean jumpRight = targetScreenPosition.x - cameraPosition.x < -jumpThresholdX;
		boolean jumpUp = targetScreenPosition.y - cameraPosition.y < -jumpThresholdY; 
		boolean jumpDown = cameraPosition.y - targetScreenPosition.y < -jumpThresholdY;
		
		if (jumpLeft) 
			cameraPosition.x = cameraPosition.x + mapWidth * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpRight)
			cameraPosition.x = cameraPosition.x - mapWidth * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpUp)
			cameraPosition.y = cameraPosition.y - mapHeight * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpDown)
			cameraPosition.y = cameraPosition.y + mapHeight * RenderDefs.PIXELS_PER_UNIT;
		
		cameraPosition.lerp(targetScreenPosition, delta);
			
		if (cameraMode == CameraMode.LOOSE)
			position.set(cameraPosition.x, cameraPosition.y, 0);
		else
			position.set(targetScreenPosition.x, targetScreenPosition.y, 0);
		
		//update zoom
		//float speed = playerSpaceship.getVelocity().len();
		//float maxZoom = 2;
		//float minZoom = 1;
		//camera.zoom = Math.min(maxZoom, minZoom + speed * 0.0005f);
			
		update();
	}
}
