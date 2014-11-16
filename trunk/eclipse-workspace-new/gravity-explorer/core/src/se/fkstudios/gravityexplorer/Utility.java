package se.fkstudios.gravityexplorer;

public class Utility {
	
	public static float getScreenCoordinate(float modelCoordinate) {
		return modelCoordinate * Defs.PIXELS_PER_UNIT;
	}
	
	public static float getModelCoordinate(float screenCoordinate) {
		return screenCoordinate / Defs.PIXELS_PER_UNIT;
	}
}
