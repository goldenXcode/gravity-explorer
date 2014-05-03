package se.fkstudios.gravitynavigator.view;


/**
 * Class holding global but changeable rendering options. Uses singleton pattern. 
 * @author kristofer
 */
public class RenderingOptions {
	
	/*Rendering options*/
	public boolean debugRender = false;
	
	/*Singelton stuff*/
	private static final RenderingOptions instance = new RenderingOptions();
	
	private RenderingOptions() {}
	
	/**
	 * Returns the only allowed instance of this object.
	 * @return self
	 */
	public static RenderingOptions getInstance() {
		return instance;
	}
}
