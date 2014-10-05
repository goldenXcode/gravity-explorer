package se.fkstudios.gravitynavigator.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.model.SpaceshipModel;
import se.fkstudios.gravitynavigator.view.RenderOptions;

import com.badlogic.gdx.Gdx;
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
		activePointerPositions = new HashMap<Integer, Vector2>();
		activePointerStartPositions = new HashMap<Integer, Vector2>();
		lengthForFullThurst = Math.round(Math.min(screenWidth, screenHeight) / 2);		
		this.playerSpaceship = playerMapObject;
		this.camera = camera;
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
		registerPointer(pointer, screenX, screenY);
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
		}
		else if (pointerCount == 2) { 
			//Two fingers down, do rotate and zoom camera.
			//TODO implement code
		}
		return true;
		
//		if (pointerCount == 1) {
//			int dragScreenX =  screenX - pointer0StartX;
//			int dragScreenY = -(screenY - pointer0StartY);
//			float dragLength = (float) Math.sqrt((Math.pow(dragScreenX, 2) + Math.pow(dragScreenY, 2))); 
//			float amountOfThrust = Math.min(1.0f, dragLength / lengthForFullThurst);
//			float thrustLength = amountOfThrust * playerSpaceship.getMaxThrust();
//			float screenToModelRatio = thrustLength / dragLength;
//			Vector2 newThrust = new Vector2(dragScreenX * screenToModelRatio, dragScreenY * screenToModelRatio);
//			playerSpaceship.setThrust(newThrust);
//		}
//		else if (pointerCount == 2) {
//			Vector2 oldDistanceDiff = new Vector2();
//			Vector2 distanceDiff = new Vector2();
//			oldDistanceDiff.x = Math.abs(pointer0X - pointer1X);
//			oldDistanceDiff.y = Math.abs(pointer0Y - pointer1Y);
//			if (pointer == 0) {
//				distanceDiff.x = Math.abs(screenX - pointer1X); 
//				distanceDiff.y = Math.abs(screenY - pointer1Y); 
//			}
//			else if (pointer == 1) {
//				distanceDiff.x = Math.abs(screenX - pointer0X); 
//				distanceDiff.y = Math.abs(screenY - pointer0Y);
//			}
//			if (oldDistanceDiff.dst2(distanceDiff) > 10) {
//				if (oldDistanceDiff.len2() > distanceDiff.len2())
//					camera.zoom(-0.1f);
//				else if (oldDistanceDiff.len2() < distanceDiff.len2())
//					camera.zoom(0.1f);
//			}
//		}
//		
//		if (pointer == 0) {
//			pointer0X = screenX;
//			pointer0Y = screenY;
//			return true;
//		}
//		else if (pointer == 1) {
//			pointer1X = screenX;
//			pointer1Y = screenY;
//			return true;
//		}
//		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (camera.getViewport().width / Defs.PIXELS_PER_UNIT < Defs.MAP_WIDTH * Defs.CAMERA_LIMIT || amount < 0) {
			camera.zoom(amount); 
			return true;
		}
		return false;
	}
	
	private Vector2 getPointerStartPosition(int orderIndex) {
		return getPointerPosition(activePointerStartPositions, orderIndex);
	}
	
	private Vector2 getPointerPosition(int orderIndex) {
		return getPointerPosition(activePointerPositions, orderIndex);
	}
	
	private Vector2 getPointerPosition(HashMap<Integer, Vector2> pointerMap, int orderIndex) {
		LinkedList<Integer> sortedPointers = new LinkedList<>(pointerMap.keySet());
		Collections.sort(sortedPointers);
		int pointer = sortedPointers.get(orderIndex);
		return pointerMap.get(pointer);
	}
	
	
	private void registerPointer(int pointer, int positionX, int positionY) {
		if (activePointerPositions.containsKey(pointer)) {
			Vector2 position = activePointerPositions.get(pointer);
			position.x = positionX;
			position.y = positionY;
		}
		else {
			activePointerPositions.put(pointer, new Vector2(positionX, positionY));
		}
		
		if (!activePointerStartPositions.containsKey(pointer)) {
			activePointerStartPositions.put(pointer, new Vector2(positionX, positionY));
		}
		
		if (getPointerCount() > 1) {
			playerSpaceship.setThrust(0, 0);
		}
	}
	
	private void unregisterPointer(int pointer) {
		activePointerPositions.remove(pointer);
		activePointerStartPositions.remove(pointer);
		
		if (getPointerCount() == 0) {
			playerSpaceship.setThrust(0, 0);
		}
	}
	
	private int getPointerCount() {
		return activePointerPositions.size();
	}
}