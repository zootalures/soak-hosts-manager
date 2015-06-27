package edu.bath.soak.cmd;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;


/*******************************************************************************
 * Base interface for all UI commands
 * 
 * 
 * Provides the ver basic UI interaction for commands
 * 
 * See {@link AbstractUICmdImpl} for a base implementation
 * 
 * @author cspocc
 * 
 */
@XmlRootElement
public interface UICommand {
	/**
	 * A map of extensible command options,
	 * 
	 * Typically the LHS is a string and the RHS is a plugin-specific command
	 * option which can be set when the command is prepared in
	 * {@link CommandExpander#setupCommand(UICommand)}
	 * 
	 * Not that any values which inherit from {@link RenderableCommandOption}
	 * will be rendered
	 * 
	 * @return a map of command optiosn
	 */
	public Map<Object, Object> getOptionData();

	public void setOptionData(Map<Object, Object> commandFlags);

	/**
	 * The user-entered comments for this command
	 * 
	 * @return
	 */
	public String getChangeComments();

	public void setChangeComments(String changeComments);

	/**
	 * gets command options which should be rendered in the user interface
	 * 
	 * @return the sub-map set by {@link #setOptionData(Map)} where the values
	 *         extend from {@link RenderableCommandOption}
	 */
	public Map<Object, RenderableCommandOption> getRenderableOptions();

	public boolean isHasRenderableOptions();

	/**
	 * A system-generated description of the commmand
	 * 
	 * @return
	 */
	public String getCommandDescription();
}
