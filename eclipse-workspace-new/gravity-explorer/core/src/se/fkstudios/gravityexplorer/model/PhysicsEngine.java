package se.fkstudios.gravityexplorer.model;

import se.fkstudios.gravityexplorer.Defs;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Applies the "laws of physics" to a set of GamePlayMapObjects
 * 
 * @author fredrik
 * 
 */
public class PhysicsEngine {

	/* Singelton stuff */
	private static final PhysicsEngine instance = new PhysicsEngine();

	private PhysicsEngine() {
	}

	public static PhysicsEngine getInstance() {
		return instance;
	}

	/**
	 * Updates a given MapObjectModel's acceleration imposed by gravity with
	 * respect to given MapObjectModels. Using the Euler backward method.
	 * 
	 * @param mapObject the MapObjectModel to update acceleration for.
	 * @param neighbourhood the objects with gravitational influence over mapObject if GravitationalMode is set to NEIGHBOURHOOD.
	 * @param allMapObjects the objects with gravitational influence over mapObject if GravitationalMode is set to ALL.
	 * @param mapWidth the maps width.
	 * @param mapHeight the maps height.
	 * @param delta time delta since last update in seconds.
	 */
	private Vector2 applyGravityAccelerationDeltaHelper = new Vector2();
	private Vector2 applyGravityDominatingAccelerationsHelper = new Vector2();
	public void applyGravity(MapObjectModel mapObject, MapObjects influencingMapObjects,
			float mapWidth, float mapHeight, float delta) 
	{
		applyGravityAccelerationDeltaHelper.x = 0;
		applyGravityAccelerationDeltaHelper.y = 0;
		applyGravityDominatingAccelerationsHelper.x = 0;
		applyGravityDominatingAccelerationsHelper.y = 0;
		
		computeGravitationalAcceleration(mapObject, influencingMapObjects, mapWidth, mapHeight, delta, applyGravityAccelerationDeltaHelper);
	
		MapObjectModel dominatingMapObject = mapObject.getDominatingMapObject();
		while (dominatingMapObject != null) {
			applyGravityDominatingAccelerationsHelper.x += + dominatingMapObject.getAcceleration().x;
			applyGravityDominatingAccelerationsHelper.y +=  dominatingMapObject.getAcceleration().y;
			dominatingMapObject = dominatingMapObject.getDominatingMapObject();
		}
		applyGravityAccelerationDeltaHelper.x += applyGravityDominatingAccelerationsHelper.x;
		applyGravityAccelerationDeltaHelper.y += applyGravityDominatingAccelerationsHelper.y;

		mapObject.setAcceleration(mapObject.getAcceleration().x + applyGravityAccelerationDeltaHelper.x, 
				mapObject.getAcceleration().y + applyGravityAccelerationDeltaHelper.y);
	}

	public void applyStabilizingAcceleration(MapObjectModel mapObject, MapObjects influencingMapObjects, 
			float mapWidth, float mapHeight, float delta) 
	{
		MapObjectModel dominationgObject = findGravitationallyStrongestObject(mapObject, influencingMapObjects, mapWidth, mapHeight);
		
		if (dominationgObject != null) {
			Vector2 compAcceleration = calculateOrbitCompensationAcceleration(mapObject, dominationgObject, mapWidth, mapHeight, delta);		
			Vector2 currentAcceleration = mapObject.getAcceleration();
			mapObject.setAcceleration(compAcceleration.x + currentAcceleration.x, compAcceleration.y + currentAcceleration.y);
			
			//TODO: compensate dominating object or parent object with force? 
		}
	}
	
	public Vector2 calculateOrbitCompensationAcceleration(MapObjectModel mapObject, MapObjects mapObjects,
			float mapWidth, float mapHeight, float delta) 
	{
		MapObjectModel dominatingMapObject = findGravitationallyStrongestObject(mapObject, mapObjects, mapWidth, mapHeight);
		return calculateOrbitCompensationAcceleration(mapObject, dominatingMapObject, mapWidth, mapHeight, delta);
	}

	private Vector2 calculateOrbitCompensationAcceleration(MapObjectModel mapObject, MapObjectModel dominatingMapObject,
			float mapWidth, float mapHeight, float delta) 
	{
		if (dominatingMapObject == null)
			return new Vector2(0, 0);

		Vector2 positionDiff = new Vector2();
		computeShortestDistanceVector(dominatingMapObject.getPosition().x, dominatingMapObject.getPosition().y, 
				mapObject.getPosition().x, mapObject.getPosition().y, mapWidth, mapHeight, positionDiff);
		
		Vector2 velocityDiff = dominatingMapObject.getVelocity().cpy().sub(mapObject.getVelocity());
		Vector2 tangentialVector = positionDiff.cpy().rotate(90).nor();

		float targetSpeed = calculateOrbitingSpeed(positionDiff.len(), dominatingMapObject.getMass());
		
		float currentSpeed = Math.abs(velocityDiff.cpy().dot(tangentialVector));
		
		Vector2 force = tangentialVector.scl(Math.abs(targetSpeed - currentSpeed) * Defs.ORBITAL_COMPENSATIONAL_FACTOR2);

		boolean clockwise = velocityDiff.dot(tangentialVector) <= 0;
		boolean accelerating = targetSpeed > currentSpeed;

		if ((!accelerating && clockwise) || (accelerating && !clockwise)) {
			force.scl(-1);
		}

		return force.scl(delta);
	}

