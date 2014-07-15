package se.fkstudios.gravitynavigator.model;

import java.util.Arrays;

import se.fkstudios.gravitynavigator.Defs;

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
				if ( detectCollision(mapObject1,mapObject2) && mapObject1 != mapObject2) 				// for test purposes
					System.out.println("collision detected"); 
			}
		}
	}
		
	/**
	 * Computes 
	 */
	private static Vector2 computeAcceleration(MapObjectModel mapObject1, MapObjectModel mapObject2)
	{
		return computeAcceleration(mapObject1.getPosition(), mapObject2.getPosition(), mapObject1.getMass(), mapObject2.getMass()); 
	}
	
	
	private static Vector2 computeAcceleration(Vector2 position1, Vector2 position2,float mass1,float mass2 )
	{
		Vector2 distance = shortestDistance(position1, position2); 

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
	
	
	// name should be changed to shortestDistanceVector since it doesn't actually return a distance. Will do later.. 
	private static Vector2 shortestDistance(Vector2 position1, Vector2 position2) {
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
		else {
			result.y = -yDistance2;
		}

		return result;	
	}
	
	private static boolean detectCollision (MapObjectModel mapObject1, MapObjectModel mapObject2) {
		Vector2 position1 = mapObject1.getPosition(); 
		Vector2 position2 = mapObject2.getPosition(); 
		float width1 = mapObject1.getWidth(); 
		float width2 = mapObject2.getWidth(); 
		float distance = shortestDistance(position1, position2).len();
		if ((distance < width1/2 + width2/2 ) && distance > 0.1f) {
			System.out.println(" width1: " + width1 + " width2: " + width2 + " distance: " + distance);
			return true; 
		}
		else { 
			return false; 
		}
	}
	
	private static <T> T[] append(T[] arr, T element) {
	    final int N = arr.length;
	    arr = Arrays.copyOf(arr, N + 1);
	    arr[N] = element;
	    return arr;
	}
	
	public static void add(MapObjectModel model) {
		setMapObjects(append(allMapObjects,model)); 
	}
	
	


	
//	/********************** SAVE FOR LATER NOT USED ATM *********************/
	
//	private static float mapWidth;
//	private static float mapHeight;
	
//	private static void setMapDimensions(Vector2 v) {
//		mapWidth = v.x;
//		mapHeight = v.y; 
//	}

//	/**
//	 * Computes acceleration of an object due to gravity by integrating with respect to some small time delta
//	 * The reason I'm not nesting these computations in one function is simply to separate velocities 
//	 * accelerations, forces etc. 
//	 */
//	private static Vector2 computeAcceleration(MapObjectModel mapObjectModel1, MapObjectModel mapObjectModel2, float delta) {
//		return computeGravitationalForce(mapObjectModel1, mapObjectModel2).scl(delta); 	
//	}
	
//	private static Vector2 computeGravitationalForce (Vector2 p1, int m1, Vector2 p2, int m2)
//	{
//		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
//		float distance = p1.dst(p2); 
//		float f;
//		
//		if (distance != 0) {
//			f = G*m1*m2/(distance*distance); // Newtons law of gravity
//			
//		}
//		else 
//			f = 0;
//		
//		// gives the size of the force. Now we'll need the direction
//		Vector2 diff = p1.sub(p2);
//		Vector2 direction;
//		if (diff.dot(diff) != 0) {
//			direction = diff.div((float) Math.sqrt(diff.dot(diff)));
//		}
//		else
//			direction = new Vector2(0,0);
//		return direction.cpy();
//	}
	
//	private static Vector2 computeAcceleration(Vector2 p1, int m1, Vector2 p2, int m2, float delta) {
//	return computeGravitationalForce(p1,m1,p2,m2).cpy().scl(delta);
//}
//
//private static Vector2 computeAcceleration(Vector2 direction,float distance, int m1, int m2, float delta) {
//	float f = computeScalarGravitationalForce(m1,m2,distance);
//	return direction.cpy().scl(f).scl(delta);
//}
	
//	private static Vector2 computePeriodicCompensation(MapObjectModel o1, MapObjectModel o2, float delta)  {
//		Vector2 p1 = o1.getPosition();
//		Vector2 p2 = o2.getPosition(); 
//		int m1 = o1.getMass();
//		int m2 = o2.getMass(); 
//		Vector2 forceDirection = computeForceDirection(o1,o2);
//		float dist = distance(o1,o2);
//		float f = computeScalarGravitationalForce(m1,m2,dist);
//		return new Vector2(0,0);
//	}
	
//	private static Vector2 computeForceDirection (MapObjectModel o1,MapObjectModel o2) {
//		Vector2 p1 = o1.getPosition().cpy();
//		Vector2 p2 = o2.getPosition().cpy();
//		Vector2 diff = p1.sub(p2);
//		Vector2 direction;
//		if (diff.dot(diff) != 0) {
//			direction = diff.div((float) Math.sqrt(diff.dot(diff)));
//		}
//		else
//			direction = new Vector2(0,0);
//		return direction;
//	}
//	
//	private static float distance (MapObjectModel o1,MapObjectModel o2) {
//		Vector2 p1 = o1.getPosition().cpy();
//		Vector2 p2 = o2.getPosition().cpy();
//		Vector2 diff = p1.sub(p2);
//		return (float) Math.sqrt(diff.dot(diff));
//	}
	
//	private static float computeScalarGravitationalForce (int m1,int m2,float d) {
//	if (d != 0) {
//		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
//		return G*m1*m2/(d*d); // Newtons law of gravity
//		
//	}
//	else 
//		return 0;
//	}
	
}
