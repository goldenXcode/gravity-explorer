package se.fkstudios.gravityexplorer.model.resources;

import se.fkstudios.gravityexplorer.model.MapObjectModel;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResourceBinding {
	
	private MapObjectModel owner;
	
	private boolean usingOwnerPosition;
	private Vector2 position;
	private Vector2 positionOffset;

	private boolean usingOwnerSize;
	private float width;
	private float height;
	
	private boolean visible;
	private float minRenderScale;
	private float maxRenderScale;

	public GraphicResourceBinding(boolean usingOwnerPosition, Vector2 position, Vector2 positionOffset, 
			boolean usingOwnerSize, float width, float height,
			boolean visible, float minRenderScale, float maxRenderScale) 
	{
		this.owner = null;
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
	
	public GraphicResourceBinding(MapObjectModel owner, boolean usingOwnerPosition, Vector2 position, Vector2 positionOffset, 
			boolean usingOwnerSize, float width, float height,
			boolean visible, float minRenderScale, float maxRenderScale)
	{
		this(usingOwnerPosition, position, positionOffset, usingOwnerSize, width, height, visible, minRenderScale, maxRenderScale);
		this.owner = owner;
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

	public float getWidth() {
		if (isUsingOwnerSize())
			return owner.getWidth();
		else
			return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
		setUsingOwnerSize(false);
	}	

	public float getHeight() {
		if (isUsingOwnerSize())
			return owner.getHeight();
		else
			return height;
	}

	public void setHeight(float height) {
		this.height = height;
		setUsingOwnerSize(false);
	}
	
	private Vector2 gp_result = new Vector2();
	public Vector2 getPosition() {
		if (isUsingOwnerPosition())	{
			gp_result.x = owner.getPosition().x + positionOffset.x;
			gp_result.y = owner.getPosition().y + positionOffset.y;
		}
		else {
			gp_result.x = position.x + positionOffset.x;
			gp_result.y = position.y + positionOffset.y;
		}
		return gp_result;
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
	
	public MapObjectModel getOwner() {
		return owner;
	}
	
	public void setOwner(MapObjectModel owner) {
		this.owner = owner;
	}
}
