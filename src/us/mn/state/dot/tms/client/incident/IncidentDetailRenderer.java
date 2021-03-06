/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2016  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.incident;

import us.mn.state.dot.tms.IncidentDetail;
import us.mn.state.dot.tms.client.widget.IListCellRenderer;

/**
 * Cell renderer used for incident detail.
 *
 * @author Douglas Lau
 */
public class IncidentDetailRenderer extends IListCellRenderer<IncidentDetail> {

	/** Convert value to a string */
	@Override
	protected String valueToString(IncidentDetail value) {
		return value.getDescription();
	}
}
