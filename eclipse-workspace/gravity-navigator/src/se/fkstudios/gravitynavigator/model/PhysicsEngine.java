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
	
	public static MapObjectModel[] allMapObjects;
	
	public static void setMapObjects (MapObjectModel m[]) {
		allMapObjects = m; 
	}
	
	
	/*
	public static void applyGravity (float delta) 
	{
		for (MapObjectModel d : allMapObjects) {
			for (MapObjectModel e : allMapObjects){
				float velocityBoost = computeAcceleration(d,e,delta);
				e.setVelocity(velocity);
			}
			
		}
	}
	*/	
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
		float f = G*m1*m2/(distance*distance); // Newtons law of gravity
		// gives the size of the force. Now we'll need the direction
		Vector2 diff = o1.getPosition().sub(o1.getPosition());
		Vector2 direction = diff.div((float) Math.sqrt(diff.dot(diff)));
		return direction.scl(f);
	}
	
	/*
	 * Computes acceleration of an object due to gravity by integrating with respect to some small time delta
	 * The reason I'm not nesting these computations in one function is simply to separate velocities 
	 * accelerations, forces etc. 
	 */
	private static Vector2 computeAcceleration(MapObjectModel o1, MapObjectModel o2, float delta) {
		return computeGravitationalForce(o1,o2).scl(delta); 
		
		
	}
	
}
