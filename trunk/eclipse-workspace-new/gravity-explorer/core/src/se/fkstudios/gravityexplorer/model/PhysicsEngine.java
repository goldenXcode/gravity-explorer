package se.fkstudios.gravityexplorer.model;

import se.fkstudios.gravityexplorer.Defs;

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
	 * respect to given MapObjectModels.
	 * 
	 * @param mapObject
	 *            the MapObjectModel to update.
	 * @param mapObjects
	 *            the objects with gravitational influence over mapObject.
	 * @param mapWidth
	 *            the maps width.
	 * @param mapHeight
	 *            the maps height.
	 */
	public void applyGravity(MapObjectModel mapObject,
			Array<MapObjectModel> mapObjects, float mapWidth, float mapHeight) {
		Vector2 accelerationDelta = computGravitationalAcceleration(mapObject,
				mapObjects, mapWidth, mapHeight);
		mapObject.setAcceleration(mapObject.getAcceleration().x
				+ accelerationDelta.x, mapObject.getAcceleration().y
				+ accelerationDelta.y);
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
	public void applyGravity(MapObjectModel mapObject, Array<MapObjectModel>  neighbourhood, 
			Array<MapObjectModel> allMapObjects, 
			float mapWidth, float mapHeight, float delta) 
	{
		Vector2 accelerationDelta = new Vector2(0, 0);
		switch (mapObject.getGravitationalMode()) {
		case ALL:
			accelerationDelta = computGravitationalAcceleration(mapObject, allMapObjects, 
					mapWidth, mapHeight, delta);
			break;
		case NEIGHBOURHOOD:
			accelerationDelta = computGravitationalAcceleration(mapObject, neighbourhood,mapWidth, mapHeight, delta);
			break;
		case DOMINATING:
			MapObjectModel dominating = mapObject.getDominating();
			accelerationDelta = computeGravitationalAcceleration(mapObject, dominating, mapWidth, mapHeight, delta);
			Vector2 dominatingAcceleration = new Vector2(0, 0);
			while (dominating != null) {
				dominatingAcceleration.add(dominating.getAcceleration());
				dominating = dominating.getDominating();
			}
			accelerationDelta.add(dominatingAcceleration);
			break;
		case STATIONARY:
			// do nothing
			break;
		default:
			break;
		}

		mapObject.setAcceleration(mapObject.getAcceleration().x + accelerationDelta.x, 
				mapObject.getAcceleration().y + accelerationDelta.y);
	}

	public void applyStabilizingAcceleration(MapObjectModel mapObject, Array<MapObjectModel> neighbourhood, 
			Array<MapObjectModel> allMapObjects, 
			float mapWidth, float mapHeight, float delta) {
		
		MapObjectModel dominationgObject = null;
		switch (mapObject.getGravitationalMode()) {
		case ALL:
			dominationgObject = findGravitationallyStrongestObject(mapObject, allMapObjects, mapWidth, mapHeight);	
			break;
		case NEIGHBOURHOOD:
			dominationgObject = findGravitationallyStrongestObject(mapObject, neighbourhood, mapWidth, mapHeight);
			break;
		case DOMINATING:
			dominationgObject = mapObject.getDominating();
			break;
		case STATIONARY:
			//do nothing.
			break;
		default:
			break;
		}
		
		if (dominationgObject != null) {
			Vector2 compAcceleration = calculateOrbitCompensationAcceleration(mapObject, dominationgObject, 
					mapWidth, mapHeight, delta);		
			Vector2 currentAcceleration = mapObject.getAcceleration();
			mapObject.setAcceleration(compAcceleration.x + currentAcceleration.x, 
					compAcceleration.y + currentAcceleration.y);
			
			//TODO: compensate dominating object or parent object with force? 
		}
	}
	
	
	public Vector2 calculateOrbitCompensationAcceleration(
			MapObjectModel mapObject, Array<MapObjectModel> mapObjects,
			float mapWidth, float mapHeight, float delta) {
		MapObjectModel dominatingMapObject = findGravitationallyStrongestObject(
				mapObject, mapObjects, mapWidth, mapHeight);
		return calculateOrbitCompensationAcceleration(mapObject,
				dominatingMapObject, mapWidth, mapHeight, delta);
	}

	public Vector2 calculateOrbitCompensationAcceleration(
			MapObjectModel mapObject, MapObjectModel dominatingMapObject,
			float mapWidth, float mapHeight, float delta) {
		if (dominatingMapObject == null)
			return new Vector2(0, 0);

		Vector2 positionDiff = shortestDistanceVector(
				dominatingMapObject.getPosition(), mapObject.getPosition(),
				mapWidth, mapHeight);
		Vector2 velocityDiff = dominatingMapObject.getVelocity().cpy()
				.sub(mapObject.getVelocity());
		Vector2 tangentialVector = positionDiff.cpy().rotate(90).nor();

		float targetSpeed = calculateOrbitingSpeed(positionDiff.len(),
				dominatingMapObject.getMass());
		float currentSpeed = Math.abs(velocityDiff.cpy().dot(tangentialVector));
		Vector2 force = tangentialVector.scl(Math.abs(targetSpeed
				- currentSpeed)
				* Defs.ORBITAL_COMPENSATIONAL_FACTOR2);

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
	public boolean isCollision(MapObjectModel mapObject1,
			MapObjectModel mapObject2, float mapWidth, float mapHeight) {
		Vector2 position1 = mapObject1.getPosition();
		Vector2 position2 = mapObject2.getPosition();
		float width1 = mapObject1.getWidth();
		float width2 = mapObject2.getWidth();
		float distance = shortestDistance(position1, position2, mapWidth,
				mapHeight);
		return (distance < width1 / 2 + width2 / 2) && distance > 0.1f;
	}

	/**
	 * Finds the closest map object to mapObject in mapObjects. Return null if
	 * mapObjects is empty.
	 */
	public MapObjectModel findClosestObject(MapObjectModel mapObject,
			Array<MapObjectModel> mapObjects, float mapWidth, float mapHeight) {
		MapObjectModel closestMapObject = null;
		float shortestDistance = Float.MAX_VALUE;
		for (MapObjectModel otherMapObject : mapObjects) {
			float distance = shortestDistance(mapObject.getPosition(),
					otherMapObject.getPosition(), mapWidth, mapHeight);
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
	public MapObjectModel findGravitationallyStrongestObject(
			MapObjectModel mapObject, Array<MapObjectModel> mapObjects,
			float mapWidth, float mapHeight) {
		MapObjectModel resultObjectModel = null;
		float currentForce = 0;
		for (MapObjectModel otherMapObject : mapObjects) {
			if (mapObject != otherMapObject) {
				float force = computeGravitationalForce(mapObject, otherMapObject, mapWidth, mapHeight).len();
				if (force > currentForce 
					/*	&& mapObject.getMass() > otherMapObject.getMass() * Defs.COMPENSATIONAL_CUTOFF_FACTOR */) {
					currentForce = force;
					resultObjectModel = otherMapObject;
				}
			}
		}
		return resultObjectModel;
	}

	public void elasticCollision(Vector2 v1, Vector2 v2, Vector2 v12,
			Vector2 v22, float mass1, float mass2, Vector2 x1, Vector2 x2) {
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

	private Vector2 computGravitationalAcceleration(MapObjectModel mapObject,
			Array<MapObjectModel> mapObjects, float mapWidth, float mapHeight) {
		Vector2 acceleration = new Vector2(0, 0);
		for (int i = 0; i < mapObjects.size; i++) {
			MapObjectModel mapObject2 = mapObjects.get(i);
			Vector2 gravitationalForce = computeGravitationalForce(mapObject2,
					mapObject, mapWidth, mapHeight);
			Vector2 accelerationDelta = gravitationalForce.scl(1f / mapObject
					.getMass());
			acceleration.add(accelerationDelta);
		}
		return acceleration;
	}

	private Vector2 computGravitationalAcceleration(MapObjectModel mapObject,
			Array<MapObjectModel> mapObjects, float mapWidth, float mapHeight,
			float delta) {
		Vector2 acceleration = new Vector2(0, 0);
		for (int i = 0; i < mapObjects.size; i++) {
			MapObjectModel mapObject2 = mapObjects.get(i);

			Vector2 accelerationDelta = computeGravitationalAcceleration(
					mapObject, mapObject2, mapWidth, mapHeight, delta);

			// Vector2 gravitationalForce =
			// computeGravitationalForce(mapObject2, mapObject,
			// mapWidth, mapHeight,
			// delta);
			// Vector2 accelerationDelta =
			// gravitationalForce.div(mapObject.getMass());
			acceleration.add(accelerationDelta);
		}
		return acceleration;
	}

	private Vector2 computeGravitationalAcceleration(MapObjectModel mapObject1,
			MapObjectModel mapObject2, float mapWidth, float mapHeight,
			float delta) {
		Vector2 gravitationalForce = computeGravitationalForce(mapObject2,
				mapObject1, mapWidth, mapHeight, delta);
		return gravitationalForce.scl(1f / mapObject1.getMass());
	}

	private Vector2 computeGravitationalForce(MapObjectModel mapObject1,
			MapObjectModel mapObject2, float mapWidth, float mapHeight) {
		return computeGravitationalForce(mapObject1.getPosition(),
				mapObject2.getPosition(), mapObject1.getMass(),
				mapObject2.getMass(), mapObject2.getRadius(), mapWidth,
				mapHeight);
	}

	/**
	 * The Euler-Backward version
	 */
	private Vector2 computeGravitationalForce(MapObjectModel mapObject1,
			MapObjectModel mapObject2, float mapWidth, float mapHeight,
			float delta) {
		Vector2 velocity = mapObject1.getVelocity().cpy();
		Vector2 positionIncrement = velocity.scl(delta);
		return computeGravitationalForce(
				mapObject1.getPosition().cpy().add(positionIncrement),
				mapObject2.getPosition(), mapObject1.getMass(),
				mapObject2.getMass(), mapObject2.getRadius(), mapWidth,
				mapHeight);
	}

	private Vector2 computeGravitationalForce(Vector2 position1,
			Vector2 position2, float mass1, float mass2, float cutoff,
			float mapWidth, float mapHeight) {
		Vector2 distanceVector = shortestDistanceVector(position1, position2,
				mapWidth, mapHeight);
		float distance = distanceVector.len();
		float force = 0;
		if (distance > cutoff) { // cutoff to prevent singularities arising from
								 // zero distance between objects
			force = (Defs.GRAVITATIONAL_CONSTANT * mass1 * mass2) / (distance * distance); // Newtons law of gravity
		}
		Vector2 direction = distanceVector.nor();
		return direction.scl(force);
	}

	private float shortestDistance(Vector2 position1, Vector2 position2,
			float mapWidth, float mapHeight) {
		return shortestDistanceVector(position1, position2, mapWidth, mapHeight).len();
	}

	/**
	 * Computes the shortest vector from position1 to position2 with respect to
	 * periodicity.
	 * 
	 * @param position1
	 *            from position.
	 * @param position2
	 *            to position.
	 * @return the shortest vector from position1 to position2.
	 */
	private Vector2 shortestDistanceVector(Vector2 position1,
			Vector2 position2, float mapWidth, float mapHeight) {
		Vector2 result1 = shortestDistanceVectorHelper(position1, position2,
				mapWidth, mapHeight);
		Vector2 result2 = shortestDistanceVectorHelper(position2, position1,
				mapWidth, mapHeight).scl(-1);
		if (result1.len2() < result2.len2())
			return result1;
		else
			return result2;
	}

	/**
	 * Computes the shortest vector from position1 to position2 considering
	 * alternative positions for position2 only.
	 */
	private Vector2 shortestDistanceVectorHelper(Vector2 position1,
			Vector2 position2, float mapWidth, float mapHeight) {
		float x1 = position1.x;
		float x2 = position2.x;
		float y1 = position1.y;
		float y2 = position2.y;

		float xDistance1 = x1 - x2;
		float xDistance2 = -(x1 - x2) + mapWidth;

		float yDistance1 = y1 - y2;
		float yDistance2 = -(y1 - y2) + mapHeight; // yDistance2 == yDistance1 +
													// 5, not good

		Vector2 result = new Vector2();

		if (Math.abs(xDistance1) < Math.abs(xDistance2))
			result.x = xDistance1;
		else
			result.x = -xDistance2;

		if (Math.abs(yDistance1) < Math.abs(yDistance2))
			result.y = yDistance1;
		else
			result.y = -yDistance2;

		return result;
	}
}
