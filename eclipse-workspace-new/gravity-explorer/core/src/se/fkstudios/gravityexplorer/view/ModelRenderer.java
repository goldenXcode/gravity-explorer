package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.ModelResource;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

public class ModelRenderer extends PeriodicRenderer {

	private ModelBatch modelBatch;
	private Environment environment;
	
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
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
	public void updateToCamera(Camera camera) {
		modelBatch.setCamera(camera);
	}
	
	@Override
	public void renderResource(GraphicResource resource, 
		Vector2 screenPosition,
		float screenWidth, float screenHeight,
		Vector2 screenOrigin,
		float rotation)
	{
		if (!(resource instanceof ModelResource)) {
			System.out.println("Error: could not draw resource of given type");
			return;
		}

		ModelResource modelResource = (ModelResource)resource;
		
		BoundingBox boundingBox = modelResource.getModelBoundingBox();
		
		float scaleX = screenWidth / boundingBox.getWidth();
		float scaleY = screenHeight / boundingBox.getHeight();
		float scaleZ = ((screenWidth + screenHeight) / 2f) / boundingBox.getDepth();
		
		ModelInstance modelInstance = modelResource.getModelInstance();
		
		modelInstance.transform = new Matrix4();
		modelInstance.transform.translate(screenPosition.x, screenPosition.y, Defs.PLANE_POSITION_Z);
		modelInstance.transform.rotate(0f, 0f, 1f, rotation);
		modelInstance.transform.scale(scaleX, scaleY, scaleZ);
		
		if (environment != null) {
			modelBatch.render(modelInstance, environment);
		}
		else {
			modelBatch.render(modelInstance);
		}
	}	
}