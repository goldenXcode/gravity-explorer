package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;
import se.fkstudios.gravitynavigator.model.resources.TextureResource;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class PeriodicMapRenderer {
	
	/* "real game" rendering */
	private SpriteBatch spriteBatch;
	
	/* debug rendering */
	private ShapeRenderer debugRenderer;
	
	/* console rendering */
	private SpriteBatch consoleSpriteBatch;
	private BitmapFont consolFont;
	private String consoleText; 
	
	public PeriodicMapRenderer(SpriteBatch spriteBatch) {
		this.spriteBatch = spriteBatch;
		this.debugRenderer = new ShapeRenderer();
		this.consoleSpriteBatch = new SpriteBatch();
		this.consolFont = new BitmapFont();
		setConsoleText("Welcome!");
	}

	public void setConsoleText(String text ) {
		consoleText = text; 
	}
	
	/**
	 * Renders a periodic map. Assumes map background textures width equals height.
	 * @param map the map to be rendered.
	 * @param viewport the current viewport of player's camera. 
	 */
	public void render(PeriodicMapModel map, Rectangle viewport) {
		
		spriteBatch.begin();

		float ratio = 0f;
		for (TextureRegionResource resource : map.getResources()) {
			if (resource.getClass() == TextureResource.class) {
				TextureResource textureResource = (TextureResource)resource;
				Texture texture = TextureLoader.getInstance().getTexture(textureResource.textureName);
				renderMapBackgroundWithoutPerspective(map, texture, viewport, ratio);
				ratio = ratio + 0.4f;
			}
		}
			 
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender) {
			drawConsole();
			debugRender(spriteBatch.getProjectionMatrix(), map);
		}
	}
	
	private void renderMapBackgroundWithoutPerspective(PeriodicMapModel map, Texture textureRegion, Rectangle viewport, float ratio) {
		float mapScreenWidth = map.getWidth() * Defs.PIXELS_PER_UNIT;
		float mapScreenHeight = map.getHeight() * Defs.PIXELS_PER_UNIT; 
		float proportionalPositionX = (viewport.x + viewport.width / 2) / mapScreenWidth;
		float proportionalPositionY = (viewport.y + viewport.height / 2) / mapScreenHeight;
		float longestViewportSide = Math.max(viewport.width, viewport.height);
	
		spriteBatch.draw(textureRegion,
			viewport.x - proportionalPositionX * viewport.width * ratio,
			viewport.y - proportionalPositionY * viewport.height * ratio,
			longestViewportSide + viewport.width * ratio,
			longestViewportSide + viewport.height * ratio);
	}

//	private void renderMapBackgroundWithPerspective(PeriodicMapModel map, Texture textureRegion, Rectangle viewport, float ratio) {
//		
//		float longestViewportSide = Math.max(viewport.width, viewport.height);
//		spriteBatch.draw(textureRegion,
//				viewport.x * ratio,
//				viewport.y * ratio,
//				map.getWidth() * Defs.PIXELS_PER_UNIT * (1-ratio) + longestViewportSide  * ratio ,
//				map.getHeight() * Defs.PIXELS_PER_UNIT * (1-ratio)  +  longestViewportSide * ratio);
//	}
	
	private void debugRender(Matrix4 projectionMatrix, PeriodicMapModel map) {
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(Defs.MAP_BORDER_COLOR);

		debugRenderer.rect(0, 0, map.getWidth() * Defs.PIXELS_PER_UNIT,
				map.getHeight() * Defs.PIXELS_PER_UNIT);

		for (int i = 0; i < (int) map.getWidth(); i += Defs.UNIT_PER_DEBUG_LINE) {
			debugRenderer.line(i * Defs.PIXELS_PER_UNIT, 
					0, 
					i * Defs.PIXELS_PER_UNIT, 
					(int) map.getHeight() * Defs.PIXELS_PER_UNIT);
		}

		for (int i = 0; i < (int) map.getHeight(); i += Defs.UNIT_PER_DEBUG_LINE) {
			debugRenderer.line(0, 
					i * Defs.PIXELS_PER_UNIT,
					(int) map.getWidth() * Defs.PIXELS_PER_UNIT, 
					i * Defs.PIXELS_PER_UNIT);
		}

		debugRenderer.end();
	}
	
	private void drawConsole() {
        consoleSpriteBatch.begin();
        consolFont.draw(consoleSpriteBatch, consoleText, 20, 20);
        consoleSpriteBatch.end();
	}
}
