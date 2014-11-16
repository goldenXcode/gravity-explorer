package se.fkstudios.gravityexplorer.view;


/**
 * Class holding global but changeable rendering options. Uses singleton pattern. 
 * @author kristofer
 */
public class RenderOptions {
	
	/*Rendering options*/
	public boolean debugRender = false;
	
	/*Singelton stuff*/
	private static final RenderOptions instance = new RenderOptions();
	
	private RenderOptions() {}
	
	/**
	 * Returns the only allowed instance of this object.
	 * @return self
	 */
	public static RenderOptions getInstance() {
		return instance;
	}
}
