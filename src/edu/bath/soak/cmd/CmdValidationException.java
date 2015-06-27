/**
 * 
 */
package edu.bath.soak.cmd;

import java.util.List;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * A {@link CmdException} specifically associated with a failed last-minute
 * validation run.
 * 
 * {@link CommandExpander} objects should throw this when being asked to expand
 * an invalid command.
 * 
 * @author cspocc
 * 
 */
public class CmdValidationException extends CmdException {

	private static final long serialVersionUID = 3882794539315445041L;
	Errors errors;

	public CmdValidationException(Errors errors) {
		super("Object validation failed with errors " + errors.toString());
		this.errors = errors;
	}

	public Errors getErrors() {
		return errors;
	}

	public String toString() {
		String message = getMessage();
		for (FieldError error : (List<FieldError>) errors.getFieldErrors()) {
			message += error.toString();
		}
		return message;
	}
}