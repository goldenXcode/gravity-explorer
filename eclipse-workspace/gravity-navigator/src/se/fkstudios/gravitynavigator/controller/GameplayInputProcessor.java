package se.fkstudios.gravitynavigator.controller;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.SpaceshipModel;
import se.fkstudios.gravitynavigator.view.RenderOptions;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Input processor for the gameplay screen. Receives input events from mouse, keyboard and touch screens and 
 * updates model and controller accordingly.
 * @author kristofer
 */
public class GameplayInputProcessor implements InputProcessor {

	private int startDragScreenX;
	private int startDragScreenY;
	private int lengthForFullThurst;
	private SpaceshipModel playerSpaceship;
	private GameplayCamera camera;
	
	/**
	 * Creates a GameplayInputProcesor for given SpaceshipObject and viewport.
	 * remark: Does not account future changes to resolution.
	 * @param playerMapObject The player controlled object to control.
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 */
	public GameplayInputProcessor(SpaceshipModel playerMapObject, GameplayCamera camera, int screenWidth, int screenHeight) {
		this.playerSpaceship = playerMapObject;
		this.camera = camera;
		startDragScreenX = -1;
		startDragScreenY = -1;
		lengthForFullThurst = Math.round(Math.min(screenWidth, screenHeight) / 2);
	}

	@Override
	public boolean keyDown(int keycode) {
		
		if (keycode == Input.Keys.D){
			RenderOptions.getInstance().debugRender = ! RenderOptions.getInstance().debugRender;
		}
		else if (keycode == Input.Keys.A) {
			camera.zoomIn(); 
		}
		else if (keycode == Input.Keys.S) {
			camera.zoomOut(); 
		}
		else if (keycode == Input.Keys.C) {
			GameplayCamera.CameraMode mode =  camera.getCameraMode(); 
			if (mode == (GameplayCamera.CameraMode.TIGHT))
					camera.setCameraMode(GameplayCamera.CameraMode.LOOSE);
			else 
				camera.setCameraMode(GameplayCamera.CameraMode.TIGHT);
		}
		return true;	
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		startDragScreenX = screenX;
		startDragScreenY = screenY;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		startDragScreenX = -1;
		startDragScreenY = -1;
		playerSpaceship.setThrust( new Vector2(0, 0));
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		int dragScreenX = screenX - startDragScreenX;
		int dragScreenY = - (screenY - startDragScreenY);
		
		// sqrt( dragScrrenX^2 + dragScreenY^2 )
		float dragLength = (float) Math.sqrt((Math.pow(dragScreenX, 2) + Math.pow(dragScreenY, 2))); 
		float amountOfThrust = Math.min(1.0f, dragLength / lengthForFullThurst);

		float thrustLength = amountOfThrust * playerSpaceship.getMaxThrust();
		
		float screenToModelRatio = thrustLength / dragLength;
		
		Vector2 newThrust = new Vector2(dragScreenX * screenToModelRatio, dragScreenY * screenToModelRatio);
		playerSpaceship.setThrust(newThrust);
		
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		camera.zoom(amount*Defs.SCROLLING_SPEED_MODIFIER + Math.log(camera.getZoom())*amount); 
		return false;
	}
}