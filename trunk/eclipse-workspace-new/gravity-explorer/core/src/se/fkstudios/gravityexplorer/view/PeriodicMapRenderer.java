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
		mapRender(map, camera);
		if (RenderOptions.getInstance().debugRender) {
			debugRender(map);
		}
		consoleRender();
	}
	
	private void mapRender(PeriodicMapModel map, GameplayCamera camera) {
		spriteBatch.begin();
		
		for (GraphicResource resource : map.getResources()) {
			if (resource instanceof TextureRegionRenderable) {				
				float proportionalPositionX = camera.position.x / Utility.getScreenCoordinate(map.getWidth());
				float proportionalPositionY = camera.position.y / Utility.getScreenCoordinate(map.getHeight());
				float textureOriginX = Utility.getScreenCoordinate(resource.getWidth()) / 2;
				float textureOriginY = Utility.getScreenCoordinate(resource.getHeight()) / 2;
				float texScreenWidth = Utility.getScreenCoordinate(resource.getWidth());
				float texScreenHeight = Utility.getScreenCoordinate(resource.getHeight());
				float longestTexSide = Math.max(texScreenWidth, texScreenHeight);
				float longestViewportSide = Math.max(camera.viewportWidth, camera.viewportHeight);
				float ratio = 1 - longestViewportSide / longestTexSide;
				
				setPeriodicityWidth(texScreenWidth * camera.zoom);
				setPeriodicityHeight(texScreenHeight * camera.zoom);
				
				float periodicityWidth = getPeriodicityWidth();
				float periodicityHeight = getPeriodicityHeight();
				drawArea.x = camera.position.x - textureOriginX - proportionalPositionX * periodicityWidth * ratio;
				drawArea.y = camera.position.y - textureOriginY - proportionalPositionY * periodicityHeight * ratio;
				drawArea.width = Utility.getScreenCoordinate(resource.getWidth());
				drawArea.height = Utility.getScreenCoordinate(resource.getHeight());
				
				renderResourcePeriodically(resource, drawArea, 0f, camera);			
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
