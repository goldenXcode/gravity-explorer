package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.math.Vector2;

/*
 * Contains model-specific definitions such as physical constants, 
 * conversion factors, initial states etc.
 * @author fredrik
 * 
 */
public final class ModelDefs {
	
	public static Vector2 STARTING_POSITION = new Vector2(2,2);
	public static Vector2 STARTING_VELOCITY = new Vector2(0,0);
	
	public static float GRAVITATIONAL_CONSTANT = 0.001f;
	
	public static float MAP_WIDTH = 20;
	public static float MAP_HEIGHT = 20; 

}
