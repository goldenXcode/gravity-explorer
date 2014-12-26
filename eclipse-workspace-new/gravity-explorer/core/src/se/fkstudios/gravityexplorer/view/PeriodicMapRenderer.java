package se.fkstudios.gravityexplorer.view;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.controller.GameplayCamera;
import se.fkstudios.gravityexplorer.model.PeriodicMapModel;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionRenderable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class PeriodicMapRenderer extends TextureRegionRenderer {
	
	private ShapeRenderer shapeRenderer;

	private SpriteBatch consoleSpriteBatch;
	private BitmapFont consolFont;
	private String consoleText; 
	
	private Vector2 screenPosition;
	private Vector2 screenOrigin;
	
	public PeriodicMapRenderer(float periodicityWidth, float periodicityHeight) {
		super(periodicityWidth, periodicityHeight);
		consoleSpriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		consolFont = new BitmapFont();
		screenPosition = new Vector2();
		screenOrigin = new Vector2();
		setConsoleText("Welcome!");
	}

	public void setConsoleText(String text ) {
		consoleText = text; 
	}
	
	public void render(PeriodicMapModel map, GameplayCamera camera) {
		renderMap(map, camera);
		if (RenderOptions.getInstance().debugRender) {
			debugRender(map);
		}
		consoleRender();
	}
	
	private void renderMap(PeriodicMapModel map, GameplayCamera camera) {
		spriteBatch.begin();
		
		for (GraphicResource resource : map.getResources()) {
			if (resource instanceof TextureRegionRenderable) {				
				screenPosition.x = camera.position.x / Utility.getScreenCoordinate(map.getWidth());
				screenPosition.y = camera.position.y / Utility.getScreenCoordinate(map.getHeight());
				float screenWidth = Utility.getScreenCoordinate(resource.getWidth(map.getWidth()));
				float screenHeight = Utility.getScreenCoordinate(resource.getHeight(map.getHeight()));
				screenOrigin.x = screenWidth / 2;
				screenOrigin.y = screenHeight / 2;
				float longestTexSide = Math.max(screenWidth, screenHeight);
				float longestViewportSide = Math.max(camera.viewportWidth, camera.viewportHeight);
				float ratio = 1 - longestViewportSide / longestTexSide;
				
				setPeriodicityWidth(screenWidth * camera.zoom);
				setPeriodicityHeight(screenHeight * camera.zoom);
				
				float periodicityWidth = getPeriodicityWidth();
				float periodicityHeight = getPeriodicityHeight();
	
				screenPosition.x = camera.position.x - screenPosition.x * periodicityWidth * ratio;
				screenPosition.y = camera.position.y - screenPosition.y * periodicityHeight * ratio;
			
				renderResourcePeriodically(resource, screenPosition, screenWidth * camera.zoom, screenHeight * camera.zoom, screenOrigin, 0f);
			}
		}
		spriteBatch.end();
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
	
	public void updateToCamera(Camera camera) {
		super.updateToCamera(camera);
		shapeRenderer.setProjectionMatrix(camera.combined);
	}

	private void consoleRender() {
		consoleSpriteBatch.begin();
		consolFont.draw(consoleSpriteBatch, consoleText, 20, 20);
		consoleSpriteBatch.end();
	}
}
