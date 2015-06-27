package edu.bath.soak.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.util.Assert;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.model.OwnedEntity;
import edu.bath.soak.model.OwnershipInfo;
import edu.bath.soak.model.OrgUnitAcl.Permission;

/**
 * Implements security advice functions for various objects using the Acegi
 * security context holder and the stored GrantedAuthorities associated with the
 * context.
 * 
 * @author cspocc
 * 
 */
public class AcegiSecurityHelperImpl implements SecurityHelper {

	public SoakUserDetails getCurrentUser() {
		SecurityContext sctx = SecurityContextHolder.getContext();

		if (sctx == null) {
			return null;
		}

		if (sctx.getAuthentication() == null)
			return null;

		return (SoakUserDetails) sctx.getAuthentication().getPrincipal();

	}

	public boolean isAdmin() {
		SecurityContext sctx = SecurityContextHolder.getContext();
		Assert.notNull(sctx);
		Assert.notNull(sctx.getAuthentication());

		for (GrantedAuthority ga : sctx.getAuthentication().getAuthorities()) {
			if (ga.getAuthority().equals(ADMIN_ROLE))
				return true;
		}
		return false;
	}

	/**
	 * this returns a collection of allowed org units for this according to the
	 * bound security context for admin users this does not return all org
	 * units.
	 */
	public Collection<OrgUnit> getAllowedOrgUnitsForCurrentUser() {
		SecurityContext sctx = SecurityContextHolder.getContext();

		List<OrgUnit> orgUnits = new ArrayList<OrgUnit>();
		for (GrantedAuthority ga : sctx.getAuthentication().getAuthorities()) {
			if (ga instanceof OuAdminAuthority) {
				orgUnits.add(((OuAdminAuthority) ga).getOu());
			}
		}
		return orgUnits;
	}

	public boolean canUse(OrgUnitAclEntity entity, Collection<OrgUnit> ous) {

		orgUnitLoop: for (OrgUnit orgUnit : ous) {
			OrgUnitAclEntity currentEntity = entity;
			
			while (currentEntity != null) {
				Permission perm = currentEntity.getOrgUnitAcl().getAclEntries()
						.get(orgUnit);
				if (perm != null) {
					if (perm.equals(Permission.ALLOWED)) {
						return true;
					} else if (perm.equals(Permission.DENIED)) {
						continue orgUnitLoop;
					}

				}

				currentEntity = currentEntity.aclParent();
			}
		}
		return false;

	}

	public <T extends OwnedEntity> Collection<T> filterAllowedOwnedEntitiesForUser(
			Collection<T> entities) {
		ArrayList<T> entitiesOut = new ArrayList<T>();
		boolean isAdmin = isAdmin();

		for (T ent : entities) {
			if (isAdmin || canEdit(ent.getOwnership())) {
				entitiesOut.add(ent);
			}

		}
		return entitiesOut;
	}

	public <T extends OrgUnitAclEntity> Collection<T> filterAllowedEntitiesForUser(
			Collection<T> entities) {
		ArrayList<T> entitiesOut = new ArrayList<T>();
		boolean isAdmin = isAdmin();

		for (T ent : entities) {
			if (isAdmin || canUseOrgUnitAclEntity(ent)) {
				entitiesOut.add(ent);
				continue;
			}

		}
		return entitiesOut;
	}

	public boolean canUse(OrgUnitAclEntity entity, OrgUnit ou) {
		return canUse(entity, Collections.singleton(ou));
	}

	public boolean canUseOrgUnitAclEntity(OrgUnitAclEntity entity) {
		return isAdmin() || canUse(entity, getAllowedOrgUnitsForCurrentUser());
	}

	public boolean canEdit(OwnershipInfo oi) {
		return isAdmin()
				|| getAllowedOrgUnitsForCurrentUser().contains(oi.getOrgUnit());
	}

	public String getCurrentUserId() {
		SoakUserDetails sud = getCurrentUser();
		if (sud != null)
			return sud.getUsername();
		else
			;
		return null;
	}
}
