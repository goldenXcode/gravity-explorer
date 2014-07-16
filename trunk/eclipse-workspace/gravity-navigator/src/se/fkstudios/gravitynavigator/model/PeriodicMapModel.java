package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;

import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Represent a (TODO: should be, is not now) periodic map with floating point width and height. 
 * @author kristofer
 */
public class PeriodicMapModel extends Map {
	
	private final String GAMEPLAY_LAYER_NAME = "GameplayLayer";
	
	private String filePathBackgroundImageLayer1;
	private float width;
	private float height;
	private MapLayer gameplayLayer;
	private SpaceshipModel playerSpaceship;
	
	/**
	 * Creates a continuous map. 
	 * @param filePathBackgroundImageLayer1 Name of the texture to display as background.
	 * @param width Width of map.
	 * @param height Height of map.
	 */
	public PeriodicMapModel(String filePathBackgroundImageLayer1, float width, float height) {
		this.filePathBackgroundImageLayer1 = filePathBackgroundImageLayer1;
		this.width = width;
		this.height = height;
		gameplayLayer = new MapLayer();
		gameplayLayer.setName(GAMEPLAY_LAYER_NAME);
		this.getLayers().add(gameplayLayer);
		loadMapObjects();
	}
		
	public String getFilePathBackgroundImageLayer1() {
		return filePathBackgroundImageLayer1;
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
	public void loadMapObjects() {
		// TODO: specify a XML format for model map's start conditions. 
		// Preferably also create a map editor to create these.
		// Note: I'm thinking ModelDefs could contain the XML-parser. 
		Vector2 position = Defs.STARTING_POSITION;
		Vector2 velocity = Defs.STARTING_VELOCITY; 
		playerSpaceship = new SpaceshipModel(position, 
				0.33f, 
				0.57f, 
				velocity, 
				0, 
				1, 
				Defs.TEXTURE_REGION_NAME_SPACESHIP_PLAYER, 
				Defs.MAX_THRUST);


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
		TextureMapObjectModel planet = RandomMapGenerator.generatePlanet(10f);
		PhysicsEngine.add(planet); 
		gamplayMapObjects.add(planet); 
		
		// adding the player spaceship 
		gamplayMapObjects.add(playerSpaceship);
		PhysicsEngine.add(playerSpaceship);
		
		// adding the orbiting asteroids
		TextureMapObjectModel[] orbiters = RandomMapGenerator.generateOrbitingAsteroids(10f, planet,7f,7);
		for (int i = 0; i< orbiters.length; i++) {
			gamplayMapObjects.add(orbiters[i]); 
		}
		PhysicsEngine.add(orbiters);
		
	}
		
	/**
	 * Updates the map and all owned MapObject for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		
		MapObjects allMapObjects = gameplayLayer.getObjects();
		Array<MapObjectModel> gameplayMapObjects = new Array<MapObjectModel>(allMapObjects.getCount());
		allMapObjects.getByType(MapObjectModel.class, gameplayMapObjects);
		
		for (MapObjectModel gameplayMapObject : gameplayMapObjects) {
			gameplayMapObject.setAcceleration(new Vector2(0,0));
			applyMapObjectPeriodicity(gameplayMapObject);
		}
		
		PhysicsEngine.applyGravity();
		
		for (MapObjectModel gameplayMapObject : gameplayMapObjects) {
			gameplayMapObject.update(delta);
			applyMapObjectPeriodicity(gameplayMapObject);
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
}
