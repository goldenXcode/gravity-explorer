package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a texture. Either a texture region in a TextureAtlas or a stand alone texture.
 * Use TextureLoader to obtain the texture in view.
 * 
 * @author kristofer
 */
public class TextureResource extends GraphicResource {

	public final String textureName;
	
	public TextureResource(Vector2 positionOffset, String textureName) {
		super(positionOffset);
		this.textureName = textureName;
	}
}
