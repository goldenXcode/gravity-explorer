package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.ContinuousMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class ContinuousMapRenderer extends Renderer {
	
	Texture backgroundTexture;
	
	/** debug rendering **/
	private ShapeRenderer debugRenderer;
	
	/**
	 * Renders a ContinouosMap using a SpriteBatch.
	 * @param spriteBatch The spritebatch used to draw textures.
	 * @param map the map to render.
	 */
	public void render(SpriteBatch spriteBatch, ContinuousMap map) {
		spriteBatch.begin();
		
		backgroundTexture = new Texture(Gdx.files.internal(map.getFilePathBackgroundImage1()));
		float bakTexDrawWidth = map.getWidth();
		float bakTexDrawHeight = map.getHeight();
		
		spriteBatch.draw(backgroundTexture, 0.0f, 0.0f, bakTexDrawWidth, bakTexDrawHeight);
		
		spriteBatch.end();
		
		if (RenderingOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), map);
	}
	
	private void debugRender(Matrix4 projectionMatrix, ContinuousMap map) {
		
		// not optimal to create here but we don't want to create debugRenderer if 
		// never running in debug mode. That would waste memory in final game.
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();
		
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(RenderingDefs.MAP_BORDER_COLOR);
		debugRenderer.rect(0, 0, map.getWidth(), map.getHeight());
		
		
		for (int i = 0; i < (int)map.getWidth(); i += RenderingDefs.MAP_GRID_LINE_SPACING)
			debugRenderer.line(i, 0, i, (int)map.getHeight());
		
		for (int i = 0; i < (int)map.getHeight(); i += RenderingDefs.MAP_GRID_LINE_SPACING)
			debugRenderer.line(0, i, (int)map.getWidth(), i);
	
		debugRenderer.end();
	}
}
