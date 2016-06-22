package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class RESTResponseFilter implements ContainerResponseFilter {

    private final static Logger log = Logger.getLogger( RESTResponseFilter.class.getName() );

    @Override
    public void filter( ContainerRequestContext requestCtx, ContainerResponseContext responseCtx ) throws IOException {

        log.info( "Filtering REST Response" );

        if ( !requestCtx.getRequest().getMethod().equals( "OPTIONS" ) ) {
            responseCtx.getHeaders().add( "Access-Control-Allow-Origin", "*" );    // You may further limit certain client IPs with Access-Control-Allow-Origin instead of '*'
            responseCtx.getHeaders().add( "Access-Control-Allow-Credentials", "true" );
            responseCtx.getHeaders().add( "Access-Control-Allow-Methods", "GET, POST, DELETE, PUT" );
            responseCtx.getHeaders().add( "Access-Control-Allow-Headers", HTTPHeaderNames.SERVICE_KEY + ", " + HTTPHeaderNames.AUTH_TOKEN );
        }
    }
}
