package edu.bath.soak.web.host;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.Ordered;

public interface ShowHostInfoInterceptor extends Ordered {

	public void elaborateView(HostView view,HttpServletRequest request);
}
