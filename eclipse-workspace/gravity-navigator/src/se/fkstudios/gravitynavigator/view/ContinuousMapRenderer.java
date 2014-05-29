package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.ContinuousMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class ContinuousMapRenderer extends Renderer {
	
	/** debug rendering **/
	private ShapeRenderer debugRenderer;
	
	/**
	 * Renders a ContinouosMap using a SpriteBatch.
	 * @param spriteBatch The spritebatch used to draw textures.
	 * @param map the map to render.
	 */
	public void render(SpriteBatch spriteBatch, ContinuousMap map) {
		spriteBatch.begin();
		
		Texture backgroundTexture = TextureLoader.getInstance().getTexture(map.getFilePathBackgroundImage1());
		float bakTexDrawWidth = map.getWidth();
		float bakTexDrawHeight = map.getHeight();
		
		spriteBatch.draw(backgroundTexture, 
				0.0f, 
				0.0f, 
				bakTexDrawWidth * RenderingDefs.PIXELS_PER_UNIT, 
				bakTexDrawHeight * RenderingDefs.PIXELS_PER_UNIT);
		
		spriteBatch.end();
		
		if (RenderingOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), map);
	}
	
	private void debugRender(Matrix4 projectionMatrix, ContinuousMap map) {
		
		// not optimal to create debugRenderer here. However, we don't want to create it if never used.
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();
		
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(RenderingDefs.MAP_BORDER_COLOR);
		
		debugRenderer.rect(
				0, 
				0, 
				map.getWidth() * RenderingDefs.PIXELS_PER_UNIT, 
				map.getHeight() * RenderingDefs.PIXELS_PER_UNIT);

		for (int i = 0; i < (int)map.getWidth()*RenderingDefs.DEBUG_LINES_PER_UNIT; i++) {
			debugRenderer.line(
					(i / RenderingDefs.DEBUG_LINES_PER_UNIT) * RenderingDefs.PIXELS_PER_UNIT, 
					0, 
					(i / RenderingDefs.DEBUG_LINES_PER_UNIT) * RenderingDefs.PIXELS_PER_UNIT, 
					(int)map.getHeight() * RenderingDefs.PIXELS_PER_UNIT);
		}
		
		for (int i = 0; i < (int)map.getHeight()*RenderingDefs.DEBUG_LINES_PER_UNIT; i++) {
			debugRenderer.line(
					0, 
					(i / RenderingDefs.DEBUG_LINES_PER_UNIT) * RenderingDefs.PIXELS_PER_UNIT, 
					(int)map.getWidth() * RenderingDefs.PIXELS_PER_UNIT, 
					(i / RenderingDefs.DEBUG_LINES_PER_UNIT) * RenderingDefs.PIXELS_PER_UNIT);
		}
		
		debugRenderer.end();
	}
}
