package plugins.svg;

import java.awt.print.Paper;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

/**
 * @author Andy
 * 
 *         Class is resposible to provide all papers that could be used for PDF
 *         export
 */
class ExportPdfPapers {

	/*
	 * 72(dpi) / 25 (mm/inch) = 0.352 mm/px = (default value of transcoder)
	 */
	private static final double ppmm = 96 / 25.4;

	/**
	 * map to store all papers
	 */
	Map paperFormats = new HashMap();

	/**
	 * constructor
	 * 
	 * @param exportPdf
	 */
	public ExportPdfPapers() {

		initPapers();
	}

	/**
	 * 
	 * @return the names of the given paper formats
	 */
	String[] getPaperNames() {
		Object[] o_names = paperFormats.keySet().toArray();
		String[] names = new String[o_names.length];
		for (int i = 0; i < paperFormats.size(); i++) {
			names[i] = (String) o_names[i];
		}
		return names;
	}

	/**
	 * Initialize the list of papers
	 * 
	 * for more papers see {@link http://en.wikipedia.org/wiki/Paper_size}
	 */
	private void initPapers() {
		// A -Papers
		addPaper(MediaSizeName.ISO_A2, "A2");
		addPaper(MediaSizeName.ISO_A3, "A3");
		addPaper(MediaSizeName.ISO_A4, "A4");
		addPaper(MediaSizeName.ISO_A5, "A5");

		// B - Papers
		addPaper(MediaSizeName.ISO_B2, "B2");
		addPaper(MediaSizeName.ISO_B3, "B3");
		addPaper(MediaSizeName.ISO_B4, "B4");
		addPaper(MediaSizeName.ISO_B5, "B5");

	}

	/**
	 * Add a new paper to the list of papers
	 * 
	 * @param name
	 * @param displayName
	 */
	private void addPaper(MediaSizeName name, String displayName) {
		MediaSize mSize = MediaSize.getMediaSizeForName(name);
		Paper paper = new Paper();
		paper.setSize(mSize.getX(MediaSize.MM) * ppmm, mSize.getY(MediaSize.MM)
				* ppmm);
		paperFormats.put(displayName, paper);
	}

	/**
	 * Determine the paper from a given format string
	 * 
	 * @param formats
	 *            string like A3 or A4 that is one of the values of
	 *            getPaperNames()
	 * @return The paper for the given format
	 */
	Paper determinePaper(String format) {

		Object o = paperFormats.get(format);
		if (o != null && o instanceof Paper) {
			Paper result = (Paper) o;
			return result;
		} else {
			return null;
		}

	}
}
