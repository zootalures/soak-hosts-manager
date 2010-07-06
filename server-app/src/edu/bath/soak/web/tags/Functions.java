package edu.bath.soak.web.tags;

import java.util.Date;
import java.util.Set;

import org.springframework.util.Assert;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.model.OwnershipInfo;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.security.AcegiSecurityHelperImpl;

/**
 * JSTL functions See the soak.tld file for more information
 * 
 * @author cspocc
 * 
 */
public class Functions {
	public static boolean contains(Set<Object> set, Object object) {
		return set.contains(object);
	}

	public static boolean canEdit(OwnershipInfo own) {
		return new AcegiSecurityHelperImpl().canEdit(own);
	}

	public static boolean canAddToSubnet(Subnet s) {
		return new AcegiSecurityHelperImpl().canUseOrgUnitAclEntity(s);
	}

	public static boolean canUseEntity(OrgUnitAclEntity e) {
		return new AcegiSecurityHelperImpl().canUseOrgUnitAclEntity(e);
	}

	public static boolean orgUnitCanUseEntity(OrgUnit ou, OrgUnitAclEntity e) {
		return new AcegiSecurityHelperImpl().canUse(e, ou);
	}

	static class UsageSegment {
		UsageSegment(long interval, String part) {
			this.interval = interval;
			this.part = part;
		}

		long interval;
		String part;
	}

	static final UsageSegment timeSegments[] = new UsageSegment[] {
			new UsageSegment(24 * 3600 * 365, "year"),
			new UsageSegment(24 * 3600 * 30, "month"),
			new UsageSegment(24 * 3600 * 7, "week"),
			new UsageSegment(24 * 3600, "day"), new UsageSegment(3600, "hour"),
			new UsageSegment(60, "min"), new UsageSegment(1, "sec") };

	/**
	 * Returns a human readable relative time difference between two dates
	 * 
	 * @param date
	 *            the date to consider, should be before relative may be null
	 * @param relative
	 *            the date to compare to must not be null
	 * @return the human readalbe time or "" if date is not specified
	 */
	public static String relativeTime(Date date, Date relative) {

		if (date == null)
			return "";
		Assert.notNull(relative, "Relative date must be specified");
		long diff = relative.getTime() - date.getTime();
		diff = diff / 1000;
		if (diff <= 0) {
			return "0 secs";
		}

		UsageSegment segment = null;
		for (int i = 0; i < timeSegments.length; i++) {
			UsageSegment s = timeSegments[i];
			if (diff >= s.interval) {
				segment = s;
				break;
			}
		}
		Assert.notNull(segment);
		long num = 0;

		if (segment.interval > 0)
			num = diff / segment.interval;
		if (num == 1L) {
			return "1 " + segment.part;
		} else {
			return num + " " + segment.part + "s";
		}

	}

	/***************************************************************************
	 * Converts a date to a time relative to now and
	 * 
	 * @param date
	 * @return
	 */
	public static String relativeTime(Date date) {
		return relativeTime(date, new Date());
	}
}
