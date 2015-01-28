package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.ColorBinding;
import se.fkstudios.gravityexplorer.model.resources.GraphicResourceBinding;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class ColorRenderer extends PeriodicRenderer {
	
	public ShapeRenderer shapeRenderer;
	
	public ColorRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		shapeRenderer = new ShapeRenderer();
	}

	public void updateToCamera(Camera camera) {
		shapeRenderer.setProjectionMatrix(camera.combined);
	}
	
	@Override
	public void renderResource(GraphicResourceBinding resource, 
		Vector2 screenPosition,
		float screenWidth, float screenHeight,
		Vector2 screenOrigin,
		float rotation)
	{
		if (!(resource instanceof ColorBinding)) {
			System.out.println("Warning: could not draw resource of given type");
			return;
		}
		
		ColorBinding colorResource = (ColorBinding)resource;
		
		shapeRenderer.setColor(colorResource.getColor());
		
		shapeRenderer.rect(screenPosition.x - screenWidth / 2,
				screenPosition.y - screenHeight / 2, 
				screenOrigin.x, screenOrigin.y, 
				screenWidth, 
				screenHeight, 
				1f, 
				1f, 
				rotation);
	}
}
