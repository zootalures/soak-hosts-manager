package edu.bath.soak.security;

import java.util.Collection;

import edu.bath.soak.model.OrgUnit;
import edu.bath.soak.model.OrgUnitAclEntity;
import edu.bath.soak.model.OwnedEntity;
import edu.bath.soak.model.OwnershipInfo;

/**
 * Helper interface for adapters to the underlying security system
 * 
 * Allows classes to filter objects based on ownership or rights and get
 * informatiom about the state of the user. s
 * 
 * @author cspocc
 * 
 */
public interface SecurityHelper {
	public static final String ADMIN_ROLE = "ROLE_SUPERVISOR";
	public static final String USER_ROLE = "ROLE_SOAK_USER"; 

	/***************************************************************************
	 * Is this use admin?
	 * 
	 * @return
	 */
	public boolean isAdmin();

	/***************************************************************************
	 * 
	 * @return the {@link SoakUserDetails} structure for the currently active
	 *         user (or null if no user is active)
	 */
	public SoakUserDetails getCurrentUser();

	/***************************************************************************
	 * 
	 * @return the user identifier for the currently active user (or null if no
	 *         user is active)
	 */
	public String getCurrentUserId();

	/**
	 * Return the currently endowed organisational units that this user can
	 * administer
	 * 
	 * @return
	 */
	public Collection<OrgUnit> getAllowedOrgUnitsForCurrentUser();

	/**
	 * Returns the sub-list of objects (based on the input) which the current
	 * user can administer
	 * 
	 * @param <T>
	 *            the type of object to filter on
	 * @param entities
	 *            the list of objects to filter
	 * @return a list which is a subset of entities which the user can edit
	 */
	public <T extends OrgUnitAclEntity> Collection<T> filterAllowedEntitiesForUser(
			Collection<T> entities);

	/**
	 * returns the sublist of Owned entities which
	 * 
	 * @param <T>
	 * @param entities
	 * @return
	 */
	public <T extends OwnedEntity> Collection<T> filterAllowedOwnedEntitiesForUser(
			Collection<T> entities);

	/**
	 * Tests if the current user has permissions to edit the object with the
	 * given ownership information (e.g. host)
	 * 
	 * @param h
	 * @return
	 */
	public boolean canEdit(OwnershipInfo h);

	/**
	 * Tests if a the current suer has permission to "use" the given ACL'd
	 * entity
	 * 
	 * @param entity
	 * @return true if the current user can use the given entity , false
	 *         otherwise
	 */
	public boolean canUseOrgUnitAclEntity(OrgUnitAclEntity entity);

	/**
	 * Tests if a user with the given Org Unit Admin rights, has permission to
	 * edit the given ACL entity
	 * 
	 * 
	 * @param entity
	 *            the entity being tested
	 * @param ous
	 *            a collection of OrgUnits (notionally associated with a user)
	 * @return
	 */
	public boolean canUse(OrgUnitAclEntity entity, Collection<OrgUnit> ous);

	/**
	 * Tests if a user with the given single right, has permission to edit the
	 * given ACL entity
	 * 
	 * @param entity
	 * @param ous
	 * @return
	 */
	public boolean canUse(OrgUnitAclEntity entity, OrgUnit ou);

}
