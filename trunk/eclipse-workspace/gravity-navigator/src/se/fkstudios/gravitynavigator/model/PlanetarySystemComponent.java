package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.math.Vector2;

public class PlanetarySystemComponent extends TextureMapObjectModel {
	
	public PlanetarySystemComponent(Vector2 position, float width,
			float height, Vector2 velocity, float rotation, int mass,
			String textureName) {
		super(position, width, height, velocity, rotation, mass, textureName);
	}
	
	public PlanetarySystemComponent(Vector2 position, float width,
			float height, Vector2 velocity, float rotation, int mass,
			String textureName, TextureMapObjectModel model) {
		super(position, width, height, velocity, rotation, mass, textureName);
		setParentNode (model); 
	}

	private TextureMapObjectModel parentNode; 
	private TextureMapObjectModel[] childrenNodes; 
	
	public TextureMapObjectModel getParentNode() {
		return parentNode;
	}
	
	public void setParentNode(TextureMapObjectModel model) {
		parentNode = model; 
	}
	
	public TextureMapObjectModel[] getChildrenNodes() {
		return childrenNodes; 
	}
	
	public void setChildrenNodes(TextureMapObjectModel[] children) {
		childrenNodes = children; 
	}
	
	public static void main(String[] args) {
		System.out.println("hehuhest");
	}

}
