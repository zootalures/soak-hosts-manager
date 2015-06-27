package edu.bath.soak.cmd;

import org.springframework.core.Ordered;

/**
 * Tag interface to indicate that a host command option has a rendered representation
 * @author cspocc
 *
 */
public interface RenderableCommandOption extends CommandOption,Ordered{

}
