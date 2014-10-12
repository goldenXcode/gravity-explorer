package se.fkstudios.gravitynavigator.model.resources;

import se.fkstudios.gravitynavigator.view.TextureLoader;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a animation. That being a series of TextureRegions that are to be rendered.
 * 
 * @author kristofer
 */
public class AnimationResource extends TextureRegionResource {
	
	public final String animationName;
	public final String[] textureNames;
	public boolean looping;
	public float stateTime;
	
	public AnimationResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, String animationName, String[] textureNames, boolean looping) {
		super(positionOffset, visible, minRenderScale, maxRenderScale);
		this.animationName = animationName;
		this.textureNames = textureNames;
		this.looping = looping;
		stateTime = 0f; 
	}
	
	public AnimationResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height, String animationName, String[] textureNames, boolean looping) {
		super(positionOffset, visible, minRenderScale, maxRenderScale, width, height);
		this.animationName = animationName;
		this.textureNames = textureNames;
		this.looping = looping;
		stateTime = 0f;
	}

	@Override
	public TextureRegion getTextureRegion() {
		Animation animation = TextureLoader.getInstance().getAnimation(animationName);
		return animation.getKeyFrame(stateTime, looping);
	}
}
