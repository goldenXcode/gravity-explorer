package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class AnimationRenderer extends Renderer {
	
	public AnimationRenderer(ShapeRenderer shapeRenderer,
			SpriteBatch spriteBatch, 
			float mapWidth, 
			float mapHeight) {
		super(shapeRenderer, spriteBatch, mapWidth, mapHeight);
	}

	@Override
	protected TextureRegion getTextureRegion(GraphicResource resource) {
		AnimationResource animationResource = (AnimationResource)resource;
		Animation animation = TextureLoader.getInstance().getAnimation(animationResource.animationName);
		return animation.getKeyFrame(animationResource.stateTime, animationResource.looping);
	}
}
