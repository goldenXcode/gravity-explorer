package se.fkstudios.gravitynavigator.model;

import java.util.Random;
import se.fkstudios.gravitynavigator.ResourceDefs;
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
			models[i] = generateAsteroid(2f);
		}
		return models; 
	
		
	}
	
	public static TextureMapObjectModel generateAsteroid (float radius) {
		int mass = (int) calculateMass(radius);
		TextureMapObjectModel asteroid = new TextureMapObjectModel(new Vector2((float)randomInt(0,(int) ModelDefs.MAP_WIDTH), 
				(float)randomInt(0,(int) ModelDefs.MAP_HEIGHT)), radius, radius, 
				new Vector2(0.2f, 0.0f), 0, mass, ResourceDefs.TEXTURE_REGION_NAME_ASTERIOID_01);
		// density of object = 1273 kg/m² 
		return asteroid; 
	}
	
//	public static MapObjectModel generatePlanet () {
//		
//	}
	
	
	
	/*
	 * Assumes homogenous mass distribution and calculates a (circular) objects mass given it's radius.  
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
	
	
	

}
