package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import java.util.*;
import java.security.GeneralSecurityException;
import javax.security.auth.login.LoginException;

public final class Authenticator {

    private static Authenticator authenticator = null;
    private static String SERVICE_KEY = "f80ebc87-ad5c-4b29-9366-5359768df5a1";

    private final UserManager userManager;

    // An authentication token storage which stores <service_key, auth_token>.
    private final Map<String, String> authorizationTokensStorage = new HashMap();

    private Authenticator() {
        userManager = UserManager.getInstance();
    }

    public static Authenticator getInstance() {
        if ( authenticator == null ) {
            authenticator = new Authenticator();
        }

        return authenticator;
    }

    public String login( String serviceKey, String username, String password ) throws LoginException {
        if ( isServiceKeyValid(serviceKey)
                && userManager.isUserExist(username) ) {

            if ( userManager.findUserByName(username).isPasswordSame(password) ) {

                /**
                 * Once all params are matched, the authToken will be
                 * generated and will be stored in the
                 * authorizationTokensStorage. The authToken will be needed
                 * for every REST API invocation and is only valid within
                 * the login session
                 */
                String authToken = UUID.randomUUID().toString();
                authorizationTokensStorage.put( authToken, username );

                return authToken;
            }
        }

        throw new LoginException( "Don't Come Here Again!" );
    }

    /**
     * The method that pre-validates if the client which invokes the REST API is
     * from a authorized and authenticated source.
     *
     * @param serviceKey The service key
     * @param authToken The authorization token generated after login
     * @return TRUE for acceptance and FALSE for denied.
     */
    public boolean isAuthTokenValid( String serviceKey, String authToken ) {
        if ( isServiceKeyValid( serviceKey ) ) {
            if ( authorizationTokensStorage.containsKey( authToken ) ) {
                String usernameMatch2 = authorizationTokensStorage.get( authToken );
                return true;
            }
        }

        return false;
    }

    /**
     * This method checks is the service key is valid
     *
     * @param serviceKey
     * @return TRUE if service key matches the pre-generated ones in service key
     * storage. FALSE for otherwise.
     */
    public boolean isServiceKeyValid( String serviceKey ) {
        return serviceKey.compareTo(SERVICE_KEY) == 0;
    }

    public void logout( String serviceKey, String authToken ) throws GeneralSecurityException {
        if ( authorizationTokensStorage.containsKey( authToken ) ) {
            String usernameMatch1 = authorizationTokensStorage.get( authToken );

            if (isServiceKeyValid(serviceKey)) {

                /**
                 * When a client logs out, the authentication token will be
                 * remove and will be made invalid.
                 */
                authorizationTokensStorage.remove( authToken );
                return;
            }
        }

        throw new GeneralSecurityException( "Invalid service key and authorization token match." );
    }

    public User getAuthenticatedUser(String serviceKey, String authToken) {
        if(isAuthTokenValid(serviceKey, authToken)) {
            return userManager.findUserByName(authorizationTokensStorage.get(authToken));
        }
        return null;
    }
}
