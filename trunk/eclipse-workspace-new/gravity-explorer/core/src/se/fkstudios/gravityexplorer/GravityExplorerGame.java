package se.fkstudios.gravityexplorer;

import se.fkstudios.gravityexplorer.controller.GameplayScreen;

import com.badlogic.gdx.Game;

/**
 * The libGDX game class, holds and controls the game's screens.   
 * @author kristofer
 */
public class GravityExplorerGame extends Game {

	@Override
	public void create() {
		setScreen(new GameplayScreen());
	}
}

//package se.fkstudios.gravityexplorer;
//
//import com.badlogic.gdx.ApplicationAdapter;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//
//public class GravityExplorerGame extends ApplicationAdapter {
//	SpriteBatch batch;
//	Texture img;
//	
//	@Override
//	public void create () {
//		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
//	}
//
//	@Override
//	public void render () {
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
//	}
//}