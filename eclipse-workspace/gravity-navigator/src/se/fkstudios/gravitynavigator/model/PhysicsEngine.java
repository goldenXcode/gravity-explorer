package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.maps.MapObjects;

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

}
