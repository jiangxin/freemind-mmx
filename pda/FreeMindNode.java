import superwaba.ext.xplat.xml.*;

public class FreeMindNode {
	public String description;
	public AttributeList atts;
	public int nodeType;

	public FreeMindNode(String txt) {
		description = txt;
		atts = null;
	}

	public FreeMindNode(String txt, AttributeList atts, int type) {
		description = txt;
		this.atts = new AttributeList(atts);	/* Make a copy of this volatile attribute list. */
		nodeType = type;
	}

	/**
	 * If we've a description, return it. Otherwise return the TEXT attribute.
	 * If that is null, return an empty string.
	 */
	public String toString() {
		String s=null;

		if (description != null) {
			s = description;
		} else if (atts != null) {
			s = atts.getAttributeValue("TEXT");
		}

		if (s == null) {
			return "";
		} else {
			return s;
		}
	}

	/** Fetch this node's attribute list. */
	AttributeList getAttributeList() {
		return this.atts;
	}
}
