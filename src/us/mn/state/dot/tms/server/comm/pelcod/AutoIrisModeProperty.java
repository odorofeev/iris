/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2014  AHMCT, University of California
 * Copyright (C) 2014  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.pelcod;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Pelco D command property for setting auto-iris mode.
 *
 * @author Travis Swanston
 * @author Douglas Lau
 */
public class AutoIrisModeProperty extends PelcoDProperty {

	/** Requested auto-iris mode.  True for auto-iris. */
	protected final boolean autoMode;

	/** Create the property. */
	public AutoIrisModeProperty(boolean a) {
		autoMode = a;
	}

	/** Get the command bits (in the 2 LSBs) */
	@Override
	protected int getCommand() {
		return 0x2d;
	}

	/** Get control byte. */
	protected byte getCtrlByte() {
		if (autoMode)
			return (byte)0x00;
		else
			return (byte)0x01;
	}

	/** Encode a STORE request. */
	@Override
	public void encodeStore(OutputStream os, int drop) throws IOException {
		byte[] pkt = createPacket(drop);
		pkt[4] = (byte)0x00;
		pkt[5] = getCtrlByte();
		pkt[6] = calculateChecksum(pkt);
		os.write(pkt);
	}
}
