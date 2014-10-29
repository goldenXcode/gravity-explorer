package se.fkstudios.gravitynavigator.model;

import java.util.Random;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.resources.TextureAtlasResource;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RandomMapGenerator {
	
	private float objectDensity = 1273f; // calculated from asteroid1 (just for reference).
	
	/*Singelton stuff*/
	private static final RandomMapGenerator instance = new RandomMapGenerator();
	
	private RandomMapGenerator() {}
	
	public static RandomMapGenerator getInstance() {
		return instance;
	}
	
	public void setObjectDensity(float density) {
		objectDensity = density; 
	}
	
	public float getObjectDensity() {
		return objectDensity; 
	}
	
	/**
	 * Inputs  a PeriodicMapModel and generates a random map on it. 
	 */
	public MapObjectModel[] generateMapObjects(int numberOfObjects) {
		// PhysicsEngine.flushAllObjects();
		MapObjectModel[] models = new MapObjectModel[numberOfObjects];
		for (int i = 0; i < numberOfObjects; i++) {
			MapObjectModel asteroid = generateAsteroid(2f);
			asteroid.setRotationalSpeed(randomFloat((-1f)*Defs.MAX_ROTATIONAL_VELOCITY,Defs.MAX_ROTATIONAL_VELOCITY));
			models[i] = (MapObjectModel) asteroid; 
		}
		return models;
	}
	
	public MapObjectModel generatePlanet (float radius) {
		int mass = (int) calculateMass(radius);
		Array<TextureRegionResource> planetTextures = new Array<TextureRegionResource>();
		TextureAtlasResource texture = new TextureAtlasResource(new Vector2(0, 0), true, Defs.MIN_RENDER_SCALE_DEFAULT, Defs.MAX_RENDER_SCALE_DEFAULT, Defs.TEXTURE_REGION_NAME_PLANET3);
		planetTextures.add(texture);
		MapObjectModel asteroid = new MapObjectModel(centreOfUniverse(), 
				radius, 
				radius, 
				new Vector2(0.2f, 0.0f), 
				0, 
				mass, 
				planetTextures);
		
		// density of object = 1273 kg/m² 
		asteroid.setDistanceToParent(0); 
		return asteroid; 
	}

	public MapObjectModel generateAsteroid (float radius) {
		int mass = (int) calculateMass(radius);
		Array<TextureRegionResource> asteriodTextures = new Array<TextureRegionResource>();
		TextureRegionResource texture = new TextureAtlasResource(new Vector2(0,0), true, Defs.MIN_RENDER_SCALE_DEFAULT, Defs.MAX_RENDER_SCALE_DEFAULT, Defs.TEXTURE_REGION_NAME_ASTERIOID1);
		asteriodTextures.add(texture);
		
		MapObjectModel asteroid = new MapObjectModel(new Vector2((float)randomInt(0,(int) Defs.MAP_WIDTH), (float)randomInt(0,(int) Defs.MAP_HEIGHT)), 
				radius, 
				radius, 
				new Vector2(0.2f, 0.0f), 
				0, 
				mass, 
				asteriodTextures);
		
		// density of object = 1273 kg/m² 
		asteroid.setDistanceToParent(0);
		return asteroid; 
	}
	
	public MapObjectModel generateOrbitingAsteroid(float distance, MapObjectModel planet, float radius) {
		int mass = (int) calculateMass(radius);
		Vector2 planetPosition = planet.getPosition();
		float asteroidSpeed = calculateOrbitingVelocity(distance, planet.getMass());
		float rotation = randomFloat(0,360);
		Vector2 displacementVector = new Vector2(0,-distance).rotate(rotation);
		Vector2 asteroidPosition = new Vector2(planetPosition.x, planetPosition.y).add(displacementVector);
		Vector2 asteroidVelocity = new Vector2((-1f)*asteroidSpeed, 0.0f).rotate(rotation);

		Array<TextureRegionResource> asteriodTextures = new Array<TextureRegionResource>();
		TextureRegionResource texture = new TextureAtlasResource(new Vector2(0, 0), true, Defs.MIN_RENDER_SCALE_DEFAULT, Defs.MAX_RENDER_SCALE_DEFAULT, Defs.TEXTURE_REGION_NAME_ASTERIOID1);
		asteriodTextures.add(texture);
		
		MapObjectModel asteroid = new MapObjectModel(asteroidPosition, 
				radius, 
				radius, 
				asteroidVelocity, 
				0, 
				mass, 
				asteriodTextures);
		
		asteroid.setDistanceToParent(distance);
		// density of object = 1273 kg/m² 
		asteroid.setParentNode(planet);
		asteroid.setDistanceToParent(distance);
		return asteroid; 
	}
	
	public MapObjectModel[] generateOrbitingAsteroids(float distance, MapObjectModel planet, float spacing, int numberOfAsteroids) {
		MapObjectModel[] asteroids = new MapObjectModel[numberOfAsteroids]; 
		float asteroidRadius = planet.getRadius() * (float)Defs.PLANET_TO_ASTEROID_SIZE_RATIO;
		for (int i = 0; i<asteroids.length; i++) {
			asteroids[i] = generateOrbitingAsteroid(i*spacing + spacing, planet, asteroidRadius);
			asteroids[i].setParentNode(planet);
			asteroids[i].setDistanceToParent(i*spacing + spacing); 
		}
		return asteroids; 
	}
	
	public static int randomInt(int min, int max) {

	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	public static float calculateOrbitingVelocity(float distance, float planetMass) {
		return (float) Math.sqrt(Defs.GRAVITATIONAL_CONSTANT * planetMass/distance);
	}
	
	private static float  randomFloat(float min, float max) {
		  return (float) (Math.random() * (max-min) + min);
		}
	
	private Vector2 centreOfUniverse()  {
		return new Vector2(Defs.MAP_WIDTH/2,Defs.MAP_HEIGHT/2); 
	}
	
	/**
	 * Assumes homogeneous mass distribution and calculates a (circular) objects mass given it's radius.  
	 */
	private float calculateMass(float radius) {
		return (float) (Math.pow(radius, 2)*Math.PI*objectDensity); 
	}
}