package se.fkstudios.gravitynavigator.controller;

import se.fkstudios.gravitynavigator.ResourceDefs;
import se.fkstudios.gravitynavigator.model.ContinuousMap;
import se.fkstudios.gravitynavigator.model.SimpleMapObject;
import se.fkstudios.gravitynavigator.model.SpaceshipMapObject;
import se.fkstudios.gravitynavigator.view.ContinuousMapRenderer;
import se.fkstudios.gravitynavigator.view.RenderingDefs;
import se.fkstudios.gravitynavigator.view.RenderingOptions;
import se.fkstudios.gravitynavigator.view.SimpleMapObjectRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

/**
 * Responsible for controlling a actual gaming level.
 * @author kristofer
 */
public class GameplayScreen implements Screen {

	//Model stuff
	private ContinuousMap map;
	private SpaceshipMapObject playerSpaceship;

	//Controller stuff
	private InputProcessor inputProccessor;
	
	//Rendering stuff
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private ContinuousMapRenderer mapRenderer;
	private SimpleMapObjectRenderer staticMapObjectRenderer;
	
	@Override
	public void show() throws IllegalStateException {
	    
		map = new ContinuousMap(ResourceDefs.FILE_PATH_MAP_BACKGROUND_1, 200, 100);
	    
		playerSpaceship = map.getPlayerSpaceship();
		Vector2 playerMapObjectPos = playerSpaceship.getPosition();

		camera = new OrthographicCamera(
				RenderingDefs.VIEWPORT_WIDTH, 
				RenderingDefs.VIEWPORT_HEIGHT);
	    
		camera.position.set(
				playerMapObjectPos.x * RenderingDefs.PIXELS_PER_UNIT, 
				playerMapObjectPos.y * RenderingDefs.PIXELS_PER_UNIT, 
				0.0f);
		
	    camera.update();

		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();
		
	    //remark: we used different input controllers for different devices but not anymore. 
		//Keep if we want to change this /kristofer
	    switch(Gdx.app.getType()) {
		    case Android:
		    	inputProccessor = new GameplayInputProcessor(playerSpaceship, 
		    			screenWidth, 
		    			screenHeight);
		    	break;
		    case iOS:
		    	inputProccessor = new GameplayInputProcessor(playerSpaceship, 
		    			screenWidth, 
		    			screenHeight);
		    	break;
		    case Desktop:
		    	inputProccessor = new GameplayInputProcessor(playerSpaceship, 
		    			screenWidth, 
		    			screenHeight);
		    	break;
		    default:
		    	throw new IllegalStateException("Input device not recognized");
	    }
	    
	    Gdx.input.setInputProcessor(inputProccessor);
	    
	    spriteBatch = new SpriteBatch();
	    
	    mapRenderer = new ContinuousMapRenderer();
	    staticMapObjectRenderer = new SimpleMapObjectRenderer();
	}

	/*
	 * (non-Javadoc)
	 * The game loop. Named render in libGDX due to stupidity... But is the main game loop handling both rendering and game logic.
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {
		updateModel(delta);
		updateCameraPosition( delta);		
		realRender();
	}
	
	/** 
	 * Updates model state.
	 * @param delta The time in seconds since the last call to updateGameState. 
	 */
	private void updateModel(float delta) {
		map.update(delta);
	}
	
	/**
	 * The real render method for actual rendering of graphics, i.e. draws the current state of model.
	 */
	private void realRender() {
		//clear buffer.
		Gdx.gl.glClearColor(0.1f, 0.1f,  0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		        	
		mapRenderer.render(spriteBatch, map);
		
		Array<SimpleMapObject> staticMapObjects;
		MapObjects allMapObjects;
		
		for (MapLayer layer : map.getLayers()) {	
			allMapObjects = layer.getObjects();
			staticMapObjects = allMapObjects.getByType(SimpleMapObject.class);
			
			for (SimpleMapObject mapObj : staticMapObjects)
				staticMapObjectRenderer.render(spriteBatch, mapObj);
		}
	}

	@Override
	public void resize(int width, int height) {
		Vector2 size = Scaling.fit.apply(camera.viewportWidth, camera.viewportHeight, width, height);
        int viewportX = (int)(width - size.x) / 2;
        int viewportY = (int)(height - size.y) / 2;
        int viewportWidth = (int)size.x;
        int viewportHeight = (int)size.y;
        
        Gdx.gl.glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, viewportWidth, viewportHeight);
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
	
	/**
	 * Update camera's position and zoom based on spaceship's (player's) position and speed.
	 */
	private void updateCameraPosition(float delta) {	
		
		//update position
		Vector2 targetPosition = playerSpaceship.getPosition().cpy().scl(RenderingDefs.PIXELS_PER_UNIT);
		Vector2 cameraPosition = new Vector2(camera.position.x, camera.position.y);
		cameraPosition.lerp(targetPosition, delta);

		camera.position.set(
				cameraPosition.x, 
				cameraPosition.y,
				0);
		
		//update zoom
		float speed = playerSpaceship.getVelocity().len();
		float maxZoom = 2;
		float minZoom = 1;
		camera.zoom = Math.min(maxZoom, minZoom + speed * 0.0005f);
			
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
	}
}
