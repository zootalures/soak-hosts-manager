package edu.bath.soak.model;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Proxy;

@Entity
//@Proxy(lazy=false)
@Embeddable
public class ChangeInfo implements Serializable{

	private Date changedAt;
	private Date createdAt;
	private String changedBy;
	private String createdBy;
	@Column(name="changed_at")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getChangedAt() {
		return changedAt;
	}
	@Column(name="changed_by")
	public String getChangedBy() {
		return changedBy;
	}
	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedAt() {
		if(null==createdAt)
			return new Date() ;
		return createdAt;
	}
	@Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}
	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}
	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((changedAt == null) ? 0 : changedAt.hashCode());
		result = prime * result
				+ ((changedBy == null) ? 0 : changedBy.hashCode());
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ChangeInfo other = (ChangeInfo) obj;
		if (changedAt == null) {
			if (other.changedAt != null)
				return false;
		} else if (!changedAt.equals(other.changedAt))
			return false;
		if (changedBy == null) {
			if (other.changedBy != null)
				return false;
		} else if (!changedBy.equals(other.changedBy))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		return true;
	}

}
