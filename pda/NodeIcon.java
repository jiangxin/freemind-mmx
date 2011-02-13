import waba.fx.*;

/** This class holds all the bits the Node class needs to know to build and manage icons. */
public interface NodeIcon  {

	/** Returns the current icon for the image. This may change. */
	public Image getImage();

	/** Return the width of the icon. Need not necessarily be the actual
	 * size of the image.
	 * @return	Image width in pixels.
	 */
	public int getWidth();

}
