package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;

public class ModelResource extends GraphicResource {

	private ModelInstance modelInstance;
	
	public ModelResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, ModelInstance modelInstance) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize,
				width, height, visible, minRenderScale, maxRenderScale);
		
		this.modelInstance = modelInstance;
	}
	
	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}
}
