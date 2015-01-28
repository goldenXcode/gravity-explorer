package se.fkstudios.gravityexplorer.model.resources;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.HashSet;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.model.MapObjectModel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Maps textures and texture regions to strings so that they are not loaded more than once. 
 * 
 * Implements the Singleton pattern. 
 * @author kristofer
 */
public class GraphicsLoader {

	private static final GraphicsLoader instance = new GraphicsLoader();
	
	public static GraphicsLoader getInstance() {
		return instance;
	}
	
	//raw resources
	private TextureAtlas textureAtlas;
	private HashMap<String, Texture> textureMap;
	private HashMap<String, Animation> animationMap;
	private HashMap<Integer, Model> modelMap;
	
	private int modelCounter;
	
	private HashSet<AnimationBinding> ownedAnimationBindings;
	private HashSet<TextureRegionBinding> ownedTextureBindings;
	private HashSet<ColorBinding> ownedColorBindings;
	private HashSet<ModelBinding> ownedModelBindings;
	
	private ModelBuilder modelBuilder;
	
	private GraphicsLoader() {
		textureAtlas = new TextureAtlas(Gdx.files.internal(Defs.TEXTURE_PACK_FILE_PATH));
		textureMap = new HashMap<String, Texture>(Defs.TEXTURE_FILE_PATHS.length);
		animationMap = new HashMap<String, Animation>(Defs.ANIMATION_TEXTURE_REGION_NAMES.length);
		modelMap = new HashMap<Integer, Model>();
		
		modelCounter = 0;
		
		ownedAnimationBindings = new HashSet<AnimationBinding>();
		ownedTextureBindings = new HashSet<TextureRegionBinding>();
		ownedColorBindings = new HashSet<ColorBinding>();
		ownedModelBindings = new HashSet<ModelBinding>();
		
		modelBuilder = new ModelBuilder();
		
		loadTextures();
		loadAnimations();
	}
			
	public ModelBinding createSphere(Color color, float diameter, Vector2 position) {
		Material material = new Material(ColorAttribute.createDiffuse(color));
		Model model = modelBuilder.createSphere(diameter, diameter, diameter, 32, 32, material, Usage.Position | Usage.Normal);
		ModelInstance instance = new ModelInstance(model);
		ModelBinding binding = new ModelBinding(false, position, new Vector2(0,0), false, diameter, diameter, true, 0f, Float.MAX_VALUE, instance);
		modelMap.put(modelCounter++, model);
		return binding;
	}
	
	public ModelBinding createSphere(MapObjectModel owner, Color color) {
		float diameter = owner.getHeight() + owner.getWidth() / 2f;
		ModelBinding binding = createSphere(color, diameter, owner.getPosition());
		ownedModelBindings.add(binding);
		binding.setOwner(owner);
		binding.setUsingOwnerPosition(true);
		binding.setUsingOwnerSize(true);
		return binding;
	}
	
	public AnimationBinding createAnimation(String animationName, float width, float height, Vector2 position) {
		Animation animation = animationMap.get(animationName);
		AnimationBinding binding = new AnimationBinding(false, position, new Vector2(0,0), false, width, height, true, 0f, Float.MAX_VALUE, true, animation);
		binding.setVisible(false);
		return binding;
	}
	
	public AnimationBinding createAnimation(MapObjectModel owner, String animationName, float width, float height, Vector2 positionOffset) {
		AnimationBinding binding = createAnimation(animationName, width, height, owner.getPosition());
		ownedAnimationBindings.add(binding);
		binding.setOwner(owner);
		binding.setUsingOwnerPosition(true);
		binding.setPositionOffset(positionOffset);
		return binding;
	}
	
	public AnimationBinding createAnimation(MapObjectModel owner, String animationName) {
		AnimationBinding binding = createAnimation(animationName, owner.getWidth(), owner.getHeight(), owner.getPosition());
		ownedAnimationBindings.add(binding);
		binding.setOwner(owner);
		binding.setUsingOwnerPosition(true);
		binding.setUsingOwnerSize(true);
		return binding;
	}
	
	public TextureRegionBinding createAtlasTextureBinding(String textureName, float width, float height, Vector2 position) {
		TextureRegion textureRegion = textureAtlas.findRegion(textureName);
		TextureRegionBinding binding = new TextureRegionBinding(false, position, new Vector2(0,0), false, width, height, true, 0f, Float.MAX_VALUE, textureRegion);
		return binding;
	}
	
