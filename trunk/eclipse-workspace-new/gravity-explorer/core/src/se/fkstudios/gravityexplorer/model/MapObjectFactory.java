package se.fkstudios.gravityexplorer.model;

import java.util.Random;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.model.resources.AnimationResource;
import se.fkstudios.gravityexplorer.model.resources.AtlasResource;
import se.fkstudios.gravityexplorer.model.resources.ColorResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicResource;
import se.fkstudios.gravityexplorer.model.resources.GraphicsLoader;
import se.fkstudios.gravityexplorer.model.resources.ModelResource;
import se.fkstudios.gravityexplorer.model.resources.RenderableModel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
 class MapObjectFactory {
	
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
		Array<GraphicResource> allResources = new Array<GraphicResource>();
		allResources.add(new AtlasResource(true, 
				new Vector2(0, 0), 
				new Vector2(0, 0), 
				true, 	
				0f, 0f, 
				true, 
				Defs.MIN_RENDER_SCALE_SPACESHIP, Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.TEXTURE_REGION_NAME_SPACESHIP_PLAYER));
				
		Array<AnimationResource> thrustAnimations = new Array<AnimationResource>();
	
		thrustAnimations.add(new AnimationResource(true, new Vector2(0,0), 
				new Vector2(0.40f, -1.25f), 
				false, 
				0.25f, 
				0.3f, 
				true, 
				Defs.MIN_RENDER_SCALE_SPACESHIP, 
				Defs.MAX_RENDER_SCALE_DEFAULT,
				Defs.ANIMATION_NAMES[0], 
				Defs.ANIMATION_TEXTURE_REGION_NAMES[0], 
				true));
		
		thrustAnimations.add(new AnimationResource(true, 
				new Vector2(0,0), 
				new Vector2(0.15f, -1.3f), 
				false, 
				0.25f, 
				0.3f, 
				true, Defs.MIN_RENDER_SCALE_SPACESHIP, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.ANIMATION_NAMES[0], 
				Defs.ANIMATION_TEXTURE_REGION_NAMES[0], 
				true));
		
		thrustAnimations.add(new AnimationResource(true, 
				new Vector2(0,0), 
				new Vector2(-0.15f, -1.3f), 
				false, 0.25f, 0.3f, 
				true,
				Defs.MIN_RENDER_SCALE_SPACESHIP,
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.ANIMATION_NAMES[0], 
				Defs.ANIMATION_TEXTURE_REGION_NAMES[0], 
				true));
		
		thrustAnimations.add(new AnimationResource(true, new Vector2(0,0), 
				new Vector2(-0.40f, -1.25f), 
				false, 
				0.25f, 
				0.3f, 
				true, 
				Defs.MIN_RENDER_SCALE_SPACESHIP, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				Defs.ANIMATION_NAMES[0], 
				Defs.ANIMATION_TEXTURE_REGION_NAMES[0],
				true));

		allResources.addAll(thrustAnimations);
		
		SpaceshipModel spaceship = new SpaceshipModel(1.32f,
				2.28f, 
				Defs.STARTING_POSITION, 
				Defs.STARTING_VELOCITY, 
				1, 
				0f,
				Defs.MAX_THRUST, 
				allResources, 
				thrustAnimations);
		spaceship.setSelfStabilizing(true);
		return spaceship;
	}
	
	public MapObjectModel createStationaryPlanet(float diameter, Vector2 position, float rotationSpeed, Color color, Boolean lightSource) {	
		return createStationaryPlanet(diameter, position, rotationSpeed, 1f, color, lightSource);
	}
	
	public MapObjectModel createStationaryPlanet(float diameter, Vector2 position, float rotationSpeed, float dencityFactor, Color color, Boolean lightSource) 
	{
		int mass = (int)calculateMass(diameter / 2, dencityFactor);
		
		GraphicsLoader loader = GraphicsLoader.getInstance();
		ModelBuilder builder = loader.getModelBuilder();
		
		Model model = builder.createSphere(diameter, diameter, diameter, 
				64, 64, 
				new Material(ColorAttribute.createDiffuse(color)),
				Usage.Position | Usage.Normal);
		
		ModelInstance instance = new ModelInstance(model);
		
		ModelResource resource = new ModelResource(true, 
				new Vector2(0,0), 
				new Vector2(0,0), 
				true, 
				diameter, diameter, 
				true, 
				0.1f, 
				Integer.MAX_VALUE, 
				instance);	
		
		
		if (lightSource) {
			resource.setLigthSoruce(new PointLight().set(Color.WHITE, 0f, 0f, Defs.PLANE_POSITION_Z, 100000f));
		}
		
		MapObjectModel planet = new MapObjectModel(diameter, diameter, 
				position, 
				new Vector2(0, 0), 
				mass, 
				0f, 
				rotationSpeed, 
				false,
				resource);
		
		planet.setGravitationalModeToStationary();
		
		return planet;
	}
	
	public MapObjectModel createOrbitingPlanet(MapObjectModel primaryMapObject,
			float distance,
			float degrees,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed,
			Color color) 
	{
		int mass = (int) (primaryMapObject.getMass() * relativeMass);
		float radius = calculateRadius(mass, 1f);
		float diameter = radius * 2;
		
		GraphicsLoader loader = GraphicsLoader.getInstance();
		
		ModelBuilder builder = loader.getModelBuilder();
		
		Model model = builder.createSphere(diameter, diameter, diameter, 
						64, 64, 
						new Material(ColorAttribute.createDiffuse(color)),
						Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		
		ModelInstance instance = new ModelInstance(model);
		
		ModelResource resource = new ModelResource(true, 
				new Vector2(0,0), 
				new Vector2(0,0), 
				true, 
				diameter, diameter, 
				true, 
				0.1f, 
				Integer.MAX_VALUE, 
				instance);
		
		MapObjectModel mapObject = new MapObjectModel(diameter, diameter, 
				new Vector2(0,0), 
				new Vector2(0,0), 
				mass, 
				0f, 
				rotationSpeed,
				false,
				resource);
		
		mapObject.setGravitationalModeToObject(primaryMapObject);
		
		placeMapObjectInOrbit(mapObject, primaryMapObject, distance, degrees, clockwise);

		return mapObject;
	}

	public MapObjectModel createOrbitingAsteroid(MapObjectModel primaryMapObject,
			float distance,
			float degrees,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed) 
	{
		GraphicResource resource = createRandomTextureRegion(Defs.TEXTURE_REGION_NAMES_ASTERIOIDS);
		return createOrbitingMapObject(primaryMapObject, 
				distance, 
				degrees, 
				relativeMass, 
				clockwise, 
				rotationSpeed, 
				resource);
	}
	
	public ColorResource createParticleResource(RenderableModel owner,
			float width, float height, 
			Vector2 position) 
	{
		Color color = new Color(randomFloat(0, 1f), randomFloat(0, 1f), randomFloat(0, 1f), 1f);
		return createParticleResource(owner, width, height, position, color);
	}
	
	public ColorResource createParticleResource(RenderableModel owner,
			float width, float height, 
			Vector2 position, 
			Color color) 
	{
		ColorResource resource = new ColorResource(false, 
				position, 
				new Vector2(0, 0), 
				false, 
				width, 
				height, 
				true, 
				0.1f, 
				Defs.MAX_RENDER_SCALE_DEFAULT, 
				color);
		
		owner.getResources().add(resource);
		return resource;
	}
	
	public void placeMapObjectInOrbit(MapObjectModel mapObject,
			MapObjectModel primaryMapObject, 
			float distance,
			float angularOffset,
			boolean clockwise)
	{
		Vector2 primarysPosition = primaryMapObject.getPosition();
		Vector2 displacementVector = new Vector2(0, -distance).rotate(angularOffset);
		Vector2 position = new Vector2(primarysPosition.x, primarysPosition.y).add(displacementVector);
		mapObject.setPosition(position);
		
		float speed = physicsEngine.calculateOrbitingSpeed(distance, primaryMapObject.getMass());
		if (clockwise)
			speed = -speed;
		Vector2 velocity = new Vector2(speed, 0.0f).rotate(angularOffset);
		velocity.add(primaryMapObject.getVelocity());
		mapObject.setVelocity(velocity.x, velocity.y);
	}

	public int randomInt(int min, int max) {
		return rand.nextInt(max + 1 - min) + min;
	}
	
	public float randomFloat(float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}
	
	private MapObjectModel createOrbitingMapObject(MapObjectModel primaryMapObject,
			float distance,
			float angularOffset,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed,
			GraphicResource resource) 
	{
		int mass = (int) (primaryMapObject.getMass() * relativeMass);
		float radius = calculateRadius(mass, 1f);
		
		MapObjectModel mapObject = new MapObjectModel(radius * 2, radius * 2, 
				new Vector2(0,0), 
				new Vector2(0,0), 
				mass, 
				0f, 
				rotationSpeed,
				false, 
				resource);
		
		mapObject.setGravitationalModeToObject(primaryMapObject);
		
		placeMapObjectInOrbit(mapObject, primaryMapObject, distance, angularOffset, clockwise);

		return mapObject;
	}
	
	private AtlasResource createRandomTextureRegion(String[] textureRegionNames) {
		int textureIndex = randomInt(0, textureRegionNames.length - 1);
		String textureName = textureRegionNames[textureIndex];
		return new AtlasResource(true, 
				new Vector2(0,0), 
				new Vector2(0,0), 
				true, 
				0f, 0f, 
				true, 
				Defs.MIN_RENDER_SCALE_DEFAULT, Defs.MAX_RENDER_SCALE_DEFAULT, 
				textureName);	
	}
	
	private float calculateMass(float radius, float densityFactor) {
		return (float) (Math.pow(radius, 2) * Math.PI * objectDensity * densityFactor); 
	}
	
	private float calculateRadius(float mass, float densityFactor) {
		return (float) (Math.sqrt(mass / (Math.PI * objectDensity * densityFactor)));
	}
}
