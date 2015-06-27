package edu.bath.soak.web;

import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.definition.StateDefinition;
import org.springframework.webflow.execution.FlowExecutionException;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.FlowSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * An empty class used for adding aspect-injected listeners to flow execution
 * listeners
 * 
 * @author cspocc
 * 
 */
public class SoakFlowExecutionListener extends FlowExecutionListenerAdapter {

	@Override
	public void stateEntered(RequestContext context,
			StateDefinition previousState, StateDefinition newState) {
		// TODO Auto-generated method stub
		super.stateEntered(context, previousState, newState);
	}

	@Override
	public void exceptionThrown(RequestContext context,
			FlowExecutionException exception) {
		// TODO Auto-generated method stub
		super.exceptionThrown(context, exception);
	}

	@Override
	public void sessionStarting(RequestContext context,
			FlowSession session,
			MutableAttributeMap input) {
		super.sessionStarting(context,session,input);
	};

}
