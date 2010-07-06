package edu.bath.soak.web;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

public interface OrderedHandlerInterceptor extends Ordered, HandlerInterceptor {

}
