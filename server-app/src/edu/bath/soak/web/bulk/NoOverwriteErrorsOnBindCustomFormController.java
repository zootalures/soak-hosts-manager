package edu.bath.soak.web.bulk;

import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * This is a slight variation on the standard formAction which doesn't overwite
 * the scoped errors object on an explicit bind (iff there were no bind errors)
 * 
 * @author cspocc
 * 
 */
public class NoOverwriteErrorsOnBindCustomFormController extends FormAction {
	public Event bindWithoutOverwritingErrors(RequestContext context)
			throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Executing bind");
		}
		Object formObject = getFormObject(context);
		DataBinder binder = createBinder(context, formObject);
		doBind(context, binder);
		if (binder.getErrors().hasErrors()) {// we only overwrite the errros
			// if there were bind errors.

			getFormObjectAccessor(context).putFormErrors(binder.getErrors(),
					getFormErrorsScope());
			return error();

		} else {
			Errors validationErrors = (Errors) context.getFlowScope().get(
					"savedValidationErrors");
			if (null != validationErrors) {
				getFormObjectAccessor(context).putFormErrors(validationErrors,
						getFormErrorsScope());
			} else {
				getFormObjectAccessor(context).putFormErrors(
						binder.getErrors(), getFormErrorsScope());

			}
			return success();
		}
	}

	public Event clearValidationErrors(RequestContext context) throws Exception {
		context.getFlowScope().put("savedValidationErrors", null);
		Errors errors = createBinder(context, getFormObject(context))
				.getErrors();
		getFormObjectAccessor(context).putFormErrors(errors,
				getFormErrorsScope());
		

		return success();
	}

	@Override
	protected void doValidate(RequestContext context, Object formObject,
			Errors errors) throws Exception {
		super.doValidate(context, formObject, errors);
		context.getFlowScope().put("savedValidationErrors", errors);
	}

}
