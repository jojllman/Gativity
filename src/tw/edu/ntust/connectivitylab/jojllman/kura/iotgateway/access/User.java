package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojllman on 2016/4/15.
 */

public class User {
    private static final Logger s_logger = LoggerFactory.getLogger(User.class);

    private String username;
    private String userId;
    private String password;
    private List<Group> groups;
    private boolean admin;

    public User() {
        groups = new ArrayList<>();
        admin = false;
    }

    public boolean setGroup(Group group) {
        return this.groups.add(group);
    }
    public List<Group> getGroups() {
        return groups;
    }
    public String setUsername(String name) {
        this.username = name;
        return this.username;
    }
    public String getUsername() {
        return this.username;
    }
    public boolean setPassword(String password) {
        this.password = password;
        return true;
    }
    public boolean isPasswordSame(String password) {
        return this.password.compareTo(password) == 0;
    }
    public String setUserId(String id) {
        this.userId = id;
        return this.userId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setAdministrator(boolean admin) { this.admin = admin; }
    public boolean isAdministrator() { return admin; }
}
