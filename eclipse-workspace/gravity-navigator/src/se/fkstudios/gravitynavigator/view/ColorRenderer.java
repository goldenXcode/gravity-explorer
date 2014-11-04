package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.MapObjectModel;
import se.fkstudios.gravitynavigator.model.resources.ColorResource;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class ColorRenderer {
	
	protected ShapeRenderer shapeRenderer;
	protected float periodicityWidth;
	protected float periodicityHeight;
	
	public ColorRenderer(float periodicityWidth, float periodicityHeight) {
		shapeRenderer = new ShapeRenderer();
		this.periodicityWidth = periodicityWidth;
		this.periodicityHeight = periodicityHeight;
	}
	
	public void render(GameplayCamera camera,
			MapObjectModel mapObject,
			ColorResource resource) {
		
		float width;
		float height;
		if (resource.useParentSize) {
			width = mapObject.getWidth();
			height = mapObject.getHeight();
		}
		else {
			width = resource.width;
			height = resource.height;
		}
		
		float screenPositionX = Utility.getScreenCoordinate(mapObject.getPosition().x - width / 2 + resource.positionOffset.x);
		float screenPositionY = Utility.getScreenCoordinate(mapObject.getPosition().y - height / 2 + resource.positionOffset.y);
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		
		shapeRenderer.begin(ShapeType.Filled);
//		shapeRenderer.rect(drawArea.x, drawArea.y, drawArea.width, drawArea.height);
		
		shapeRenderer.rect(screenPositionX, screenPositionY, width, height);
		shapeRenderer.end();
		
	
	}
}
