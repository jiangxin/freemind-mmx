import waba.fx.*;
import superwaba.ext.xplat.xml.*;

/** This class holds all the bits the Node class needs to know to build and manage icons. */
public class FmIcon implements NodeIcon {

	private Image img;
	protected boolean internal;	/* True when the image is for system use only, not from MM*/
	protected AttributeList atts;
	
	/** Attach an icon to a node.
	 * 
	 * @param img	The actual image.
	 * @param type	Usually "BUILTIN".
	 * @param name	The name given to the icon by FreeMind's .MM file.
	 */
	public FmIcon(Image img, AttributeList atts) {
		this.img=img;
		this.atts=atts;
		internal=false;
	}

	/** Attach an icon that only has use inside the FreeMindPDA program to
	 * a node. This is used for things like links.
	 * 
	 * @param img	The actual image.
	 */
	public FmIcon(Image img) {
		internal=true;
		this.img=img;
	}

	public Image getImage() {
		return img;
	}

	public int getWidth() {
		return img.getWidth();
	}

}
