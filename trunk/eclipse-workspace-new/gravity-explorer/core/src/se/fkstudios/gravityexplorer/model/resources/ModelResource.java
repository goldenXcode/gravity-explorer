package se.fkstudios.gravityexplorer.model.resources;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;

public class ModelResource extends GraphicResource {

	private ModelInstance modelInstance;
	private PointLight lightSource;
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
		lightSource = null;
		this.boundingBox = new BoundingBox();
		boundingBoxSet = false;
	}
	
	public ModelResource(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, ModelInstance modelInstance, float lightIntencity) 
	{
		this(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
			 height, visible, minRenderScale, maxRenderScale, modelInstance);
		lightSource = new PointLight();
		lightSource.set(Color.YELLOW, 0, 0, Defs.PLANE_POSITION_Z, lightIntencity);
		new DirectionalLight();
		
	}
	
	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}
	
	public Boolean isLightSource() {
		return lightSource != null;
	}
	
	public PointLight getLightSource(Vector2 ownerPosition) {
		if (isLightSource()) {
			Vector2 position = getPosition(ownerPosition);
			lightSource.position.x = Utility.getScreenCoordinate(position.x);
			lightSource.position.y = Utility.getScreenCoordinate(position.y);
			return lightSource;
		}
		return null;
	}
	
	public void setLigthSoruce(PointLight lightSource) {
		this.lightSource = lightSource;
	}
	
	public BoundingBox getModelBoundingBox() {
		if (!boundingBoxSet) {
			modelInstance.model.calculateBoundingBox(boundingBox);
			boundingBoxSet = true;
		}
		return boundingBox;
	}
}
