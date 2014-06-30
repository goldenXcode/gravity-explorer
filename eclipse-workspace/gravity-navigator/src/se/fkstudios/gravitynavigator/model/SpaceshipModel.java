package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Spaceship gameplay object, the object the player controls. 
 * @author kristofer
 */
public class SpaceshipModel extends TextureMapObjectModel {

	private Vector2 thrust;
	private int maxThrust; //in Newton

	public Vector2 getThrust() {
		return thrust;
	}

	public void setThrust(Vector2 thrust) {
		this.thrust.x = thrust.x;
		this.thrust.y = thrust.y;
	}

	public int getMaxThrust() {
		return maxThrust;
	}

	public void setMaxThrust(int maxThrust) {
		this.maxThrust = maxThrust;
	}

	public SpaceshipModel(Vector2 position, float width, float height, Vector2 velocity, float rotation, int mass, String textureName, int maxThrust) {
		super(position, width, height, velocity, rotation, mass, textureName);
		this.thrust = new Vector2(0, 0);
		this.maxThrust = maxThrust;
	}
	
	@Override
	public void update(float delta) {
		int mass = getMass();
		Vector2 acceleration = thrust.cpy().div(mass);
		//System.out.println("thurst: " + thrust.toString());
		getAcceleration().add(acceleration);
		super.update(delta);
	} 	
	
	/**
	 * Sets the velocity and rotates ship in traveling direction.
	 */
	@Override
	public void setVelocity(Vector2 velocity) {
		super.setVelocity(velocity);
		
		if (thrust.len2() > 0)
			setRotation(thrust.angle());
	}
}