package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a animation. That being a series of TextureRegions that are to be rendered.
 * 
 * @author kristofer
 */
public class AnimationResource extends GraphicResource {
	
	public final String animationName;
	public final String[] textureNames;
	public boolean looping;
	public float stateTime;
	
	public AnimationResource(Vector2 positionOffset, String animationName, String[] textureNames, boolean looping) {
		super(positionOffset);
		this.animationName = animationName;
		this.textureNames = textureNames;
		this.looping = looping;
		stateTime = 0f; 
	}
	
	public AnimationResource(Vector2 positionOffset, float width, float height, String animationName, String[] textureNames, boolean looping) {
		super(positionOffset, width, height);
		this.animationName = animationName;
		this.textureNames = textureNames;
		this.looping = looping;
		stateTime = 0f;
	}
}
