package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Simplest visible gameplay object associated with a texture.
 * @author kristofer
 */
public class SimpleMapObject extends GameplayMapObject {
	
	private final String textureName;
	
	public SimpleMapObject(Vector2 position, 
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
}
