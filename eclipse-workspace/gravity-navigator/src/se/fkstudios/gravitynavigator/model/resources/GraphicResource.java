package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResource {

	public boolean useParentSize;
	
	public Vector2 positionOffset;
	public float width;
	public float height;
	public boolean visible;
	
	public GraphicResource(Vector2 positionOffset, boolean visible) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		width = -1;
		height = -1;
		useParentSize = true;
	}
	
	public GraphicResource(Vector2 positionOffset, boolean visible, float width, float height) {
		this.positionOffset = positionOffset;
		this.visible = visible;
		this.width = width;
		this.height = height;
		this.useParentSize = false;
	}
}
