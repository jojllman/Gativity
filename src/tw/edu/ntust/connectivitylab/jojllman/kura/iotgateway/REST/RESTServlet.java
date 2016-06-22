package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;

public class RESTServlet extends CXFNonSpringJaxrsServlet {

    private static final long serialVersionUID = -1531317723099896635L;

    @Override
    protected Application createApplicationInstance(String appClassName, ServletConfig servletConfig)
            throws ServletException {
        return new RESTApplication();
    }

}
