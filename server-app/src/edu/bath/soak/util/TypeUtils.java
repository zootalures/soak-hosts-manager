package edu.bath.soak.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import edu.bath.soak.net.model.IPRange;

public class TypeUtils {

	static Logger log = Logger.getLogger(TypeUtils.class);

	/**
	 * Converts a 4 byte array of unsigned bytes to an long
	 * 
	 * @param b
	 *            an array of 4 unsigned bytes
	 * @return a long representing the unsigned int
	 */
	public static final long unsignedIntToLong(byte[] b) {
		long l = 0;
		l |= b[0] & 0xFF;
		l <<= 8;
		l |= b[1] & 0xFF;
		l <<= 8;
		l |= b[2] & 0xFF;
		l <<= 8;
		l |= b[3] & 0xFF;
		return l;
	}

	/**
	 * Converts an IP address object to its numerical representation
	 * 
	 * Note that the output type is signed, so comparison will not work in a
	 * sensible way.
	 * 
	 * @param inip
	 *            the IP address to convert.
	 * @return a long containing the numerical value of this address
	 */
	public static long ipToInt(Inet4Address inip) {
		return unsignedIntToLong(inip.getAddress());
		// return new BigInteger(inip.getAddress()).longValue();

	}

	public static Inet4Address txtToIP(String ip) {
		String parts[] = ip.split("\\.");
		if (parts.length != 4)
			throw new IllegalArgumentException(ip + " is not an IP address");

		int nparts[] = new int[4];
		for (int i = 0; i < 4; i++) {
			try {
				nparts[i] = Integer.parseInt(parts[i]);
				if (nparts[i] < 0 || nparts[i] > 255) {
					throw new IllegalArgumentException(ip
							+ "is not an IP address");
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(ip + " is not an IP address");
			}
		}

		try {
			return (Inet4Address) InetAddress.getByName(ip);

		} catch (Exception e) {
			throw new NumberFormatException("Invalid IP Address"
					+ e.getMessage());
		}

	}

	public static int numNetmaskBits(Inet4Address netmask) {
		int x = (32 - (Long.numberOfTrailingZeros(TypeUtils.ipToInt(netmask))));
		if (x < 0) {
			return 0;
		}
		return x;
	}

	/**
	 * Converts a ptr reverse address into its corresponding IP address
	 * 
	 * @param name
	 * @return
	 */
	public static Inet4Address ptrNameToIP(String name) {
		Assert.isTrue(name.endsWith(".in-addr.arpa."), "address does not appear to be a PTR address");
		String[] parts = name.substring(0,
				name.length() - ".in-addr.apra.".length()).split("\\.");
		String ip = "";
		for (int i = parts.length - 1; i >= 0; i--) {
			ip += parts[i];
			if (i != 0)
				ip = ip + ".";
		}

		return TypeUtils.txtToIP(ip);

	}
	
	public static String intToIPTxt(long intip) {
		return intToIP(intip).getHostAddress();
	}

	/***************************************************************************
	 * Converts a long into an IP address
	 * 
	 * @param intip
	 * @return A new IP address Object
	 * @throws NumberFormatException
	 *             if the long is not of the appropriate length
	 */
	public static Inet4Address intToIP(long intip) {

		try {
			int val = (int) intip;
			byte b[] = new byte[4];
			int i, shift;

			for (i = 0, shift = 24; i < 4; i++, shift -= 8)
				b[i] = (byte) (0xFF & (val >> shift));

			return (Inet4Address) InetAddress.getByAddress(b);
		} catch (UnknownHostException e) {
			throw new NumberFormatException(e.getMessage());
		}
	}

	private static String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7",
			"8", "9", "A", "B", "C", "D", "E", "F" };

	/**
	 * Returns the Hex value of a byte as a string
	 * 
	 * @param b
	 * @return A string between "00" and "FF" indicating the hex value of the
	 *         given byte
	 */
	public static String byteToHexString(byte b) {
		String b1, b2;
		byte ch = (byte) (b & 0xF0);
		ch = (byte) (ch >>> 4);
		ch = (byte) (ch & 0x0F);
		b1 = pseudo[(int) ch];
		ch = (byte) (b & 0x0F);
		b2 = pseudo[(int) ch];
		return b1 + b2;
	}

	/**
	 * Extracts the byte array value from a hex string .
	 * 
	 * @param hexString
	 *            a hexidecimal string to convert to bytes.
	 * @return the byte array value of the given hex string
	 * @throws IllegalArgumentException
	 *             if the string is not a hex string or does not have an even
	 *             number of characters
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString.length() % 2 != 0) {
			throw new IllegalArgumentException("String must be of even length");
		}

		byte data[] = new byte[hexString.length() / 2];
		for (int i = 0; i < hexString.length() / 2; i++) {
			int bindex = i * 2;
			data[i] = (byte) Integer.parseInt(hexString.substring(bindex,
					bindex + 2).toLowerCase(), 16);

		}
		return data;

	}

	/**
	 * Checks that a given base address and bit mask is a valid CIDR address. an
	 * address is valid if the base address has zero bits for 32- nbits at the
	 * end of its address
	 * 
	 * @param baseIP
	 *            the base ip to use for the network
	 * @param nbits
	 *            the number of bits to be masked from this address to create
	 *            the network address
	 * @return true if the address and bit range are valid for a CIDR adddres,
	 *         false if not
	 */
	public static boolean checkAddressIsCIDR(Inet4Address baseIP, int nbits) {
		// Check IP spec is correct.
		long intip = TypeUtils.ipToInt(baseIP);
		long toshift = 32 - nbits;
		// System.err.println("shifting " + toshift + ": " +
		// (intip >>> toshift) + " to "+
		// TypeUtils.intToIP((intip>>>toshift)<<toshift).getHostAddress());

		if (((intip >>> toshift) << toshift) != intip) {
			// System.err.println("Not a CIDR");
			return false;
		}
		return true;
	}

