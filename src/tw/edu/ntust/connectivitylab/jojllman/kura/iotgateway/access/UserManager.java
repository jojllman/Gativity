package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojllman on 2016/4/19.
 */

public class UserManager {
    private static final Logger s_logger = LoggerFactory.getLogger(UserManager.class);
    private static UserManager instance;
    public static UserManager getInstance() { return instance; }

    private List<User> userList;

    public UserManager() {
        userList = new ArrayList<>();
        instance = this;
    }

    public boolean isUserExist(User user) { return userList.contains(user); }
    public boolean isUserExist(String userName) { return findUserByName(userName) != null; }
    public User findUserByName(String username) {
        for(User user : userList) {
            if(user.getUsername().compareToIgnoreCase(username) == 0)
                return user;
        }

        return null;
    }
    public User findUserById(String userid) {
        for(User user : userList) {
            if(user.getUserId().compareToIgnoreCase(userid) == 0)
                return user;
        }

        return null;
    }

    public User addUser(String username, String password) {
        if (isUserExist(username))
            return null;

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setUserId(AccessControlManager.GetRandomUserId());
        userList.add(user);
        return user;
    }
    public boolean removeUser(User user) {
        User user2 = findUserByName(user.getUsername());
        if (user2 == null)
            return false;
        AccessControlManager.getInstance().onUserRemoved(user);
        userList.remove(user2);
        return true;
    }

    public List<User> getAllUser() {
        return userList;
    }
}
