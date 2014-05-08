package com.plugtree.dm.core;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

	public static long timeAgo(Date date, TimeUnit unit) {
		long diff = new Date().getTime() - date.getTime();
		return unit.convert(diff, TimeUnit.MILLISECONDS);
	}
}
