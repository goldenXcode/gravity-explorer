package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
import se.fkstudios.gravitynavigator.model.resources.ResourceContainer;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Modeling the properties of all gameplay map objects in game.
 * @author kristofer
 */
public class MapObjectModel extends MapObject implements ResourceContainer {
	
	private float width;
	private float height;
	private Vector2 position;
	private Vector2 acceleration;
	private Vector2 velocity;
	private int mass; // in kg
	private float rotation;
	private float rotationalSpeed; // in degrees per second
	private boolean selfStabilizing; 
	private boolean stationary;
	
	private Array<TextureRegionResource> resources;
	
	public MapObjectModel(float width, float height,
			Vector2 position,
			Vector2 velocity,
			int mass,
			float rotation,
			float rotationSpeed,
			boolean selfStabilizing,
			boolean stationary,
			Array<TextureRegionResource> resources) 
	{
		this.width = width;
		this.height = height;
		this.position = position;
		this.acceleration = new Vector2(0, 0);
		this.velocity = velocity;
		this.mass = mass;
		this.rotation = rotation;
		this.rotationalSpeed = rotationSpeed;
		this.selfStabilizing = selfStabilizing;
		this.stationary = stationary;
		this.resources = resources;
	}

	public MapObjectModel(float width, float height,
			Vector2 position,
			Vector2 velocity,
			int mass,
			float rotation,
			float rotationSpeed,
			boolean selfStabilizing,
			boolean stationary,
			TextureRegionResource resource) 
	{
		this(width, height, position, velocity, mass, rotation, rotationSpeed, selfStabilizing, stationary, 
				new Array<TextureRegionResource>(1));
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
		return this.rotationalSpeed; 
	}
	
	public float getRadius() {
		float result = (float) (Math.sqrt(Math.pow(getWidth(), 2) +Math.pow(getHeight(), 2)));
		return result; 
	}
	
	public Array<TextureRegionResource> getResources() {
		return resources;
	}
	
	public boolean isSelfStabilizing() {
		return selfStabilizing;
	}
	
	public void setSelfStabilizing(boolean value) {
		selfStabilizing = value;
	}
	
	public boolean isStationary() {
		return stationary;
	}
	
	/**
	 * Update the object for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		if (!stationary) {
			//add current acceleration
			Vector2 newVelocity = getVelocity().cpy();
			newVelocity.add(acceleration.cpy().scl(delta));
			//setts the new velocity
			setVelocity(newVelocity);
			//update position according to velocity and delta.
			setPosition(position.cpy().add(velocity.cpy().scl(delta)));	
		}
		setRotation(((getRotation() + getRotationalSpeed()*delta) % 360f));
		//update stateTime for any animation resources.
		for(TextureRegionResource resource : getResources()) {
			if (resource.getClass() == AnimationResource.class) {
				((AnimationResource)resource).stateTime += delta;
			}
		}
	}
}
	

	
		

	

