package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.ModelResource;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class ModelRenderer extends PeriodicRenderer {

	private ModelBatch modelBatch;
	int counter = 0;
	
	public ModelRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		modelBatch = new ModelBatch();
	}

	public ModelBatch getModelBatch() {
		return modelBatch;
	}
	
	public void setModelBatch(ModelBatch modelBatch) {
		this.modelBatch = modelBatch;
	}
	
	public void updateToCamera(Camera camera) {
		modelBatch.setCamera(camera);
	}
	
	@Override
	protected void renderResource(GraphicResource resource, Rectangle drawArea,
			float originX, float originY, float scale, float rotation)
	{
		if (!(resource instanceof ModelResource)) {
			System.out.println("Warning: could not draw resource of given type");
			return;
		}
		
		ModelResource modelResource = (ModelResource)resource;
		ModelInstance modelInstance = modelResource.getModelInstance();
//		modelInstance.transform.rotate(0, 0, 1, rotation);

		modelInstance.transform = new Matrix4();
		modelInstance.transform.translate(drawArea.x + originX, drawArea.y + originY, 0);
//		modelInstance.transform.scale(scale, scale, scale);
		modelBatch.render(modelInstance);
	}	
}