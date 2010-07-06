package edu.bath.soak.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import edu.bath.soak.util.Tuple;

public class AddHeadersFilter implements Filter {

	List<Tuple<String, String>> headers = new ArrayList<Tuple<String, String>>();

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		for (Tuple<String, String> val : headers) {
			httpResponse.addHeader(val.getFrom(), val.getTo());
		}
		chain.doFilter(request, response);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void init(FilterConfig config) throws ServletException {
		Enumeration<String> params = config.getInitParameterNames();
		while (params.hasMoreElements()) {
			String param = params.nextElement();
			headers.add(new Tuple<String, String>(param, (String) config
					.getInitParameter(param)));
		}
	}

	public AddHeadersFilter() {
		// TODO Auto-generated constructor stub
	}

}