	/**
	 * Detect if two objects has collided.
	 */
	public boolean isCollision(MapObjectModel mapObject1, MapObjectModel mapObject2, float mapWidth, float mapHeight) {
		Vector2 position1 = mapObject1.getPosition();
		Vector2 position2 = mapObject2.getPosition();
		float distance = computeShortestDistance(position1.x, position1.y, position2.x, position2.y, mapWidth, mapHeight);
		float width1 = mapObject1.getWidth();
		float width2 = mapObject2.getWidth();
		return (distance < width1 / 2 + width2 / 2) && distance > 0.1f;
	}

	/**
	 * Finds the closest map object to mapObject in mapObjects. Return null if
	 * mapObjects is empty.
	 */
	public MapObjectModel findClosestObject(MapObjectModel mapObject, Array<MapObjectModel> mapObjects, float mapWidth, float mapHeight) {
		MapObjectModel closestMapObject = null;
		float shortestDistance = Float.MAX_VALUE;
		for (MapObjectModel otherMapObject : mapObjects) {
			Vector2 position1 = mapObject.getPosition();
			Vector2 position2 = otherMapObject.getPosition();
			float distance = computeShortestDistance(position1.x, position1.y, position2.x, position2.y, mapWidth, mapHeight);
			if ((mapObject != otherMapObject) && (distance < shortestDistance)) {
				closestMapObject = otherMapObject;
				shortestDistance = distance;
			}
		}
		return closestMapObject;
	}

	/**
	 * Finds the map object in mapObjects influencing mapObject with the
	 * greatest gravitational force. Return null if mapObjects is empty.
	 */
	public MapObjectModel findGravitationallyStrongestObject(MapObjectModel mapObject, MapObjects mapObjects,
			float mapWidth, float mapHeight)
	{
		MapObjectModel resultObjectModel = null;
		float currentForce = 0;
		for (MapObject otherMapObject : mapObjects) {
			MapObjectModel otherMapObjectModel = (MapObjectModel)otherMapObject;
			if (mapObject != otherMapObjectModel) {
				float force = computeGravitationalForce(mapObject, otherMapObjectModel, mapWidth, mapHeight);
				if (force > currentForce 
					/*	&& mapObject.getMass() > otherMapObject.getMass() * Defs.COMPENSATIONAL_CUTOFF_FACTOR */) {
					currentForce = force;
					resultObjectModel = otherMapObjectModel;
				}
			}
		}
		return resultObjectModel;
	}

	public void elasticCollision(Vector2 v1, Vector2 v2, Vector2 v12, Vector2 v22, 
			float mass1, float mass2, Vector2 x1, Vector2 x2) 
	{
		// first calculation
		float factor1 = (2 * mass2 / (mass1 + mass2));
		float factor2 = (v1.sub(v2)).dot(x1.sub(x2));
		float factor3 = x1.sub(x2).len2();
		Vector2 term2 = x1.sub(x2).scl(factor1 * factor2 / factor3);
		v12 = v1.sub(term2);

		// second calculation is just the first calculation with indices
		// permuted (i.e. v12 -> v22)
		float factor12 = (2 * mass1 / (mass2 + mass1));
		float factor22 = (v2.sub(v1)).dot(x2.sub(x1));
		float factor32 = x2.sub(x1).len2();
		Vector2 term22 = x2.sub(x1).scl(factor12 * factor22 / factor32);
		v22 = v2.sub(term22);
	}

	public float calculateOrbitingSpeed(float distance, float planetMass) {
		return (float) Math.sqrt(Defs.GRAVITATIONAL_CONSTANT * planetMass / distance);
	}

	private Vector2 computeGravitationalAccelerationHelper = new Vector2();
	private Vector2 computeGravitationalAcceleration(MapObjectModel mapObject, MapObjects mapObjects, 
			float mapWidth, float mapHeight, float delta, Vector2 resultVector) 
	{
		resultVector.x = 0;
		resultVector.y = 0;
		for (int i = 0; i < mapObjects.getCount(); i++) {	
			MapObjectModel mapObject2 = (MapObjectModel) mapObjects.get(i);
			computeGravitationalAcceleration(mapObject, mapObject2, mapWidth, mapHeight, delta, computeGravitationalAccelerationHelper);
			resultVector.x += computeGravitationalAccelerationHelper.x;
			resultVector.y += computeGravitationalAccelerationHelper.y;
		}
		return resultVector;
	}

	private void computeGravitationalAcceleration(MapObjectModel mapObject1,
			MapObjectModel mapObject2, float mapWidth, float mapHeight, float delta, Vector2 resultVector) 
	{
		computeGravitationalForceVector(mapObject2, mapObject1, mapWidth, mapHeight, delta, resultVector);
		resultVector.scl(1f / mapObject1.getMass());
	}

