package edu.bath.soak.util;

/**
 * A very simple Tuple class Why doesn't java already have one of these?!?!?!?!
 * 
 * @author cspocc
 * 
 * @param <Left>
 *            the type of the left hand side
 * @param <Right>
 *            the type of the right hand side
 */
public class Tuple<Left, Right> {

	public Tuple(Left from, Right to) {
		this.from = from;
		this.to = to;
	}

	Left from;
	Right to;

	public Left getFrom() {
		return from;
	}

	public void setFrom(Left from) {
		this.from = from;
	}

	public Right getTo() {
		return to;
	}

	public void setTo(Right to) {
		this.to = to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
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
		final Tuple other = (Tuple) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (to == null) {
			if (other.to != null)
				return false;
		} else if (!to.equals(other.to))
			return false;
		return true;
	}

}
