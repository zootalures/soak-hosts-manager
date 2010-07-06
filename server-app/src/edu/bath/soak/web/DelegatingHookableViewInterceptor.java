package edu.bath.soak.web;

import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.bath.soak.util.OrderedComparator;

/**
 * Pluggable handler for allowing HandlerInterceptors to be extended by plugins
 * 
 * Plugins should register handlers via registerHandler
 * @author cspocc
 * 
 */
public class DelegatingHookableViewInterceptor implements HandlerInterceptor {
	TreeSet<OrderedHandlerInterceptor> handlerInterceptors = new TreeSet<OrderedHandlerInterceptor>(
			new OrderedComparator());

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		for (OrderedHandlerInterceptor ohi : handlerInterceptors) {
			ohi.afterCompletion(request, response, handler, ex);
		}

	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		for (OrderedHandlerInterceptor ohi : handlerInterceptors) {
			ohi.postHandle(request, response, handler, modelAndView);
		}

	}

	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// TODO Auto-generated method stub
		for (OrderedHandlerInterceptor ohi : handlerInterceptors) {
			boolean val = ohi.preHandle(request, response, handler);
			if (!val) {
				return false;
			}
		}
		return true;
	}

	public void registerHandlerInterceptor(OrderedHandlerInterceptor interceptor) {
		handlerInterceptors.add(interceptor);
	}
}
