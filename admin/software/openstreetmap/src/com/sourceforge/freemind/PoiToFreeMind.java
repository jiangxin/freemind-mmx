/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2012 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.sourceforge.freemind;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import net.osmand.binary.BinaryMapIndexReader;
import net.osmand.binary.BinaryMapPoiReaderAdapter.PoiRegion;
import net.osmand.data.Amenity;
import net.osmand.data.AmenityType;
import net.osmand.osm.MapUtils;

/**
 * @author foltin
 * @date 21.05.2012
 */
public class PoiToFreeMind extends BinaryMapIndexReader {
	/**
	 * @param pRaf
	 * @throws IOException
	 */
	public PoiToFreeMind(RandomAccessFile pRaf) throws IOException {
		super(pRaf);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("OsmAndToFreeMindImporter Version 1.10");
		if(!(args.length == 2 || (args.length == 7 && "-b".equals(args[0])))) {
			System.err.println("Usage: create [-b lat1 lon1 lat2 lon2] <inputfile> <outputfile>");
			System.exit(1);
		}
		String inputFile;
		String outputFile;
		int[] boundingBox = null;
		if(args.length == 7) {
			boundingBox = new int[4];
			for(int i=0;i<4;++i) {
				final double value = Double.parseDouble(args[i+1]);
				System.out.println("Value " + value);
				if ((i % 2) == 1) {
					boundingBox[i] = MapUtils.get31TileNumberX(value);
				} else {
					boundingBox[i] = MapUtils.get31TileNumberY(value);
				}
			}
			inputFile = args[5];
			outputFile = args[6];
		} else {
			inputFile = args[0];
			outputFile = args[1];
		}
		File file = new File(outputFile);
		if(file.exists()) {
			System.err.println("Destination file " + file + " already exists. Please remove it before and retry.");
			System.exit(1);
		}
		RandomAccessFile raf = new RandomAccessFile(new File(inputFile), "r");
		PoiToFreeMind reader = new PoiToFreeMind(raf);
		println("VERSION " + reader.getVersion()); //$NON-NLS-1$
		FileWriter writer = new FileWriter(file);
		long time = System.currentTimeMillis();

		if (true) {
			PoiRegion poiRegion = reader.getPoiIndexes().get(0);

			int sleft;
			int sright;
			int stop;
			int sbottom;
			if (boundingBox == null) {
				sleft = MapUtils.get31TileNumberX(poiRegion.getLeftLongitude());
				sright = MapUtils.get31TileNumberX(poiRegion
						.getRightLongitude());
				stop = MapUtils.get31TileNumberY(poiRegion.getTopLatitude());
				sbottom = MapUtils.get31TileNumberY(poiRegion
						.getBottomLatitude());
			} else {
				sleft = boundingBox[1];
				sright = boundingBox[3];
				stop = boundingBox[0];
				sbottom = boundingBox[2];
			}
			SearchRequest<Amenity> req = buildSearchPoiRequest(sleft, sright, stop, sbottom, -1, new SearchPoiTypeFilter() {
				@Override
				public boolean accept(AmenityType type, String subcategory) {
//					return type == AmenityType.TRANSPORTATION && "fuel".equals(subcategory);
					return true;
				}
			}, null);
			List<Amenity> results = reader.searchPoi(req);
			HashMap<AmenityType, HashMap<String, Vector<Amenity> > > sortedList = new HashMap<AmenityType, HashMap<String, Vector<Amenity> >>(); 
			for (Amenity a : results) {
				if(a.getName().length()>0) {
					if(!sortedList.containsKey(a.getType())) {
						sortedList.put(a.getType(), new HashMap<String, Vector<Amenity> >());
					}
					HashMap<String,Vector<Amenity> > hashMap = sortedList.get(a.getType());
					if(!hashMap.containsKey(a.getSubType())) {
						hashMap.put(a.getSubType(), new Vector<Amenity>());
					}
					Vector<Amenity> vector = hashMap.get(a.getSubType());
					vector.add(a);
//					println(a.getType() + " " + a.getSubType() + " " + a.getName() + " " + a.getLocation());
				}
			}
			long id = 1;
			print(writer, "<map version=\"1.0.0\"><node TEXT=\"");
			writeEncoded(writer, inputFile);
			println(writer, "\" ID=\"" + id
					+ "\">");
			id++;
			println(writer, "<node TEXT=\"Map data (c) OpenStreetMap contributors, CC-BY-SA\" " +
					"LINK=\"http://creativecommons.org/licenses/by-sa/2.0/\" " +
					"POSITION=\"left\" FOLDED=\"false\" ID=\""
					+ id + "\"/>");
			id++;

			Vector<AmenityType> typeKeySet = new Vector<AmenityType>(sortedList.keySet());
			Collections.sort(typeKeySet, new Comparator<AmenityType>() {

				@Override
				public int compare(AmenityType pO1, AmenityType pO2) {
					return pO1.toString().compareToIgnoreCase(pO2.toString());
				}
			});
			for (AmenityType type : typeKeySet) {
				print(writer, "<node TEXT=\"");
				writeEncoded(writer, type.name());
				println(writer, "\" POSITION=\"right\" FOLDED=\"true\" ID=\"" + id
					+ "\">");
				id++;
				Vector<String> subtypeKeyset = new Vector<String>(sortedList.get(type).keySet());
				Collections.sort(subtypeKeyset, new Comparator<String>() {

					@Override
					public int compare(String pO1, String pO2) {
						return pO1.compareToIgnoreCase(pO2);
					}
				});
				for (String subType : subtypeKeyset) {
					print(writer, "  <node TEXT=\"");
					writeEncoded(writer, subType);
					println(writer, "\" POSITION=\"right\" FOLDED=\"true\" ID=\"" + id
					+ "\">");
					id++;
					Vector<Amenity> amenityVector = sortedList.get(type).get(subType);
					Collections.sort(amenityVector, new Comparator<Amenity>() {

						@Override
						public int compare(Amenity pO1, Amenity pO2) {
							return pO1.getName().compareToIgnoreCase(pO2.getName());
						}
					});
					for (Amenity amenity : amenityVector) {
						print(writer, "    <node TEXT=\"");
						writeEncoded(writer, amenity.getName());
						print(writer, "\" ID=\"" + id + "\"");
						id++;
						if (null != amenity.getSite()
								&& !amenity.getSite().isEmpty()) {
							print(writer, " LINK=\"");
							writeEncoded(writer, amenity.getSite());
							print(writer, "\"");
						}
						println(writer, ">");
						println(writer, "      <hook NAME=\"plugins/map/MapNodePositionHolder.properties\">\n"
								+ "        <Parameters XML_STORAGE_MAP_LAT=\""
								+ amenity.getLocation().getLatitude()
								+ "\" "
								+ "XML_STORAGE_MAP_LON=\""
								+ amenity.getLocation().getLongitude()
								+ "\" "
								+ "XML_STORAGE_POS_LAT=\""
								+ amenity.getLocation().getLatitude()
								+ "\" "
								+ "XML_STORAGE_MAP_TOOLTIP_LOCATION=\"false\" "
								+ "XML_STORAGE_POS_LON=\""
								+ amenity.getLocation().getLongitude()
								+ "\" "
								+ "XML_STORAGE_TILE_SOURCE=\"org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource\\$Mapnik\" "
								+ "XML_STORAGE_ZOOM=\""
								+ 16
								+ "\"/>\n"
								+ "      </hook>");
						println(writer, "    </node>");
					}
					println(writer, "  </node>");
				}
				println(writer, "</node>");
			}
			println(writer, "</node></map>");
		}

		println("MEMORY " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())); //$NON-NLS-1$
		println("Time " + (System.currentTimeMillis() - time)); //$NON-NLS-1$
		
