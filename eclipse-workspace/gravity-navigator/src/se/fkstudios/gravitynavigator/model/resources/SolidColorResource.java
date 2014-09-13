package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class SolidColorResource extends GraphicResource {
	
	public final Color color;
	
	public SolidColorResource(Vector2 positionOffset, boolean visible, Color color) {
		super(positionOffset, visible);
		this.color = color;
	}
}