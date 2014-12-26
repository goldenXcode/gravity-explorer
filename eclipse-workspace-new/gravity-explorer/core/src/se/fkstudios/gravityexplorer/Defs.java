package se.fkstudios.gravityexplorer;

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
	
	public static final float GRAVITATIONAL_CONSTANT = 0.01f;	
	public static final float COMPENSATIONAL_CUTOFF_FACTOR = 1.2f;
	public static final float ORBITAL_COMPENSATIONAL_FACTOR2 = 0.001f;
	
	/* The (hard-coded) solar-system will have a radius of ASTEROID_SPACING*NUMBER_OF_ASTEROIDS*PLANET*SIZE
	 * MAP_WIDTH and HEIGHT should be set to roughly six times of that*/ 
	public static final float MAP_WIDTH = 1000;
	public static final float MAP_HEIGHT = 1000; 
	
	public static final float MAX_ROTATIONAL_VELOCITY = 20; 
	public static final float MIN_ROTATIONAL_VELOCITY = 0; 
	
	public static final int MAX_THRUST = 30; 
		
	public static final Vector2 STARTING_POSITION = new Vector2(MAP_WIDTH / 4, MAP_HEIGHT / 4);
	public static final Vector2 STARTING_VELOCITY = new Vector2(0,0);
	
	/* Map-generation-related stuff  */
	public static final float PLANET_TO_ASTEROID_SIZE_RATIO = 0.1f; 
	public static final float ORBITAL_COMPENSATIONAL_FACTOR = 1; 
	public static final float ASTEROID_SPACING = 1f; // final spacing is planet-size*spacing
	public static final float PLANET_SIZE = 30f; 
	public static final float TOLERATED_ORBITAL_DEVIATION = 300; 
	public static final int NUMBER_OF_ASTEROIDS = 7; 
	
	/* spaceship-fuel-related stuff */ 
	public static final float ZERO_FUEL = 0; 
	public static final float STARTING_FUEL = 100; // in percent
	public static final float FUEL_SCALING_FACTOR = 0.01f;

	
    /* ************************************************************************************
     ************************************* Controller ************************************* 
     ************************************************************************************** */
	
	// to be used in the Camera-class. Use outside may cause some peculiar effects. 
	// modifier one is for the base, 2 is for the exponent
	public static final float SCROLLING_SPEED_MODIFIER_1 = 1f; 
	public static final float SCROLLING_SPEED_MODIFIER_2 = 0.1f; 
	// "number of universes" you're allowed to see (zoom-wise) 
	public static final float CAMERA_LIMIT = 2; 
	public static final float CAMERA_POSITION_Z = 2f;
	public static final float CAMERA_FAR = 110f;
	public static final float CAMERA_NEAR = 1f;

	
    /* ************************************************************************************
     ************************************* Rendering ************************************** 
     ************************************************************************************** */

	public static final float MIN_RENDER_SCALE_SPACESHIP = 0.33f;
	public static final float MIN_RENDER_SCALE_DEFAULT = 0f;
	public static final float MAX_RENDER_SCALE_DEFAULT = Float.MAX_VALUE;
	
	/* debug rendering */
	public static final Color MAP_BORDER_COLOR = Color.MAGENTA;
	public static final Color MAP_OBJECT_BORDER_COLOR = Color.CYAN;
	public static final Color MAP_OBJECT_CENTER_MARKER_COLOR = Color.CYAN;
	public static final Color INPUT_DRAG_LINE_COLOR = Color.RED;
	public static final int UNIT_PER_DEBUG_LINE = 10;
	
	/* viewport  */ 
	public static final int PIXELS_PER_UNIT = 5;	
	
	
    /* ************************************************************************************
	   *********************************** Resources ************************************** 
       ************************************************************************************ */
	
	/* Graphics */

	public static final String TEXTURE_PACK_FILE_PATH = "images/spritesheets/textures/textures.pack";
	
	public static final String TEXTURE_REGION_NAME_SPACESHIP_PLAYER = "spaceship1";
	public static final String TEXTURE_REGION_NAME_ASTERIOID1 = "asteroid1";
	public static final String TEXTURE_REGION_NAME_ASTERIOID2 = "asteroid2";
	public static final String TEXTURE_REGION_NAME_ASTERIOID3 = "asteroid3";
	public static final String TEXTURE_REGION_NAME_ASTERIOID4 = "asteroid4";
	public static final String TEXTURE_REGION_NAME_ASTERIOID5 = "asteroid5";
	public static final String TEXTURE_REGION_NAME_PLANET1 = "planet1";
	public static final String TEXTURE_REGION_NAME_PLANET2 = "planet2";
	public static final String TEXTURE_REGION_NAME_PLANET3 = "planet3";
	public static final String TEXTURE_REGION_NAME_THURST_FRAME1 = "thrust-frame1";
	public static final String TEXTURE_REGION_NAME_THURST_FRAME2 = "thrust-frame2";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME1 = "explosion1-frame1";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME2 = "explosion1-frame2";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME3 = "explosion1-frame3";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME4 = "explosion1-frame4";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME5 = "explosion1-frame5";
	public static final String TEXTURE_REGION_NAME_EXPLOSION1_FRAME6 = "explosion1-frame6";
	
	public static final String[] TEXTURE_REGION_NAMES_ASTERIOIDS = {
		TEXTURE_REGION_NAME_ASTERIOID1,
		TEXTURE_REGION_NAME_ASTERIOID2, 
		TEXTURE_REGION_NAME_ASTERIOID3, 
		TEXTURE_REGION_NAME_ASTERIOID4,
		TEXTURE_REGION_NAME_ASTERIOID5
	};

	public static final String[] TEXTURE_REGION_NAMES_PLANETS = {
		TEXTURE_REGION_NAME_PLANET1,
		TEXTURE_REGION_NAME_PLANET2, 
		TEXTURE_REGION_NAME_PLANET3
	};
	
	/** WARNING: Indexes in ANIMATION_TEXTURE_REGION_NAMES, ANIMATION_NAMES and ANIMATION_FRAME_DURATIONS must match! */
	public static final String[][] ANIMATION_TEXTURE_REGION_NAMES = {
		{TEXTURE_REGION_NAME_THURST_FRAME1, TEXTURE_REGION_NAME_THURST_FRAME2},
		{TEXTURE_REGION_NAME_EXPLOSION1_FRAME1, TEXTURE_REGION_NAME_EXPLOSION1_FRAME2, TEXTURE_REGION_NAME_EXPLOSION1_FRAME3, TEXTURE_REGION_NAME_EXPLOSION1_FRAME4, TEXTURE_REGION_NAME_EXPLOSION1_FRAME5, TEXTURE_REGION_NAME_EXPLOSION1_FRAME6}
	};

	/** WARNING: Indexes in ANIMATION_TEXTURE_REGION_NAMES, ANIMATION_NAMES and ANIMATION_FRAME_DURATIONS must match! */
	public static final String[] ANIMATION_NAMES = {
		"thurst",
		"explosion1",
	};
	
	/** WARNING: Indexes in ANIMATION_TEXTURE_REGION_NAMES, ANIMATION_NAMES and ANIMATION_FRAME_DURATIONS must match! */
	public static final float[] ANIMATION_FRAME_DURATIONS = {
		0.05f,
		0.025f
	};
	
	/** WARNING: Indexes in TEXTURE_FILE_PATHS and TEXTURE_NAMES must match! */
	public static final String[] TEXTURE_FILE_PATHS = {
		"images/map_backgrounds/map1-background-layer1.png",
		"images/map_backgrounds/map1-background-layer2.png"};
	
	/** WARNING: Indexes in TEXTURE_FILE_PATHS and TEXTURE_NAMES must match! */
	public static final String[] TEXTURE_NAMES = {
		"map1-background-layer1", 
		"map1-background-layer2"}; 
	
	/* Sound */
	
}