package se.fkstudios.gravitynavigator;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;

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
		TexturePacker2.process("../gravity-navigator-android/assets/images", 
				"../gravity-navigator-android/assets/images/textures", 
				"textures.pack");
	}
}