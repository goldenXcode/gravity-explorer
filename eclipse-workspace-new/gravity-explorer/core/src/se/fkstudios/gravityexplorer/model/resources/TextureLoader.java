package se.fkstudios.gravityexplorer.model.resources;

import java.util.HashMap;

import se.fkstudios.gravityexplorer.Defs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Maps textures and texture regions to strings so that they are not loaded more than once. 
 * 
 * Implements the Singleton patterns. 
 * 
 * @author kristofer
 */
public class TextureLoader {

	private static final TextureLoader instance = new TextureLoader();
	
	private TextureAtlas textureAtlas;
	private HashMap<String, Texture> textureMap;
	private HashMap<String, TextureRegion> textureRegionMap;
	private HashMap<String, Animation> animationMap;
	
	private TextureLoader() {
		
		textureAtlas = new TextureAtlas(Gdx.files.internal(Defs.TEXTURE_PACK_FILE_PATH));
		
		textureMap = new HashMap<String, Texture>(Defs.TEXTURE_FILE_PATHS.length);
		for (int i = 0; i < Defs.TEXTURE_FILE_PATHS.length; i++) {

			String name = Defs.TEXTURE_NAMES[i];
			String path = Defs.TEXTURE_FILE_PATHS[i];
			Texture texture = new Texture(Gdx.files.internal(path));
			textureMap.put(name, texture);
		}
	
		textureRegionMap = new HashMap<String, TextureRegion>();
		
		animationMap = new HashMap<String, Animation>(Defs.ANIMATION_TEXTURE_REGION_NAMES.length);
		for (int i = 0; i < Defs.ANIMATION_TEXTURE_REGION_NAMES.length; i++) {
			String animationName = Defs.ANIMATION_NAMES[i];
			float frameDuration = Defs.ANIMATION_FRAME_DURATIONS[i];
			
			String[] textureRegionNames = Defs.ANIMATION_TEXTURE_REGION_NAMES[i];
			Array<TextureRegion> textureRegions = new Array<TextureRegion>(textureRegionNames.length);
			for (int j = 0; j < textureRegionNames.length; j++) {
				TextureRegion textureRegion = textureAtlas.findRegion(textureRegionNames[j]);
				if (textureRegion != null)
					textureRegions.add(textureRegion);
				else
					throw new NullPointerException("texture region with name '" + textureRegionNames[j] + "' not found");
			}	
			animationMap.put(animationName, new Animation(frameDuration, textureRegions));
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
		return textureMap.get(textureName);
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
		TextureRegion region = textureRegionMap.get(textureRegionName);
		if (region == null) {
			region = textureAtlas.findRegion(textureRegionName);
			textureRegionMap.put(textureRegionName, region);
		}
		return region;
	}
	
	/**
	 * TODO: write this shit!
	 * @param animationName
	 * @return
	 */
	public Animation getAnimation(String animationName) {
		return animationMap.get(animationName);
	}
	
	/**
	 * Returns the only allowed instance of this object.
	 * @return self
	 */
	public static TextureLoader getInstance() {
		return instance;
	}
}
