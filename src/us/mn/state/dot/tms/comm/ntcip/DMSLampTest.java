/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2002-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.ntcip;

import java.io.IOException;
import us.mn.state.dot.tms.Base64;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSImpl;
import us.mn.state.dot.tms.SystemAttributeHelper;
import us.mn.state.dot.tms.comm.AddressedMessage;

/**
 * Operation to perform a lamp test on a DMS
 *
 * @author Douglas Lau
 */
public class DMSLampTest extends DMSOperation {

	/** Create a new DMS lamp test object */
	public DMSLampTest(DMSImpl d) {
		super(COMMAND, d);
	}

	/** Create the first real phase of the operation */
	protected Phase phaseOne() {
		return new InitialStatus();
	}

	/** Phase to query the initial status of lamp test activation */
	protected class InitialStatus extends Phase {

		/** Query the initial status of lamp test activation */
		protected Phase poll(AddressedMessage mess) throws IOException {
			LampTestActivation test = new LampTestActivation();
			mess.add(test);
			try {
				mess.getRequest();
			}
			catch(SNMP.Message.NoSuchName e) {
				DMS_LOG.log(dms.getName() + ": " +
					e.getMessage());
				return null;
			}
			if(test.getInteger() == LampTestActivation.NO_TEST)
				return new ActivateLampTest();
			else {
				DMS_LOG.log(dms.getName() + ": " + test);
				return null;
			}
		}
	}

	/** Phase to activate the lamp test */
	protected class ActivateLampTest extends Phase {

		/** Activate the lamp test */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(new LampTestActivation());
			mess.setRequest();
			return new CheckTestCompletion();
		}
	}

	/** Phase to check for test completion */
	protected class CheckTestCompletion extends Phase {

		/** Lamp test activation */
		protected final LampTestActivation test =
			new LampTestActivation();

		/** Time to stop checking if the test has completed */
		protected final long expire = System.currentTimeMillis() + 
			SystemAttributeHelper.getDmsLampTestTimeout() * 1000;

		/** Check for test completion */
		protected Phase poll(AddressedMessage mess) throws IOException {
			mess.add(test);
			mess.getRequest();
			if(test.getInteger() == LampTestActivation.NO_TEST)
				return new QueryLampStatus();
			if(System.currentTimeMillis() > expire) {
				DMS_LOG.log(dms.getName() + ": lamp test " +
					"timeout expired -- giving up");
				return null;
			} else
				return this;
		}
	}

	/** Phase to query lamp status */
	protected class QueryLampStatus extends Phase {

		/** Query lamp status */
		protected Phase poll(AddressedMessage mess) throws IOException {
			LampFailureStuckOff l_off = new LampFailureStuckOff();
			LampFailureStuckOn l_on = new LampFailureStuckOn();
			mess.add(l_off);
			mess.add(l_on);
			mess.getRequest();
			dms.setLampStatus(createFailureBitmaps(l_off, l_on));
			return null;
		}
	}

	/** Encode failure bitmaps to Base64 */
	protected String[] createFailureBitmaps(LampFailureStuckOff l_off,
		LampFailureStuckOn l_on)
	{
		String[] b64 = new String[2];
		b64[DMS.STUCK_OFF_BITMAP] =
			Base64.encode(l_off.getOctetString());
		b64[DMS.STUCK_ON_BITMAP] = Base64.encode(l_on.getOctetString());
		return b64;
	}
}
