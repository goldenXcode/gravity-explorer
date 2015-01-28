package se.fkstudios.gravityexplorer.model;

import se.fkstudios.gravityexplorer.Defs;
import se.fkstudios.gravityexplorer.Utility;
import se.fkstudios.gravityexplorer.model.resources.AnimationBinding;
import se.fkstudios.gravityexplorer.model.resources.GraphicResourceBinding;
import se.fkstudios.gravityexplorer.model.resources.GraphicsLoader;
import se.fkstudios.gravityexplorer.model.resources.ModelBinding;
import se.fkstudios.gravityexplorer.model.resources.TextureRegionBinding;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
 class MapObjectFactory {
	
	private PhysicsEngine physicsEngine;
	private GraphicsLoader graphicLoader;
	private float objectDensity;
	
	/* Singelton stuff */
	private static final MapObjectFactory instance = new MapObjectFactory();
	
	private MapObjectFactory() {
		physicsEngine = PhysicsEngine.getInstance();
		graphicLoader = GraphicsLoader.getInstance();
		objectDensity = 1273f;
	}
	
	public static MapObjectFactory getInstance() {
		return instance;
	}
	
	public SpaceshipModel createPlayerSpaceship() {
		SpaceshipModel spaceship = new SpaceshipModel(3.96f, 6.84f,  Defs.STARTING_POSITION, Defs.STARTING_VELOCITY, 1, 0f, Defs.MAX_THRUST);
		spaceship.setSelfStabilizing(true);
		
		TextureRegionBinding textureRegionBinding = graphicLoader.createAtlasTextureBinding(spaceship, Defs.TEXTURE_REGION_NAME_SPACESHIP_PLAYER);
		setSpaceshipResourceRenderScale(textureRegionBinding);
		
		AnimationBinding thurstAnimation1 = 
				graphicLoader.createAnimation(spaceship, Defs.ANIMATION_NAMES[0], 0.75f, 0.9f, new Vector2(1.20f, -3.75f));
		setSpaceshipResourceRenderScale(thurstAnimation1);
		
		AnimationBinding thurstAnimation2 = 
				graphicLoader.createAnimation(spaceship, Defs.ANIMATION_NAMES[0], 0.75f, 0.9f, new Vector2(0.45f, -3.9f));
		setSpaceshipResourceRenderScale(thurstAnimation2);
		
		AnimationBinding thurstAnimation3 = 
				graphicLoader.createAnimation(spaceship, Defs.ANIMATION_NAMES[0], 0.75f, 0.9f, new Vector2(-0.45f, -3.9f));
		setSpaceshipResourceRenderScale(thurstAnimation3);
		
		AnimationBinding thurstAnimation4 = 
				graphicLoader.createAnimation(spaceship, Defs.ANIMATION_NAMES[0], 0.75f, 0.9f, new Vector2(-1.20f, -3.75f));
		setSpaceshipResourceRenderScale(thurstAnimation4);
		
		Array<AnimationBinding> thrustAnimations = spaceship.getThrustAnimations();
		thrustAnimations.add(thurstAnimation1);
		thrustAnimations.add(thurstAnimation2);
		thrustAnimations.add(thurstAnimation3);
		thrustAnimations.add(thurstAnimation4);
		
		return spaceship;
	}
	
	private void setSpaceshipResourceRenderScale(GraphicResourceBinding resourceBinding) {
		resourceBinding.setMinRenderScale(Defs.MIN_RENDER_SCALE_SPACESHIP);
		resourceBinding.setMaxRenderScale(Defs.MAX_RENDER_SCALE_DEFAULT);
	}
	
	public MapObjectModel createStationaryPlanet(float diameter, Vector2 position, float rotationSpeed, Color color, Boolean lightSource) {	
		return createStationaryPlanet(diameter, position, rotationSpeed, 1f, color, lightSource);
	}
	
	public MapObjectModel createStationaryPlanet(float diameter, Vector2 position, float rotationSpeed, float dencityFactor, Color color, 
			Boolean lightSource)  
	{
		int mass = (int)calculateMass(diameter / 2, dencityFactor);
		MapObjectModel planet = new MapObjectModel(diameter, diameter, position, new Vector2(0, 0), mass, 0f, rotationSpeed, false);
		planet.setGravitationalModeToStationary();
		
		ModelBinding resourceBinding = graphicLoader.createSphere(planet, color);
		if (lightSource) {
			resourceBinding.setLightSources(new PointLight().set(Color.WHITE, 0f, 0f, Defs.PLANE_POSITION_Z, 100000f));
		}
		return planet;
	}
	
	public MapObjectModel createOrbitingPlanet(MapObjectModel primaryMapObject, float distance, float angularOffset, float relativeMass,
			boolean clockwise, float rotationSpeed, Color color) 
	{
		MapObjectModel planet = createOrbitingMapObject(primaryMapObject, distance, angularOffset, relativeMass, clockwise, rotationSpeed);
		graphicLoader.createSphere(planet, color);
		return planet;
	}

	public MapObjectModel createOrbitingAsteroid(MapObjectModel primaryMapObject, float distance, float degrees, float relativeMass,
			boolean clockwise, float rotationSpeed) 
	{
		MapObjectModel asterioid = createOrbitingMapObject(primaryMapObject, distance, degrees, relativeMass, clockwise, rotationSpeed);
		createRandomTextureRegionBinding(asterioid, Defs.TEXTURE_REGION_NAMES_ASTERIOIDS);
		return asterioid;
	}
	
	private MapObjectModel createOrbitingMapObject(MapObjectModel primaryMapObject,
			float distance,
			float angularOffset,
			float relativeMass,
			boolean clockwise,
			float rotationSpeed) 
	{
		int mass = (int) (primaryMapObject.getMass() * relativeMass);
		float diameter = calculateRadius(mass, 1f) * 2;
		MapObjectModel mapObject = new MapObjectModel(diameter, diameter, new Vector2(0,0), new Vector2(0,0), mass, 0f, rotationSpeed, false);
		mapObject.setGravitationalModeToObject(primaryMapObject);
		placeMapObjectInOrbit(mapObject, primaryMapObject, distance, angularOffset, clockwise);
		return mapObject;
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
	
	private TextureRegionBinding createRandomTextureRegionBinding(MapObjectModel owner, String[] textureRegionNames) {
		int textureIndex = Utility.randomInt(0, textureRegionNames.length - 1);
		String textureName = textureRegionNames[textureIndex];
		return graphicLoader.createAtlasTextureBinding(owner, textureName);
	}
	
	private float calculateMass(float radius, float densityFactor) {
		return (float) (Math.pow(radius, 2) * Math.PI * objectDensity * densityFactor); 
	}
	
	private float calculateRadius(float mass, float densityFactor) {
		return (float) (Math.sqrt(mass / (Math.PI * objectDensity * densityFactor)));
	}
}
