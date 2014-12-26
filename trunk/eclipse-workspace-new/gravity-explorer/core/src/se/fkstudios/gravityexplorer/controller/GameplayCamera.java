package se.fkstudios.gravityexplorer.controller;

import se.fkstudios.gravityexplorer.Defs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameplayCamera extends OrthographicCamera {

	public enum CameraMode { TIGHT, LOOSE };
	
	private CameraMode cameraMode;
	private Vector3 screenTargetPosition;
	
	/*to get a proper logarithmic zoom we'll use a variable modified by the user (zoomDomain) and 
	 a (exponential) mapping from zoomDomain to zoomRange which will be the actual zoom seen on screen. 
	one will be modulated by SCROLLING_SPEED_MODIFIER_1 and the other by SCROLLING_SPEED_MODIFIER_2 */
	private float zoomDomain; 
	
	/**
	 * WARNING: Only updated after getViewport() was called.
	 * tempViewport should only be used by getViewport() for optimization.
	 */
	private Rectangle tempViewport; 
	
	public GameplayCamera(float viewportWidth, float viewportHeight, Vector3 startPosition) {
		super(viewportWidth, viewportHeight);
		cameraMode = CameraMode.LOOSE;
		zoomDomain = zoom; 
		near = Defs.CAMERA_NEAR;
		far = Defs.CAMERA_FAR;
		position.x = startPosition.x;
		position.y = startPosition.y;
		position.z = startPosition.z;
		tempViewport = new Rectangle(0,0, viewportWidth, viewportHeight);
		screenTargetPosition = new Vector3(position.x, position.y, position.z);
	}
	
	public void setCameraMode(CameraMode value) {
		cameraMode = value; 
	}
	
	public CameraMode getCameraMode () {
		return cameraMode; 	
	}
	
	public void zoomIn() {
	    zoomDomain += -0.1f;
	    zoom = (float) zoomMapping(zoomDomain); 
	}
	
	public void zoomOut() {
		zoomDomain += 0.1f; 
		zoom = (float) zoomMapping(zoomDomain); 
	}
	
	public void zoom (double d) {
		zoomDomain += d * Defs.SCROLLING_SPEED_MODIFIER_1; 	
		zoom = (float) zoomMapping(zoomDomain); 
	}
	
	private double zoomMapping (double d) {
		return Math.exp(d*Defs.SCROLLING_SPEED_MODIFIER_2); 
	}
	
	/* 
	 *  Returns the "radius" of the viewport squared. Ignored the sqrt as an optimization. 
	 *  getZoom is still monotone in both x and y of the viewportwidth
	 */
	public float getZoom() {
//		float x = getViewport().x; 
//		float y = getViewport().y; 
//		return (float) Math.sqrt((Math.pow(x, 2) + Math.pow(y, 2))); // might want to optimize this later. sqrt is costly and somewhat redundant
		return zoom; 
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
		screenTargetPosition.x = targetPosition.x * Defs.PIXELS_PER_UNIT;
		screenTargetPosition.y = targetPosition.y * Defs.PIXELS_PER_UNIT;
		
		if (cameraMode == CameraMode.LOOSE) {
			float jumpThresholdX = mapWidth * Defs.PIXELS_PER_UNIT * 0.5f;
			float jumpThresholdY = mapHeight * Defs.PIXELS_PER_UNIT * 0.5f;
			
			boolean jumpLeft = position.x - screenTargetPosition.x < -jumpThresholdX;
			boolean jumpRight = screenTargetPosition.x - position.x < -jumpThresholdX;
			boolean jumpUp = screenTargetPosition.y - position.y < -jumpThresholdY; 
			boolean jumpDown = position.y - screenTargetPosition.y < -jumpThresholdY;
			
			if (jumpLeft) 
				position.x = position.x + mapWidth * Defs.PIXELS_PER_UNIT;
			else if (jumpRight)
				position.x = position.x - mapWidth * Defs.PIXELS_PER_UNIT;
			else if (jumpUp)
				position.y = position.y - mapHeight * Defs.PIXELS_PER_UNIT;
			else if (jumpDown)
				position.y += position.y + mapHeight * Defs.PIXELS_PER_UNIT;
				
			position.lerp(screenTargetPosition, 3 * delta);
			position.z = Defs.CAMERA_POSITION_Z;
		}
		else {
		
			position.x = screenTargetPosition.x;
			position.y = screenTargetPosition.y;
			position.z = Defs.CAMERA_POSITION_Z;
		}
			
	}
		
	public float getRotation()
	{
	    return (float)Math.atan2(up.x, up.y) * MathUtils.radiansToDegrees;
	}
}
