package se.fkstudios.gravityexplorer.controller;

import java.math.BigDecimal;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.model.MapObjectModel;
import se.fkstudios.gravityexplorer.model.PeriodicMapModel;
import se.fkstudios.gravityexplorer.model.SpaceshipModel;
import se.fkstudios.gravityexplorer.model.resources.ColorResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionResource;
import se.fkstudios.gravityexplorer.view.ColorRenderer;
import se.fkstudios.gravityexplorer.view.PeriodicMapRenderer;
import se.fkstudios.gravityexplorer.view.TextureRegionRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

/**
 * Responsible for controlling a actual gaming level.
 * 
 * @author kristofer
 */
public class GameplayScreen implements Screen {

	// Model
	private PeriodicMapModel map;
	private SpaceshipModel playerSpaceship;

	// Controller
	private InputProcessor inputProccessor;
	
	// Camera
	private GameplayCamera camera;

	// Rendering
	private PeriodicMapRenderer mapRenderer;
	private TextureRegionRenderer textureRegionRenderer;
	private ColorRenderer colorRenderer;
	
	
	
	@Override
	public void show() throws IllegalStateException {

		map = new PeriodicMapModel(Defs.TEXTURE_NAMES[0],
				Defs.TEXTURE_NAMES[1], Defs.MAP_WIDTH, Defs.MAP_HEIGHT);

		playerSpaceship = map.getPlayerSpaceship();
		Vector2 playerMapObjectPos = playerSpaceship.getPosition();

		camera = new GameplayCamera(Defs.VIEWPORT_WIDTH, Defs.VIEWPORT_HEIGHT);

		camera.position.set(playerMapObjectPos.x * Defs.PIXELS_PER_UNIT,
				playerMapObjectPos.y * Defs.PIXELS_PER_UNIT, 0.0f);

		camera.update();

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		// remark: we used different input controllers for different devices but
		// not anymore.
		// We keep the code if we want to change this back /kristofer
		switch (Gdx.app.getType()) {
		case Android:
			inputProccessor = new GameplayInputProcessor(playerSpaceship,
					camera, screenWidth, screenHeight);
			break;
		case iOS:
			inputProccessor = new GameplayInputProcessor(playerSpaceship,
					camera, screenWidth, screenHeight);
			break;
		case Desktop:
			inputProccessor = new GameplayInputProcessor(playerSpaceship,
					camera, screenWidth, screenHeight);
			break;
		default:
			throw new IllegalStateException("Input device not recognized");
		}

		Gdx.input.setInputProcessor(inputProccessor);

		float longestViewportSide = Math.max(Defs.VIEWPORT_WIDTH, Defs.VIEWPORT_HEIGHT);
		
		mapRenderer = new PeriodicMapRenderer(longestViewportSide, longestViewportSide);
		textureRegionRenderer = new TextureRegionRenderer(Utility.getScreenCoordinate(map.getWidth()), 
				Utility.getScreenCoordinate(map.getHeight()));
		colorRenderer = new ColorRenderer(Utility.getScreenCoordinate(map.getWidth()), 
				Utility.getScreenCoordinate(map.getHeight()));
	}

	/*
	 * (non-Javadoc) The game loop. Named render in libGDX due to stupidity...
	 * But is the main game loop handling both rendering and game logic.
	 * 
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		camera.updatePosition(delta, playerSpaceship.getPosition(), map.getWidth(), map.getHeight());
		camera.update();
		updateModel(delta);
		realRender(delta);
	}

	/**
	 * Updates model state.
	 * @param delta The time in seconds since the last call to updateGameState.
	 */
	private void updateModel(float delta) {
		map.update(delta, camera);
	}

	/**
	 * The real render method for actual rendering of graphics, i.e. draws the
	 * current state of model.
	 */
	private void realRender(float delta) {
		// clear buffer.
	    Gdx.gl.glClearColor(0, 0, 0.2f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRenderer.setProjectionMatrix(camera.combined);
		mapRenderer.setConsoleText("Running in " + Float.toString(round(1 / delta, 0)) + " FPS"
				+ ", fuel left: " + playerSpaceship.getFuelLeft()
				+ ", zoom: " + camera.zoom);
		mapRenderer.render(map, camera);
		

		textureRegionRenderer.setProjectionMatrix(camera.combined);
		colorRenderer.setProjectionMatrix(camera.combined);
		
		for (MapLayer layer : map.getLayers()) {
			MapObjects allMapObjects = layer.getObjects();
			Array<MapObjectModel> mapObjects = allMapObjects.getByType(MapObjectModel.class);
			for (MapObjectModel mapObject : mapObjects) {
				Array<GraphicResource> resources = mapObject.getResources();
				for (GraphicResource resource : resources) {
					if (resource.isVisible()) {
						if (resource instanceof ColorResource) {
							colorRenderer.renderObjectPeriodically(mapObject, resource, camera);
						}
						else if (resource instanceof TextureRegionResource) {
							textureRegionRenderer.renderObjectPeriodically(mapObject, resource, camera);
						}
						else {
							throw new IllegalStateException("Could not find renderer for given resource class.");
						}
					}
				}
			}
		}
	}
	
	@Override
	public void resize(int width, int height) {
		Vector2 size = Scaling.fit.apply(camera.viewportWidth, camera.viewportHeight, width, height);
		int viewportX = (int) (width - size.x) / 2;
		int viewportY = (int) (height - size.y) / 2;
		int viewportWidth = (int) size.x;
		int viewportHeight = (int) size.y;
		Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		Gdx.input.setInputProcessor(null);
	}

	private float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}