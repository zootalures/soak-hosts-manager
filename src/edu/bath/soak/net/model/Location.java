package edu.bath.soak.net.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Formula;
import org.springframework.util.StringUtils;

@Entity
@Embeddable
@XmlType
public class Location implements Serializable {
	private String building;
	private String room;

	public Location() {

	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	@Formula(value = "concat(building,room)")
	public String getFullLocation() {
		return toString();
	}

	public void setFullLocation(String txt) {
		// does nothing
	}

	public String toString() {
		String str = "";
		if (StringUtils.hasText(building)) {
			str = building;
			if (StringUtils.hasText(room))
				str += " ";
		}
		if (StringUtils.hasText(room))
			str += room;
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((building == null) ? 0 : building.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
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
		final Location other = (Location) obj;
		if (building == null) {
			if (other.building != null)
				return false;
		} else if (!building.equals(other.building))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		return true;
	}
}
