package edu.bath.soak.undo;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandDispatcherRegistryImpl;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.ExecutableCommand;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.StoredCommand;
import edu.bath.soak.net.model.UnresolvedEntityException;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.security.SoakUserDetails;
import edu.bath.soak.undo.cmd.UndoCmd;
import edu.bath.soak.xml.SoakXMLManager;

/**
 * Manager/Controller for storing and recovering commands which can then be
 * undone at a later date.
 * 
 * 
 * 
 * @author cspocc
 * 
 */
public class UndoManagerImpl implements UndoManager, CommandExpander<UndoCmd>,
		InitializingBean {
	NetDAO hostsDAO;
	CommandDispatcherRegistryImpl commandDispatcherRegistry;
	SecurityHelper securityHelper;
	Logger log = Logger.getLogger(UndoManagerImpl.class);
	SoakXMLManager xmlManager;

	public boolean canExpand(Class clazz) {
		return UndoCmd.class.isAssignableFrom(clazz);
	}

	/**
	 * Creates a persistent stored version of an executed compsite command this
	 * relies on the {@link SoakXMLManager} to convert commands into XML which
	 * are then stored in a {@link StoredCommand}
	 * 
	 * @param command
	 *            the command to save
	 */
	public void saveUndoState(BaseCompositeCommand command) {
		Assert.notNull(command.getCommandId());
		StoredCommand stored = new StoredCommand();
		stored.setChangeComments(command.getBaseChange().getChangeComments());
		stored.setChangeTime(new Date());
		stored.setCommandDescription(command.getBaseChange()
				.getCommandDescription());

		SoakUserDetails ud = securityHelper.getCurrentUser();
		if (ud != null) {
			stored.setUser(ud.getUsername());
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		xmlManager.marshall(command, bos);
		stored.setCommandXML(new String(bos.toByteArray()));
		stored.setId(command.getCommandId());
		hostsDAO.saveStoredCommand(stored);
	}

	/***************************************************************************
	 * Expands an undo command into a given base composite command which can be
	 * executed.
	 * 
	 * <ol>
	 * <li>Restores the original command state from the {@link StoredCommand}</li>
	 * <li>Calls back to the {@link CommandDispatcherRegistry} to get other
	 * expanders to generate an inverse of the command</li>
	 * </ol>
	 * 
	 * @throws CmdException
	 *             if the command cannot be restored (i.e. if an entity
	 *             referenced by the command has been deleted)
	 * @throws UndoNotSupportedException
	 *             if one or more of the command expanders refused to produce an
	 *             undo version of the command
	 * 
	 * 
	 */
	public void expandCmd(UndoCmd cmd, BaseCompositeCommand result)
			throws CmdException {
		BaseCompositeCommand baseCommand;
		log.debug("expanding undo command");

		try {
			baseCommand = hostsDAO.getBaseCommandForStoredCommand(cmd
					.getStoredCommand());
		} catch (UnresolvedEntityException e) {
			throw new CmdException(
					"Cannot undo command as some of the referenced objects no longer exist");
		} catch (RuntimeException e) {
			throw new CmdException(e);
		}

		for (ExecutableCommand exec : baseCommand.getCommands()) {
			log.debug("expanding undo of subcommand " + exec);
			try {
				commandDispatcherRegistry.expandUndoSubCommand(exec, result);

			} catch (UndoNotSupportedException e) {
				log.debug("can't expand command  " + e.getCommand() + " : "
						+ e.getMessage());
				throw e;
			}
		}
	}

	public void afterPropertiesSet() throws Exception {
		//HACK: this reso
		commandDispatcherRegistry.setUndoManager(this);

	}

	public int getOrder() {
		return 0;
	}

	public void setupCommand(UndoCmd cmd) {

	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistryImpl commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setXmlManager(SoakXMLManager xmlManager) {
		this.xmlManager = xmlManager;
	}
}
