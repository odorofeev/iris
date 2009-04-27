/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2009  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.kml;

import java.io.IOException;
import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;

import us.mn.state.dot.tms.DMSImpl;
import us.mn.state.dot.tms.DMSList;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.SignMessage;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.I18NMessages;

/**
 * A KML file, used to write kml and kmz files. An existing file is 
 * overwritten. The file extension is used to determine if a kml or
 * kmz file should be written (kml or kmz). Case doesn't matter.
 * The kml/kmz file name is specified by a system attribute. Another
 * system attribute is used to turn on/off the kml/kmz file writing
 * functionality.
 *
 * @author Michael Darter
 * @created 11/25/08
 * @see KmlObject
 */
public class KmlFile 
{
	/** newline */
	final static String R = "\n";

	/** kml file author comment */
	protected static final String AUTHOR_NOTE = 
		"This file was generated by the IRIS open-source ATMS";

	/** kml doc to write */
	protected KmlDocument m_doc;

	/** constructor 
	 *  @param doc An object that implements the KmlDocument interface.
	 *  @see KmlDocument, KmlObject
	 */
	public KmlFile(KmlDocument doc) {
		m_doc = doc;
	}

	/** write kml file, overwriting existing file.
	 *  @param fname File name to write. Should end in "kml" or "kmz".
	 *  @return true on success else false on error. */
	public boolean writeKmlKmz(String fname) {
		if(fname == null || fname.length() <=0) {
			System.err.println("KmlFile.writeKml(): warning: " + 
				" bogus kml/kmz file name: "+fname);
			return false;
		}

		// determine if should write kml or kmz
		boolean writeKml;
		if(fname.toLowerCase().endsWith(".kml"))
			writeKml = true;
		else if(fname.toLowerCase().endsWith(".kmz"))
			writeKml = false;
		else {
			System.err.println("KmlFile.writeKmlKmz(): " +
				"warning: bogus kml/kmz file name " +
				"ignored: "+fname);
			return false;
		}

		// write file
		OutputStream os = null;
		boolean ok = false;
		try {
			// open stream for kml
			if(writeKml) {
				File f = new File(fname);
				os = new FileOutputStream(f.getAbsolutePath());
			// open stream for kmz
			} else {
				File f = new File(fname);
				FileOutputStream dest = new FileOutputStream(
					f.getAbsolutePath());
				ZipOutputStream ostemp = new ZipOutputStream(
					new BufferedOutputStream(dest));
				// the internal file name doesn't matter
				ZipEntry entry = new ZipEntry("irisobjs.kml");
				ostemp.putNextEntry(entry);
				os = ostemp;
			}
			// write
			ok = createFile(os);

		} catch(IOException ex) {
			System.err.println("KmlFile.writeKml(): ex: " + ex);
			return false;
		// catch every problem
		} catch(Exception ex) {
			System.err.println("KmlFile.writeKml(): ex: " + ex);
			ex.printStackTrace();
			return false;
		} finally {
			if(!close(os))
				return false;
		}
		return ok;
	}

	/** create kml file */
	protected boolean createFile(OutputStream os) {
		if(m_doc == null || os==null)
			return false;
		StringBuilder sb = new StringBuilder();

		// start
		Kml.start(sb);
		Kml.comment(sb, AUTHOR_NOTE + " " + new Date());
		sb.append(R);

		// body
		sb.append(KmlRenderer.render(m_doc));

		// end
		Kml.end(sb);

		// write
		try {
			os.write(sb.toString().getBytes());
		} catch(IOException ex) {
			System.err.println("KmlFile.createFile((): ex: " + ex);
			return false;
		// catch every problem
		} catch(Exception ex) {
			System.err.println("KmlFile.createFile((): ex: " + ex);
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/** close */
	protected boolean close(OutputStream os) {
		try {
			if(os != null)
				os.close();
			return true;
		} catch(Exception ex) {
			System.err.println("KmlFile.close(): ex: " + ex);
		}
		return false;
	}

	/** create and write iris server kml file */
	public static void writeServerFile(KmlDocument doc) {
		if(doc == null)
			return;
		if(!SystemAttrEnum.KML_FILE_ENABLE.getBoolean())
			return;
		String fname = SystemAttrEnum.KML_FILENAME.getString();
		if(fname == null || fname.length() <=0 ) {
			System.err.println("KmlFile.writeServerFile(): " +
				"warning: bogus kml file name: "+fname);
			return;
		}
		KmlFile kf = new KmlFile(doc);
		if(kf != null) {
			if(kf.writeKmlKmz(fname))
				System.err.println("Wrote " + fname);
			else
				System.err.println("Failed to write " + fname);
		}
	}
}
