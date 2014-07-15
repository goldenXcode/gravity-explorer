package se.fkstudios.gravitynavigator.model;

import java.util.Random;

import se.fkstudios.gravitynavigator.Defs;

import com.badlogic.gdx.math.Vector2;

public class RandomMapGenerator {
	
	private static float objectDensity = 1273f; // calculated from asteroid1 (just for reference).
	
	/*
	 * Inputs  a PeriodicMapModel and generates a random map on it. 
	 */
	public static TextureMapObjectModel[] generateMapObjects(int numberOfObjects) {
		// PhysicsEngine.flushAllObjects();
		TextureMapObjectModel[] models = new TextureMapObjectModel[numberOfObjects];
		for (int i = 0; i < numberOfObjects; i++) {
			MapObjectModel asteroid = generateAsteroid(2f);
			asteroid.setRotationalSpeed(randomFloat((-1f)*Defs.MAX_ROTATIONAL_VELOCITY,Defs.MAX_ROTATIONAL_VELOCITY));
			models[i] = (TextureMapObjectModel) asteroid; 
		}
		return models; 
	
		
	}


	public static TextureMapObjectModel generateAsteroid (float radius) {
		int mass = (int) calculateMass(radius);
		TextureMapObjectModel asteroid = new TextureMapObjectModel(new Vector2((float)randomInt(0,(int) Defs.MAP_WIDTH), 
				(float)randomInt(0,(int) Defs.MAP_HEIGHT)), radius, radius, 
				new Vector2(0.2f, 0.0f), 0, mass, Defs.TEXTURE_REGION_NAME_ASTERIOID_01);
		// density of object = 1273 kg/m² 
		return asteroid; 
	}
	
	public static TextureMapObjectModel generatePlanet (float radius) {
		int mass = (int) calculateMass(radius);
		TextureMapObjectModel asteroid = new TextureMapObjectModel(centreOfUniverse(), radius, radius, 
				new Vector2(0.2f, 0.0f), 0, mass, Defs.TEXTURE_REGION_NAME_ASTERIOID_01);
		// density of object = 1273 kg/m² 
		return asteroid; 
	}
	
	public static PlanetarySystemComponent generateLinearPlanetarySystem (int depth) {
		PlanetarySystemComponent topNode = (PlanetarySystemComponent) generatePlanet(10); 
		return topNode; 
	}
	
	public static TextureMapObjectModel generateOrbitingAsteroid(float distance, TextureMapObjectModel planet, float radius) {
		int mass = (int) calculateMass(radius);
		Vector2 planetPosition = planet.getPosition();
		float asteroidSpeed = calculateOrbitingVelocity(distance, planet.getMass());
		float rotation = randomFloat(0,360);
		Vector2 displacementVector = new Vector2(0,-distance).rotate(rotation);
		Vector2 asteroidPosition = new Vector2(planetPosition.x, planetPosition.y).add(displacementVector);
		Vector2 asteroidVelocity = new Vector2((-1f)*asteroidSpeed, 0.0f).rotate(rotation);

		TextureMapObjectModel asteroid = new TextureMapObjectModel(asteroidPosition, radius, radius, 
				asteroidVelocity, 0, mass, Defs.TEXTURE_REGION_NAME_ASTERIOID_01);
		// density of object = 1273 kg/m² 
		return asteroid; 
		
	}
	
	private static Vector2 centreOfUniverse()  {
		return new Vector2(Defs.MAP_WIDTH/2,Defs.MAP_HEIGHT/2); 
	}
	
	
	
	
	/*
	 * Assumes homogeneous mass distribution and calculates a (circular) objects mass given it's radius.  
	 */
	private static float calculateMass(float radius) {

		return (float) (Math.pow(radius, 2)*Math.PI*objectDensity); 
		
	}
	
	public static void setObjectDensity(float density) {
		objectDensity = density; 
		
	}
	
	public static float getObjectDensity() {
		return objectDensity; 
	}
	
	public static int randomInt(int min, int max) {

	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
	
	private static float  randomFloat(float min, float max) {
		  return (float) (Math.random() * (max-min) + min);
		}
	
	private static float calculateOrbitingVelocity(float distance, float planetMass) {
		return (float) Math.sqrt(Defs.GRAVITATIONAL_CONSTANT*planetMass/distance);
	}
	

	
	
	

}
