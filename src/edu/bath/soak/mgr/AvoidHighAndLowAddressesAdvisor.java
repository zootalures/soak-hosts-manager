package edu.bath.soak.mgr;

import java.net.Inet4Address;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.NetworkClass;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.util.TypeUtils;

/**
 * Simple advisor which prefers not to allocate addresses at the top and bottom
 * of ranges above a certain size
 * 
 * @author cspocc
 * 
 */
public class AvoidHighAndLowAddressesAdvisor implements AddressManagerAdvisor {

	int topRange = 10;
	int bottomRange = 10;
	NetworkClass networkClass = null;
	int minSubnetSize = 120;

	public AddressManagerAdvice getAdviceForAllocation(Host h, Subnet s,
			Inet4Address addr) {

		if (s.getNumUseableAddresses() >= minSubnetSize
				&& (TypeUtils.ipInRange(addr, s.getMinUsableAddress(),
						TypeUtils.ipMath(s.getMinUsableAddress(), bottomRange)) || TypeUtils
						.ipInRange(addr, TypeUtils.ipMath(s
								.getMaxUsableAddress(), -topRange), s
								.getMaxUsableAddress()))) {
			return AddressManagerAdvice.PREFER_NOT;

		} else {
			return AddressManagerAdvice.OK;
		}
	}

	public int getOrder() {
		return 0;
	}

	/**
	 * The number of address to skip from the highest usable address in the
	 * selected subnet.
	 * 
	 * @return
	 */
	public int getTopRange() {
		return topRange;
	}

	/**
	 * The number of address to skip from the highest usable address in the
	 * selected subnet.
	 * 
	 * @return
	 */

	public void setTopRange(int topRange) {
		this.topRange = topRange;
	}

	/**
	 * The number of addresses to skip from the lowest usable range in the
	 * selected subnet.
	 * 
	 * @return
	 */
	public int getBottomRange() {
		return bottomRange;
	}

	/**
	 * @param bottomRange
	 *            ParaThe number of addresses to skip from the lowest usable
	 *            range in the selected subnet.
	 * 
	 * 
	 * @return
	 */
	public void setBottomRange(int bottomRange) {
		this.bottomRange = bottomRange;
	}

	/**
	 * An option network class for subnets to apply this policy to
	 * 
	 * @return
	 */
	public NetworkClass getNetworkClass() {
		return networkClass;
	}

	public void setNetworkClass(NetworkClass networkClass) {
		this.networkClass = networkClass;
	}

	/**
	 * The minumum size (in useable addresses) of a given subnet before this
	 * rule is applied.
	 * 
	 * For subnets with fewer than this number of usable addresses the rule is
	 * skipped.
	 * 
	 * @return
	 */
	public int getMinSubnetSize() {
		return minSubnetSize;
	}

	public void setMinSubnetSize(int minSubnetSize) {
		this.minSubnetSize = minSubnetSize;
	}

}
