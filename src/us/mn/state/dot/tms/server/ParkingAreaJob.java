/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2018  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server;

import java.util.Calendar;
import java.util.Iterator;
import us.mn.state.dot.sched.Job;
import us.mn.state.dot.sched.TimeSteward;
import us.mn.state.dot.tms.ParkingArea;
import us.mn.state.dot.tms.ParkingAreaHelper;
import us.mn.state.dot.tms.TMSException;

/**
 * Job to periodically calculate parking area availability.
 *
 * @author Douglas Lau
 */
public class ParkingAreaJob extends Job {

	/** Seconds to offset each poll from start of interval */
	static private final int OFFSET_SECS = 13;

	/** Create a new job to calculate parking area availability */
	public ParkingAreaJob() {
		super(Calendar.SECOND, 30, Calendar.SECOND, OFFSET_SECS);
	}

	/** Perform the job */
	@Override
	public void perform() throws TMSException {
		updateAvailability();
	}

	/** Update availability for all parking areas */
	private void updateAvailability() throws TMSException {
		Iterator<ParkingArea> it = ParkingAreaHelper.iterator();
		while (it.hasNext()) {
			ParkingArea pa = it.next();
			if (pa instanceof ParkingAreaImpl) {
				ParkingAreaImpl pai = (ParkingAreaImpl) pa;
				pai.updateAvailable();
			}
		}
	}
}
