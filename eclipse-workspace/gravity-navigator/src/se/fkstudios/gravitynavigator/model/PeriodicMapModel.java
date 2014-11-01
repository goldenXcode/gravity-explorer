package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.resources.ResourceContainer;
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

	private PhysicsEngine physicsEngine;
	
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
		

		physicsEngine = PhysicsEngine.getInstance();
		
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
	
	public Array<TextureRegionResource> getResources() {
		return resources;
	}
	
	public Array<MapObjectModel> getGameplayObjects() {
		return gameplayLayer.getObjects().getByType(MapObjectModel.class);
	}
	
	/**
	 * Updates the map and all owned MapObject for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta, GameplayCamera camera) {
		
		Array<MapObjectModel> gameplayMapObjects = getGameplayObjects();
		
		for (MapObjectModel mapObject : gameplayMapObjects) {
			mapObject.setAcceleration(new Vector2(0,0));
			physicsEngine.applyGravity(mapObject, gameplayMapObjects, getWidth(), getHeight(), delta);
			if (mapObject.isSelfStabilizing()) {
				applyStabilizingAcceleration(mapObject, delta);
			}
			mapObject.update(delta);
			applyMapObjectPeriodicity(mapObject);
		}
	}
	
	private void applyStabilizingAcceleration(MapObjectModel mapObject, float delta) {
		MapObjectModel dominationgObject = physicsEngine.findGravitationallyStrongestObject(mapObject, 
				getGameplayObjects(), 
				getWidth(), getHeight());
		
		if (dominationgObject != null) {
			Vector2 compAcceleration = physicsEngine.calculateOrbitCompensationAcceleration(mapObject, 
					dominationgObject, 
					getWidth(), getHeight(), 
					delta);
			
			Vector2 currentAcceleration = mapObject.getAcceleration();
			
			mapObject.setAcceleration(compAcceleration.x + currentAcceleration.x, 
					compAcceleration.y + currentAcceleration.y);
			
			//TODO: compensate dominating object or parent object with force.
		}
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
	 * Loads the map objects to the map. 
	 * TODO: in a distant future we want the map to be loaded from a file.
	 */
	private void loadMapObjects() {
		MapObjectFactory factory = MapObjectFactory.getInstance();
		Array<MapObjectModel> neighborhood = new Array<MapObjectModel>();
		
		MapObjectModel stationaryPlanet = factory.createStationaryPlanet(neighborhood, 66, 66, new Vector2(width / 2, height / 2), 2f);
		for (int i = 0; i < 3; i++) {
			MapObjectModel orbitingPlanet = factory.createOrbitingPlanet(neighborhood, stationaryPlanet, (i+2) * 65, (i * 33) % 360, 0.05f, false, -4f * i);
			if (i == 2) {
				factory.createOrbitingAsteroid(neighborhood, orbitingPlanet, 25f, 0f, 0.05f, true, -8f);
			}
		}
		//player spaceship
		playerSpaceship = factory.createPlayerSpaceship();
		neighborhood.add(playerSpaceship);
		
		MapObjects gamplayMapObjects = gameplayLayer.getObjects();
		for (MapObjectModel mapObject : neighborhood) {
			gamplayMapObjects.add(mapObject);
		}
	}
}
