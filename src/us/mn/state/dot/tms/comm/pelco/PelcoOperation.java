/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006-2008  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.pelco;

import us.mn.state.dot.tms.ControllerImpl;
import us.mn.state.dot.tms.VideoMonitor;
import us.mn.state.dot.tms.comm.ControllerOperation;

/**
 * PelcoOperation is the base class for the Pelco comm protocol.
 *
 * @author Timothy Johnson
 * @author Douglas Lau
 */
abstract public class PelcoOperation extends ControllerOperation {

	/** Video monitor to select camera on */
	protected final VideoMonitor monitor;

	/** Camera to perform operation on */
	protected final String camera;

	/** Begin the operation */
	public final void begin() {
		phase = phaseOne();
	}

	/** Create the first real phase of the operation */
	abstract protected Phase phaseOne();

	/** Create a new Pelco operation */
	public PelcoOperation(int p, ControllerImpl c, VideoMonitor m,
		String cam)
	{
		super(p, c);
		monitor = m;
		camera = cam;
	}
}
