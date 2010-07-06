package edu.bath.soak.dhcp.model;

import java.io.Serializable;

import javax.persistence.Entity;

import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

@Entity
@BeanViews( {
		@BeanView(value = "beanview/dhcp/DBBackedDHCPServer"),
		@BeanView(value = "beanview/dhcp/DBBackedDHCPServer-form", view = "form") })
public class DBBackedDHCPServer extends DHCPServer implements Serializable {

}
