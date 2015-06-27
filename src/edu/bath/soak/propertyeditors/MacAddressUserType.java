package edu.bath.soak.propertyeditors;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.springframework.util.Assert;

import edu.bath.soak.util.MacAddress;

/**
 * Mac address mapping type for hibernate
 * 
 * @author cspocc
 * 
 */
public class MacAddressUserType implements UserType,Serializable {

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object arg) throws HibernateException {
		return arg;
	}

	public boolean equals(Object mac1, Object mac2) throws HibernateException {
		if (mac1 != null) {
			return mac1.equals(mac2);
		} else {
			return mac2 == null;
		}
	}

	public int hashCode(Object val) throws HibernateException {
		return val.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] columns, Object parent)
			throws HibernateException, SQLException {
		Assert.isTrue(columns.length == 1);
		String val = rs.getString(columns[0]);
		if (!rs.wasNull()) {
			if (val != null) {
				return MacAddress.fromText(val);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public void nullSafeSet(PreparedStatement ps, Object val, int idx)
			throws HibernateException, SQLException {
		String dbval = null;
		if (val != null) {
			if (val instanceof MacAddress) {
				dbval = ((MacAddress) val).toString();

			} else {
				dbval = val.toString();
			}
		}

		ps.setString(idx, dbval);
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return MacAddress.class;
	}

	static int[] sqlTypes = new int[] { Types.VARCHAR };

	public int[] sqlTypes() {
		return sqlTypes;
	}

}
