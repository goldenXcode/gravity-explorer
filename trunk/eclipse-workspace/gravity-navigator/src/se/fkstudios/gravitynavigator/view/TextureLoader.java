package se.fkstudios.gravitynavigator.view;

import java.util.HashMap;

import se.fkstudios.gravitynavigator.Defs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Maps textures and texture regions to strings so that they are not loaded more than once. 
 * 
 * Implements the Singleton patterns. 
 * 
 * @author kristofer
 */
public class TextureLoader {

	private static final TextureLoader instance = new TextureLoader();
	
	private HashMap<String, Texture> textureMapper;
	private TextureAtlas textureAtlas;
	
	private TextureLoader() {
		
		textureAtlas = new TextureAtlas(Gdx.files.internal(Defs.TEXTURE_PACK_FILE_PATH));
		
		textureMapper = new HashMap<>(Defs.TEXTURE_FILE_PATHS.length);
		for (int i = 0; i < Defs.TEXTURE_FILE_PATHS.length; i++) {

			String name = Defs.TEXTURE_NAMES[i];
			String path = Defs.TEXTURE_FILE_PATHS[i];
			Texture texture = new Texture(Gdx.files.internal(path));
			textureMapper.put(name, texture);
		}
	}
	
	/**
	 * Gets texture stored in game's texture atlas. Texture region names should be constants 
	 * in ResourcesDefs.TEXTURE_NAMES.
	 * 
	 * @param textureName the name of the texture to search for.
	 * @return the texture with the given name, null if not found.
	 */
	public Texture getTexture(String textureName) {
		return textureMapper.get(textureName);
	}
	
	/**
	 * Gets texture region stored in game's texture atlas. Texture region names should be constants 
	 * in ResourcesDefs. Regions names also matches the file names of the .PNG files when running 
	 * the texture packer. 
	 * 
	 * @param textureRegionName the name of the texture region to search for.
	 * @return the texture region with the given name, null if not found.
	 */
	public TextureRegion getTextureRegion(String textureRegionName) {
		return textureAtlas.findRegion(textureRegionName);
	}
	
	/**
	 * Returns the only allowed instance of this object.
	 * @return self
	 */
	public static TextureLoader getInstance() {
		return instance;
	}
}
