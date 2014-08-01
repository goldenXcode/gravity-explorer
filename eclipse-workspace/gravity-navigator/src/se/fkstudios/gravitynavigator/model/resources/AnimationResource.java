package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a animation. That being a series of TextureRegions that are to be rendered.
 * 
 * @author kristofer
 */
public class AnimationResource extends GraphicResource {
	
	public final String[] textureNames;
	
	public AnimationResource(Vector2 positionOffset, String[] textureNames) {
		super(positionOffset);
		this.textureNames = textureNames;
	}
}
