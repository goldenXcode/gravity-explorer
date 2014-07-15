package se.fkstudios.gravitynavigator.controller;

import se.fkstudios.gravitynavigator.model.ModelDefs;
import se.fkstudios.gravitynavigator.view.RenderDefs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameplayCamera extends OrthographicCamera {

	public enum CameraMode { TIGHT, LOOSE };
	
	private CameraMode cameraMode;
	
	/**
	 * WARNING: Only updated after getViewport() was called.
	 * tempViewport should only be used by getViewport() for optimization.
	 */
	private Rectangle tempViewport; 
	
	public GameplayCamera(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);
		cameraMode = CameraMode.LOOSE;
		tempViewport = new Rectangle(0,0, viewportWidth, viewportHeight);
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
		zoom += amount * ModelDefs.SCROLLING_SPEED_MODIFIER; 	
	}

	/**
	 * Gets the viewport accounting zoom and position.
	 * @return the viewport as a rectangle.
	 */
	public Rectangle getViewport() {
		tempViewport.x = position.x - zoom * viewportWidth / 2;
		tempViewport.y = position.y - zoom * viewportHeight / 2;
		tempViewport.width = zoom * viewportWidth;
		tempViewport.height = zoom * viewportHeight;
		return tempViewport;
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
	}
}
