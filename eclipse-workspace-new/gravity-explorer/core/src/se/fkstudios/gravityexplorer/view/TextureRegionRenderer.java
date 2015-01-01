package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionResource;

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
	public void renderResource(GraphicResource resource, 
		Vector2 screenPosition,
		float screenWidth, float screenHeight,
		Vector2 screenOrigin,
		float rotation)
	{
		if (!(resource instanceof TextureRegionResource)) {
			System.out.println("Error: could not draw resource of given type");
			return;
		}
		
		TextureRegionResource textureRegionResouce = (TextureRegionResource)resource;
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
