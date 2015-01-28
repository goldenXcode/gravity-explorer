package se.fkstudios.gravityexplorer.model;

import java.util.HashMap;

import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.model.resources.GraphicResourceBinding;
import se.fkstudios.gravityexplorer.model.resources.GraphicsLoader;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionBinding;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represent a periodic map with floating point width and height. 
 * @author kristofer
 */
public class PeriodicMapModel extends Map {
	
	private PhysicsEngine physicsEngine;
	
	private float width;
	private float height;
	private Array<GraphicResourceBinding> resources;
	private SpaceshipModel playerSpaceship;
	private MapObjects allMapObjects;
	private HashMap<MapObjectModel, MapLayer> mapObjectLayerMap;
	private float spawnParticleCounter;
	private Environment environment;
	
	/**
	 * Creates a continuous map. 
	 * @param filePathBackgroundImageLayer1 Name of the texture to display as background.
	 * @param width Width of map.
	 * @param height Height of map.
	 */
	public PeriodicMapModel(String layer1TextureName, String layer2TextureName, float width, float height) {
		this.width = width;
		this.height = height;
		allMapObjects = new MapObjects();
		mapObjectLayerMap = new HashMap<MapObjectModel, MapLayer>();
		
		GraphicsLoader graphicLoader = GraphicsLoader.getInstance();
		float longestSide = Utility.getModelCoordinate(Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		TextureRegionBinding layer1Texure = graphicLoader.createTextureBinding(layer1TextureName, longestSide, longestSide, new Vector2(0,0));
		TextureRegionBinding layer2Texure = graphicLoader.createTextureBinding(layer2TextureName, longestSide * 2, longestSide * 2, new Vector2(0,0));
		layer1Texure.setMaxRenderScale(1f);
		layer2Texure.setMaxRenderScale(1f);
		
		resources = new Array<GraphicResourceBinding>();
		resources.add(layer1Texure);
		resources.add(layer2Texure);
		
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
	
	public Array<GraphicResourceBinding> getResources() {
		return resources;
	}
	
	public void registerMapObject(MapObjectModel mapObject, MapLayer neighbourhood) {
		allMapObjects.add(mapObject);
		neighbourhood.getObjects().add(mapObject);		
		mapObjectLayerMap.put(mapObject, neighbourhood);
	}
	
	public void unregisterMapObject(MapObjectModel mapObject) {
		MapLayer neighbourhood = mapObjectLayerMap.get(mapObject);
		neighbourhood.getObjects().remove(mapObject);
		allMapObjects.remove(mapObject);
		mapObjectLayerMap.remove(mapObject);
	}
	
	public void switchNeighbourhood(MapObjectModel mapObject, MapLayer oldNeighbourhood, MapLayer newNeighbourhood) {
//		System.out.println("SWITCH");
//		System.out.println("From: " + oldNeighbourhood.getName());
//		System.out.println("To: " + newNeighbourhood.getName());
		oldNeighbourhood.getObjects().remove(mapObject);
		newNeighbourhood.getObjects().add(mapObject);
		mapObjectLayerMap.put(mapObject, newNeighbourhood);
	}
	
	/**
	 * Updates the map and all owned MapObject for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		spawnParticleCounter += delta;
		
		updateSpaceshipsNeighbourhood();
		
		MapLayers neighbourhoods = getLayers();
		for (int i = 0; i < neighbourhoods.getCount(); i++) {
			MapLayer neighbourhood = neighbourhoods.get(i);
			MapObjects neighbourhoodObjects = neighbourhood.getObjects();
			
			for (int j = 0; j < neighbourhoodObjects.getCount(); j++) {
				MapObjectModel mapObject = (MapObjectModel)neighbourhoodObjects.get(j);
				
				updateAcceleration(mapObject, neighbourhoodObjects, delta);
				
				mapObject.update(delta);
				
				applyMapObjectPeriodicity(mapObject);
				
				if ((spawnParticleCounter > 0.1f) && (mapObject.isGeneratingParticles())) {
					Vector2 position = mapObject.getPosition().cpy();
					Color color = Utility.randomColor();
					GraphicsLoader.getInstance().createColorBinding(mapObject, color, 0.5f, 0.5f, position);
				}
			}
		}
		
		if (spawnParticleCounter > 0.1f) {
			spawnParticleCounter = 0f;
		}
	}
	
	private void updateAcceleration(MapObjectModel mapObject, MapObjects neighbourhood, float delta) {
		float width = getWidth();
		float height = getHeight();
		
		mapObject.setAcceleration(0, 0);
		
		MapObjects influencingMapObjects = mapObject.getInfluencingObjects(allMapObjects, neighbourhood);
		
		physicsEngine.applyGravity(mapObject, influencingMapObjects, width, height, delta);
		if (mapObject.isSelfStabilizing())
			physicsEngine.applyStabilizingAcceleration(mapObject, influencingMapObjects, width, height, delta);
	}
	
	private void updateSpaceshipsNeighbourhood() {
		MapObjectModel dominatingObject = physicsEngine.findGravitationallyStrongestObject(playerSpaceship, 
				allMapObjects, 
				getWidth(), getHeight());
		
		MapLayer currentNeighbourhood = mapObjectLayerMap.get(playerSpaceship);
		MapLayer dominatingNeighbourhood = mapObjectLayerMap.get(dominatingObject);
		
		if (currentNeighbourhood != dominatingNeighbourhood) {

			dominatingObject = physicsEngine.findGravitationallyStrongestObject(playerSpaceship, 
					allMapObjects, 
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

		environment = new Environment();
		float ambientIntensity = 0.5f;
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, ambientIntensity, ambientIntensity, ambientIntensity, 1f));
	
		MapObjectFactory factory = MapObjectFactory.getInstance();
		
		//Create start neighborhood. spaceship and one planet.
		
		MapLayer startNeighbourhood = new MapLayer();
		startNeighbourhood.setName("start neighbourhood");
		getLayers().add(startNeighbourhood);
		
		MapObjectModel startPlanet = factory.createStationaryPlanet(20, new Vector2(width * 0.13f, width * 0.1f), 3f, Color.MAROON, false);
		registerMapObject(startPlanet, startNeighbourhood);
		
		playerSpaceship = factory.createPlayerSpaceship();
		factory.placeMapObjectInOrbit(playerSpaceship, startPlanet, 25, 0f, true);
		registerMapObject(playerSpaceship, startNeighbourhood);

		//Create the entire "gameplay" neighborhood.
		MapLayer gameplayNeighborhood = new MapLayer();
		gameplayNeighborhood.setName("gameplay neighborhood");
		getLayers().add(gameplayNeighborhood);
		
		MapObjectModel dominatringPlanet = factory.createStationaryPlanet(100, new Vector2(width / 2f, height / 2f), 2, 0.2f, Color.WHITE, true);
		registerMapObject(dominatringPlanet, gameplayNeighborhood);
		
		MapObjectModel firstTarget = factory.createOrbitingPlanet(dominatringPlanet, 140, 0, 0.015f, true, 1, Color.TEAL);
		registerMapObject(firstTarget, gameplayNeighborhood);
		
		MapObjectModel secondTarget = factory.createOrbitingPlanet(dominatringPlanet, 300, 0f, 0.05f, true, 3.3f, Color.MAROON);
		registerMapObject(secondTarget, gameplayNeighborhood);
		for (int i = 0; i < 3; i++) {
			Color color = Utility.randomColor();
			MapObjectModel moon = factory.createOrbitingPlanet(secondTarget, 15 + 10*i, 45*i, 0.05f - i / 100, true, 0.2f, color);
			registerMapObject(moon, gameplayNeighborhood);
		}

		float degrees = 0;
		int asteriodCount = 300;
		for (int i = 0; i < asteriodCount; i++) {
			int distanceOffset = Utility.randomInt(-10, 10);
			
			if (Utility.randomInt(0, 10) < 5) {
				distanceOffset += Utility.randomInt(-20, 20);
			}
			
			if (Utility.randomInt(0, 10) < 1) {
				distanceOffset += Utility.randomInt(-40, 40);
			}
			
			degrees += i * 360 / asteriodCount;
			float relativeMass = 0.0006f * Utility.randomFloat(0.5f, 1.5f);
			MapObjectModel asteroid = factory.createOrbitingAsteroid(dominatringPlanet, 190 + distanceOffset, 
					degrees, relativeMass, true, 0.5f);
			registerMapObject(asteroid, gameplayNeighborhood);
		}
		
		MapObjectModel thirdTarget = factory.createOrbitingPlanet(dominatringPlanet, 430, 0f, 0.007f, true, 3.0f, Color.ORANGE);
		registerMapObject(thirdTarget, gameplayNeighborhood);
	}
	
	/**
	 * Loads the map objects to the map. 
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
