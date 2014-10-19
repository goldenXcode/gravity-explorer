package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
import se.fkstudios.gravitynavigator.model.resources.ResourceContainer;
import se.fkstudios.gravitynavigator.model.resources.TextureAtlasResource;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represent a (TODO: should be, is not now) periodic map with floating point width and height. 
 * @author kristofer
 */
public class PeriodicMapModel extends Map implements ResourceContainer {
	
	private final String GAMEPLAY_LAYER_NAME = "gameplayLayer";

	private float width;
	private float height;
	private MapLayer gameplayLayer;
	private Array<TextureRegionResource> resources;
	private SpaceshipModel playerSpaceship;
	
	/**
	 * Creates a continuous map. 
	 * @param filePathBackgroundImageLayer1 Name of the texture to display as background.
	 * @param width Width of map.
	 * @param height Height of map.
	 */
	public PeriodicMapModel(
			String backgroundLayer1ImageName,
			String backgroundLayer2ImageName,
			float width, float height) {
		this.width = width;
		this.height = height;
		
		float longestViewportSide = Utility.getModelCoordinate(Math.max(Defs.VIEWPORT_WIDTH, Defs.VIEWPORT_HEIGHT));
		resources = new Array<TextureRegionResource>();
		resources.add(
			new TextureResource(new Vector2(0,0),
				true, 
				1f,
				1f,
				longestViewportSide,
				longestViewportSide,
				backgroundLayer1ImageName));

		resources.add(
			new TextureResource(new Vector2(0,0), 
				true, 
				1f,
				1f,
				longestViewportSide * 2f, 
				longestViewportSide * 2f, 
				backgroundLayer2ImageName));
		
		gameplayLayer = new MapLayer();
		gameplayLayer.setName(GAMEPLAY_LAYER_NAME);
		this.getLayers().add(gameplayLayer);
		loadMapObjects();
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	public SpaceshipModel getPlayerSpaceship() {
		return playerSpaceship;
	}
	
	/**
	 * Loads the map objects to the map. 
	 * TODO: in future we want a map go be given a file and loading the map from it. 
	 */
	private void loadMapObjects() {
		// TODO: specify a XML format for model map's start conditions. 
		// Preferably also create a map editor to create these.
		// Note: I'm thinking ModelDefs could contain the XML-parser. 
		Vector2 position = Defs.STARTING_POSITION;
		Vector2 velocity = Defs.STARTING_VELOCITY; 
		Array<TextureRegionResource> allResources = new Array<TextureRegionResource>();
		allResources.add(new TextureAtlasResource(new Vector2(0, 0), 
				true, 
				Defs.MIN_RENDER_SCALE_SPACESHIP, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.TEXTURE_REGION_NAME_SPACESHIP_PLAYER));
		Array<AnimationResource> thrustAnimations = new Array<AnimationResource>();
		thrustAnimations.add(new AnimationResource(new Vector2(0.40f, -1.25f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(0.15f, -1.3f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(-0.15f, -1.3f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(-0.40f, -1.25f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		allResources.addAll(thrustAnimations);
		playerSpaceship = new SpaceshipModel(position, 
				1.32f, 
				2.28f, 
				velocity, 
				0, 
				1,
				Defs.MAX_THRUST,
				allResources,
				thrustAnimations);


//		TextureMapObjectModel asterioid2 = new TextureMapObjectModel(new Vector2(2, 1), 0.33f, 0.5f, 
//				new Vector2(0.01f, -0.4f), 0, 10, ResourceDefs.TEXTURE_REGION_NAME_ASTERIOID_02);
	
		
		MapObjects gamplayMapObjects = gameplayLayer.getObjects();
		
		PhysicsEngine.setPeriodicMapModel(this);

//		//adding the asteroids 
//		TextureMapObjectModel[] asteroids = RandomMapGenerator.generateMapObjects(Defs.NUMBER_OF_ASTEROIDS);
//		PhysicsEngine.add(asteroids);
//		for (int i = 0; i< asteroids.length; i++) {
//			gamplayMapObjects.add(asteroids[i]); 
//		}
		
		// adding the planet 
		MapObjectModel planet = RandomMapGenerator.generatePlanet(Defs.PLANET_SIZE);
		PhysicsEngine.add(planet); 
		planet.setSelfStabilizing(false); 
		gamplayMapObjects.add(planet); 
		
		// adding the player spaceship 
		gamplayMapObjects.add(playerSpaceship);
		PhysicsEngine.add(playerSpaceship);
		
		// adding the orbiting asteroids
		float spacing = planet.getRadius()*Defs.ASTEROID_SPACING; 
		MapObjectModel[] orbiters = RandomMapGenerator.generateOrbitingAsteroids(7f, planet,spacing,Defs.NUMBER_OF_ASTEROIDS);
		for (int i = 0; i< orbiters.length; i++) {
			gamplayMapObjects.add(orbiters[i]); 
		}
		PhysicsEngine.add(orbiters);
	}
	

	private void applyMapObjectPeriodicity(MapObjectModel gameplayMapObject) {

		Vector2 position = gameplayMapObject.getPosition();

		if (position.x > this.getWidth())
			position.x = position.x % getWidth();
		
		if (position.y > getHeight())
			position.y = position.y % getHeight(); 
		
		if (position.x < 0)
			position.x = Math.abs(position.x + getWidth());
		
		if (position.y < 0)
			position.y = Math.abs(position.y + getHeight());
	}
	
	/**
	 * Updates the map and all owned MapObject for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta, GameplayCamera camera) {
		
		MapObjects allMapObjects = gameplayLayer.getObjects();
		Array<MapObjectModel> gameplayMapObjects = new Array<MapObjectModel>(allMapObjects.getCount());
		allMapObjects.getByType(MapObjectModel.class, gameplayMapObjects);
		
		for (MapObjectModel gameplayMapObject : gameplayMapObjects) {
			gameplayMapObject.setAcceleration(new Vector2(0,0));
			applyMapObjectPeriodicity(gameplayMapObject);
		}
		
		//PhysicsEngine.applyGravity();
		PhysicsEngine.applyGravity2(delta); 
		
		for (MapObjectModel gameplayMapObject : gameplayMapObjects) {
			gameplayMapObject.update(delta);
			applyMapObjectPeriodicity(gameplayMapObject);
		}
	}
	
	public Array<TextureRegionResource> getResources() {
		return resources;
	}
}
