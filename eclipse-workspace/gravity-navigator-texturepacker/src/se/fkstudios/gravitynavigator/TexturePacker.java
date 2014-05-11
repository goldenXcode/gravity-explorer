package se.fkstudios.gravitynavigator;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

/**
 * Tiny program using libGDX's imagepacker to combine the game's images into one composite sprite sheet (one large 
 * image). Specifically, the images in 'gravity-navigator-android/assets/images' are combine into one in
 * 'gravity-navigator-android/assets/images/textures'.  
 * 
 * The TextureAtlas and TextureRegion classes are then used in the game to access individual images. 
 * 
 * NOTE: OpenGL runs faster using fewer textures. This because when streaming coordinates (for calculate the texture section 
 * to display) to the vertex shaders, the graphics hardware does not need to switch textures (e.i. reloading shaders) 
 * like when using separate textures files.
 * 
 * @author kristofer
 */
public class TexturePacker {
	
	public static void main(String[] args) {
		
		Settings settings = new Settings();
		
		// not all devices have support for bigger textures (without losing performance). 
		// http://stackoverflow.com/questions/17292404/texture-packer-size-issue-in-libgdx
		settings.maxHeight = 1024;
		settings.maxWidth = 1024;
	
		TexturePacker2.process(settings, 
				"../gravity-navigator-android/assets/images/spritesheets", 
				"../gravity-navigator-android/assets/images/spritesheets/textures", 
				"textures.pack");
	}
}