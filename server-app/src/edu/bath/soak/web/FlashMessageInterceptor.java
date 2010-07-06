package edu.bath.soak.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Inserts the appropriate flash message into the model as "flashMessage"
 * 
 * @author cspocc
 * 
 */
public class FlashMessageInterceptor implements OrderedHandlerInterceptor {
	MessageSource messageSource;

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String flashMsg;
		if (null != (flashMsg = request.getParameter("flash"))) {
			request.setAttribute("flashMessage", messageSource.getMessage(
					flashMsg, new Object[] {}, "Flash Message " + flashMsg,
					Locale.ENGLISH));
		}
		return true;

	}

	@Required
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
