package edu.bath.soak.model;

/**
 * A entity which is simply owned by a user
 * 
 * @author cspocc
 * 
 */
public interface OwnedEntity {
	public OwnershipInfo getOwnership();
}
