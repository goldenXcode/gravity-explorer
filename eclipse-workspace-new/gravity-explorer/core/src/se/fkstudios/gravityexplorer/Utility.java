package se.fkstudios.gravityexplorer;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Utility {
	
	private static Random rand = new Random();

	public static float getScreenCoordinate(float modelCoordinate) {
		return modelCoordinate * Defs.PIXELS_PER_UNIT;
	}
	
	public static Vector2 getScreenPosition(Vector2 position) {
		Vector2 gsp_result = new Vector2();
		gsp_result.x = getScreenCoordinate(position.x);
		gsp_result.y = getScreenCoordinate(position.y);
		return gsp_result;
	}
	
	public static float getModelCoordinate(float screenCoordinate) {
		return screenCoordinate / Defs.PIXELS_PER_UNIT;
	}
	
	public static Vector2 getModelPosition(Vector2 position) {
		Vector2 gmp_result = new Vector2();
		gmp_result.x = getModelCoordinate(position.x);
		gmp_result.y = getModelCoordinate(position.y);
		return gmp_result;
	}
	
	public static int randomInt(int min, int max) {
		return rand.nextInt(max + 1 - min) + min;
	}
	
	public static float randomFloat(float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}
	
	public static Color randomColor() {
		return new Color(randomFloat(0f, 1f), randomFloat(0f, 1f), randomFloat(0f, 1f), 1f); 
	}
	
	public static Array<Vector2> calculatePerodicPositions(Vector2 centerPosition, float periodicityWidth, float periodicityHeight) {
		
		Array<Vector2> cpp_result = new Array<Vector2>(1);
		Vector2 ccp_positionTopLeft = new Vector2(0,0);
		Vector2 ccp_positionTopCenter = new Vector2(0,0);
		Vector2 ccp_positionTopRight = new Vector2(0,0);
		Vector2 ccp_positionCenterLeft = new Vector2(0,0);
		Vector2 ccp_positionCenterCenter = new Vector2(0,0);
		Vector2 ccp_positionCenterRight = new Vector2(0,0);
		Vector2 ccp_positionBottomLeft = new Vector2(0,0);
		Vector2 ccp_positionBottomCenter = new Vector2(0,0);
		Vector2 ccp_positionBottomRight = new Vector2(0,0);
	
		ccp_positionTopLeft.x = centerPosition.x - periodicityWidth;
		ccp_positionTopLeft.y = centerPosition.y + periodicityHeight;
		
		ccp_positionTopCenter.x =  centerPosition.x;
		ccp_positionTopCenter.y = centerPosition.y + periodicityHeight;
		
		ccp_positionTopRight.x = centerPosition.x + periodicityWidth;
		ccp_positionTopRight.y = centerPosition.y + periodicityHeight;
		
		ccp_positionCenterLeft.x = centerPosition.x - periodicityWidth;
		ccp_positionCenterLeft.y = centerPosition.y;
		
		ccp_positionCenterCenter.x = centerPosition.x;
		ccp_positionCenterCenter.y = centerPosition.y;
		
		ccp_positionCenterRight.x = centerPosition.x + periodicityWidth;
		ccp_positionCenterRight.y = centerPosition.y;

		ccp_positionBottomLeft.x = centerPosition.x - periodicityWidth;
		ccp_positionBottomLeft.y = centerPosition.y - periodicityHeight;
		
		ccp_positionBottomCenter.x = centerPosition.x;
		ccp_positionBottomCenter.y = centerPosition.y - periodicityHeight;
		
		ccp_positionBottomRight.x = centerPosition.x + periodicityWidth;
		ccp_positionBottomRight.y = centerPosition.y - periodicityHeight;
		
		if (cpp_result.size == 0) {
			cpp_result.add(ccp_positionTopLeft);
			cpp_result.add(ccp_positionTopCenter);
			cpp_result.add(ccp_positionTopRight);
			cpp_result.add(ccp_positionCenterLeft);
			cpp_result.add(ccp_positionCenterCenter);
			cpp_result.add(ccp_positionCenterRight);
			cpp_result.add(ccp_positionBottomLeft);
			cpp_result.add(ccp_positionBottomCenter);
			cpp_result.add(ccp_positionBottomRight);
		}
		
		return cpp_result;
	}
}
