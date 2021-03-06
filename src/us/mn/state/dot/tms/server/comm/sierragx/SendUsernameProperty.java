/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015-2016  SRF Consulting Group
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
package us.mn.state.dot.tms.server.comm.sierragx;

import java.io.InputStream;
import java.io.IOException;
import us.mn.state.dot.tms.utils.LineReader;
import us.mn.state.dot.tms.server.comm.PromptReader;

/**
 * Property to send a username and wait for a "Password:"
 * prompt-style response (no trailing EOL).
 *
 * @author John L. Stanley
 * @author Douglas Lau
 */
public class SendUsernameProperty extends SierraGxProperty {

	/** Create a new send username property */
	public SendUsernameProperty(String un) {
		super(un + "\r");
	}

	/** Create a new line reader.
	 * @param is Input stream to read. */
	@Override
	protected LineReader newLineReader(InputStream is) throws IOException {
		return new PromptReader(is, MAX_CHARS, "Password: ");
	}

	protected boolean bGotPwPrompt = false;

	public boolean gotPwPrompt() {
		return bGotPwPrompt;
	}

	@Override
	protected boolean parseResponse(String resp) throws IOException {
		if (resp.contains("Password:")) {
			bGotPwPrompt = true;
			return true;  // found it, we're done
		}
		return false;  // keep looking
	}

	/** Get a string representation */
	@Override
	public String toString() {
		return "GotPwPrompt:" + bGotPwPrompt;
	}
}
