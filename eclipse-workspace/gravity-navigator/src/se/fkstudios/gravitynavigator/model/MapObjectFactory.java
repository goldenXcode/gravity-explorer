package se.fkstudios.gravitynavigator.model;

import java.util.Random;

import se.fkstudios.gravitynavigator.Defs;
import se.fkstudios.gravitynavigator.model.resources.AnimationResource;
import se.fkstudios.gravitynavigator.model.resources.TextureAtlasResource;
import se.fkstudios.gravitynavigator.model.resources.TextureRegionResource;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class MapObjectFactory {
	
	private PhysicsEngine physicsEngine;
	private Random rand;
	private float objectDensity;
	/* Singelton stuff */
	private static final MapObjectFactory instance = new MapObjectFactory();
	
	private MapObjectFactory() {
		physicsEngine = PhysicsEngine.getInstance();
		rand = new Random();
		objectDensity = 1273f;
	}
	
	public static MapObjectFactory getInstance() {
		return instance;
	}
	
	public SpaceshipModel createPlayerSpaceship() {
		Array<TextureRegionResource> allResources = new Array<TextureRegionResource>();
		allResources.add(new TextureAtlasResource(new Vector2(0, 0), 
				true, 
				Defs.MIN_RENDER_SCALE_SPACESHIP, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.TEXTURE_REGION_NAME_SPACESHIP_PLAYER));
		Array<AnimationResource> thrustAnimations = new Array<AnimationResource>();
		thrustAnimations.add(new AnimationResource(new Vector2(0.40f, -1.25f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(0.15f, -1.3f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(-0.15f, -1.3f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		thrustAnimations.add(new AnimationResource(new Vector2(-0.40f, -1.25f), false, Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 0.25f, 0.3f, Defs.ANIMATION_NAMES[0], Defs.ANIMATION_TEXTURE_REGION_NAMES[0], true));
		allResources.addAll(thrustAnimations);
		
		return new SpaceshipModel(1.32f, 2.28f, Defs.STARTING_POSITION, Defs.STARTING_VELOCITY, 1, 0f, Defs.MAX_THRUST, allResources, thrustAnimations);
	}
	
	public MapObjectModel createStationaryPlanet(Array<MapObjectModel> neighborhood, 
			float width, float height,
			Vector2 position,
			float rotationSpeed) 
	{
		float radius = (width + height) / 4;
		int mass = (int)calculateMass(radius, 1f);
		TextureRegionResource resource = createRandomTextureRegion(Defs.TEXTURE_REGION_NAMES_PLANETS);
		
		MapObjectModel planet = new MapObjectModel(width, height, 
				position, 
				new Vector2(0, 0), 
				mass, 
				0f, 
				rotationSpeed, 
				false,
				true, 
				resource);
		
		neighborhood.add(planet);
		return planet;
	}
	
	public MapObjectModel createOrbitingPlanet(Array<MapObjectModel> neighborhood, 
			MapObjectModel primaryMapObject,
			float distance,
			float degrees,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed) 
	{
		TextureRegionResource resource = createRandomTextureRegion(Defs.TEXTURE_REGION_NAMES_PLANETS);
		return createOrbitingMapObject(neighborhood, primaryMapObject, distance, degrees, relativeMass, clockwise, rotationSpeed, resource);
				
	}
	
	public MapObjectModel createOrbitingAsteroid(Array<MapObjectModel> neighborhood, 
			MapObjectModel primaryMapObject,
			float distance,
			float degrees,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed) 
	{
		TextureRegionResource resource = createRandomTextureRegion(Defs.TEXTURE_REGION_NAMES_ASTERIOIDS);
		return createOrbitingMapObject(neighborhood, primaryMapObject, distance, degrees, relativeMass, clockwise, rotationSpeed, resource);
	}
	
	private MapObjectModel createOrbitingMapObject(Array<MapObjectModel> neighborhood, 
			MapObjectModel primaryMapObject,
			float distance,
			float degrees,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed,
			TextureRegionResource resource) 
	{
		int mass = (int) (primaryMapObject.getMass() * relativeMass);
		float radius = calculateRadius(mass, 1f);
		
		Vector2 primarysPosition = primaryMapObject.getPosition();
		Vector2 displacementVector = new Vector2(0, -distance).rotate(degrees);
		Vector2 position = new Vector2(primarysPosition.x, primarysPosition.y).add(displacementVector);
		
		float speed = physicsEngine.calculateOrbitingSpeed(distance, primaryMapObject.getMass());
		if (clockwise) {
			speed = -speed;
		}
		Vector2 velocity = new Vector2(speed, 0.0f).rotate(degrees);
		
		MapObjectModel mapObject = new MapObjectModel(radius * 2, radius * 2, 
				position, 
				velocity, 
				mass, 
				0f, 
				rotationSpeed, 
				true,
				false, 
				resource);
		
		neighborhood.add(mapObject);
		return mapObject;
	}
	
	private TextureRegionResource createRandomTextureRegion(String[] textureRegionNames) {
		int textureIndex = randomInt(0, textureRegionNames.length - 1);
		String textureName = textureRegionNames[textureIndex];
		return new TextureAtlasResource(new Vector2(0,0), 
				true, 
				Defs.MIN_RENDER_SCALE_DEFAULT, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				textureName);	
	}
	
	private int randomInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	private float  randomFloat(float min, float max) {
		return (float) (Math.random() * (max-min) + min);
	}
	
	private float calculateMass(float radius, float densityOffset) {
		return (float) (Math.pow(radius, 2) * Math.PI * objectDensity * densityOffset); 
	}
	
	private float calculateRadius(float mass, float densityOffset) {
		return (float) (Math.sqrt(mass / (Math.PI * objectDensity * densityOffset)));
	}
}
