package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/* 
 * Applies the "laws of physics" to a set of GamePlayMapObjects
 * @author fredrik 
 * 
 */
public class PhysicsEngine {
	
	private static MapObjectModel[] allMapObjects;
	private static float mapWidth;
	private static float mapHeight;
	
	private static void setMapDimensions(Vector2 v) {
		mapWidth = v.x;
		mapHeight = v.y; 
	}
	
	public static void setMapObjects (MapObjectModel m[]) {
		allMapObjects = m; 
		System.out.println("map objects set for physics engine");
	}
	
	
	// TODO: fixa så att den jobbar periodiskt 
	public static void applyGravity (float delta) 
	{
		//System.out.println("gravity applied");
		for (int i =0;i<allMapObjects.length ; i++) {
			for (int j =0;j<allMapObjects.length ; j++){
				//System.out.println(i + "" + j);
				MapObjectModel a = allMapObjects[i];
				MapObjectModel b = allMapObjects[j];
				Vector2 velocityBoost = computeAcceleration(a,b,delta);
				b.setVelocity(b.getVelocity().add(velocityBoost));
			}
			
		}
	}
	
	/*
	 * Note how computeGravitationalForce is symmetric with respect to o1 and o2
	 * 
	 */
	private static Vector2 computeGravitationalForce (MapObjectModel o1, MapObjectModel o2)
	{
		int m1 = o1.getMass();
		int m2 = o2.getMass(); 
		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
		float distance = o1.getPosition().dst(o2.getPosition()); 
		float f;
		
		if (distance != 0) {
			f = G*m1*m2/(distance*distance); // Newtons law of gravity
			
		}
		else 
			f = 0;
		
		// gives the size of the force. Now we'll need the direction
		Vector2 p1 = o1.getPosition().cpy();
		Vector2 p2 = o2.getPosition().cpy();
		Vector2 diff = p1.sub(p2);
		Vector2 direction;
		if (diff.dot(diff) != 0) {
			direction = diff.div((float) Math.sqrt(diff.dot(diff)));
		}
		else
			direction = new Vector2(0,0);
		return direction;
	}
	
	private static Vector2 computeGravitationalForce (Vector2 p1, int m1, Vector2 p2, int m2)
	{
		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
		float distance = p1.dst(p2); 
		float f;
		
		if (distance != 0) {
			f = G*m1*m2/(distance*distance); // Newtons law of gravity
			
		}
		else 
			f = 0;
		
		// gives the size of the force. Now we'll need the direction
		Vector2 diff = p1.sub(p2);
		Vector2 direction;
		if (diff.dot(diff) != 0) {
			direction = diff.div((float) Math.sqrt(diff.dot(diff)));
		}
		else
			direction = new Vector2(0,0);
		return direction.cpy();
	}
	
	/*
	 * Computes acceleration of an object due to gravity by integrating with respect to some small time delta
	 * The reason I'm not nesting these computations in one function is simply to separate velocities 
	 * accelerations, forces etc. 
	 */
	private static Vector2 computeAcceleration(MapObjectModel o1, MapObjectModel o2, float delta) {
		return computeGravitationalForce(o1,o2).scl(delta); 	
	}
	
	private static Vector2 computeAcceleration(Vector2 p1, int m1, Vector2 p2, int m2, float delta) {
		return computeGravitationalForce(p1,m1,p2,m2).cpy().scl(delta);
	}
	
	private static Vector2 computeAcceleration(Vector2 direction,float distance, int m1, int m2, float delta) {
		float f = computeScalarGravitationalForce(m1,m2,distance);
		return direction.cpy().scl(f).scl(delta);
	}
	
	private static float computeScalarGravitationalForce (int m1,int m2,float d) {
	if (d != 0) {
		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
		return G*m1*m2/(d*d); // Newtons law of gravity
		
	}
	else 
		return 0;
	}
	
	private static Vector2 computeForceDirection (MapObjectModel o1,MapObjectModel o2) {
		Vector2 p1 = o1.getPosition().cpy();
		Vector2 p2 = o2.getPosition().cpy();
		Vector2 diff = p1.sub(p2);
		Vector2 direction;
		if (diff.dot(diff) != 0) {
			direction = diff.div((float) Math.sqrt(diff.dot(diff)));
		}
		else
			direction = new Vector2(0,0);
		return direction;
	}
	
	private static float distance (MapObjectModel o1,MapObjectModel o2) {
		Vector2 p1 = o1.getPosition().cpy();
		Vector2 p2 = o2.getPosition().cpy();
		Vector2 diff = p1.sub(p2);
		return (float) Math.sqrt(diff.dot(diff));
	}
	
	private static Vector2 computePeriodicCompensation(MapObjectModel o1, MapObjectModel o2, float delta)  {
		Vector2 p1 = o1.getPosition();
		Vector2 p2 = o2.getPosition(); 
		int m1 = o1.getMass();
		int m2 = o2.getMass(); 
		Vector2 forceDirection = computeForceDirection(o1,o2);
		float dist = distance(o1,o2);
		float f = computeScalarGravitationalForce(m1,m2,dist);
		return new Vector2(0,0);

				
		
	}
	
	
	
}
