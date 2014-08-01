package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.view.RenderOptions;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

/**
 * Modeling the properties of all gameplay map objects in game.
 * @author kristofer
 */
public abstract class MapObjectModel extends MapObject {

	private float width;
	private float height;
	private Vector2 position;
	private Vector2 acceleration;
	private Vector2 velocity;
	float rotation;
	private int mass; // in kg
	private float rotationalSpeed; // in degrees per second
	
	private float distanceToParent; 
	private TextureMapObjectModel parentNode; 
	private TextureMapObjectModel[] childrenNodes; 
	
	public MapObjectModel(Vector2 position, 
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
		this.acceleration = new Vector2(0, 0);
		this.rotationalSpeed = 0; 
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
	
	public void setDistanceToParent (float distance) {
		distanceToParent = distance; 
	}
	
	public float getDistanceToParent () {
		return distanceToParent; 
	}
	
	public TextureMapObjectModel getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(TextureMapObjectModel model) {
		parentNode = model; 
	}
	
	public TextureMapObjectModel[] getChildrenNodes() {
		return childrenNodes; 
	}
	
	public void setChildrenNodes(TextureMapObjectModel[] children) {
		childrenNodes = children; 
	}
	
	/**
	 * Update the object for next game loop iteration.
	 * @param delta Time in seconds since last update call.
	 */
	public void update(float delta) {
		Vector2 newVelocity = getVelocity().cpy();
		newVelocity.add(acceleration.cpy().scl(delta));
		newVelocity.add(calculateOrbitCompensationalVelocity().scl(delta));
		
		setVelocity(newVelocity);
		setPosition(position.cpy().add(velocity.cpy().scl(delta)));
		setRotation(((getRotation() + getRotationalSpeed()*delta) % 360f)); 
	}
		
	private Vector2 calculateOrbitCompensationalVelocity() {
		if (getParentNode() != null) {
			float distance = PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).len();
			float targetDistance = getDistanceToParent();  
			float diff = targetDistance - distance; 
			
			if (RenderOptions.getInstance().debugRender && (Math.abs(diff) > Defs.TOLERATED_ORBITAL_DEVIATION))
				System.out.println("asteroids deviating too much from their orbit. Consider adjusting ORBITAL_COMPENSATIONAL_FACTOR"); 
			
			TextureMapObjectModel planet = getParentNode(); 
			float compFactor = Defs.ORBITAL_COMPENSATIONAL_FACTOR*diff*PhysicsEngine.computeAcceleration(this, planet).len(); 
			return PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).nor().scl(compFactor); 
		}
		else {
			return new Vector2(0,0); 
		}
	}
}
