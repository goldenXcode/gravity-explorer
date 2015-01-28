package se.fkstudios.gravityexplorer.controller;

import java.math.BigDecimal;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.model.PeriodicMapModel;
import se.fkstudios.gravityexplorer.model.SpaceshipModel;
import se.fkstudios.gravityexplorer.model.resources.AnimationBinding;
import se.fkstudios.gravityexplorer.model.resources.ColorBinding;
import se.fkstudios.gravityexplorer.model.resources.GraphicsLoader;
import se.fkstudios.gravityexplorer.model.resources.ModelBinding;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionBinding;
import se.fkstudios.gravityexplorer.view.ColorRenderer;
import se.fkstudios.gravityexplorer.view.ModelRenderer;
import se.fkstudios.gravityexplorer.view.PeriodicMapRenderer;
import se.fkstudios.gravityexplorer.view.TextureRegionRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
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
	private float deltaBuffer;
	
	// Camera
	private GameplayCamera camera;

	// Rendering
	private PeriodicMapRenderer mapRenderer;
	private TextureRegionRenderer textureRegionRenderer;
	private ColorRenderer colorRenderer;
	private ModelRenderer modelRenderer;
	
	Array<BaseLight> allLightSources;
	
	@Override
	public void show() throws IllegalStateException {

		map = new PeriodicMapModel(Defs.TEXTURE_NAMES[0], Defs.TEXTURE_NAMES[1], Defs.MAP_WIDTH, Defs.MAP_HEIGHT);

		playerSpaceship = map.getPlayerSpaceship();
		Vector2 playerMapObjectPos = playerSpaceship.getPosition();
		
		Vector3 cameraStartPos = new Vector3(playerMapObjectPos.x * Defs.PIXELS_PER_UNIT, playerMapObjectPos.y * Defs.PIXELS_PER_UNIT, Defs.CAMERA_POSITION_Z);
		camera = new GameplayCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cameraStartPos);
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

		deltaBuffer = 0f;
		
		float longestViewportSide = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		mapRenderer = new PeriodicMapRenderer(longestViewportSide, longestViewportSide);

		float screenMapWidth = Utility.getScreenCoordinate(map.getWidth());
		float screenMapHeight = Utility.getScreenCoordinate(map.getHeight());
		
		textureRegionRenderer = new TextureRegionRenderer(screenMapWidth, screenMapHeight);
		colorRenderer = new ColorRenderer(screenMapWidth, screenMapHeight);
		modelRenderer = new ModelRenderer(screenMapWidth, screenMapHeight);
		
		allLightSources = new Array<BaseLight>();
	}

	/*
	 * (non-Javadoc) The game loop. Named render in libGDX due to stupidity...
	 * 
	 * But is the main game loop handling both rendering and game logic.
	 * @see com.badlogic.gdx.Screen#render(float)
	 */
	@Override
	public void render(float delta) {		
		float fixedDelta = 1f / 100f;
		deltaBuffer += delta;
		
		while(deltaBuffer > fixedDelta) {		
			map.update(fixedDelta);
			deltaBuffer =- fixedDelta;
		}
		
		if (deltaBuffer > 0) {
			map.update(deltaBuffer);
		}
		
		camera.updatePosition(fixedDelta, playerSpaceship.getPosition(), map.getWidth(), map.getHeight());
		camera.update();
		realRender(delta); //delta only used to measure fps.
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
		GraphicsLoader.getInstance().dispose();
		map = null;
	}
	
	/**
	 * The real render method for actual rendering of graphics, i.e. draws the
	 * current state of model.
	 */
	private void realRender(float delta) {
		// clear buffers.
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		renderMap(delta);
		
		renderModelResources();
		renderColorResources();
		renderTextureRegionResources();
		renderAnimationResources(delta);
	}
	
	private void renderAnimationResources(float delta) {
		GraphicsLoader loader = GraphicsLoader.getInstance();
		Iterable<AnimationBinding> bindings = loader.getOwnedResourceBindings(AnimationBinding.class); 
				
		textureRegionRenderer.updateToCamera(camera);
		textureRegionRenderer.spriteBatch.begin();

		for (AnimationBinding resource : bindings) {
			resource.incStateTime(delta);
			if (resource.isVisible()) {
				textureRegionRenderer.renderObjectPeriodically(resource.getOwner(), resource, camera);
			}
		}
		textureRegionRenderer.spriteBatch.end();
	}
	
	private void renderModelResources() {
		GraphicsLoader loader = GraphicsLoader.getInstance();
		Iterable<ModelBinding> resources = loader.getOwnedResourceBindings(ModelBinding.class);	
		
		Environment environment = calculateEnvironment();
		modelRenderer.setEnvironment(environment);
		
		modelRenderer.getModelBatch().begin(camera);
		
		for (ModelBinding resource : resources) {
			if (resource.isVisible()) {
				if (resource.isLightSource()) {
					modelRenderer.renderObjectPeriodically(resource.getOwner(), resource, camera);
				}
				else {
					modelRenderer.renderObjectPeriodically(resource.getOwner(), resource, camera);
				}
			}
		}
		modelRenderer.getModelBatch().end();			
	}
	
	private void renderTextureRegionResources() {
		GraphicsLoader loader = GraphicsLoader.getInstance();
		Iterable<TextureRegionBinding> resources = loader.getOwnedResourceBindings(TextureRegionBinding.class);
		
		textureRegionRenderer.updateToCamera(camera);
		textureRegionRenderer.spriteBatch.begin();

		for (TextureRegionBinding resource : resources) {
			if (resource.isVisible()) {
				textureRegionRenderer.renderObjectPeriodically(resource.getOwner(), resource, camera);
			}
		}
		textureRegionRenderer.spriteBatch.end();
	}
	
	private void renderColorResources() {
		GraphicsLoader loader = GraphicsLoader.getInstance();
		Iterable<ColorBinding> resources = loader.getOwnedResourceBindings(ColorBinding.class);
		
		colorRenderer.updateToCamera(camera);
		colorRenderer.shapeRenderer.begin(ShapeType.Filled);
		for (ColorBinding resource : resources) {
			if (resource.isVisible()) {
				colorRenderer.renderObjectPeriodically(resource.getOwner(), resource, camera);
			}
		}
		colorRenderer.shapeRenderer.end();
	}

	private void renderMap(float delta) {
		mapRenderer.updateToCamera(camera);
		mapRenderer.setConsoleText("Running in " + Float.toString(round(1 / delta, 0)) + " FPS"
				+ ", fuel left: " + playerSpaceship.getFuelLeft()
				+ ", zoom: " + camera.zoom
				+ ", render calls: " + textureRegionRenderer.spriteBatch.totalRenderCalls);
		mapRenderer.render(map, camera);
	}
	
	private Environment calculateEnvironment() {        
		GraphicsLoader loader = GraphicsLoader.getInstance();
		Iterable<ModelBinding> resources = loader.getOwnedResourceBindings(ModelBinding.class);	

		Environment environment = new Environment();
		float colorIntencity = 0.4f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, colorIntencity, colorIntencity, colorIntencity, 1f));

		allLightSources.clear();
		
		for (ModelBinding resource : resources) {
			if (resource.isLightSource()) { 
				float screenMapWidth = Utility.getScreenCoordinate(map.getWidth());
				float screenMapHeight = Utility.getScreenCoordinate(map.getHeight());
				Array<PointLight> lightSources = resource.getLightSources(screenMapWidth, screenMapHeight);
				for (PointLight ligthSource : lightSources) {
					allLightSources.add(ligthSource);
				}
			}
		}
		
		if (allLightSources.size > 0)
			environment.add(allLightSources);
		
		return environment;
	}
	
	private float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}