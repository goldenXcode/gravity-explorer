package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionRenderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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
	protected void renderResource(GraphicResource resource, 
			Rectangle drawArea,
			float originX, float originY, 
			float scale,
			float rotation) 
	{
		if (!(resource instanceof TextureRegionRenderable)) {
			System.out.println("Warning: could not draw resource of given type");
			return;
		}
		
		TextureRegionRenderable textureRegionResouce = (TextureRegionRenderable)resource;
		TextureRegion textureRegion = textureRegionResouce.getTextureRegion();
		
		spriteBatch.draw(textureRegion, 
			drawArea.x, 
			drawArea.y, 
			originX, 
			originY,
			drawArea.width, 
			drawArea.height, 
			scale, 
			scale, 
			rotation);
	}
}