	/**
	 * Does a numerical compare of two IP addresses
	 * 
	 * @param a1
	 *            first address to compare
	 * @param a2
	 *            second address to compare
	 * @return <0 if a1 < a2, 0 if a1==a2, >0 if a1 > a2
	 */
	public static int ipCompare(Inet4Address a1, Inet4Address a2) {
		byte[] adb1 = a1.getAddress();
		byte[] adb2 = a2.getAddress();

		for (int i = 0; i < 4; i++) {
			// Damn unsigned bytes
			int a = adb1[i] < 0 ? 256 + adb1[i] : adb1[i];
			int b = adb2[i] < 0 ? 256 + adb2[i] : adb2[i];
			// System.out.print("compareing " + a + " and " + b);
			if (a < b)
				return -1;
			if (a > b)
				return 1;
		}

		return 0;

	}

	/**
	 * returns the IP address following a given IP i.e. 138.38.52.1 >
	 * 138.38.52.2 138.38.52.255 > 138.38.53.1
	 * 
	 * @param ip
	 *            the Address to increment
	 * @return the next IP address
	 */

	public static Inet4Address ipIncrement(Inet4Address ip) {
		return ipMath(ip, 1);
	}

	public static Inet4Address ipDecrement(Inet4Address ip) {
		return ipMath(ip, -1);
	}

	/**
	 * Returns the IP address
	 * 
	 * @param ip
	 * @param num
	 * @return
	 */
	public static Inet4Address ipMath(Inet4Address ip, long num) {
		long intval1 = (int) TypeUtils.ipToInt(ip);// exploit integer overflow
		long intval2 = intval1 + num;

		Inet4Address ipout = TypeUtils.intToIP(intval2);
		return ipout;
	}

	static long pow(long x, long y) {
		Assert.isTrue(y >= 0);
		if (0 == y)
			return 1;
		long v = 1;
		while (y > 0) {
			v = v * x;
			y--;
		}
		return v;
	}

	public static Inet4Address getCIDRMaxAddress(Inet4Address addr, int nbits) {
		if (!checkAddressIsCIDR(addr, nbits)) {
			throw new RuntimeException("Invalid CIDR combination");
		}
		long v = pow(2, (32 - nbits));
		// log.trace("V is " + v);

		Inet4Address maxAddr = ipMath(addr, v - 1);
		return maxAddr;
	}

	/**
	 * Returns : * 0 : a==b * <0 : a<b * >0 : a>b
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int ipCmp(Inet4Address a, Inet4Address b) {
		return ((Long) ipToInt(a)).compareTo((Long) ipToInt(b));
	}

	/**
	 * checks for IP's presence in a range
	 * 
	 * @param val
	 *            value to test
	 * @param start
	 *            start of range
	 * @param end
	 *            end of range
	 * @return true if val is inside (inclusive) the range start- end, false
	 *         otherwise
	 * @throws AssertionError
	 *             if start > end
	 */
	public static boolean ipInRange(Inet4Address val, Inet4Address start,
			Inet4Address end) {
		Assert.isTrue(ipCmp(start, end) <= 0);
		return ipCmp(val, end) <= 0 && ipCmp(val, start) >= 0;
	}

	public static boolean nullSafeCompare(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		if (o1 == null || o2 == null)
			return false;
		return o1.equals(o2);
	}

	/**
	 * Creates an IP range based on a CIDR string (i.e. 138.38.0.0/16)
	 * 
	 * Throws an {@link IllegalArgumentException} if the string is not parseable
	 * as a CIDR
	 * 
	 * @param val
	 * @return
	 */
	public static IPRange txtToCIDRRange(String val) {
		IPRange range = new IPRange();
		int slashidx = val.indexOf('/');
		if (slashidx == -1) {
			throw new IllegalArgumentException("Invalid CIDR syntax " + val);
		}
		String ip = val.substring(0, slashidx);
		String bits = val.substring(slashidx + 1);

		// log.trace("got address component \"" + ip + "\" and bits component
		// \"" + bits + "\"");

		try {
			Inet4Address addr = TypeUtils.txtToIP(ip);
			int nbits = Integer.parseInt(bits);
			range.setMinIP(addr);
			range.setMaxIP(TypeUtils.getCIDRMaxAddress(addr, nbits));

			return range;
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"unable to parse address or bits component ");
		}

	}

	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @return
	 */
	public static String joinStrings(Collection<String> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> iter = s.iterator();
		if (iter.hasNext()) {
			buffer.append(iter.next());
			while (iter.hasNext()) {
				buffer.append(delimiter);
				buffer.append(iter.next());
			}
		}
		return buffer.toString();
	}

}
