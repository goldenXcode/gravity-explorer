package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class PeriodicMapRenderer extends TextureRegionRenderer {
	
	/* console rendering */
	private SpriteBatch consoleSpriteBatch;
	private BitmapFont consolFont;
	private String consoleText; 
	
	public PeriodicMapRenderer(SpriteBatch spriteBatch,
			float periodicityWidth,
			float periodicityHeight) {
		super(spriteBatch, periodicityWidth, periodicityHeight);
		this.consoleSpriteBatch = new SpriteBatch();
		this.consolFont = new BitmapFont();
		setConsoleText("Welcome!");
	}

	public void setConsoleText(String text ) {
		consoleText = text; 
	}
	
	public void render(PeriodicMapModel map, GameplayCamera camera) {
		for (TextureRegionResource resource : map.getResources()) {
			if (resource.getClass() == TextureResource.class) {
				float proportionalPositionX = camera.position.x / Utility.getScreenCoordinate(map.getWidth());
				float proportionalPositionY = camera.position.y / Utility.getScreenCoordinate(map.getHeight());
				float textureOriginX = Utility.getScreenCoordinate(resource.width) / 2;
				float textureOriginY = Utility.getScreenCoordinate(resource.height) / 2;
				float texScreenWidth = Utility.getScreenCoordinate(resource.width);
				float texScreenHeight = Utility.getScreenCoordinate(resource.height);
				float longestTexSide = Math.max(texScreenWidth, texScreenHeight);
				float longestViewportSide = Math.max(camera.viewportWidth, camera.viewportHeight);
				float ratio = 1 - longestViewportSide / longestTexSide;
				
				periodicityWidth = texScreenWidth * camera.zoom;
				periodicityHeight = texScreenHeight * camera.zoom; 
				
				render(resource, 
						camera.position.x - textureOriginX - proportionalPositionX * periodicityWidth * ratio, 
						camera.position.y - textureOriginY - proportionalPositionY * periodicityHeight * ratio, 
						Utility.getScreenCoordinate(resource.width),
						Utility.getScreenCoordinate(resource.height), 
						0f, 
						camera);
			}
		}
		
		if (RenderOptions.getInstance().debugRender) {
			drawConsole();
			debugRender(spriteBatch.getProjectionMatrix(), map);
		}
	}
	
	private void debugRender(Matrix4 projectionMatrix, PeriodicMapModel map) {
		shapeRenderer.setProjectionMatrix(projectionMatrix);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Defs.MAP_BORDER_COLOR);

		shapeRenderer.rect(0, 0, map.getWidth() * Defs.PIXELS_PER_UNIT,
				map.getHeight() * Defs.PIXELS_PER_UNIT);

		for (int i = 0; i < (int) map.getWidth(); i += Defs.UNIT_PER_DEBUG_LINE) {
			shapeRenderer.line(i * Defs.PIXELS_PER_UNIT, 
					0, 
					i * Defs.PIXELS_PER_UNIT, 
					(int) map.getHeight() * Defs.PIXELS_PER_UNIT);
		}

		for (int i = 0; i < (int) map.getHeight(); i += Defs.UNIT_PER_DEBUG_LINE) {
			shapeRenderer.line(0, 
					i * Defs.PIXELS_PER_UNIT,
					(int) map.getWidth() * Defs.PIXELS_PER_UNIT, 
					i * Defs.PIXELS_PER_UNIT);
		}

		shapeRenderer.end();
	}
	
	private void drawConsole() {
        consoleSpriteBatch.begin();
        consolFont.draw(consoleSpriteBatch, consoleText, 20, 20);
        consoleSpriteBatch.end();
	}
}
