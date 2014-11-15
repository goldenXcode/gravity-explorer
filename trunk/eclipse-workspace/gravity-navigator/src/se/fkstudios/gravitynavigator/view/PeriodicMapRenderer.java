package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.Utility;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;
import se.fkstudios.gravitynavigator.model.resources.GraphicResource;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class PeriodicMapRenderer extends TextureRegionRenderer {
	
	private Rectangle drawArea;
	
	private ShapeRenderer shapeRenderer;

	private SpriteBatch consoleSpriteBatch;
	private BitmapFont consolFont;
	private String consoleText; 
	
	public PeriodicMapRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		drawArea = new Rectangle();
		consoleSpriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		consolFont = new BitmapFont();
		setConsoleText("Welcome!");
	}

	public void setConsoleText(String text ) {
		consoleText = text; 
	}
	
	public void render(PeriodicMapModel map, GameplayCamera camera) {
		for (GraphicResource resource : map.getResources()) {
			if (resource instanceof TextureRegionResource) {				
				float proportionalPositionX = camera.position.x / Utility.getScreenCoordinate(map.getWidth());
				float proportionalPositionY = camera.position.y / Utility.getScreenCoordinate(map.getHeight());
				float textureOriginX = Utility.getScreenCoordinate(resource.width) / 2;
				float textureOriginY = Utility.getScreenCoordinate(resource.height) / 2;
				float texScreenWidth = Utility.getScreenCoordinate(resource.width);
				float texScreenHeight = Utility.getScreenCoordinate(resource.height);
				float longestTexSide = Math.max(texScreenWidth, texScreenHeight);
				float longestViewportSide = Math.max(camera.viewportWidth, camera.viewportHeight);
				float ratio = 1 - longestViewportSide / longestTexSide;
				
				setPeriodicityWidth(texScreenWidth * camera.zoom);
				setPeriodicityHeight(texScreenHeight * camera.zoom);
				
				drawArea.x = camera.position.x - textureOriginX - proportionalPositionX * getPeriodicityWidth() * ratio;
				drawArea.y = camera.position.y - textureOriginY - proportionalPositionY * getPeriodicityHeight() * ratio;
				drawArea.width = Utility.getScreenCoordinate(resource.width);
				drawArea.height = Utility.getScreenCoordinate(resource.height);
				
				renderResourcePeriodically(resource, drawArea, 0f, camera);			
			}
		}
		
		if (RenderOptions.getInstance().debugRender) {
			drawConsole();
			debugRender(map);
		}
	}
	
	private void debugRender(PeriodicMapModel map) {
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
	
	public void setProjectionMatrix(Matrix4 projectionMatrix) {
		super.setProjectionMatrix(projectionMatrix);
		shapeRenderer.setProjectionMatrix(projectionMatrix);
	}

	private void drawConsole() {
		consoleSpriteBatch.begin();
		consolFont.draw(consoleSpriteBatch, consoleText, 20, 20);
		consoleSpriteBatch.end();
	}
}
