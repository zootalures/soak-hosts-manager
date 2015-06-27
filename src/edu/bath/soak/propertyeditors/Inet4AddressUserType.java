package edu.bath.soak.propertyeditors;

import java.io.Serializable;
import java.net.Inet4Address;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;
import org.springframework.util.Assert;

import edu.bath.soak.util.TypeUtils;

/**
 * Custom User type for mapping Inet4Addresses into Hibernate
 * 
 * @author cspocc
 * 
 */
public class Inet4AddressUserType extends Object implements UserType,Serializable {

	public Object assemble(Serializable ipval, Object owner)
			throws HibernateException {
			return (Inet4Address)ipval;
	}

	public Object deepCopy(Object arg0) throws HibernateException {
		return arg0;
	}

	public Serializable disassemble(Object arg0) throws HibernateException {
		
		return (Inet4Address)arg0;
	}

	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		if (arg0 != null)
			return arg0.equals(arg1);
		else {
			return arg1 == null;
		}
	}

	public int hashCode(Object ip) throws HibernateException {
		return ip.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		Assert.isTrue(names.length == 1);

		Long val = (Long) rs.getObject(names[0]);
		if (val == null || rs.wasNull())
			return null;
		else
			return TypeUtils.intToIP(val);
	}

	public void nullSafeSet(PreparedStatement ps, Object value, int argIdx)
			throws HibernateException, SQLException {
		if (value != null) {
			Assert.isInstanceOf(Inet4Address.class, value);
			ps.setLong(argIdx, TypeUtils.ipToInt((Inet4Address) value));
		} else {
			ps.setObject(argIdx, null);
		}

	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return Inet4Address.class;
	}

	static int[] sqlTypes = new int[] { java.sql.Types.BIGINT };

	public int[] sqlTypes() {
		return sqlTypes;

	}

}
