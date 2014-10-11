package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
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
	private float aliveTime; 
	private float fuelLeft; 
	
	private Array<AnimationResource> thrustAnimations;
	
	public void setFuelLeft(float fuelLeft) {
		this.fuelLeft = fuelLeft; 
	}
	
	public float getFuelLeft() {
		return fuelLeft; 
	}

	public float getAliveTime() {
		return aliveTime; 
	}
	
	public Vector2 getThrust() {
		return thrust;
	}

	public void setThrust(Vector2 thrust) {
		setThrust(thrust.x, thrust.y);
	}

	public void setThrust(float thrustX, float thrustY) {
		thrust.x = thrustX;
		thrust.y = thrustY;
		for (AnimationResource thrustAnimation : thrustAnimations)
			thrustAnimation.visible = thrust.len2() > 0;
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
			Array<GraphicResource> allResources, 
			Array<AnimationResource> thrustAnimations,
			float minRenderSizeFactor) {
		super(position, width, height, velocity, rotation, mass, allResources, minRenderSizeFactor);
		this.maxThrust = maxThrust;
		this.thrustAnimations = thrustAnimations;
		this.thrust = new Vector2(0, 0);
		this.aliveTime = 0; 
		this.fuelLeft = Defs.STARTING_FUEL;
	}
	
	@Override
	public void update(float delta) {
		int mass = getMass();
		Vector2 acceleration = thrust.cpy().div(mass);
		setFuelLeft(getFuelLeft() - thrust.cpy().len() * Defs.FUEL_SCALING_FACTOR*fuelEfficiencyMapping(getFuelLeft()) ); 
		getAcceleration().add(acceleration).scl(fuelEfficiencyMapping(fuelLeft));
		aliveTime += delta; 
		super.update(delta);
		
		if (thrust.len2() > 0) 
			setRotation(thrust.angle() - 90); //LibGdx render with rotation 0 from y-axis while Vector2 calculates angle from x-axis, thus -90.
	} 	
	
	/* 
	 * Maps the fuel left to engine efficiency. As a primary requirement it should tend to zero as fuel left tends to zero. also it should map 100 to 1; (perfect 
	 * efficiency. 
	 * 
	 */
	private float fuelEfficiencyMapping (float fuelLeft) {
		if (fuelLeft > 0)
			return fuelLeft/100; 
		else {
			System.out.println("fuel depleted"); 
			return 0;
		}
		
	}
	
}
