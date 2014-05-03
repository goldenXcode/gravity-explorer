package se.fkstudios.gravitynavigator;

import se.fkstudios.gravitynavigator.controller.GameplayScreen;

import com.badlogic.gdx.Game;

/**
 * The libGDX game class, holds and controls the game's screens.   
 * @author kristofer
 */
public class GravityNavigatorGame extends Game {

	@Override
	public void create() {
		setScreen(new GameplayScreen());
	}
}
