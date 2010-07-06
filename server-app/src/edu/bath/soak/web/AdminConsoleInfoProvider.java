package edu.bath.soak.web;

import java.util.List;

import edu.bath.soak.web.admin.AdminConsoleObject;

public interface AdminConsoleInfoProvider {
	public List<AdminConsoleObject> getAdminConsoleInfo();
}
