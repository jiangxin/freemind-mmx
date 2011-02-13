import waba.fx.*;
import waba.util.*;

/** A class that creates a set of icons appropriately scaled for the device in question. */
public class ScaledIcons {

	protected Image imgHome;
	protected Image imgLink;
	protected Image imgHelp;
	protected Image imgOk;
	protected Image imgNotOk;
	protected Image imgUnknown;
	/* Keep 2 matched lists of icon images and icon names. */
	protected Vector allIcons=new Vector();
	protected Vector iconNames=new Vector();
	protected int iconSize;

	public ScaledIcons(int minSize) {
		iconSize=minSize;
		imgHome=createImage("icHome.bmp","$home");
		imgLink=createImage("icLink.bmp","$system_link");
		imgOk=createImage("icOk.bmp","button_ok");
		imgNotOk=createImage("icNotOk.bmp","button_cancel");
		imgHelp=createImage("icHelp.bmp","help");
		imgUnknown=createImage("icUnknown.bmp","$system_unknown");

		/* Images we don't really need specific links to. */
		createImage("icAlert.bmp","messagebox_warning");
		createImage("icIdea.bmp","idea");
	}

	/** Search the icon names list for this name, and return the relevant icon or
	 * a null.
	 * 
	 * @param findMe
	 * @return
	 */
	public Image findIcon(String findMe) {
		for (int i=0; i<iconNames.size();i++) {
			if (((String)(iconNames.items[i])).equals(findMe)) {
				return (Image)(allIcons.items[i]);
			}
		}
		return null;
	}

	/** Create an image, and add a reference to the vector list using the name.
	 * 
	 * @param imageFile	The icon file to use
	 * @param imageName	Name used in descriptor. 
	 * @return	The Image.
	 */ 
	private Image createImage(String imageFile,String imageName) {
		Image newImg=new Image(imageFile).getScaledInstance(iconSize,iconSize);
		allIcons.add(newImg);
		iconNames.add(imageName);
		return newImg;
	}
}
