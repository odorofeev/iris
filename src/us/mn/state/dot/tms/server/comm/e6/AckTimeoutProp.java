/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.e6;

import java.io.IOException;
import us.mn.state.dot.tms.server.comm.ParsingException;

/**
 * Data acknowledge timeout property.
 *
 * @author Douglas Lau
 */
public class AckTimeoutProp extends E6Property {

	/** System information command */
	static private final Command CMD =new Command(CommandGroup.SYSTEM_INFO);

	/** Store command code */
	static private final int STORE = 0x0018;

	/** Query command code */
	static private final int QUERY = 0x0019;

	/** Protocol values */
	public enum Protocol {
		udp_ip, serial, serial_debug;
		static public Protocol fromOrdinal(int o) {
			for (Protocol p: values())
				if (p.ordinal() == o)
					return p;
			return null;
		}
	};

	/** Communication protocol */
	private final Protocol protocol;

	/** Data acknowledge timeout (ms) */
	private int timeout = 0;

	/** Create a new acknowledge timeout property */
	public AckTimeoutProp(Protocol p) {
		protocol = p;
	}

	/** Get the command */
	@Override
	public Command command() {
		return CMD;
	}

	/** Get the query packet data */
	@Override
	public byte[] queryData() {
		byte[] d = new byte[3];
		format16(d, 0, QUERY);
		format8(d, 2, protocol.ordinal());
		return d;
	}

	/** Parse a received packet */
	public void parse(byte[] d) throws IOException {
		if (d.length != 7)
			throw new ParsingException("DATA LEN: " + d.length);
		if (parse16(d, 2) != QUERY)
			throw new ParsingException("SUB CMD");
		if (Protocol.fromOrdinal(parse8(d, 4)) != protocol)
			throw new ParsingException("PROTOCOL");
		timeout = parse16(d, 5);
	}

	/** Get a string representation */
	@Override
	public String toString() {
		return protocol + " ack timeout: " + timeout + " ms";
	}
}