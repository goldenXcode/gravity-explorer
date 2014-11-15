package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Models a dependency to a animation. That being a series of TextureRegions that are to be rendered.
 * 
 * @author kristofer
 */
public class AnimationResource extends GraphicResource implements TextureRegionResource {
	
	public final String animationName;
	public final String[] textureNames;
	private boolean looping;
	private float stateTime;
	
	public AnimationResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, String animationName, String[] textureNames, boolean looping) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
				height, visible, minRenderScale, maxRenderScale);

		this.animationName = animationName;
		this.textureNames = textureNames;
		this.looping = looping;
		this.stateTime = 0;
	}	
	
	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

	public void incStateTime(float delta) {
		stateTime = stateTime + delta;
	}
	
	@Override
	public TextureRegion getTextureRegion() {
		Animation animation = TextureLoader.getInstance().getAnimation(animationName);
		return animation.getKeyFrame(stateTime, looping);
	}
}
