package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Modeling the properties of all gameplay map objects in game.
 * @author kristofer
 */
public class MapObjectModel extends MapObject {
//
	
	private float width;
	private float height;
	private Vector2 position;
	private Vector2 acceleration;
	private Vector2 velocity;
	private float rotation;
	private int mass; // in kg
	private float rotationalSpeed; // in degrees per second
	private boolean selfStabilizing; 
	
	private Array<GraphicResource> resources;
	
	private float distanceToParent; 
	private MapObjectModel parentNode; 
	private MapObjectModel[] childrenNodes; 

	public final float minRenderScale;
	
	public MapObjectModel(Vector2 position, 
			float width,
			float height, 
			Vector2 velocity,
			float rotation,
			int mass,
			Array<GraphicResource> resources,
			float minRenderScale) {
		
		this.position = position;
		this.width = width;
		this.height = height;
		this.velocity = velocity;
		this.rotation = rotation;
		this.mass = mass;
		this.resources = resources;
		this.acceleration = new Vector2(0, 0);
		this.rotationalSpeed = 0; 
		this.minRenderScale = minRenderScale;
		this.selfStabilizing = true; 
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
	
	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
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
	
	public Array<GraphicResource> getResources() {
		return resources;
	}
	
	public void setDistanceToParent (float distance) {
		distanceToParent = distance; 
	}
	
	public float getDistanceToParent () {
		return distanceToParent; 
	}
	
	public MapObjectModel getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(MapObjectModel model) {
		parentNode = model; 
	}
	
	public MapObjectModel[] getChildrenNodes() {
		return childrenNodes; 
	}
	
	public void setChildrenNodes(MapObjectModel[] children) {
		childrenNodes = children; 
	}
	
	public boolean isSelfStabilizing() {
		return selfStabilizing;
	}
	
	public void setSelfStabilizing(boolean value) {
		selfStabilizing = value;
	}
	
	/**
	 * Update the object for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		//add current acceleration
		Vector2 newVelocity = getVelocity().cpy();
		newVelocity.add(acceleration.cpy().scl(delta));
		
		//adjusting velocity to get into orbit if object is self stabilizing. 
		if (selfStabilizing) {
			Vector2 compVel = calculateOrbitCompensationalVelocity().scl(delta);
			newVelocity.add(compVel);
			// to uphold newtons laws need to give the parent-planet a proportional force when correcting for gravity
			if (getParentNode() != null) {
				Vector2 force = compVel.scl(getParentNode().getMass());
				Vector2 compVel2 = force.scl(delta/getParentNode().getMass());
				getParentNode().setVelocity(getParentNode().getVelocity().add(compVel2));
			}
		}
		
		//setts the new velocity
		setVelocity(newVelocity);
		
		//update position according to velocity and delta.
		setPosition(position.cpy().add(velocity.cpy().scl(delta)));
		
		setRotation(((getRotation() + getRotationalSpeed()*delta) % 360f));
		
		//update stateTime for any animation resources. (kind of breaking design pattern... bad but keep till it is a problem).
		for(GraphicResource resource : getResources()) {
			if (resource.getClass() == AnimationResource.class) {
				((AnimationResource)resource).stateTime += delta;
			}
		}
	}
	
	private Vector2 calculateOrbitCompensationalVelocity () {
		MapObjectModel planet = PhysicsEngine.excertsGreatestForce(this); 
		// first we'll need a unit vector parallel to it's circular orbit 
		Vector2 positionDiff = PhysicsEngine.shortestDistanceVector(planet.getPosition().cpy(),this.getPosition().cpy());
		Vector2 velocityDiff = planet.getVelocity().cpy().sub(this.getVelocity().cpy()); 
		Vector2 tangentialVector = positionDiff.cpy().rotate(90).nor(); 
		boolean clockwise; 
		if (velocityDiff.dot(tangentialVector) > 0) {
			clockwise = false; 
		}
		else {
			clockwise = true; 
		}
		
		float targetSpeed = RandomMapGenerator.calculateOrbitingVelocity(positionDiff.len(), planet.getMass()); 
		float currentSpeed = Math.abs(velocityDiff.cpy().dot(tangentialVector)); 
		
		
		boolean accelerating; 
		if (targetSpeed > currentSpeed) {
			accelerating = true; 
		}
		else {
			accelerating = false; 
		}
		
		Vector2 resultVector = tangentialVector.scl(Math.abs(targetSpeed-currentSpeed)*Defs.ORBITAL_COMPENSATIONAL_FACTOR2); 
		if (!accelerating) {
			resultVector.scl(-1); 
		}
		if (!clockwise) {
			resultVector.scl(-1); 
		}
		
		return resultVector; 
	}
	
	//KC: användes inte så tog kommenterade bort, fredrik får avgöra om vi ska slänga den helt!
//	private Vector2 calculateOrbitCompensationalVelocity() {
//		if (getParentNode() != null) {
//			float distance = PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).len();
//			float targetDistance = getDistanceToParent();  
//			float diff = targetDistance - distance; 
//			
//			if (RenderOptions.getInstance().debugRender && (Math.abs(diff) > Defs.TOLERATED_ORBITAL_DEVIATION))
//				System.out.println("asteroids deviating too much from their orbit. Consider adjusting ORBITAL_COMPENSATIONAL_FACTOR"); 
//			
//			MapObjectModel planet = getParentNode(); 
//			float compFactor = Defs.ORBITAL_COMPENSATIONAL_FACTOR*diff*PhysicsEngine.computeAcceleration(this, planet).len(); 
//			return PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).nor().scl(compFactor); 
//		}
//		else {
//			return new Vector2(0,0); 
//		}
//	}
}
	

	
		

	

