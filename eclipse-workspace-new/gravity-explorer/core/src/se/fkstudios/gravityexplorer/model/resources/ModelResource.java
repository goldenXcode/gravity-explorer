package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

public class ModelResource extends GraphicResource {

	private ModelInstance modelInstance;
	private BoundingBox boundingBox;
	private Boolean boundingBoxSet;
	
	public ModelResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, ModelInstance modelInstance) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize,
				width, height, visible, minRenderScale, maxRenderScale);
		
		this.modelInstance = modelInstance;
		this.boundingBox = new BoundingBox();
		boundingBoxSet = false;
	}
	
	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}
	
	public BoundingBox getModelBoundingBox() {
		if (!boundingBoxSet) {
			modelInstance.model.calculateBoundingBox(boundingBox);
			boundingBoxSet = true;
		}
		return boundingBox;
	}
}
