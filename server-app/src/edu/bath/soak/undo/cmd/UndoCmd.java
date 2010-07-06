package edu.bath.soak.undo.cmd;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.cmd.AbstractUICmdImpl;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/*******************************************************************************
 * UI command which indicates an undo operation
 * 
 * @author cspocc
 * 
 */
@XmlRootElement
@BeanView("beanview/host/UndoCmd")
public class UndoCmd extends AbstractUICmdImpl implements UICommand,
		Serializable {

	StoredCommand storedCommand;

	public Map<Object, Object> getOptionData() {
		// TODO Auto-generated method stub
		return null;
	}

	public StoredCommand getStoredCommand() {
		return storedCommand;
	}

	public void setStoredCommand(StoredCommand storedCommand) {
		this.storedCommand = storedCommand;
	}

	@Transient
	@XmlTransient
	public String getCommandDescription() {
		return "Undo operation: " + storedCommand.getCommandDescription();
	}
}
