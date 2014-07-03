package se.fkstudios.gravitynavigator.controller;

import se.fkstudios.gravitynavigator.ResourceDefs;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;
import se.fkstudios.gravitynavigator.model.TextureMapObjectModel;
import se.fkstudios.gravitynavigator.model.SpaceshipModel;
import se.fkstudios.gravitynavigator.view.PeriodicMapRenderer;
import se.fkstudios.gravitynavigator.view.RenderDefs;
import se.fkstudios.gravitynavigator.view.RenderOptions;
import se.fkstudios.gravitynavigator.view.TextureMapObjectRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	private PeriodicMapModel map;
	private SpaceshipModel playerSpaceship;

	//Controller stuff
	private InputProcessor inputProccessor;
	
	//Rendering stuff
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private PeriodicMapRenderer mapRenderer;
	private TextureMapObjectRenderer textureMapObjectRenderer;
	
	@Override
	public void show() throws IllegalStateException {
	    
		map = new PeriodicMapModel(ResourceDefs.TEXTURE_NAMES[0], 10, 5);
	    
		playerSpaceship = map.getPlayerSpaceship();
		Vector2 playerMapObjectPos = playerSpaceship.getPosition();

		camera = new OrthographicCamera(
				RenderDefs.VIEWPORT_WIDTH, 
				RenderDefs.VIEWPORT_HEIGHT);
	    
		camera.position.set(
				playerMapObjectPos.x * RenderDefs.PIXELS_PER_UNIT, 
				playerMapObjectPos.y * RenderDefs.PIXELS_PER_UNIT, 
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
	    
	    mapRenderer = new PeriodicMapRenderer();
	    textureMapObjectRenderer = new TextureMapObjectRenderer(new ShapeRenderer(), spriteBatch,
	    		map.getWidth(), map.getHeight(), camera.viewportWidth, camera.viewportHeight);
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
		
		Array<TextureMapObjectModel> textureMapObjects;
		MapObjects allMapObjects;
		
		for (MapLayer layer : map.getLayers()) {	
			allMapObjects = layer.getObjects();
			textureMapObjects = allMapObjects.getByType(TextureMapObjectModel.class);
			
			for (TextureMapObjectModel textureMapObject : textureMapObjects)
				textureMapObjectRenderer.render(textureMapObject, camera.position);
		}
	}

	@Override
	public void resize(int width, int height) {
		Vector2 size = Scaling.fit.apply(camera.viewportWidth, camera.viewportHeight,  width, height);
		
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
		Vector2 targetPosition = playerSpaceship.getPosition().cpy().scl(RenderDefs.PIXELS_PER_UNIT);
		Vector2 cameraPosition = new Vector2(camera.position.x, camera.position.y);
		 
		float jumpThresholdX = map.getWidth() * RenderDefs.PIXELS_PER_UNIT * 0.5f;
		float jumpThresholdY = map.getHeight() * RenderDefs.PIXELS_PER_UNIT * 0.5f;
		
		boolean jumpLeft = cameraPosition.x - targetPosition.x < -jumpThresholdX;
		boolean jumpRight = targetPosition.x - cameraPosition.x < -jumpThresholdX;
		boolean jumpUp = targetPosition.y - cameraPosition.y < -jumpThresholdY; 
		boolean jumpDown = cameraPosition.y - targetPosition.y < -jumpThresholdY;
		
		if (jumpLeft) 
			cameraPosition.x = cameraPosition.x + map.getWidth() * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpRight)
			cameraPosition.x = cameraPosition.x - map.getWidth() * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpUp)
			cameraPosition.y = cameraPosition.y - map.getHeight() * RenderDefs.PIXELS_PER_UNIT;
		else if (jumpDown)
			cameraPosition.y = cameraPosition.y + map.getHeight() * RenderDefs.PIXELS_PER_UNIT;
		
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