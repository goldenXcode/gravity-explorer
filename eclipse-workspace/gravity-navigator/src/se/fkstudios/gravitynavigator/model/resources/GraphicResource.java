package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResource {
	
	
	public boolean useParentSize;
	public Vector2 positionOffset;
	public float width;
	public float height;
	public boolean visible;
	public float minRenderScale;
	public float maxRenderScale;
	
	public GraphicResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		this.minRenderScale = minRenderScale;
		this.maxRenderScale = maxRenderScale;
		width = -1;
		height = -1;
		useParentSize = true;
	}
	
	public GraphicResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		this.minRenderScale = minRenderScale;
		this.maxRenderScale = maxRenderScale;
		this.width = width;
		this.height = height;
		useParentSize = false;
	}
	
	public float getWidth(float parentWidth) {
		if (useParentSize)
			return parentWidth;
		else
			return width;
	}

	public float getHeight(float parentHeight) {
		if (useParentSize)
			return parentHeight;
		else
			return height;
	}
	
	public float getPositionX(float parentPositionX) {
		return parentPositionX + positionOffset.x;
	}
	
	public float getPositionY(float parentPositionY) {
		return parentPositionY + positionOffset.y;
	}
}
