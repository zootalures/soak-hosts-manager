package edu.bath.soak.util;

import java.io.Serializable;
import java.util.Arrays;

import org.springframework.util.Assert;

public class MacAddress implements Serializable, Comparable<MacAddress> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MacAddress(byte[] data) {
		Assert.notNull(data);
		Assert.isTrue(data.length == 6);
		this.macData = data;

	}

	byte[] macData;

	public static boolean isPartialMac(String s) {
		Assert.notNull(s);
		if (!s.toLowerCase().matches("^([0-9a-f]{2}\\:)+[0-9a-f]{0,2}$")) {
			return false;

		}
		String macParts[] = s.toLowerCase().split("\\:");
		if (macParts.length > 6)
			return false;

		return true;
	}

	/**
	 * parses a string mac address and returns a mac address object
	 * 
	 * @param strMac
	 *            A stringified mac address
	 * @return a new MacAddress orbject representing the mac
	 * @throws NumberFormatException
	 *             if the mac cannot be parsed
	 */
	public static MacAddress fromText(String strMac) {
		if (strMac == null) {
			return null;
		}
		String reduced_mac = strMac.replaceAll(":", "").replaceAll("-", "")
				.replaceAll("\\.", "").toUpperCase();
		if (reduced_mac.length() != 12) {
			throw new IllegalArgumentException(strMac
					+ " does not have enough hex bytes to be a MAC");
		}
		MacAddress mac = new MacAddress(TypeUtils.hexStringToBytes(reduced_mac));
		return mac;
	}

	/***************************************************************************
	 * 
	 * @param macData
	 *            the bytes of the mac address
	 * @return a new Mac address
	 * @throws AssertionError
	 *             if the array is not exactly 6 bytes long
	 */
	public static MacAddress fromBytes(byte[] macData) {
		assert macData != null && macData.length == 6 : "mac must have 6 bytes";
		return new MacAddress(macData);
	}

	public byte[] getMacData() {
		return macData;
	}

	/**
	 * Returns an upper-case colon-seperated version of the mac.
	 */
	public String toString() {

		return String.format("%s:%s:%s:%s:%s:%s", new Object[] {
				TypeUtils.byteToHexString(macData[0]),
				TypeUtils.byteToHexString(macData[1]),
				TypeUtils.byteToHexString(macData[2]),
				TypeUtils.byteToHexString(macData[3]),
				TypeUtils.byteToHexString(macData[4]),
				TypeUtils.byteToHexString(macData[5]) });

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MacAddress other = (MacAddress) obj;
		if (!Arrays.equals(macData, other.macData))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(macData);
		return result;
	}

	public int compareTo(MacAddress o) {
		return toString().compareTo(o.toString());
	}
}
