package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.*;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.Event;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.EventManager;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.auth.login.LoginException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Stateless
public class RESTResource implements RESTResourceProxy {
    private static final long serialVersionUID = -6663599014192066936L;
    private static final Logger s_logger = LoggerFactory.getLogger(RESTResource.class);

    @Override
    public Response login(
            HttpHeaders httpHeaders,
            String username,
            String password) {

        Authenticator authenticator = Authenticator.getInstance();
        String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);

        try {
            String authToken = authenticator.login(serviceKey, username, password);

            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("auth_token", authToken);
            User caller = Authenticator.getInstance().
                    getAuthenticatedUser(
                            httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                            authToken);
            if(caller.isAdministrator()) {
                jsonObjBuilder.add("admin", true);
                jsonObjBuilder.add("id", caller.getUserId());
            }
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();

        } catch (final LoginException ex) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "Problem matching service key, username and password");
            JsonObject jsonObj = jsonObjBuilder.build();

            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).entity(jsonObj.toString()).build();
        }
    }

    @Override
    public Response demoGetMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoGetMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response demoPostMethod() {
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "Executed demoPostMethod");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response queryUserList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if (caller.isAdministrator()) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            List<User> users = UserManager.getInstance().getAllUser();
            for (User user : users) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("user_name", user.getUsername())
                        .add("user_id", user.getUserId()));
            }

            jsonObjBuilder.add("users", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryGroupList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if (caller.isAdministrator()) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            List<Group> groups = GroupManager.getInstance().getAllGroup();
            for (Group group : groups) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("group_name", group.getGroupName())
                        .add("group_id", group.getGroupId()));
            }

            jsonObjBuilder.add("groups", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryGroupUserList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if (caller.isAdministrator()) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            List<Group> groups = GroupManager.getInstance().getAllGroup();
            for (Group group : groups) {
                JsonArrayBuilder userJsonArrayBuilder = Json.createArrayBuilder();
                List<User> users = group.getUsers();
                for(User user : users) {
                    userJsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("user_name", user.getUsername())
                            .add("user_id", user.getUserId()));
                }
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("group_name", group.getGroupName())
                        .add("group_id", group.getGroupId())
                        .add("users", userJsonArrayBuilder));
            }

            jsonObjBuilder.add("groups", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response queryChannelList(HttpHeaders httpHeaders) {
        return null;
    }

    @Override
    public Response queryDeviceList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        List<IDeviceProfile> devices = DeviceManager.getInstance().getDeviceProfiles();
        if (caller.isAdministrator()) {
            for (IDeviceProfile device : devices) {
                JsonArrayBuilder channelBuilder = Json.createArrayBuilder();
                List<TopicChannel<?>> channels = device.getChannels();
                for(TopicChannel channel : channels) {
                    channelBuilder.add(Json.createObjectBuilder()
                            .add("topic", channel.getTopic())
                            .add("description", channel.getDescription())
                            .add("value", channel.getValue() == null ? "null" : channel.getValue().toString())
                            .add("mode", channel.getMode().toString())
                            .add("type", channel.getType().toString())
                            .add("id", channel.getId()));
                }
                User owner = AccessControlManager.getInstance().getDeviceOwner(device);
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("name", device.getName())
                        .add("id", device.getId())
                        .add("description", device.getDescription())
                        .add("type", device.getType().toString())
                        .add("uuid", device.getUUID().toString())
                        .add("protocol", device.getDataExchangeProtocol().toString())
                        .add("owner", owner==null?"null" : owner.getUsername())
                        .add("channels", channelBuilder));
            }
        } else {
            AccessControlManager accessControlManager = AccessControlManager.getInstance();
            for (IDeviceProfile device : devices) {
                if (accessControlManager.canUserReadDevice(caller, device)) {
                    JsonArrayBuilder channelBuilder = Json.createArrayBuilder();
                    List<TopicChannel<?>> channels = device.getChannels();
                    for(TopicChannel channel : channels) {
                        if(accessControlManager.canUserReadChannel(caller, channel)) {
                            channelBuilder.add(Json.createObjectBuilder()
                                    .add("topic", channel.getTopic())
                                    .add("description", channel.getDescription())
                                    .add("value", channel.getValue() == null ? "null" : channel.getValue().toString())
                                    .add("mode", channel.getMode().toString())
                                    .add("type", channel.getType().toString())
                                    .add("id", channel.getId()));
                        }
                    }
                    User owner = AccessControlManager.getInstance().getDeviceOwner(device);
                    jsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", device.getName())
                            .add("id", device.getId())
                            .add("description", device.getDescription())
                            .add("type", device.getType().toString())
                            .add("uuid", device.getUUID().toString())
                            .add("protocol", device.getDataExchangeProtocol().toString())
                            .add("owner", owner==null?"null" : owner.getUsername())
                            .add("channel_num", device.getChannels().size()));
                }
            }
        }
        jsonObjBuilder.add("devices", jsonArrayBuilder);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response queryEventList(HttpHeaders httpHeaders) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        s_logger.debug("Query event list");
        if (caller.isAdministrator()) {
            Map<String, List<Event>> userEvents = EventManager.getInstance().getAllEvents();
            Iterator<Map.Entry<String, List<Event>>> it = userEvents.entrySet().iterator();
            while (it.hasNext()) {
                s_logger.debug("Has events");
                JsonArrayBuilder eventJsonArrayBuilder = Json.createArrayBuilder();
                Map.Entry<String, List<Event>> entry = it.next();
                String userid = entry.getKey();
                User user = UserManager.getInstance().findUserById(userid);
                List<Event> events = entry.getValue();
                for (Event event : events) {
                    eventJsonArrayBuilder.add(Json.createObjectBuilder()
                            .add("name", event.getEventName())
                            .add("id", event.getEventId())
                            .add("if", event.getIfString())
                            .add("then", event.getThenString())
                            .add("repeat", event.isRepeat())
                            .add("period", event.getPeriod())
                            .add("active", event.isActive()));
                }
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("user_id", userid)
                        .add("events", eventJsonArrayBuilder));
            }
        } else {
            List<Event> events = EventManager.getInstance().getUserEvents(caller.getUserId());
            JsonArrayBuilder eventJsonArrayBuilder = Json.createArrayBuilder();
            for (Event event : events) {
                eventJsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("name", event.getEventId())
                        .add("if", event.getIfString())
                        .add("then", event.getThenString()));
            }
            jsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("user_id", caller.getUserId())
                    .add("events", eventJsonArrayBuilder));
        }
        jsonObjBuilder.add("user_events", jsonArrayBuilder);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response writeChannelData(HttpHeaders httpHeaders, String channelId, String type, String value) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));
        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        if(channel == null || caller == null)
            return null;
        if(channel.getType() != TopicChannel.ChannelDataType.valueOf(type))
            return null;
        if(!AccessControlManager.getInstance().canUserWriteChannel(caller, channel))
            return null;

        switch (channel.getType()) {
            case Boolean:
                channel.setValue(Boolean.valueOf(value));
                break;
            case Short:
                channel.setValue(Short.valueOf(value));
                break;
            case Integer:
                channel.setValue(Integer.valueOf(value));
                break;
            case String:
                channel.setValue(value);
                break;
            default:
                return null;
        }

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "value has changed");
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response readChannelData(HttpHeaders httpHeaders, String channelId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));
        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        if(channel == null || caller == null)
            return null;
        if(!AccessControlManager.getInstance().canUserReadChannel(caller, channel))
            return null;

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "value has acquired");
        jsonObjBuilder.add("value", channel.getValue().toString());
        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
    }

    @Override
    public Response addUser(HttpHeaders httpHeaders, String username, String password, String group_id) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        User user = null;
        Group group = GroupManager.getInstance().findGroupById(group_id);
        if(group != null) {
            user = UserManager.getInstance().addUser(username, password);
            group.addUser(user);
        }
        if(user != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "user has been added");
            jsonObjBuilder.add("user_id", user.getUserId());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response removeUser(HttpHeaders httpHeaders, String userId) {
        return null; //TODO: remove user
    }

    @Override
    public Response addGroup(HttpHeaders httpHeaders, String groupName) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        Group group = GroupManager.getInstance().addGroup(groupName);
        if(group != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "group has been added");
            jsonObjBuilder.add("group_id", group.getGroupId());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }
        return null;
    }

    @Override
    public Response removeGroup(HttpHeaders httpHeaders, String groupId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        Group group = GroupManager.getInstance().findGroupById(groupId);
        if(group != null) {
            GroupManager.getInstance().removeGroup(group);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "group has been removed");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response setUserGroup(HttpHeaders httpHeaders, String userId, String groupId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        Group group = GroupManager.getInstance().findGroupById(groupId);
        User user = UserManager.getInstance().findUserById(userId);
        if(group != null && user != null) {
            user.setGroup(group);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "user has been set to group");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response getUserGroup(HttpHeaders httpHeaders, String userId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        User user = UserManager.getInstance().findUserById(userId);
        List<Group> groups = user.getGroups();
        if(groups != null && user != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            for(Group group : groups) {
                jsonArrayBuilder.add(Json.createObjectBuilder()
                        .add("group_id", group.getGroupId())
                        .add("group_name", group.getGroupName()));
            }
            jsonObjBuilder.add("message", "user's groups are acquired");
            jsonObjBuilder.add("groups", jsonArrayBuilder);
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response setDeviceOwner(HttpHeaders httpHeaders, String deviceId, String userId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        IDeviceProfile device = DeviceManager.getInstance().findDeviceById(deviceId);
        User user = UserManager.getInstance().findUserById(userId);
        if(device != null && user != null) {
            AccessControlManager.getInstance().setDeviceOwner(device, user);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "user has been set to device owner");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response getDeviceOwner(HttpHeaders httpHeaders, String deviceId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        IDeviceProfile device = DeviceManager.getInstance().findDeviceById(deviceId);
        User user = AccessControlManager.getInstance().getDeviceOwner(device);
        if(device != null && user != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "device's owner is acquired");
            jsonObjBuilder.add("user_id", user.getUserId());
            jsonObjBuilder.add("user_name", user.getUsername());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response setDeviceGroup(HttpHeaders httpHeaders, String deviceId, String groupId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        IDeviceProfile device = DeviceManager.getInstance().findDeviceById(deviceId);
        Group group = GroupManager.getInstance().findGroupById(groupId);
        if(device != null && group != null) {
            AccessControlManager.getInstance().setDeviceGroup(device, group);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "device has been set to group");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response getDeviceGroup(HttpHeaders httpHeaders, String deviceId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        IDeviceProfile device = DeviceManager.getInstance().findDeviceById(deviceId);
        Group group = AccessControlManager.getInstance().getDeviceGroup(device);
        if(device != null && group != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "device's group is acquired");
            jsonObjBuilder.add("group_id", group.getGroupId());
            jsonObjBuilder.add("group_name", group.getGroupName());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response setChannelOwner(HttpHeaders httpHeaders, String channelId, String userId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        User user = UserManager.getInstance().findUserById(userId);
        if(channel != null && user != null) {
            AccessControlManager.getInstance().setChannelOwner(channel, user);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "user has been set to channel owner");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response getChannelOwner(HttpHeaders httpHeaders, String channelId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        User user = AccessControlManager.getInstance().getChannelOwner(channel);
        if(channel != null && user != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "channel's owner is acquired");
            jsonObjBuilder.add("user_id", user.getUserId());
            jsonObjBuilder.add("user_name", user.getUsername());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response setChannelGroup(HttpHeaders httpHeaders, String channelId, String groupId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        Group group = GroupManager.getInstance().findGroupById(groupId);
        if(channel != null && group != null) {
            AccessControlManager.getInstance().setChannelGroup(channel, group);
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "channel has been set to group");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response getChannelGroup(HttpHeaders httpHeaders, String channelId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(!caller.isAdministrator()) {
            return null;
        }

        TopicChannel channel = DeviceManager.getInstance().findChannelById(channelId);
        Group group = AccessControlManager.getInstance().getChannelGroup(channel);
        if(channel != null && group != null) {
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "channel's group is acquired");
            jsonObjBuilder.add("group_id", group.getGroupId());
            jsonObjBuilder.add("group_name", group.getGroupName());
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response addEvent(HttpHeaders httpHeaders,
                             String eventName,
                             String ifString,
                             String thenString,
                             String repeat,
                             String period) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        boolean repeatVal;
        if(repeat == null)
            repeatVal = false;
        else
            repeatVal = Boolean.valueOf(repeat);

        int periodVal;
        if(period == null)
            periodVal = 1000;
        else
            periodVal = Integer.valueOf(period);

        Event event = new Event(eventName, ifString, thenString, repeatVal, periodVal);
        //TODO: Access control
        String eventId = EventManager.getInstance().addEvent(caller.getUserId(), event);

        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("message", "event has been added");
        jsonObjBuilder.add("event_id", eventId);
        JsonObject jsonObj = jsonObjBuilder.build();
        return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
    }

    @Override
    public Response removeEvent(HttpHeaders httpHeaders, String eventId) {
        User caller = Authenticator.getInstance().
                getAuthenticatedUser(
                        httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY),
                        httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN));

        if(EventManager.getInstance().doesUserHasEvent(caller.getUserId(), eventId)) {
            EventManager.getInstance().removeEvent(caller.getUserId(),
                    EventManager.getInstance().getUserEvent(caller.getUserId(), eventId));
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("message", "event has been removed");
            JsonObject jsonObj = jsonObjBuilder.build();
            return getNoCacheResponseBuilder(Response.Status.ACCEPTED).entity(jsonObj.toString()).build();
        }

        return null;
    }

    @Override
    public Response logout(
            HttpHeaders httpHeaders) {
        try {
            Authenticator authenticator = Authenticator.getInstance();
            String serviceKey = httpHeaders.getHeaderString(HTTPHeaderNames.SERVICE_KEY);
            String authToken = httpHeaders.getHeaderString(HTTPHeaderNames.AUTH_TOKEN);

            authenticator.logout(serviceKey, authToken);

            return getNoCacheResponseBuilder(Response.Status.NO_CONTENT).build();
        } catch (final GeneralSecurityException ex) {
            return getNoCacheResponseBuilder(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setMaxAge(-1);
        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);
    }
}
