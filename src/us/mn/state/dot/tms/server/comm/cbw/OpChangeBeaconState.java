/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.cbw;

import java.io.IOException;
import us.mn.state.dot.tms.server.BeaconImpl;
import us.mn.state.dot.tms.server.comm.CommMessage;
import us.mn.state.dot.tms.server.comm.OpDevice;
import us.mn.state.dot.tms.server.comm.PriorityLevel;

/**
 * Operation to change a beacon state.
 *
 * @author Douglas Lau
 */
public class OpChangeBeaconState extends OpDevice<CBWProperty> {

	/** Beacon device */
	private final BeaconImpl beacon;

	/** New state to change beacon */
	private final boolean flash;

	/** Create a new change beacon state operation */
	public OpChangeBeaconState(BeaconImpl b, boolean f) {
		super(PriorityLevel.COMMAND, b);
		beacon = b;
		flash = f;
	}

	/** Operation equality test */
	@Override
	public boolean equals(Object o) {
		if (o instanceof OpChangeBeaconState) {
			OpChangeBeaconState op = (OpChangeBeaconState) o;
			return beacon == op.beacon && flash == op.flash;
		} else
			return false;
	}

	/** Create the second phase of the operation */
	@Override
	protected Phase<CBWProperty> phaseTwo() {
		return new ChangeBeacon();
	}

	/** Phase to change the beacon state */
	protected class ChangeBeacon extends Phase<CBWProperty> {

		/** Change the beacon state */
		protected Phase<CBWProperty> poll(
			CommMessage<CBWProperty> mess) throws IOException
		{
			int p = beacon.getPin();
			CommandProperty prop = new CommandProperty(p, flash);
			mess.add(prop);
			mess.storeProps();
			Integer vp = beacon.getVerifyPin();
			if (vp != null)
				return new ChangeVerify(vp);
			else
				return null;
		}
	}

	/** Phase to change current sensor (verify) circuit */
	protected class ChangeVerify extends Phase<CBWProperty> {

		/** Verify pin */
		private final int pin;

		/** Create change verify phase */
		protected ChangeVerify(int p) {
			pin = p;
		}

		/** Enable verify circuit */
		protected Phase<CBWProperty> poll(
			CommMessage<CBWProperty> mess) throws IOException
		{
			CommandProperty prop = new CommandProperty(pin, flash);
			mess.add(prop);
			mess.storeProps();
			return null;
		}
	}

	/** Cleanup the operation */
	@Override
	public void cleanup() {
		if (isSuccess())
			beacon.setFlashingNotify(flash);
		super.cleanup();
	}
}
