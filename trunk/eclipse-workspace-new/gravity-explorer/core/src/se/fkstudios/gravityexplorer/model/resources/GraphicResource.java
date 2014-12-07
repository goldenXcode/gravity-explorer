package se.fkstudios.gravityexplorer.model.resources;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResource {
	
	private boolean usingOwnerPosition;
	private Vector2 position;
	private Vector2 positionOffset;

	private boolean usingOwnerSize;
	private float width;
	private float height;
	
	private boolean visible;
	private float minRenderScale;
	private float maxRenderScale;
	
	public GraphicResource(boolean usingOwnerPosition, Vector2 position, Vector2 positionOffset, 
			boolean usingOwnerSize, float width, float height,
			boolean visible, float minRenderScale, float maxRenderScale) 
	{
		this.usingOwnerPosition = usingOwnerPosition;
		this.position = position;
		this.positionOffset = positionOffset;
		this.usingOwnerSize = usingOwnerSize;
		this.width = width;
		this.height = height;
		this.visible = visible;
		this.minRenderScale = minRenderScale;
		this.maxRenderScale = maxRenderScale;
	}
	
	public boolean isUsingOwnerPosition() {
		return usingOwnerPosition;
	}

	public void setUsingOwnerPosition(boolean usingOwnerPosition) {
		this.usingOwnerPosition = usingOwnerPosition;
	}

	public Vector2 getPositionOffset() {
		return positionOffset;
	}

	public void setPositionOffset(Vector2 positionOffset) {
		setPositionOffset(positionOffset.x, positionOffset.y);
	}
	
	public void setPositionOffset(float positionOffsetX, float positionOffsetY) {
		this.positionOffset.x = positionOffsetX;
		this.positionOffset.y = positionOffsetY;
	}

	public boolean isUsingOwnerSize() {
		return usingOwnerSize;
	}

	public void setUsingOwnerSize(boolean ownerSize) {
		this.usingOwnerSize = ownerSize;
	}

	public float getWidth(float ownerWidth) {
		if (isUsingOwnerSize())
			return ownerWidth;
		else
			return width;
	}

	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
		setUsingOwnerSize(false);
	}	

	public float getHeight(float parentHeight) {
		if (isUsingOwnerSize())
			return parentHeight;
		else
			return height;
	}

	public void setHeight(float height) {
		this.height = height;
		setUsingOwnerSize(false);
	}

	public float getHeight() {
		return height;
	}
	
	private Vector2 combinedPosition1 = new Vector2();
	public Vector2 getPosition(Vector2 ownerPosition) {
		if (isUsingOwnerPosition())	{
			combinedPosition1.x = ownerPosition.x + positionOffset.x;
			combinedPosition1.y = ownerPosition.y + positionOffset.y;
		}
		else {

			combinedPosition1.x = position.x + positionOffset.x;
			combinedPosition1.y = position.y + positionOffset.y;
		}
		return combinedPosition1;
	}

	private Vector2 combinedPosition2 = new Vector2();
	public Vector2 getPosition() {	
		combinedPosition2.x = position.x + positionOffset.x;
		combinedPosition2.y = position.y + positionOffset.y;
		return combinedPosition2;
	}
	
	public void setPosition(Vector2 position) {
		setPosition(position.x, position.y);
	}	
	
	public void setPosition(float positionX, float positionY) {
		this.position.x = positionX;
		this.position.y = positionY;
		setUsingOwnerPosition(false);
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public float getMinRenderScale() {
		return minRenderScale;
	}

	public void setMinRenderScale(float minRenderScale) {
		this.minRenderScale = minRenderScale;
	}

	public float getMaxRenderScale() {
		return maxRenderScale;
	}

	public void setMaxRenderScale(float maxRenderScale) {
		this.maxRenderScale = maxRenderScale;
	}
}
