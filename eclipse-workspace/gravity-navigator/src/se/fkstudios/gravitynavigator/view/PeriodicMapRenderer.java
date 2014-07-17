package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.controller.GameplayCamera;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;

public class PeriodicMapRenderer {
	
	private String consoleText; 
	private SpriteBatch spriteBatch;
	
	public void setConsoleText(String text ) {
		consoleText = text; 
	}
	
	public PeriodicMapRenderer(SpriteBatch spriteBatch) {
		this.spriteBatch = spriteBatch;
		setConsoleText("Welcome!");
	}

	/** debug rendering **/
	private ShapeRenderer debugRenderer;

	/**
	 * Renders a ContinouosMap using a SpriteBatch.
	 * 
	 * @param spriteBatch
	 *            The spritebatch used to draw textures.
	 * @param map
	 *            the map to render.
	 */
	public void render(PeriodicMapModel map, GameplayCamera camera) {
		
		Texture backgroundTexture = TextureLoader.getInstance().getTexture(map.getFilePathBackgroundImageLayer1());
		
		spriteBatch.begin();
		
		spriteBatch.draw(backgroundTexture,
			0f,
			0f,
			64f,
			64f);
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), map);
		
		drawConsole(map, spriteBatch, consoleText);

		spriteBatch.end();
	}

	private void debugRender(Matrix4 projectionMatrix, PeriodicMapModel map) {

		// not optimal to create debugRenderer here. However, we don't want to
		// create it if never used.
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();

		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(Defs.MAP_BORDER_COLOR);

		debugRenderer.rect(0, 0, map.getWidth() * Defs.PIXELS_PER_UNIT,
				map.getHeight() * Defs.PIXELS_PER_UNIT);

		for (int i = 0; i < (int) map.getWidth(); i += Defs.UNIT_PER_DEBUG_LINE) {
			debugRenderer.line(i * Defs.PIXELS_PER_UNIT, 0, i
					* Defs.PIXELS_PER_UNIT, (int) map.getHeight()
					* Defs.PIXELS_PER_UNIT);
		}

		for (int i = 0; i < (int) map.getHeight(); i += Defs.UNIT_PER_DEBUG_LINE) {
			debugRenderer.line(0, i * Defs.PIXELS_PER_UNIT,
					(int) map.getWidth() * Defs.PIXELS_PER_UNIT, i
							* Defs.PIXELS_PER_UNIT);
		}

		debugRenderer.end();
	}
	
	private void drawConsole( PeriodicMapModel map, SpriteBatch spriteBatch, String text) {
        BitmapFont font;
        CharSequence str = text;
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();

        spriteBatch.begin();
        font.draw(spriteBatch, str, 20, 20);
        spriteBatch.end();
	}
}
