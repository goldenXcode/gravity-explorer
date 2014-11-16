package se.fkstudios.gravityexplorer.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.model.SpaceshipModel;
import se.fkstudios.gravityexplorer.view.RenderOptions;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Input processor for the gameplay screen. Receives input events from mouse, keyboard and touch screens and 
 * updates model and controller accordingly.
 * @author kristofer
 */
public class GameplayInputProcessor implements InputProcessor {

	private HashMap<Integer, Vector2> activePointerPositions;
	private HashMap<Integer, Vector2> activePointerStartPositions;
	private SpaceshipModel playerSpaceship;
	private GameplayCamera camera;
	private int lengthForFullThurst;
	private int lengthToDoubleZoom;
	private float lastLength;
	private Vector2 lastVector;
	private Vector2 currentVector;
	
	/**
	 * Creates a GameplayInputProcesor for given SpaceshipObject and viewport.
	 * remark: Does not account future changes to resolution.
	 * @param playerMapObject The player controlled object to control.
	 * @param screenWidth Screen width in pixels.
	 * @param screenHeight Screen height in pixels.
	 */
	public GameplayInputProcessor(SpaceshipModel playerMapObject, GameplayCamera camera, int screenWidth, int screenHeight) {
		activePointerPositions = new HashMap<Integer, Vector2>();
		activePointerStartPositions = new HashMap<Integer, Vector2>();
		this.playerSpaceship = playerMapObject;
		this.camera = camera;
		lengthForFullThurst = Math.round(Math.min(screenWidth, screenHeight) / 2);		
		lengthToDoubleZoom = Math.round(Math.min(screenWidth, screenHeight) / 1.5f);
		lastLength = 0f;
		lastVector = new Vector2(0, 0);
		currentVector = new Vector2(0, 0);
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
		else if (keycode == Input.Keys.R) {
			camera.rotate(10f);
		}
		else if (keycode == Input.Keys.T) {
			camera.rotate(-10);
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
		registerPointer(pointer, screenX, screenY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		unregisterPointer(pointer);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		updatePointer(pointer, screenX, screenY);
		int pointerCount = getPointerCount();
		if (pointerCount == 1) { 
			//One finger down, do thrust from start drag position.
			Vector2 pointerStartPos = getPointerStartPosition(0);
			float dragX =  -(screenX - pointerStartPos.x);
			float dragY = screenY - pointerStartPos.y;
			float dragLength = (float) Math.sqrt((Math.pow(dragX, 2) + Math.pow(dragY, 2)));
			float thurstAmount = Math.min(1.0f, dragLength / lengthForFullThurst);
			float thrustPower = thurstAmount * playerSpaceship.getMaxThrust();
			float thrustPowerPerDragLength = thrustPower / Math.max(dragLength, Float.MIN_VALUE);
			playerSpaceship.setThrust(dragX * thrustPowerPerDragLength, dragY * thrustPowerPerDragLength);
			playerSpaceship.getThrust().rotate(-camera.getRotation());
		}
		else if (pointerCount == 2) { 
			//Two fingers down, do rotate and zoom camera.
			//zoom
			Vector2 pointer0 = getPointerPosition(0);
			Vector2 pointer1 = getPointerPosition(1);
			float length = pointer0.dst(pointer1);
			float lengthDelta = lastLength - length;
			float lengthDeltaOfScreen = lengthDelta / lengthToDoubleZoom;
			camera.zoom = camera.zoom + lengthDeltaOfScreen * camera.zoom;
			lastLength = length;
			//rotation
			currentVector.x = pointer0.x - pointer1.x;
			currentVector.y = pointer0.y - pointer1.y;
			float angleDelta = lastVector.angle() - currentVector.angle();			
			camera.rotate(angleDelta);
			lastVector.x = currentVector.x;
			lastVector.y = currentVector.y;
		}
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (camera.getViewport().width / Defs.PIXELS_PER_UNIT < Defs.MAP_WIDTH * Defs.CAMERA_LIMIT || amount < 0)
			camera.zoom(amount); 
		return true;
	}
	
	private Vector2 getPointerStartPosition(int orderIndex) {
		return getPointerPosition(activePointerStartPositions, orderIndex);
	}
	
	private Vector2 getPointerPosition(int orderIndex) {
		return getPointerPosition(activePointerPositions, orderIndex);
	}
	
	private Vector2 getPointerPosition(HashMap<Integer, Vector2> pointerMap, int orderIndex) {
		LinkedList<Integer> sortedPointers = new LinkedList<Integer>(pointerMap.keySet());
		Collections.sort(sortedPointers);
		int pointer = sortedPointers.get(orderIndex);
		return pointerMap.get(pointer);
	}
	
	
	private void registerPointer(int pointer, int positionX, int positionY) {
		activePointerPositions.put(pointer, new Vector2(positionX, positionY));
		activePointerStartPositions.put(pointer, new Vector2(positionX, positionY));
		
		int pointerCount = getPointerCount();
		if (pointerCount > 1) {
			playerSpaceship.setThrust(0, 0);
		}
		if (pointerCount == 2)
		{
			Vector2 pointer0 = getPointerPosition(0);
			Vector2 pointer1 = getPointerPosition(1);
			lastLength = pointer0.dst(pointer1);
			lastVector.x = pointer0.x - pointer1.x;
			lastVector.y = pointer0.y - pointer1.y;
		}
	}
	
	private void unregisterPointer(int pointer) {
		activePointerPositions.remove(pointer);
		activePointerStartPositions.remove(pointer);
		
		int pointerCount = getPointerCount();
		if (pointerCount == 0) {
			playerSpaceship.setThrust(0, 0);
		}
		else if (pointerCount == 1) {
			Vector2 pointerPos = getPointerPosition(0);
			Vector2 pointerStartPos = getPointerStartPosition(0);
			pointerStartPos.x = pointerPos.x;
			pointerStartPos.y = pointerPos.y;
		}
		else if (pointerCount == 2) {
			Vector2 pointer0 = getPointerPosition(0);
			Vector2 pointer1 = getPointerPosition(1);
			lastLength = pointer0.dst(pointer1);
			lastVector.x = pointer0.x - pointer1.x;
			lastVector.y = pointer0.y - pointer1.y;
		}
	}
	
	private void updatePointer(int pointer, int positionX, int positionY) {
		if (activePointerPositions.containsKey(pointer)) {
			Vector2 position = activePointerPositions.get(pointer);
			position.x = positionX;
			position.y = positionY;
		}
	}
	
	private int getPointerCount() {
		return activePointerPositions.size();
	}
}