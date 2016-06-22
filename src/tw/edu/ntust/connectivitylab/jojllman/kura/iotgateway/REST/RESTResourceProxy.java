package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.REST;

import java.io.Serializable;
import javax.ejb.Local;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/gateway")
public interface RESTResourceProxy extends Serializable {

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @Context HttpHeaders httpHeaders,
            @FormParam("username") String username,
            @FormParam("password") String password);

    @GET
    @Path("/demo-get-method")
    @Produces(MediaType.APPLICATION_JSON)
    public Response demoGetMethod();

    @POST
    @Path("/demo-post-method")
    @Produces(MediaType.APPLICATION_JSON)
    public Response demoPostMethod();

    @GET
    @Path("/query-user-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryUserList(@Context HttpHeaders httpHeaders);

    @GET
    @Path("/query-group-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryGroupList(@Context HttpHeaders httpHeaders);

    @GET
    @Path("/query-group-user-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryGroupUserList(@Context HttpHeaders httpHeaders);

    @GET
    @Path("/query-channel-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryChannelList(@Context HttpHeaders httpHeaders);

    @GET
    @Path("/query-device-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDeviceList(@Context HttpHeaders httpHeaders);

    @GET
    @Path("/query-event-list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryEventList(@Context HttpHeaders httpHeaders);

    @POST
    @Path("/write-channel-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeChannelData(@Context HttpHeaders httpHeaders,
                                     @FormParam("channel_id") String channelId,
                                     @FormParam("type") String type,
                                     @FormParam("value") String value);

    @POST
    @Path("/read-channel-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readChannelData(@Context HttpHeaders httpHeaders,
                                    @FormParam("channel_id") String channelId);

    @POST
    @Path("/add-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@Context HttpHeaders httpHeaders,
                            @FormParam("user_name") String username,
                            @FormParam("password") String password,
                            @FormParam("group_id") String group_id);

    @POST
    @Path("/remove-user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@Context HttpHeaders httpHeaders,
                               @FormParam("user_id") String userId);

    @POST
    @Path("/add-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGroup(@Context HttpHeaders httpHeaders,
                             @FormParam("group_name") String groupName);

    @POST
    @Path("/remove-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeGroup(@Context HttpHeaders httpHeaders,
                                @FormParam("group_id") String groupId);

    @POST
    @Path("/set-user-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setUserGroup(@Context HttpHeaders httpHeaders,
                                 @FormParam("user_id") String userId,
                                 @FormParam("group_id") String groupId);

    @POST
    @Path("/get-user-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserGroup(@Context HttpHeaders httpHeaders,
                                 @FormParam("user_id") String userId);

    @POST
    @Path("/set-device-owner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDeviceOwner(@Context HttpHeaders httpHeaders,
                                   @FormParam("device_id") String deviceId,
                                   @FormParam("user_id") String userId);

    @POST
    @Path("/get-device-owner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceOwner(@Context HttpHeaders httpHeaders,
                                   @FormParam("device_id") String deviceId);

    @POST
    @Path("/set-device-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setDeviceGroup(@Context HttpHeaders httpHeaders,
                                   @FormParam("device_id") String deviceId,
                                   @FormParam("group_id") String groupId);

    @POST
    @Path("/get-device-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDeviceGroup(@Context HttpHeaders httpHeaders,
                                   @FormParam("device_id") String deviceId);

    @POST
    @Path("/set-channel-owner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setChannelOwner(@Context HttpHeaders httpHeaders,
                                    @FormParam("channel_id") String channelId,
                                    @FormParam("user_id") String userId);

    @POST
    @Path("/get-channel-owner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelOwner(@Context HttpHeaders httpHeaders,
                                    @FormParam("channel_id") String channelId);

    @POST
    @Path("/set-channel-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setChannelGroup(@Context HttpHeaders httpHeaders,
                                    @FormParam("channel_id") String channelId,
                                    @FormParam("group_id") String groupId);

    @POST
    @Path("/get-channel-group")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChannelGroup(@Context HttpHeaders httpHeaders,
                                    @FormParam("channel_id") String channelId);

    @POST
    @Path("/add-event")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addEvent(@Context HttpHeaders httpHeaders,
                             @FormParam("name") String eventName,
                             @FormParam("if") String ifString,
                             @FormParam("then") String thenString,
                             @FormParam("repeat") String repeat,
                             @FormParam("period") String period);

    @POST
    @Path("/remove-event")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeEvent(@Context HttpHeaders httpHeaders,
                                @FormParam("event_id") String eventId);

    @POST
    @Path("/logout")
    public Response logout(
            @Context HttpHeaders httpHeaders
    );
}
