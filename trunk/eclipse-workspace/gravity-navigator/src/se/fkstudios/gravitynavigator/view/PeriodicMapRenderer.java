package se.fkstudios.gravitynavigator.view;

import se.fkstudios.gravitynavigator.model.PeriodicMapModel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class PeriodicMapRenderer extends Renderer {
	
	/** debug rendering **/
	private ShapeRenderer debugRenderer;
	
	/**
	 * Renders a ContinouosMap using a SpriteBatch.
	 * @param spriteBatch The spritebatch used to draw textures.
	 * @param map the map to render.
	 */
	public void render(SpriteBatch spriteBatch, PeriodicMapModel map) {
		spriteBatch.begin();
		
		Texture backgroundTexture = TextureLoader.getInstance().getTexture(map.getFilePathBackgroundImage1());
		float bakTexDrawWidth = map.getWidth();
		float bakTexDrawHeight = map.getHeight();
		
		spriteBatch.draw(backgroundTexture, 
				0.0f, 
				0.0f, 
				bakTexDrawWidth * RenderDefs.PIXELS_PER_UNIT, 
				bakTexDrawHeight * RenderDefs.PIXELS_PER_UNIT);
		
		spriteBatch.end();
		
		if (RenderOptions.getInstance().debugRender)
			debugRender(spriteBatch.getProjectionMatrix(), map);
	}
	
	private void debugRender(Matrix4 projectionMatrix, PeriodicMapModel map) {
		
		// not optimal to create debugRenderer here. However, we don't want to create it if never used.
		if (debugRenderer == null)
			debugRenderer = new ShapeRenderer();
		
		debugRenderer.setProjectionMatrix(projectionMatrix);
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(RenderDefs.MAP_BORDER_COLOR);
		
		debugRenderer.rect(
				0, 
				0, 
				map.getWidth() * RenderDefs.PIXELS_PER_UNIT, 
				map.getHeight() * RenderDefs.PIXELS_PER_UNIT);

		for (int i = 0; i < (int)map.getWidth()*RenderDefs.DEBUG_LINES_PER_UNIT; i++) {
			debugRenderer.line(
					(i / RenderDefs.DEBUG_LINES_PER_UNIT) * RenderDefs.PIXELS_PER_UNIT, 
					0, 
					(i / RenderDefs.DEBUG_LINES_PER_UNIT) * RenderDefs.PIXELS_PER_UNIT, 
					(int)map.getHeight() * RenderDefs.PIXELS_PER_UNIT);
		}
		
		for (int i = 0; i < (int)map.getHeight()*RenderDefs.DEBUG_LINES_PER_UNIT; i++) {
			debugRenderer.line(
					0, 
					(i / RenderDefs.DEBUG_LINES_PER_UNIT) * RenderDefs.PIXELS_PER_UNIT, 
					(int)map.getWidth() * RenderDefs.PIXELS_PER_UNIT, 
					(i / RenderDefs.DEBUG_LINES_PER_UNIT) * RenderDefs.PIXELS_PER_UNIT);
		}
		
		debugRenderer.end();
	}
}
