package se.fkstudios.gravitynavigator;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gravity-navigator";
		cfg.useGL20 = false;
		
		// testing with IPhone 4 resolution. Rather typical smartphone resolution. 
		cfg.width = 640;
		cfg.height = 960;
		
		new LwjglApplication(new GravityNavigatorGame(), cfg);
	}
}
