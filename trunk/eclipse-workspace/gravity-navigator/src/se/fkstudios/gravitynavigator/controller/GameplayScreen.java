package se.fkstudios.gravitynavigator.controller;

import java.math.BigDecimal;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;
import se.fkstudios.gravitynavigator.model.SpaceshipModel;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;
import se.fkstudios.gravitynavigator.view.MapObjectTextureRegionRenderer;
import se.fkstudios.gravitynavigator.view.PeriodicMapRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private SpriteBatch spriteBatch;
	private PeriodicMapRenderer mapRenderer;
	private MapObjectTextureRegionRenderer textureRegionRenderer;

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

		spriteBatch = new SpriteBatch();

		float longestViewportSide = Math.max(Defs.VIEWPORT_WIDTH, Defs.VIEWPORT_HEIGHT);
		
		mapRenderer = new PeriodicMapRenderer(spriteBatch, longestViewportSide, longestViewportSide);
		textureRegionRenderer = new MapObjectTextureRegionRenderer(spriteBatch,
				Utility.getScreenCoordinate(map.getWidth()), 
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
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		spriteBatch.setProjectionMatrix(camera.combined);

		mapRenderer.setConsoleText("Running in " + Float.toString(round(1 / delta, 0)) 
				+ " FPS , time alive: " + round(playerSpaceship.getAliveTime(), 0) 
				+ ", fuel left: " + playerSpaceship.getFuelLeft()
				+ ", zoom: " + camera.zoom);
		
		mapRenderer.render(map, camera);
		
		Array<MapObjectModel> mapObjects;
		MapObjects allMapObjects;

		for (MapLayer layer : map.getLayers()) {
			allMapObjects = layer.getObjects();
			mapObjects = allMapObjects.getByType(MapObjectModel.class);
			for (MapObjectModel mapObject : mapObjects) {
				Array<TextureRegionResource> resources = mapObject.getResources();
				for (TextureRegionResource resource : resources) {
					if (resource.visible) {
						textureRegionRenderer.render(mapObject, resource, camera);
					}
				}
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		Vector2 size = Scaling.fit.apply(camera.viewportWidth,
				camera.viewportHeight, width, height);

		int viewportX = (int) (width - size.x) / 2;
		int viewportY = (int) (height - size.y) / 2;
		int viewportWidth = (int) size.x;
		int viewportHeight = (int) size.y;

		Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);

		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, viewportWidth,
				viewportHeight);
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