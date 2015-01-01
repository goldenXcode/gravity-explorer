package se.fkstudios.gravityexplorer.controller;

import java.math.BigDecimal;
import java.util.HashMap;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.model.MapObjectModel;
import se.fkstudios.gravityexplorer.model.PeriodicMapModel;
import se.fkstudios.gravityexplorer.model.SpaceshipModel;
import se.fkstudios.gravityexplorer.model.resources.ColorResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.ModelResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionResource;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
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
	
	private HashMap<TextureRegionResource, MapObjectModel> textureRegionResourcesMap;
	private HashMap<ModelResource, MapObjectModel> modelResourcesMap;
	private HashMap<ColorResource, MapObjectModel> colorResourcesMap;
	private Array<ModelResource> lightSources;
	
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
		
		textureRegionResourcesMap = new HashMap<TextureRegionResource, MapObjectModel>();
		modelResourcesMap = new HashMap<ModelResource, MapObjectModel>();
		colorResourcesMap = new HashMap<ColorResource, MapObjectModel>();
		lightSources = new Array<ModelResource>();
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
	}
	
	/**
	 * The real render method for actual rendering of graphics, i.e. draws the
	 * current state of model.
	 */
	private void realRender(float delta) {
		// clear buffers.
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		renderMap(delta);
		
		prepareResources();
		
		renderModelResources();
		renderColorResources();
		renderTextureRegionResources();
	}
	
	/**
	 * Map renderable resources to corresponding map objects based on resource type. 
	 */
	private void prepareResources() {
		textureRegionResourcesMap.clear();
		modelResourcesMap.clear(); 
		colorResourcesMap.clear();
		lightSources.clear();
		
		for (MapLayer layer : map.getLayers()) {
			MapObjects allMapObjects = layer.getObjects();
			for (MapObject mapObject : allMapObjects) {
				MapObjectModel mapObjectModel = (MapObjectModel)mapObject;
				Array<GraphicResource> resources = mapObjectModel.getResources();
				for (GraphicResource resource : resources) {	
					if (resource.isVisible()) {
						if (resource instanceof ModelResource) {
							ModelResource modelResource = (ModelResource)resource;
							modelResourcesMap.put(modelResource, mapObjectModel);
						}
						else if (resource instanceof TextureRegionResource) {
							textureRegionResourcesMap.put((TextureRegionResource)resource, mapObjectModel);
						}
						else if (resource instanceof ColorResource) {
							colorResourcesMap.put((ColorResource)resource, mapObjectModel);
						}
						else {
							throw new IllegalStateException("Could not find renderer for given resource class.");
						}
					}
				}
			}
		}
	}
	
	private void renderModelResources() {
		modelRenderer.getModelBatch().begin(camera);
		Environment environment = calculateEnvironment();
		
		for (ModelResource resource : modelResourcesMap.keySet()) {
			MapObjectModel mapObject = modelResourcesMap.get(resource);	
			
			if (resource.isLightSource()) {
				modelRenderer.setEnvironment(null);
				modelRenderer.renderObjectPeriodically(mapObject, resource, camera);
			}
			else {
				modelRenderer.setEnvironment(environment);
				modelRenderer.renderObjectPeriodically(mapObject, resource, camera);
			}
		}
		modelRenderer.getModelBatch().end();			
	}
	
	private void renderTextureRegionResources() {
		textureRegionRenderer.updateToCamera(camera);
		textureRegionRenderer.spriteBatch.begin();
		for (TextureRegionResource resource : textureRegionResourcesMap.keySet()) {
			MapObjectModel mapObject = textureRegionResourcesMap.get(resource);
			textureRegionRenderer.renderObjectPeriodically(mapObject, resource, camera);
		}
		textureRegionRenderer.spriteBatch.end();
	}
	
	private void renderColorResources() {
		colorRenderer.updateToCamera(camera);
		colorRenderer.shapeRenderer.begin(ShapeType.Filled);
		for (ColorResource resource : colorResourcesMap.keySet()) {
			MapObjectModel mapObject = colorResourcesMap.get(resource);
			colorRenderer.renderObjectPeriodically(mapObject, resource, camera);
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
		Environment environment = new Environment();
		float colorIntencity = 0.6f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, colorIntencity, colorIntencity, colorIntencity, 1f));
		
		Array<BaseLight> lightSources = new Array<BaseLight>(modelResourcesMap.keySet().size());
		
		for (ModelResource resource : modelResourcesMap.keySet()) {
			if (resource.isLightSource()) {
				MapObjectModel model = modelResourcesMap.get(resource);
				lightSources.add(resource.getLightSource(model.getPosition()));
			}
		}
		
		if (lightSources.size > 0)
			environment.add(lightSources);
		
		return environment;
	}
	
	private float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}