package edu.bath.soak.net;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.mgr.AddressSpaceManager;
import edu.bath.soak.mgr.HostsManager;
import edu.bath.soak.net.cmd.AlterHostCmd;
import edu.bath.soak.net.cmd.DeleteHostDBCmd;
import edu.bath.soak.net.cmd.DeleteHostDBCmdValidator;
import edu.bath.soak.net.cmd.DeleteHostUICmdValidator;
import edu.bath.soak.net.cmd.DeleteHostUICmd;
import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.net.cmd.HostsDBCommand;
import edu.bath.soak.net.cmd.SaveHostCmd;
import edu.bath.soak.net.cmd.SaveHostCmdValidator;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostChange;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.undo.UndoNotSupportedException;

/**
 * Implementation class for all middle-tier operations on hosts.
 * 
 * provides the command interface for creating/updating hosts through
 * CmdImplementor<BaseUICmd>
 * 
 * Delegates each host command to the chain of {@link CommandExpander} objects
 * which expand given commands.
 * 
 * Aggregate changes are implemented through a set of {@link CommandProcessor}
 * objects
 * 
 * @see HostsManager
 * @author cspocc
 * 
 */
public class HostsManagerImpl implements HostsManager,
		CommandExpander<UICommand>, CommandProcessor<HostsDBCommand> {

	int order = 0;
	NetDAO hostsDAO;

	AddressSpaceManager addressSpaceManager;
	Logger log = Logger.getLogger(HostsManagerImpl.class);

	HookableValidator alterHostCmdValidator;
	DeleteHostUICmdValidator deleteHostUICmdValidator;
	DeleteHostDBCmdValidator deleteHostDBCmdValidator;

	SaveHostCmdValidator saveHostCmdValidator;

	@Required
	public void setHostsDAO(NetDAO dao) {
		this.hostsDAO = dao;
	}

	/**
	 * expands a delete host command
	 * 
	 * @param cmd
	 * @param result
	 */
	void expandDeleteHostCmd(DeleteHostUICmd cmd, BaseCompositeCommand result) {
		log.trace("(re)validating host deletion for host "
				+ cmd.getHost().getHostName());
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(deleteHostUICmdValidator, cmd,
				objectErrors);
		if (objectErrors.hasErrors()) {
			throw new CmdValidationException(objectErrors);
		}
		DeleteHostDBCmd ocmd = new DeleteHostDBCmd();
		ocmd.setHost(cmd.getHost());
		ocmd.setChangeComments(cmd.getChangeComments());
		ocmd.setVersionBeforeChange(cmd.getHost().getVersion());
		result.getCommands().add(ocmd);
	}

	/**
	 * Expands a host alteration or creation command
	 * 
	 * @param cmd
	 * @param result
	 */
	void expandAlterHostCmd(AlterHostCmd cmd, BaseCompositeCommand result) {
		log.trace("(re)validating host alteration for host "
				+ cmd.getNewHost().getHostName());

		// the validator implcitly ensures that an address is allocated, this is
		// probably a bit wrong
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(alterHostCmdValidator, cmd,
				objectErrors);
		if (objectErrors.hasErrors()) {
			throw new CmdValidationException(objectErrors);
		}

		log.trace("previewing host creation for host "
				+ cmd.getNewHost().getHostName());

		if (cmd.isSpecifyIp() == false) {
			Assert.notNull(cmd.getSubnet());
			cmd.getNewHost().setIpAddress(
					addressSpaceManager.allocateIPAddress(cmd.getNewHost(), cmd
							.getSubnet()));

		}
		SaveHostCmd saveCommand = new SaveHostCmd();
		saveCommand.setHost(cmd.getNewHost());
		saveCommand.setChangeComments(cmd.getChangeComments());
		saveCommand.setCreation(cmd.isCreation());
		if (saveCommand.isCreation()) {
			saveCommand.setVersionBeforeChange(-1L);
		} else {
			saveCommand.setVersionBeforeChange(cmd.getNewHost().getVersion());
		}
		result.getCommands().add(saveCommand);
	}

	/**
	 * This restores deleted hosts by recovering the version stored before the
	 * save from the HostChange history
	 * 
	 * 
	 * @param cmd
	 *            the command to undo
	 * @param result
	 *            the result to expand the undo operations into
	 * @throws CmdException
	 * @throws UndoNotSupportedException
	 *             if the edited host was not found, or has been changed since
	 *             the specified version
	 * 
	 */
	public void expandUndo(HostsDBCommand cmd, BaseCompositeCommand result)
			throws CmdException, UndoNotSupportedException {

		if (cmd instanceof SaveHostCmd) {
			SaveHostCmd original = (SaveHostCmd) cmd;
			Host h = original.getHost();
			Assert.notNull(h.getId());

			Host realh;
			try {
				realh = hostsDAO.loadHost(h.getId());
			} catch (ObjectNotFoundException e) {
				throw new UndoNotSupportedException(cmd,
						"cant undo host  operation for " + h.getHostName()
								+ ", host no longer exists");
			}

			if (original.isCreation()) {
				if (realh.getVersion() != 0L
						&& realh.equals(original.getHost())) {
					// for creations we pull the original host out of the
					// command
					throw new UndoNotSupportedException(cmd,
							"cant undo host  edit for " + h.getHostName()
									+ " it has been changed since this edit ");

				}
				DeleteHostDBCmd deleteCmd = new DeleteHostDBCmd();

				deleteCmd.setHost(realh);
				deleteCmd.setChangeComments(original.getChangeComments());

				Errors objectErrors = new BeanPropertyBindingResult(deleteCmd,
						"cmd");
				ValidationUtils.invokeValidator(deleteHostDBCmdValidator,
						deleteCmd, objectErrors);
				if (objectErrors.hasErrors())
					throw new UndoNotSupportedException(cmd,
							"Can't undo creation of this host, another conflicting host now exists");

				result.getCommands().add(deleteCmd);
			} else {
				// for edits we get the previous version from the host changes
				// table

				if (realh.getVersion() != original.getVersionBeforeChange() + 1
						&& !realh.equals(original.getHost())) {
					throw new UndoNotSupportedException(cmd,
							"cant undo host  edit for " + h.getHostName()
									+ " it has been changed since this edit ");
				}
				Long version = original.getVersionBeforeChange();
				HostChange hc = hostsDAO.getHostChangeAtVersion(h.getId(),
						version);
				if (null == hc) {
					throw new UndoNotSupportedException(cmd,
							"Can't undo host edit, saved version was not found");
				}
				Assert.notNull(hc);

				SaveHostCmd newCmd = new SaveHostCmd();
				newCmd.setHost(hc.getHost());
				newCmd.setChangeComments(original.getChangeComments());

				Errors objectErrors = new BeanPropertyBindingResult(newCmd,
						"cmd");
				ValidationUtils.invokeValidator(saveHostCmdValidator, newCmd,
						objectErrors);
				if (objectErrors.hasErrors())
					throw new UndoNotSupportedException(cmd,
							"Can't undo editing of this host, another conflicting host now exists");

				result.getCommands().add(newCmd);
			}
		} else if (cmd instanceof DeleteHostDBCmd) {
			DeleteHostDBCmd original = (DeleteHostDBCmd) cmd;
			SaveHostCmd newCommand = new SaveHostCmd();
			HostChange hc = hostsDAO.getHostChangeAtVersion(original.getHost()
					.getId(), ((DeleteHostDBCmd) cmd).getVersionBeforeChange());
			if (null == hc) {
				throw new UndoNotSupportedException(cmd,
						"Can't undo host deletion, saved version was not found");
			}
			Assert.notNull(hc);
			newCommand.setCreation(true);
			newCommand.setHost(hc.getHost());
			newCommand.setChangeComments(result.getBaseChange()
					.getChangeComments());
			result.appendCommand(newCommand);
			newCommand.setVersionBeforeChange(hc.getVersion());

			Errors objectErrors = new BeanPropertyBindingResult(newCommand,
					"cmd");
			ValidationUtils.invokeValidator(saveHostCmdValidator, newCommand,
					objectErrors);
			if (objectErrors.hasErrors())
				throw new UndoNotSupportedException(cmd,
						"Can't undo deletion of this host, another conflicting host now exists");

		} else {
			throw new UndoNotSupportedException(cmd,
					"Can't undo unknown command type ");
		}
	}

	/**
	 * Generates a changeResult for the hypothetical implementation of this
	 * command
	 * 
	 * This will re-validate the command
	 * 
	 * all changes are passed through the set of filters defined by
	 * 
	 * @param hc
	 *            the host change to expand,
	 * @param changeResult
	 * @throws CmdException
	 */
	public void expandCmd(UICommand hc, BaseCompositeCommand changeResult)
			throws CmdException {

		if (hc instanceof DeleteHostUICmd) {
			expandDeleteHostCmd((DeleteHostUICmd) hc, changeResult);

		} else if (hc instanceof AlterHostCmd) {

			expandAlterHostCmd((AlterHostCmd) hc, changeResult);

		} else {
			throw new UnsupportedOperationException("command type"
					+ hc.getClass() + " not supported");

		}

		log.trace("preview expanded command with "
				+ changeResult.getCommands().size() + "extra operations");

	}

	/**
	 * Implements a host change throws a CmdException in the case of a
	 * fatal,last-ditch effort, does not roll-back changes.
	 * 
	 * @param baseCommand
	 *            the parent command which the root command comes from
	 * @param hc
	 *            the host command to implement
	 * @throws CmdException
	 */
	public void implementCmd(BaseCompositeCommand baseCommand, HostsDBCommand hc)
			throws CmdException {
		Assert.notNull(hc);

		if (hc instanceof DeleteHostDBCmd) {
			DeleteHostDBCmd cmd = (DeleteHostDBCmd) hc;

			Errors objectErrors = new BeanPropertyBindingResult(hc, "cmd");
			ValidationUtils.invokeValidator(deleteHostDBCmdValidator, hc,
					objectErrors);
			if (objectErrors.hasErrors())
				throw new CmdValidationException(objectErrors);

			cmd.setVersionBeforeChange(cmd.getHost().getVersion());
			hostsDAO.deleteHost(cmd.getHost().getId(), baseCommand
					.getCommandId(), cmd.getChangeComments());
		} else if (hc instanceof SaveHostCmd) {
			Errors objectErrors = new BeanPropertyBindingResult(hc, "cmd");
			ValidationUtils.invokeValidator(saveHostCmdValidator, hc,
					objectErrors);
			if (objectErrors.hasErrors())
				throw new CmdValidationException(objectErrors);

			SaveHostCmd cmd = (SaveHostCmd) hc;
			cmd.setVersionBeforeChange(cmd.getHost().getVersion());

			hostsDAO.saveHost(cmd.getHost(), baseCommand.getCommandId(), cmd
					.getChangeComments(), baseCommand.getBaseChange()
					.getCommandDescription());
		} else {
			throw new IllegalArgumentException(hc.getClass()
					+ " is not valid for this changer");
		}
		return;
	}

	public void setupCommand(UICommand cmd) {

	}

	/**
	 * The address space manager which is used to allocate IP addresses to hosts
	 * when needed
	 * 
	 * @param addressSpaceManager
	 */
	@Required
	public void setAddressSpaceManager(AddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	public boolean supportsCmdType(Class type) {
		return HostsDBCommand.class.isAssignableFrom(type);
	}

	public boolean canExpand(Class clazz) {
		return AlterHostCmd.class.isAssignableFrom(clazz)
				|| DeleteHostUICmd.class.isAssignableFrom(clazz);
	}

	@Required
	public void setAlterHostCmdValidator(HookableValidator alterHostCmdValidator) {
		this.alterHostCmdValidator = alterHostCmdValidator;
	}

	public int getOrder() {
		return order;
	}

	@Required
	public void setDeleteHostUICmdValidator(
			DeleteHostUICmdValidator deleteHostUICmdValidator) {
		this.deleteHostUICmdValidator = deleteHostUICmdValidator;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Required
	public void setSaveHostCmdValidator(
			SaveHostCmdValidator saveHostCmdValidator) {
		this.saveHostCmdValidator = saveHostCmdValidator;
	}

	@Required
	public void setDeleteHostDBCmdValidator(
			DeleteHostDBCmdValidator deleteHostDBCmdValidator) {
		this.deleteHostDBCmdValidator = deleteHostDBCmdValidator;
	}
}
