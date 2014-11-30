package se.fkstudios.gravityexplorer.model;

import java.util.HashMap;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.controller.GameplayCamera;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.ResourceContainer;
import se.fkstudios.gravityexplorer.model.resources.TextureResource;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represent a (TODO: should be, is not now) periodic map with floating point width and height. 
 * @author kristofer
 */
public class PeriodicMapModel extends Map implements ResourceContainer {
	
	private PhysicsEngine physicsEngine;
	
	private float width;
	private float height;
	private Array<GraphicResource> resources;
	private SpaceshipModel playerSpaceship;
	private Array<MapObjectModel> allMapObjects;
	private HashMap<MapObjectModel, MapLayer> mapObjectLayerMap;
	
	private float spawnParticleCounter;
	
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
		allMapObjects = new Array<MapObjectModel>();
		mapObjectLayerMap = new HashMap<MapObjectModel, MapLayer>();
		
		float longestViewportSide = Utility.getModelCoordinate(Math.max(Defs.VIEWPORT_WIDTH, Defs.VIEWPORT_HEIGHT));
		resources = new Array<GraphicResource>();
		resources.add(
			new TextureResource(false, 
					new Vector2(0,0), 
					new Vector2(0,0), 
					false, 
					longestViewportSide, longestViewportSide, 
					true, 
					1f, 1f, 
					backgroundLayer1ImageName));
		
		resources.add(
				new TextureResource(false, 
					new Vector2(0,0), 
					new Vector2(0,0), 
					false, 
					longestViewportSide * 2, longestViewportSide * 2, 
					true, 
					1f, 1f, 
					backgroundLayer2ImageName));
		
		spawnParticleCounter = 0f;

		physicsEngine = PhysicsEngine.getInstance();
		
		loadMapObjectsDemoMap();
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
	
	public Array<GraphicResource> getResources() {
		return resources;
	}
	
	public void addMapObject(MapObjectModel mapObject, MapLayer neighbourhood) {
		allMapObjects.add(mapObject);
		neighbourhood.getObjects().add(mapObject);		
		mapObjectLayerMap.put(mapObject, neighbourhood);
	}
	
	public void removeMapObject(MapObjectModel mapObject) {
		MapLayer neighbourhood = mapObjectLayerMap.get(mapObject);
		neighbourhood.getObjects().remove(mapObject);
		allMapObjects.removeValue(mapObject, true);
		mapObjectLayerMap.remove(mapObject);
	}
	
	public void switchNeighbourhood(MapObjectModel mapObject, MapLayer oldNeighbourhood, MapLayer newNeighbourhood) {
		oldNeighbourhood.getObjects().remove(mapObject);
		newNeighbourhood.getObjects().add(mapObject);
		mapObjectLayerMap.put(mapObject, newNeighbourhood);
	}
	
	public Array<MapObjectModel> getAllMapObject() {
		return allMapObjects;
	}
	
