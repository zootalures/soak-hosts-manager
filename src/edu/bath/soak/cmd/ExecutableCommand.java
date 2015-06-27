package edu.bath.soak.cmd;

import javax.xml.bind.annotation.XmlSeeAlso;

import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dns.cmd.DNSCmd;

/**
 * Tag base class for executable command objects
 * 
 * (this should be an interface but JAXB doesn't seem to like it when it isn't)
 * 
 * @author cspocc
 * 
 */
@XmlSeeAlso( { DHCPCmd.class, DNSCmd.class })
public abstract class ExecutableCommand {

}
