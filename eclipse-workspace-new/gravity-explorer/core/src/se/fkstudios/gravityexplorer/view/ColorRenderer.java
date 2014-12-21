package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.ColorResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class ColorRenderer extends PeriodicResourceRenderer {
	
	public ShapeRenderer shapeRenderer;
	
	public ColorRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		shapeRenderer = new ShapeRenderer();
	}

	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		shapeRenderer.setProjectionMatrix(projectionMatrix);
	}
	
	@Override
	protected void renderResource(GraphicResource resource, 
			Rectangle drawArea,
			float originX, 
			float originY, 
			float scale, 
			float rotation) 
	{
		if (!(resource instanceof ColorResource)) {
			System.out.println("Warning: could not draw resource of given type");
			return;
		}
		ColorResource colorResource = (ColorResource)resource;
		shapeRenderer.setColor(colorResource.getColor());
		shapeRenderer.rect(drawArea.x, drawArea.y, originX, originY, drawArea.width, drawArea.height, scale, scale, rotation);
	}
}
