package edu.bath.soak.web.host;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.net.HostsFileGenerator;

/**
 * Web controller for generating a hosts file
 * 
 * @author cspocc
 * 
 */
public class HostsFileController extends MultiActionController {
	HostsFileGenerator hostsFileGenerator;

	public ModelAndView hostsFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/plain");
		response.setBufferSize(2000000);

		hostsFileGenerator.generateHostsFile(response.getWriter());

		return null;
	}

	@Required
	public void setHostsFileGenerator(HostsFileGenerator hostsFileGenerator) {
		this.hostsFileGenerator = hostsFileGenerator;
	}
}