	/**
	 * Updates the map and all owned MapObject for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta, GameplayCamera camera) {
		spawnParticleCounter += delta;
		
		updateSpaceshipsNeighbourhood();
		
		for (MapLayer neighbourhood : getLayers()) {
			Array<MapObjectModel> neighbourhoodObjects = neighbourhood.getObjects().getByType(MapObjectModel.class);
			
			for (MapObjectModel mapObject : neighbourhoodObjects) {
				
				updateAcceleration(mapObject, neighbourhoodObjects, delta);
				
				mapObject.update(delta);
				
				applyMapObjectPeriodicity(mapObject);
				
				if ((spawnParticleCounter > 0.1f) && (mapObject.isGeneratingParticles())) {
					Vector2 position = mapObject.getPosition().cpy();
					MapObjectFactory.getInstance().createParticleResource(mapObject, 0.5f, 0.5f, position);
				}
			}
		}
		
		if (spawnParticleCounter > 0.1f) {
			spawnParticleCounter = 0f;
		}
	}
	
	private void updateAcceleration(MapObjectModel mapObject, Array<MapObjectModel> neighbourhood, float delta) {
		float width = getWidth();
		float height = getHeight();
		Array<MapObjectModel> allMapObjects = getAllMapObject();
		
		mapObject.setAcceleration(new Vector2(0,0));
		physicsEngine.applyGravity(mapObject, neighbourhood, allMapObjects, width, height, delta);
		if (mapObject.isSelfStabilizing())
			physicsEngine.applyStabilizingAcceleration(mapObject, neighbourhood, allMapObjects, width, height, delta);
	}
	
	private void updateSpaceshipsNeighbourhood() {
		MapObjectModel dominatingObject = physicsEngine.findGravitationallyStrongestObject(playerSpaceship, 
				getAllMapObject(), 
				getWidth(), getHeight());
		
		MapLayer currentNeighbourhood = mapObjectLayerMap.get(playerSpaceship);
		MapLayer dominatingNeighbourhood = mapObjectLayerMap.get(dominatingObject);
		
		if (currentNeighbourhood != dominatingNeighbourhood) {
			System.out.println("SWITCH");
			System.out.println("From: " + currentNeighbourhood.getName());
			System.out.println("To: " + dominatingNeighbourhood.getName());
			dominatingObject = physicsEngine.findGravitationallyStrongestObject(playerSpaceship, 
					getAllMapObject(), 
					getWidth(), getHeight());
			switchNeighbourhood(playerSpaceship, currentNeighbourhood, dominatingNeighbourhood);
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
	
	private void loadMapObjectsDemoMap() {
		
		MapObjectFactory factory = MapObjectFactory.getInstance();
		
		//Create start neighborhood. spaceship and one planet.
		
		MapLayer startNeighbourhood = new MapLayer();
		startNeighbourhood.setName("start neighbourhood");
		getLayers().add(startNeighbourhood);
		
		MapObjectModel startPlanet = factory.createStationaryPlanet(15, 15, 1f, new Vector2(width * 0.1f, width * 0.1f), 3f);
		addMapObject(startPlanet, startNeighbourhood);
		
		playerSpaceship = factory.createPlayerSpaceship();
		factory.placeMapObjectInOrbit(playerSpaceship, startPlanet, 25, 0f, true);
		addMapObject(playerSpaceship, startNeighbourhood);

		//Create the entire "gameplay" neighborhood.
		
		MapLayer gameplayNeighborhood = new MapLayer();
		gameplayNeighborhood.setName("gameplay neighborhood");
		getLayers().add(gameplayNeighborhood);
		
		MapObjectModel dominatringPlanet = factory.createStationaryPlanet(270, 270, 0.2f, new Vector2(width / 2, height / 2), 2f);
		addMapObject(dominatringPlanet, gameplayNeighborhood);
	}
	
	/**
	 * Loads the map objects to the map. 
	 * TODO: in a distant future we want the map to be loaded from a file.
	 */
//	private void loadMapObjects() {
//		MapObjectFactory factory = MapObjectFactory.getInstance();
//		Array<MapObjectModel> neighborhood = new Array<MapObjectModel>();
//		
//		MapObjectModel stationaryPlanet = factory.createStationaryPlanet(neighborhood, 
//				120, 120,
//				new Vector2(width / 2, height / 2), 
//				2f);
//		
//		gameplayLayer.setDominatingMapObject(stationaryPlanet);
//		
//		for (int i = 1; i < 5; i++) {
//			float pDistance = i * 80;
//			float pDegrees = (i * 90f) % 360f;
//			float pRelativeMass = i * 0.005f;
//			float pRotationSpeed = i - 4f;
//			MapObjectModel orbitingPlanet = factory.createOrbitingPlanet(neighborhood, 
//					stationaryPlanet, 
//					pDistance, 
//					pDegrees, 
//					pRelativeMass, 
//					false, 
//					pRotationSpeed);
//			
//			if ((i > 1) && i < 5) {
//				
//				for (int j = 1; j < i; j++) {	
//					float aDistance = 2f + j * 10f;
//					float aDegrees = (j * 180) % 360f;
//					float aRelativeMass = 0.05f;
//					float aRotationSpeed = j * -2f;
//					factory.createOrbitingAsteroid(neighborhood, 
//							orbitingPlanet, 
//							aDistance, 
//							aDegrees, 
//							aRelativeMass, 
//							true, 
//							aRotationSpeed);
//				}
//			}
//		}
//		
//		//player spaceship
//		playerSpaceship = factory.createPlayerSpaceship();
//		neighborhood.add(playerSpaceship);
//		
//		MapObjects gamplayMapObjects = gameplayLayer.getObjects();
//		for (MapObjectModel mapObject : neighborhood) {
//			gamplayMapObjects.add(mapObject);
//		}
//	}
}
