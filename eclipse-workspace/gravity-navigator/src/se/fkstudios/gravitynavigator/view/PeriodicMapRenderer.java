package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.PeriodicMapModel;

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
	 * Renders a periodic map.
	 * @param map the map to be rendered.
	 * @param viewport the current viewport of player's camera. 
	 */
	public void render(PeriodicMapModel map, Rectangle viewport) {
		
		Texture backgroundTexture = TextureLoader.getInstance().getTexture(map.getFilePathBackgroundImageLayer1());
		
		spriteBatch.begin();
		
		//REMARK: assumes the backgoundTexture has width = height.
		spriteBatch.draw(backgroundTexture,
			viewport.x,
			viewport.y,
			Math.max(viewport.width, viewport.height),
			Math.max(viewport.width, viewport.height));
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender) {
			drawConsole();
			debugRender(spriteBatch.getProjectionMatrix(), map);
		}
	}

	private void debugRender(Matrix4 projectionMatrix, PeriodicMapModel map) {
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
	
	private void drawConsole() {
        consoleSpriteBatch.begin();
        consolFont.draw(consoleSpriteBatch, consoleText, 20, 20);
        consoleSpriteBatch.end();
	}
}
