package se.fkstudios.gravitynavigator.model;

import se.fkstudios.gravitynavigator.Defs;

import com.badlogic.gdx.math.Vector2;

/**
 * Simplest visible gameplay object associated with a texture.
 * @author kristofer
 */
public class TextureMapObjectModel extends MapObjectModel {
	
	private final String textureName;
	
	public float getRadius() {
		float result = (float) (Math.sqrt(Math.pow(getWidth(), 2) +Math.pow(getHeight(), 2)));
		return result; 
		
	}
	
	public TextureMapObjectModel(Vector2 position, 
			float width,
			float height, 
			Vector2 velocity,
			float rotation,
			int mass,
			String textureName) {
		super(position, width, height, velocity, rotation, mass, textureName);
		this.textureName = textureName;
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	// migrated stuff 
	private float distanceToParent; 
	
	public void setDistanceToParent (float distance) {
		distanceToParent = distance; 
	}
	
	public float getDistanceToParent () {
		return distanceToParent; 
	}
	
	private TextureMapObjectModel parentNode; 
	private TextureMapObjectModel[] childrenNodes; 
	
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
	
	public void update (float delta) {
		super.update(delta); 
		setVelocity(getVelocity().cpy().add(calculateOrbitCompensationalVelocity().cpy().scl(delta)));
	}
	
	private Vector2 calculateOrbitCompensationalVelocity() {
		if (getParentNode() != null) {
		float distance = PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).len();
		float targetDistance = getDistanceToParent();  
		float diff = targetDistance - distance; 
		float compFactor = Defs.ORBITAL_COMPENSATIONAL_FACTOR*diff; 
		return PhysicsEngine.shortestDistanceVector(getPosition(), getParentNode().getPosition()).nor().scl(compFactor); 
		}
		else {
			return new Vector2(0,0); 
		}
	}
		
	}
	