	private Vector2 computeGravitationalForceHelper = new Vector2();
	private float computeGravitationalForce(MapObjectModel mapObject1, MapObjectModel mapObject2, 
			float mapWidth, float mapHeight) 
	{
		computeGravitationalForceVector(mapObject1.getPosition().x, mapObject1.getPosition().y,
				mapObject2.getPosition().x, mapObject2.getPosition().y, 
				mapObject1.getMass(), mapObject2.getMass(), 
				mapObject1.getRadius() + mapObject2.getRadius(),
				mapWidth, mapHeight, computeGravitationalForceHelper);
		return computeGravitationalForceHelper.len();
	}

	/**
	 * The Euler-Backward version
	 */
	private void computeGravitationalForceVector(MapObjectModel mapObject1, MapObjectModel mapObject2, 
			float mapWidth, float mapHeight, float delta, Vector2 resultVector) 
	{
		float xVel1 = mapObject1.getVelocity().x;
		float yVel1 = mapObject1.getVelocity().y;
		
		float xPos1Inc = xVel1 * delta;
		float yPos1Inc = yVel1 * delta;
		
		float xPos1 = mapObject1.getPosition().x;
		float yPos1 = mapObject1.getPosition().y;
		
		float xPos2 = mapObject2.getPosition().x;
		float yPos2 = mapObject2.getPosition().y;
		
		computeGravitationalForceVector(xPos1 + xPos1Inc, yPos1 + yPos1Inc, xPos2, yPos2, 
				mapObject1.getMass(), mapObject2.getMass(), 
				mapObject1.getRadius() + mapObject2.getRadius(),
				mapWidth, mapHeight, resultVector);
	}

	private void computeGravitationalForceVector(float xPos1, float yPos1, float xPos2, float yPos2,
			float mass1, float mass2, float cutoff, float mapWidth, float mapHeight, Vector2 resultVector) 
	{
		computeShortestDistanceVector(xPos1, yPos1, xPos2, yPos2, mapWidth, mapHeight, resultVector);
		float distance = resultVector.len();
		float force = 0;
		if (distance > cutoff) { // cutoff to prevent singularities arising from
								 // zero distance between objects
			force = (Defs.GRAVITATIONAL_CONSTANT * mass1 * mass2) / (distance * distance); // Newtons law of gravity
		}
		resultVector.nor();
		resultVector.scl(force);
	}

	private Vector2 shortestDistanceHelper = new Vector2(0,0);
	private float computeShortestDistance(float xPos1, float yPos1, float xPos2, float yPos2, float mapWidth, float mapHeight) {
		computeShortestDistanceVector(xPos1, yPos1, xPos2, yPos2, mapWidth, mapHeight, shortestDistanceHelper);
		return shortestDistanceHelper.len();
	}
	
	private Vector2 shortestDistanceVectorHelper1 = new Vector2(0, 0);
	private Vector2 shortestDistanceVectorHelper2 = new Vector2(0, 0);
	/**
	 * Computes the shortest vector from position1 to position2 with respect to periodicity.
	 * @param position1 from position.
	 * @param position2 to position.
	 * @return the shortest vector from position1 to position2.
	 */
	private void computeShortestDistanceVector(float xPos1, float yPos1, float xPos2, float yPos2, float mapWidth, float mapHeight, Vector2 resultVector) {	
		shortestDistanceVectorHelper(xPos1, yPos1, xPos2, yPos2, mapWidth, mapHeight, shortestDistanceVectorHelper1);
		
		shortestDistanceVectorHelper(xPos2, yPos2, xPos1,yPos1, mapWidth, mapHeight, shortestDistanceVectorHelper2);
		shortestDistanceVectorHelper2.scl(-1);
		
		if (shortestDistanceVectorHelper1.len2() < shortestDistanceVectorHelper2.len2()) {
			resultVector.x = shortestDistanceVectorHelper1.x;
			resultVector.y = shortestDistanceVectorHelper1.y;
		}
		else {
			resultVector.x = shortestDistanceVectorHelper2.x;
			resultVector.y = shortestDistanceVectorHelper2.y;
		}
	}

	/**
	 * Computes the shortest vector from position1 to position2 considering
	 * alternative positions for position2 only.
	 */
	private void shortestDistanceVectorHelper(float xPos1, float yPos1, float xPos2, float yPos2, 
			float mapWidth, float mapHeight, Vector2 resultVector) 
	{
		float xDistance1 = xPos1 - xPos2;
		float xDistance2 = -(xPos1 - xPos2) + mapWidth;

		float yDistance1 = yPos1 - yPos2;
		float yDistance2 = -(yPos1 - yPos2) + mapHeight;
		
		if (Math.abs(xDistance1) < Math.abs(xDistance2))
			resultVector.x = xDistance1;
		else
			resultVector.x = -xDistance2;

		if (Math.abs(yDistance1) < Math.abs(yDistance2))
			resultVector.y = yDistance1;
		else
			resultVector.y = -yDistance2;
	}
}
