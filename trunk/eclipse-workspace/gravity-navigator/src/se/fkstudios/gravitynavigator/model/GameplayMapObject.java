package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

/**
 * Modeling the properties of all gameplay map objects in game.
 * @author kristofer
 */
public abstract class GameplayMapObject extends MapObject {

	private float width;
	private float height;
	private Vector2 position;
	private Vector2 velocity;
	float rotation;
	private int mass; // in kg
	
	public GameplayMapObject(Vector2 position, 
			float width,
			float height, 
			Vector2 velocity,
			float rotation,
			int mass,
			String textureName) {
		this.position = position;
		this.width = width;
		this.height = height;
		this.velocity = velocity;
		this.rotation = rotation;
		this.mass = mass;
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

	public Vector2 getVelocity() {
		return velocity;
	}
	
	protected void setVelocity(Vector2 velocity) {
		this.velocity.set(velocity);
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
	
	/**
	 * Update the object for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		position.add(velocity.cpy().scl(delta));
	}
}
