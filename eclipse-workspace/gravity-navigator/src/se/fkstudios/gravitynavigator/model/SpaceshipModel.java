package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Spaceship gameplay object, the object the player controls. 
 * @author kristofer
 */
public class SpaceshipModel extends MapObjectModel {

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

	public SpaceshipModel(Vector2 position, 
			float width, 
			float height, 
			Vector2 velocity, 
			float rotation, 
			int mass, 
			int maxThrust, 
			Array<GraphicResource> resources, 
			float minRenderSizeFactor) {
		super(position, width, height, velocity, rotation, mass, resources, minRenderSizeFactor);
		this.thrust = new Vector2(0, 0);
		this.maxThrust = maxThrust;
	}
	
	@Override
	public void update(float delta) {
		int mass = getMass();
		Vector2 acceleration = thrust.cpy().div(mass);
		getAcceleration().add(acceleration);
		
		super.update(delta);
		
		if (thrust.len2() > 0) 
			setRotation(thrust.angle() - 90); //LibGdx render with rotation 0 from y-axis while Vector2 calculates angle from x-axis, thus -90.
	} 	
}
