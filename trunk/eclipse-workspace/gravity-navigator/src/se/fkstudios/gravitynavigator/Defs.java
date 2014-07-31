package se.fkstudios.gravitynavigator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Resource definitions.
 * @author kristofer
 */

public final class Defs {

    /* ************************************************************************************
     **************************************** Model *************************************** 
     ************************************************************************************** */
	
	public static Vector2 STARTING_POSITION = new Vector2(2,2);
	public static Vector2 STARTING_VELOCITY = new Vector2(0,0);
	
	public static float GRAVITATIONAL_CONSTANT = 0.01f;
	
	
	/* The (hard-coded) solar-system will have a radius of ASTEROID_SPACING*NUMBER_OF_ASTEROIDS*PLANET*SIZE
	 * MAP_WIDTH and HEIGHT should be set to roughly six times of that*/ 
	public static float MAP_WIDTH = 1000;
	public static float MAP_HEIGHT = 1000; 
	
	
	
	public static float MAX_ROTATIONAL_VELOCITY = 20; 
	public static float MIN_ROTATIONAL_VELOCITY = 0; 
	
	public static int MAX_THRUST = 30; 
	
	/* Map-generation-related stuff  */
	public static float PLANET_TO_ASTEROID_SIZE_RATIO = 0.1f; 
	public static float ORBITAL_COMPENSATIONAL_FACTOR = 0.000002f; 
	public static float ASTEROID_SPACING = 1f; // final spacing is planet-size*spacing
	public static float PLANET_SIZE = 30f; 
	public static float TOLERATED_ORBITAL_DEVIATION = 300; 
	public static int NUMBER_OF_ASTEROIDS = 7; 
 
	

	
    /* ************************************************************************************
     ************************************* Controller ************************************* 
     ************************************************************************************** */
	
	public static float SCROLLING_SPEED_MODIFIER = 0.1f; 

	
    /* ************************************************************************************
     ************************************* Rendering ************************************** 
     ************************************************************************************** */

	/* debug rendering */
	public static final Color MAP_BORDER_COLOR = Color.MAGENTA;
	public static final Color MAP_OBJECT_BORDER_COLOR = Color.CYAN;
	public static final Color MAP_OBJECT_CENTER_MARKER_COLOR = Color.CYAN;
	public static final Color INPUT_DRAG_LINE_COLOR = Color.RED;
	public static final int UNIT_PER_DEBUG_LINE = 10;
	
	/* viewport  */ 
	public static final int VIEWPORT_WIDTH = 490;
	public static final int VIEWPORT_HEIGHT = 320;
	public static final int PIXELS_PER_UNIT = 100;	
	
	
    /* ************************************************************************************
	   *********************************** Resources ************************************** 
       ************************************************************************************ */
	
	/* Graphics */
	
	/** WARNING: Indexes in TEXTURE_FILE_PATHS and TEXTURE_NAMES must match! */
	public static final String[] TEXTURE_FILE_PATHS = {
		"images/map_backgrounds/map-01-background-layer1.png"};
	
	/** WARNING: Indexes in TEXTURE_FILE_PATHS and TEXTURE_NAMES must match! */
	public static final String[] TEXTURE_NAMES = {"map-01-background-layer1"}; 
	
	public static final String TEXTURE_PACK_FILE_PATH = "images/spritesheets/textures/textures.pack";
	
	public static final String TEXTURE_REGION_NAME_SPACESHIP_PLAYER = "spaceship-01";
	public static final String TEXTURE_REGION_NAME_ASTERIOID_01 = "asteroid-01";
	public static final String TEXTURE_REGION_NAME_ASTERIOID_02 = "asteroid-02";
	public static final String TEXTURE_REGION_NAME_ASTERIOID_03 = "asteroid-03";
	public static final String TEXTURE_REGION_NAME_ASTERIOID_04 = "asteroid-04";
	public static final String TEXTURE_REGION_NAME_ASTERIOID_05 = "asteroid-05";
	public static final String TEXTURE_REGION_NAME_PLANET_01 = "planet-01";
	public static final String TEXTURE_REGION_NAME_PLANET_02 = "planet-02";
	public static final String TEXTURE_REGION_NAME_PLANET_03 = "planet-03";
	
	/* Sound */
	
}