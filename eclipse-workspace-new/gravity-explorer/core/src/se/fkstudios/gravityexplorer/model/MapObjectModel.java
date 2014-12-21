package se.fkstudios.gravityexplorer.model;

import se.fkstudios.gravityexplorer.model.resources.AnimationResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.Renderable;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Modeling the properties of all gameplay map objects in game.
 * @author kristofer
 */
public class MapObjectModel extends MapObject implements Renderable {
	
	public enum GravitationalMode { ALL, NEIGHBOURHOOD, OBJECT, STATIONARY } 
	
	private float width;
	private float height;
	private Vector2 position;
	private Vector2 acceleration;
	private Vector2 velocity;
	private int mass; // in kg
	private float rotation;
	private float rotationalSpeed; // in degrees per second
	private boolean selfStabilizing;
	private boolean generatesParticles;
	private GravitationalMode gravitationalMode;
	private MapObjects influencingMapObjects;
	private MapObjectModel dominatingMapObject;
	
	private Array<GraphicResource> resources;
	
	public MapObjectModel(float width, float height,
			Vector2 position,
			Vector2 velocity,
			int mass,
			float rotation,
			float rotationSpeed,
			boolean ganeratesParticles,
			Array<GraphicResource> resources) 
	{
		this.width = width;
		this.height = height;
		this.position = position;
		this.acceleration = new Vector2(0, 0);
		this.velocity = velocity;
		this.mass = mass;
		this.rotation = rotation;
		this.rotationalSpeed = rotationSpeed;
		this.selfStabilizing = false;
		this.resources = resources;
		this.generatesParticles = ganeratesParticles;
		this.gravitationalMode = GravitationalMode.NEIGHBOURHOOD;
		this.influencingMapObjects = new MapObjects();
		this.dominatingMapObject = null;
	}

	public MapObjectModel(float width, float height,
			Vector2 position,
			Vector2 velocity,
			int mass,
			float rotation,
			float rotationSpeed,
			boolean generatesParticles,
			GraphicResource resource) 
	{
		this(width, height, position, velocity, mass, rotation, rotationSpeed, 
				generatesParticles, new Array<GraphicResource>(1));
		this.resources.add(resource);
	}
	
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height)	{
		this.height = height;
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	protected void setPosition(Vector2 position) {
		this.position.set(position);
	}
	
	protected void setPosition(float x, float y) {
		this.position.x = x;
		this.position.y = y;
	}

	public Vector2 getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Vector2 velocity) {
		this.velocity.set(velocity);
	}
	
	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public int getMass() {
		return mass;
	}
	
	public void setMass(int mass) {
		this.mass = mass;
	}
	
	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	public void setAcceleration(Vector2 acceleration) {
		this.acceleration.x = acceleration.x;
		this.acceleration.y	= acceleration.y;
	}
	
	public void setAcceleration(float x, float y) {
		this.acceleration.x = x;
		this.acceleration.y = y;
	}
	
	public void setRotationalSpeed (float rotationSpeed) {
		this.rotationalSpeed = rotationSpeed; 
	}
	
	public float getRotationalSpeed () {
		return rotationalSpeed; 
	}
	
	public float getRadius() {
//		return (float) (Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2))); 
//		kristofer 2014-11-30. say what? radius = segment line or what?
		return (getWidth() + getHeight()) / 4;
	}
	
	public Array<GraphicResource> getResources() {
		return resources;
	}
	
	public boolean isSelfStabilizing() {
		return selfStabilizing;
	}
	
	public void setSelfStabilizing(boolean value) {
		selfStabilizing = value;
	}
	
	public boolean isGeneratingParticles() {
		return generatesParticles;
	}
	
	public void setGeneratingParticles(boolean value) {
		generatesParticles = value;
	}
	
	public GravitationalMode getGravitationalMode() {
		return gravitationalMode;
	}

	public void setGravitationalModeToAll() {
		gravitationalMode = GravitationalMode.ALL;
		ClearInfluencingMapObjects();
		dominatingMapObject = null;
	}
	
	public void setGravitationalModeToNeighbourhood() {
		gravitationalMode = GravitationalMode.NEIGHBOURHOOD;
		ClearInfluencingMapObjects();
		dominatingMapObject = null;
	}
	
	public void setGravitationalModeToStationary() {
		gravitationalMode = GravitationalMode.STATIONARY;
		ClearInfluencingMapObjects();
		dominatingMapObject = null;
	}
	
//	public void setGravitationalModeToObjects(MapObjectModel dominatingMapObject, Array<MapObjectModel> otherMapObjects) {
//		gravitationalMode = GravitationalMode.OBJECTS;
//		influencingMapObjects = otherMapObjects;
//		influencingMapObjects.add(dominatingMapObject);
//		this.dominatingMapObject = dominatingMapObject;
//	}

	public void setGravitationalModeToObject(MapObjectModel dominatingMapObject) {
		gravitationalMode = GravitationalMode.OBJECT;
		ClearInfluencingMapObjects();
		influencingMapObjects.add(dominatingMapObject);
		this.dominatingMapObject = dominatingMapObject;
	}
	
	public MapObjectModel getDominatingMapObject() {
		return dominatingMapObject;
	}
	
	public MapObjects getInfluencingObjects(MapObjects allMapObjects, MapObjects neighbourhood) {
		GravitationalMode mode = getGravitationalMode();
		switch (mode) {
		case ALL:
			return allMapObjects;
		case NEIGHBOURHOOD:
			return neighbourhood;
		case OBJECT:
		case STATIONARY:
		default:
			return influencingMapObjects;
		}
	}
	
	public MapObjects getInfluencingObjectsSourceCollection() {
		return influencingMapObjects;
	}
	
	/**
	 * Update the object for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		if (gravitationalMode != GravitationalMode.STATIONARY) {
			setVelocity(velocity.x + acceleration.x * delta, velocity.y + acceleration.y * delta);
			setPosition(position.x + velocity.x * delta, position.y + velocity.y * delta);
		}
		setRotation(((getRotation() + getRotationalSpeed()*delta) % 360f));
		//update stateTime for any animation resources.
		for(GraphicResource resource : getResources()) {
			if (resource.getClass() == AnimationResource.class) {
				((AnimationResource)resource).incStateTime(delta);
			}
		}
	}
	
	private void ClearInfluencingMapObjects() {
		while(influencingMapObjects.getCount() > 0) {
			int index = influencingMapObjects.getCount() - 1;
			influencingMapObjects.remove(index);
		}
	}
}
	

	
		

	

