package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.resources.ColorResource;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class ColorRenderer extends PeriodicResourceRenderer {
	
	private ShapeRenderer shapeRenderer;
	
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
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.rect(drawArea.x, drawArea.y, drawArea.width * scale, drawArea.height * scale, originX, originY, rotation);
		shapeRenderer.end();
	}
}
