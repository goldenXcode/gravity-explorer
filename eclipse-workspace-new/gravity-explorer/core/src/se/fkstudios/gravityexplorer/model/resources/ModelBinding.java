package se.fkstudios.gravityexplorer.model.resources;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

public class ModelBinding extends GraphicResourceBinding {

	private ModelInstance modelInstance;
	private Array<PointLight> lightSources;
	private BoundingBox boundingBox;
	private Boolean boundingBoxSet;
	
	public ModelBinding(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, ModelInstance modelInstance) 
	{
		super(usingOwnerPosition, position, positionOffset, usingOwnerSize,
				width, height, visible, minRenderScale, maxRenderScale);
		
		this.modelInstance = modelInstance;
		lightSources = new Array<PointLight>();
		this.boundingBox = new BoundingBox();
		boundingBoxSet = false;
	}
	
	public ModelBinding(boolean usingOwnerPosition, Vector2 position,
			Vector2 positionOffset, boolean usingOwnerSize, float width,
			float height, boolean visible, float minRenderScale,
			float maxRenderScale, ModelInstance modelInstance, float lightIntencity) 
	{
		this(usingOwnerPosition, position, positionOffset, usingOwnerSize, width,
			 height, visible, minRenderScale, maxRenderScale, modelInstance);
		
		PointLight lightSource = new PointLight();
		lightSource.set(Color.YELLOW, 0, 0, Defs.PLANE_POSITION_Z, lightIntencity);
		setLightSources(lightSource);
	}
	
	public ModelInstance getModelInstance() {
		return modelInstance;
	}
	
	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}
	
	public Boolean isLightSource() {
		return lightSources.size > 0;
	}
	
	public Array<PointLight> getLightSources(float screenPeriodicityWidth, float screenPeriodicityHeight) {
		if (isLightSource()) {
			Vector2 centerScreenPosition = Utility.getScreenPosition(getPosition());
			Array<Vector2> perodicScreenPositions = Utility.calculatePerodicPositions(centerScreenPosition, screenPeriodicityWidth, screenPeriodicityHeight);
			
			for (int i = 0; i < perodicScreenPositions.size; i++) {
				PointLight lightSource = lightSources.get(i);
				Vector2 screenPosition = perodicScreenPositions.get(i);
				lightSource.position.x = screenPosition.x;
				lightSource.position.y = screenPosition.y;
			}
		}
		return lightSources;
	}
	
	public void setLightSources(PointLight copyFrom) {
		if (copyFrom == null) {
			clearLightSources();
			return;
		}
		
		if (lightSources.size == 0) {
			for (int i = 0; i < 9; i++) {
				lightSources.add(new PointLight().set(copyFrom));
			}
		}
	}
	
	public void clearLightSources() {
		lightSources.clear();
	}
	
	public BoundingBox getModelBoundingBox() {
		if (!boundingBoxSet) {
			modelInstance.model.calculateBoundingBox(boundingBox);
			boundingBoxSet = true;
		}
		return boundingBox;
	}
}
