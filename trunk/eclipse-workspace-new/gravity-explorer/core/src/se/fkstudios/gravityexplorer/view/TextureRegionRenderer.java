package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionResource;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

	public class TextureRegionRenderer extends PeriodicResourceRenderer {

	public SpriteBatch spriteBatch;
	
	public TextureRegionRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		this.spriteBatch = new SpriteBatch();
	}

	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		spriteBatch.setProjectionMatrix(projectionMatrix);
	}
	
	@Override
	protected void renderResource(GraphicResource resource, 
			Rectangle drawArea,
			float originX, float originY, 
			float scale,
			float rotation) 
	{
		if (!(resource instanceof TextureRegionResource)) {
			System.out.println("Warning: could not draw resource of given type");
			return;
		}
		
		TextureRegionResource textureRegionResouce = (TextureRegionResource)resource;
		TextureRegion textureRegion = textureRegionResouce.getTextureRegion();
		
//		spriteBatch.begin();
		
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
		
//		spriteBatch.end();
	}
}
