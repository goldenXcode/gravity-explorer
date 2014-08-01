package se.fkstudios.gravitynavigator.model;

import java.util.Arrays;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.view.RenderOptions;

import com.badlogic.gdx.math.Vector2;

/** 
 * Applies the "laws of physics" to a set of GamePlayMapObjects
 * @author fredrik 
 * 
 */
public class PhysicsEngine {
	
	private static MapObjectModel[] allMapObjects;
	private static PeriodicMapModel periodicMap; 
	
	public static void setMapObjects (MapObjectModel[] mapObjectModels) {
		allMapObjects = mapObjectModels;
	}
	
	
	public static void setPeriodicMapModel(PeriodicMapModel map) {
		periodicMap = map; 
	}
	
	public static void flushAllObjects()  {
		MapObjectModel newArray[] = {};
		setMapObjects(newArray);
	}
	
	/**
	 * Applies gravity (setting force on map objects) for all map objects in allMapObjects.
	 * 
	 * @param delta The time in seconds since last update cycle.
	 */
	public static void applyGravity() 	
	{
		for (int i = 0; i < allMapObjects.length; i++) {
			for (int j = 0; j < allMapObjects.length; j++) {
				MapObjectModel mapObject1 = allMapObjects[i];
				MapObjectModel mapObject2 = allMapObjects[j];
				Vector2 acceleration = computeAcceleration(mapObject1, mapObject2).div(mapObject2.getMass());
				mapObject2.getAcceleration().add(acceleration);
				if (RenderOptions.getInstance().debugRender && detectCollision(mapObject1,mapObject2) && mapObject1 != mapObject2)
					System.out.println("collision detected"); 
			}
		}
	}
		
	/**
	 * Computes 
	 */
	public static Vector2 computeAcceleration(MapObjectModel mapObject1, MapObjectModel mapObject2)
	{
		return computeAcceleration(mapObject1.getPosition(), mapObject2.getPosition(), mapObject1.getMass(), mapObject2.getMass()); 
	}
	
	
	private static Vector2 computeAcceleration(Vector2 position1, Vector2 position2,float mass1,float mass2 )
	{
		Vector2 distance = shortestDistanceVector(position1, position2); 

		float cutoff = 0.3f;

		float force;
		if (distance.len() > cutoff) { // cutoff to prevent singularities arising from zero distance between objects
			force = ( Defs.GRAVITATIONAL_CONSTANT * mass1 * mass2 ) / (distance.len()*distance.len()); // Newtons law of gravity
		}
		else {
			force = 0;
		}
		
		Vector2 direction = distance.nor();
		
		return direction.scl(force);
	}
	

	/**
	 * Computes the shortest vector from position1 to position2 with respect to periodicity.  
	 * @param position1 from position.
	 * @param position2 to position.
	 * @return the shortest vector from position1 to position2.
	 */
	public static Vector2 shortestDistanceVector(Vector2 position1, Vector2 position2) {
		Vector2 result1 = shortestDistanceVectorHelper(position1, position2);
		Vector2 result2 = shortestDistanceVectorHelper(position2, position1).scl(-1);	
		if (result1.len2() < result2.len2())
			return result1;
		else
			return result2;
	}
	
	/**
	 * Computes the shortest vector from position1 to position2 considering alternative positions for position2 only.
	 */
	private static Vector2 shortestDistanceVectorHelper(Vector2 position1, Vector2 position2) {
		float x1 = position1.x; 
		float x2 = position2.x; 
		float y1 = position1.y; 
		float y2 = position2.y; 
		float mapWidth = periodicMap.getWidth(); 
		float mapHeight = periodicMap.getHeight(); 
		
		float xDistance1 = x1 - x2; 
		float xDistance2 = -(x1 - x2) + mapWidth; 
		
		float yDistance1 = y1 - y2; 
		float yDistance2 = -(y1 - y2) + mapHeight; // yDistance2 == yDistance1 + 5, not good 
		
		Vector2 result = new Vector2();
		
		if (Math.abs(xDistance1) < Math.abs(xDistance2))
			result.x = xDistance1;
		else 
			result.x = -xDistance2;
				
		if (Math.abs(yDistance1) < Math.abs(yDistance2))
			result.y = yDistance1;
		else
			result.y = -yDistance2;
		
		return result;
	}
	
	private static boolean detectCollision (MapObjectModel mapObject1, MapObjectModel mapObject2) {
		Vector2 position1 = mapObject1.getPosition(); 
		Vector2 position2 = mapObject2.getPosition(); 
		float width1 = mapObject1.getWidth(); 
		float width2 = mapObject2.getWidth(); 
		float distance = shortestDistanceVector(position1, position2).len();
		
		boolean result = (distance < width1/2 + width2/2 ) && distance > 0.1f;
		
		if (RenderOptions.getInstance().debugRender && result)
				System.out.println(" width1: " + width1 + " width2: " + width2 + " distance: " + distance);
		
		return result;
	}
	
	private static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public static void add(MapObjectModel model) {
		if (allMapObjects == null){
			allMapObjects = new MapObjectModel[1]; 
			allMapObjects[0] = model; 
		}
		else {
		setMapObjects(append(allMapObjects,model)); 
		}
	}
	
	public static void add(MapObjectModel[] models) {
		for (int i = 0; i<models.length; i++) {
			add(models[i]);
		}
	}	
}
