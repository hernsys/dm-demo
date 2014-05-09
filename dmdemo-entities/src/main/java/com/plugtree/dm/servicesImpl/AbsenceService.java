package com.plugtree.dm.servicesImpl;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.plugtree.dm.entities.Employee;

public class AbsenceService {
	private AbsenceService() {
		// Non-instantiable class
	}

	/**
	 * Returns the total absence days that the Employee will be out of office,
	 * between the start and end dates.
	 * 
	 * @param e
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static Integer getAbsenceDays(Employee e, Date startDate,
			Date endDate) {
		// TODO: Real service, taking into account holidays, etc
		if (startDate == null || endDate == null || (startDate.after(endDate))) {
			throw new IllegalArgumentException(
					"Start and End dates can not be null. End date must has a value after the Start Date.");
		}
		Days days = Days.daysBetween(new DateTime(startDate), new DateTime(
				endDate));
		return days.getDays();
	}
}
