package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResource {

	public boolean useParentSize;
	
	public Vector2 positionOffset;
	public float width;
	public float height;
	
	public GraphicResource(Vector2 positionOffset) {
		this.positionOffset = positionOffset;
		width = -1;
		height = -1;
		useParentSize = true;
	}
	
	public GraphicResource(Vector2 positionOffset, float width, float height) {
		this.positionOffset = positionOffset;
		this.width = width;
		this.height = height;
		this.useParentSize = false;
	}
}
