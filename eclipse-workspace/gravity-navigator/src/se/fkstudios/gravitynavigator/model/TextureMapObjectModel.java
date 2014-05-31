package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Simplest visible gameplay object associated with a texture.
 * @author kristofer
 */
public class TextureMapObjectModel extends MapObjectModel {
	
	private final String textureName;
	
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
}
