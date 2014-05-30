package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.utils.Array;

/* 
 * Applies the "laws of physics" to a set of GamePlayMapObjects
 * @author fredrik 
 * 
 */
public class PhysicsEngine {
	
	public static MapObjects[] allMapObjects;
	
	public static void setMapObjects (MapObjects m[]) {
		allMapObjects = m; 
	}
	
	public static void applyGravity (float delta) 
	{
	}
		
	/*
	 * Note how computeGravitationalForce is symmetric with respect to o1 and o2
	 * 
	 */
	private static float computeGravitationalForce (SimpleMapObject o1, SimpleMapObject o2)
	{
		int m1 = o1.getMass();
		int m2 = o2.getMass(); 
		float G = ModelDefs.GRAVITATIONAL_CONSTANT; 
		float distance = o1.getPosition().dst(o2.getPosition());
		float f = G*m1*m2/(distance*distance); // Newtons law of gravity
		return f;
	}
	
	/*
	 * Computes acceleration of an object due to gravity by integrating with respect to some small time delta
	 * The reason I'm not nesting these computations in one function is simply to separate velocities 
	 * accelerations, forces etc. 
	 */
	private static float computeAcceleration(SimpleMapObject o1, SimpleMapObject o2, float delta) {
		return computeGravitationalForce(o1,o2)*delta; 
		
		
	}
	
}