		writer.close();

	}

	/**
	 * @param pString
	 * @throws IOException 
	 */
	private static void println(Writer writer, String pString) throws IOException {
		writer.append(pString);
		writer.append("\n");
	}
	/**
	 * @param pString
	 */
	private static void println(String pString) {
		System.out.println(pString);
	}
	/**
	 * @param pString
	 * @throws IOException 
	 */
	private static void print(Writer writer, String pString) throws IOException {
		writer.append(pString);
	}

	private static void writeEncoded(Writer writer, String str) throws IOException {
		for (int i = 0; i < str.length(); i += 1) {
			char ch = str.charAt(i);
			switch (ch) {
			case '<':
				writer.write('&');
				writer.write('l');
				writer.write('t');
				writer.write(';');
				break;
			case '>':
				writer.write('&');
				writer.write('g');
				writer.write('t');
				writer.write(';');
				break;
			case '&':
				writer.write('&');
				writer.write('a');
				writer.write('m');
				writer.write('p');
				writer.write(';');
				break;
			case '"':
				writer.write('&');
				writer.write('q');
				writer.write('u');
				writer.write('o');
				writer.write('t');
				writer.write(';');
				break;
			case '\'':
				writer.write('&');
				writer.write('a');
				writer.write('p');
				writer.write('o');
				writer.write('s');
				writer.write(';');
				break;
			default:
				int unicode = (int) ch;
				if ((unicode < 32) || (unicode > 126)) {
					writer.write('&');
					writer.write('#');
					writer.write('x');
					writer.write(Integer.toString(unicode, 16));
					writer.write(';');
				} else {
					writer.write(ch);
				}
			}
		}
	}

}