	public TextureRegionBinding createAtlasTextureBinding(MapObjectModel owner, String textureName) {
		TextureRegionBinding binding = createAtlasTextureBinding(textureName, owner.getWidth(), owner.getHeight(), owner.getPosition());
		ownedTextureBindings.add(binding);
		binding.setOwner(owner);
		binding.setUsingOwnerPosition(true);
		binding.setUsingOwnerSize(true);
		return binding;
	}
	
	public TextureRegionBinding createTextureBinding(String textureName, float width, float height, Vector2 position) {
		Texture texture = textureMap.get(textureName);
		TextureRegion textureRegion = new TextureRegion(texture);
		TextureRegionBinding binding = new TextureRegionBinding(false, position, new Vector2(0,0), false, width, height, true, 0f, Float.MAX_VALUE, textureRegion);
		return binding;
	}
	
	public TextureRegionBinding createTextureBinding(MapObjectModel owner, String textureName) {
		TextureRegionBinding binding = createTextureBinding(textureName, owner.getWidth(), owner.getHeight(), owner.getPosition());
		ownedTextureBindings.add(binding);
		binding.setOwner(owner);
		binding.setUsingOwnerPosition(true);
		binding.setUsingOwnerSize(true);
		return binding;
	}
	
	public ColorBinding createColorBinding(Color color, float width, float height, Vector2 position) {
		ColorBinding binding = new ColorBinding(false, position, new Vector2(0,0), false, width, height, true, 0f, Float.MAX_VALUE, color);
		return binding;
	}
	
	public ColorBinding createColorBinding(MapObjectModel owner, Color color, float width, float height, Vector2 position) {
		ColorBinding binding = createColorBinding(color, width, height, position);
		binding.setOwner(owner);
		ownedColorBindings.add(binding);
		return binding;
	}

	@SuppressWarnings("unchecked")
	public <E extends GraphicResourceBinding> Iterable<E> getOwnedResourceBindings(Class<E> type) {
		
		if (AnimationBinding.class == type) {
			return (Iterable<E>) ownedAnimationBindings;
		}
		else if (ColorBinding.class == type) {
			return (Iterable<E>) ownedColorBindings;
		}
		else if (ModelBinding.class == type) {
			return (Iterable<E>) ownedModelBindings;
		}
		else if (TextureRegionBinding.class == type) {
			return (Iterable<E>) ownedTextureBindings;
		}
		else {
			throw new InvalidParameterException("type parameter not recognized");
		}
	}
	
	public boolean removeOwnedResourceBinding(GraphicResourceBinding resourceBinding) {

		if (resourceBinding instanceof AnimationBinding) {
			return ownedAnimationBindings.remove(resourceBinding);
		}
		else if (resourceBinding instanceof ColorBinding) {
			return ownedColorBindings.remove(resourceBinding);
		}
		else if (resourceBinding instanceof ModelBinding) {
			return ownedModelBindings.remove(resourceBinding);
		}
		else if (resourceBinding instanceof TextureRegionBinding) {
			return ownedTextureBindings.remove(resourceBinding);
		}
		else {
			throw new InvalidParameterException("type of given resource parameter not recognized");
		}
	}
	
	public void dispose() {
		ownedAnimationBindings.clear();
		ownedColorBindings.clear();
		ownedModelBindings.clear();
		ownedTextureBindings.clear();
		
		for (Texture texture : textureMap.values()) {
			texture.dispose();
		}
		textureMap.clear();
		
		animationMap.clear(); //Texture regions stored in atlas. no dispose here.
		
		for (Model model : modelMap.values()) {
			model.dispose();
		}
		modelMap.clear();
		
		modelCounter = 0;
	}
	
	private void loadTextures() {
		for (int i = 0; i < Defs.TEXTURE_FILE_PATHS.length; i++) {
			String name = Defs.TEXTURE_NAMES[i];
			String path = Defs.TEXTURE_FILE_PATHS[i];
			Texture texture = new Texture(Gdx.files.internal(path));
			textureMap.put(name, texture);
		}
	}
	
	private void loadAnimations() {
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
}
