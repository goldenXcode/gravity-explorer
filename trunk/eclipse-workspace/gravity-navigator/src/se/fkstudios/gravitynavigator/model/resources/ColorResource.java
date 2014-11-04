package se.fkstudios.gravitynavigator.model.resources;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ColorResource extends GraphicResource {

	public Color color;
	
	public ColorResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, Color color) {
		super(positionOffset, visible, minRenderScale, maxRenderScale);
		this.color = color;
	}

	public ColorResource(Vector2 positionOffset, boolean visible, float minRenderScale, float maxRenderScale, float width, float height, Color color) {
		super(positionOffset, visible, minRenderScale, maxRenderScale, width, height);
		this.color = color;
	}	
}
