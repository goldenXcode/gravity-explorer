package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.math.Vector2;

public abstract class GraphicResource {

	public Vector2 positionOffset;
	
	public GraphicResource(Vector2 positionOffset) {
		this.positionOffset = positionOffset;
	}
}
