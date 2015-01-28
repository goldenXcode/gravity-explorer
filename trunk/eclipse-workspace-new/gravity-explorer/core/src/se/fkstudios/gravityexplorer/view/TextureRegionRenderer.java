package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.GraphicResourceBinding;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionBinding;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

	public class TextureRegionRenderer extends PeriodicRenderer {

	public SpriteBatch spriteBatch;
	
	public TextureRegionRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		this.spriteBatch = new SpriteBatch();
	}

	public void updateToCamera(Camera camera) {
		spriteBatch.setProjectionMatrix(camera.combined);
	}

	@Override
	public void renderResource(GraphicResourceBinding resource, 
		Vector2 screenPosition,
		float screenWidth, float screenHeight,
		Vector2 screenOrigin,
		float rotation)
	{
		if (!(resource instanceof TextureRegionBinding)) {
			System.out.println("Error: could not draw resource of given type");
			return;
		}
		
		TextureRegionBinding textureRegionResouce = (TextureRegionBinding)resource;
		TextureRegion textureRegion = textureRegionResouce.getTextureRegion();
		
		spriteBatch.draw(textureRegion, 
			screenPosition.x - screenWidth / 2f, 
			screenPosition.y - screenHeight / 2f, 
			screenOrigin.x , 
			screenOrigin.y,
			screenWidth, 
			screenHeight, 
			1f, 
			1f, 
			rotation);	
	}
}
