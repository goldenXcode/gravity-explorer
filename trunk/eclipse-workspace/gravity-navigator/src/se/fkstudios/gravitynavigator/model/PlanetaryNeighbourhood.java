package se.fkstudios.gravitynavigator.model;

import com.badlogic.gdx.maps.MapLayer;

public class PlanetaryNeighbourhood extends MapLayer {
	
	private  MapObjectModel dominatingMapObject;

	public MapObjectModel getDominatingMapObject() {
		return dominatingMapObject;
	}

	public void setDominatingMapObject(MapObjectModel dominatingMapObject) {
		this.dominatingMapObject = dominatingMapObject;
	}	
}
