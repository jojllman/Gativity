package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/app")
public class RESTApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> s = new HashSet<Class<?>>();
        s.add(RESTResource.class);
        s.add(RESTRequestFilter.class);
        s.add(RESTResponseFilter.class);
        return s;
    }
}