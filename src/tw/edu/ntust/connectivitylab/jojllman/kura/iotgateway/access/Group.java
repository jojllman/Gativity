package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jojllman on 2016/4/18.
 */

public class Group {
    private static final Logger s_logger = LoggerFactory.getLogger(Group.class);

    private String groupName;
    private String groupId;
    private List<User> users;

    public Group() {
        users = new ArrayList<>();
    }

    private boolean containUsername(String name) {
        for(User user : users) {
            if(user.getUsername().compareToIgnoreCase(name) == 0) {
                return true;
            }
        }

        return false;
    }
    public boolean containUser(User user) {
        return users.contains(user);
    }
    public User findUser(String username) {
        for(User user : users) {
            if(user.getUsername().compareToIgnoreCase(username) == 0) {
                return user;
            }
        }
        return null;
    }
    public boolean addUser(User user) {
        if(users.contains(user) || containUsername(user.getUsername()))
            return false;

        user.setGroup(this);
        users.add(user);
        return true;
    }
    public boolean removeUser(String username) {
        User user = findUser(username);
        if(user == null)
            return false;

        user.setGroup(null);
        users.remove(user);
        return true;
    }
    public boolean removeUser(User user) {
        if(!containUsername(user.getUsername()))
            return false;

        users.remove(user);
        return true;
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public String setGroupName(String name) {
        this.groupName = name;
        return this.groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String setGroupId(String groupId) {
        this.groupId = groupId;
        return this.groupId;
    }
}
