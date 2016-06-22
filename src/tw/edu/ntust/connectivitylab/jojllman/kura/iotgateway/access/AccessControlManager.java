package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class AccessControlManager {
	private static final Logger s_logger = LoggerFactory.getLogger(AccessControlManager.class);
	private static AccessControlManager instance;
	public static AccessControlManager getInstance() { return instance; }
	private static final SessionIdentifierGenerator s_deviceIdGen = new SessionIdentifierGenerator();
	private static final SessionIdentifierGenerator s_channelIdGen = new SessionIdentifierGenerator();
	private static final SessionIdentifierGenerator s_userIdGen = new SessionIdentifierGenerator();
	private static final SessionIdentifierGenerator s_groupIdGen = new SessionIdentifierGenerator();
	private static final SessionIdentifierGenerator s_eventIdGen = new SessionIdentifierGenerator();
	public static String GetRandomDeviceId() {
		return s_deviceIdGen.nextSessionId();
	}
	public static String GetRandomChannelId() {
		return s_channelIdGen.nextSessionId();
	}
	public static String GetRandomUserId() {
		return s_userIdGen.nextSessionId();
	}
	public static String GetRandomGroupId() { return s_groupIdGen.nextSessionId(); }
	public static String GetRandomEventId() { return s_eventIdGen.nextSessionId(); }

	private DeviceManager m_deviceManager;
	private Map<String, Permission> m_devicePermissions;
	private Map<String, Permission> m_channelPermissions;
	private Map<String, Group> m_deviceGroup;
	private Map<String, Group> m_channelGroup;
	private Map<String, User> m_deviceOwner;
	private Map<String, User> m_channelOwner;
	
	public AccessControlManager() {
		m_devicePermissions = new HashMap<>();
		m_channelPermissions = new HashMap<>();
		m_deviceGroup = new HashMap<>();
		m_channelGroup = new HashMap<>();
		m_deviceOwner = new HashMap<>();
		m_channelOwner = new HashMap<>();
		
		instance = this;

		s_logger.debug("Access control manager started.");
	}

	public void setDeviceManager(DeviceManager deviceManager) {
		this.m_deviceManager = deviceManager;
	}
	
	public boolean registerDevicePermission(IDeviceProfile device, String perm) {
		String id = device.getId();
		if(m_devicePermissions.get(id) != null) {
			s_logger.debug("Device registered already");
			return false;
		}
		
		Permission permission = new Permission(perm);
		m_devicePermissions.put(id, permission);
		return true;
	}
	
	public boolean registerChannelPermission(TopicChannel<?> channel, String perm) {
		String id = channel.getId();
		if(m_channelPermissions.get(id) != null) {
			s_logger.debug("Channel registered already");
			return false;
		}
		
		Permission permission = new Permission(perm);
		m_channelPermissions.put(id, permission);
		return true;
	}
	
	public boolean unregisterDevicePermission(IDeviceProfile device) {
		String id = device.getId();
		Permission perm = m_devicePermissions.get(id);
		if(perm == null) {
			s_logger.debug("Device didn't exist");
			return false;
		}
		m_devicePermissions.remove(id);
		return true;
	}
	
	public boolean unregisterChannelPermission(TopicChannel<?> channel) {
		String id = channel.getId();
		Permission perm = m_channelPermissions.get(id);
		if(perm == null) {
			s_logger.debug("Channel didn't exist");
			return false;
		}
		m_channelPermissions.remove(id);
		return true;
	}
	
	public boolean getDeviceReadPermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getReadPermission(type);
	}
	
	public boolean getDeviceWritePermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getWritePermission(type);
	}
	
	public boolean getDeviceModifyPermission(IDeviceProfile device, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(device.getId());
		if(permission == null)
			return false;
		
		return permission.getModifyPermission(type);
	}
	
	public boolean getChannelReadPermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getReadPermission(type);
	}
	
	public boolean getChannelWritePermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getWritePermission(type);
	}
	
	public boolean getChannelModifyPermission(TopicChannel<?> channel, Permission.PermissionType type) {
		Permission permission = m_devicePermissions.get(channel.getId());
		if(permission == null)
			return false;
		
		return permission.getModifyPermission(type);
	}

	public boolean setDeviceGroup(IDeviceProfile device, Group group) {
		if(!m_deviceManager.isDeviceExist(device))
			return false;
		GroupManager groupManager = GroupManager.getInstance();
		if(!groupManager.isGroupExist(group))
			return false;

		m_deviceGroup.put(device.getId(), group);
		return true;
	}
	public boolean unsetDeviceGroup(IDeviceProfile device) {
		m_deviceGroup.remove(device.getId());
		return true;
	}
	public Group getDeviceGroup(IDeviceProfile device) {
		return m_deviceGroup.get(device.getId());
	}
	public boolean setDeviceOwner(IDeviceProfile device, User user) {
		if(!m_deviceManager.isDeviceExist(device))
			return false;
		UserManager userManager = UserManager.getInstance();
		if(!userManager.isUserExist(user))
			return false;

		m_deviceOwner.put(device.getId(), user);
		return true;
	}
	public boolean unsetDeviceOwner(IDeviceProfile device) {
		m_deviceOwner.remove(device.getId());
		return true;
	}
	public User getDeviceOwner(IDeviceProfile device) {
		return m_deviceOwner.get(device.getId());
	}

	public boolean setChannelGroup(TopicChannel channel, Group group) {
		if(!m_deviceManager.isDeviceExist(channel.getDevice()))
			return false;
		GroupManager groupManager = GroupManager.getInstance();
		if(!groupManager.isGroupExist(group))
			return false;

		m_deviceGroup.put(channel.getId(), group);
		return true;
	}
	public boolean unsetChannelGroup(TopicChannel channel) {
		m_deviceGroup.remove(channel.getId());
		return true;
	}
	public Group getChannelGroup(TopicChannel channel) {
		return m_channelGroup.get(channel.getId());
	}
	public boolean setChannelOwner(TopicChannel channel, User user) {
		if(!m_deviceManager.isDeviceExist(channel.getDevice()))
			return false;
		UserManager userManager = UserManager.getInstance();
		if(!userManager.isUserExist(user))
			return false;

		m_deviceOwner.put(channel.getId(), user);
		return true;
	}
	public boolean unsetChannelOwner(TopicChannel channel) {
		m_deviceOwner.remove(channel.getId());
		return true;
	}
	public User getChannelOwner(TopicChannel channel) {
		return m_channelOwner.get(channel.getId());
	}

	public boolean canUserReadDevice(User user, IDeviceProfile device) {
		if(user.isAdministrator())
			return true;
		if(getDeviceOwner(device).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getDeviceReadPermission(device, Permission.PermissionType.Own);
		}
		else if(getDeviceGroup(device).containUser(user)) {
			return getDeviceReadPermission(device, Permission.PermissionType.Group);
		}

		return getDeviceReadPermission(device, Permission.PermissionType.All);
	}

	public boolean canUserWriteDevice(User user, IDeviceProfile device) {
		if(user.isAdministrator())
			return true;
		if(getDeviceOwner(device).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getDeviceWritePermission(device, Permission.PermissionType.Own);
		}
		else if(getDeviceGroup(device).containUser(user)) {
			return getDeviceWritePermission(device, Permission.PermissionType.Group);
		}

		return getDeviceWritePermission(device, Permission.PermissionType.All);
	}

	public boolean canUserModifyDevice(User user, IDeviceProfile device) {
		if(user.isAdministrator())
			return true;
		if(getDeviceOwner(device).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getDeviceModifyPermission(device, Permission.PermissionType.Own);
		}
		else if(getDeviceGroup(device).containUser(user)) {
			return getDeviceModifyPermission(device, Permission.PermissionType.Group);
		}

		return getDeviceModifyPermission(device, Permission.PermissionType.All);
	}

	public boolean canUserReadChannel(User user, TopicChannel channel) {
		if(user.isAdministrator())
			return true;
		if(getChannelOwner(channel).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getChannelReadPermission(channel, Permission.PermissionType.Own);
		}
		else if(getChannelGroup(channel).containUser(user)) {
			return getChannelReadPermission(channel, Permission.PermissionType.Group);
		}

		return getChannelReadPermission(channel, Permission.PermissionType.All);
	}

	public boolean canUserWriteChannel(User user, TopicChannel channel) {
		if(user.isAdministrator())
			return true;
		if(getChannelOwner(channel).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getChannelWritePermission(channel, Permission.PermissionType.Own);
		}
		else if(getChannelGroup(channel).containUser(user)) {
			return getChannelWritePermission(channel, Permission.PermissionType.Group);
		}

		return getChannelWritePermission(channel, Permission.PermissionType.All);
	}

	public boolean canUserModifyChannel(User user, TopicChannel channel) {
		if(user.isAdministrator())
			return true;
		if(getChannelOwner(channel).getUsername().compareToIgnoreCase(user.getUsername()) == 0) {
			return getChannelModifyPermission(channel, Permission.PermissionType.Own);
		}
		else if(getChannelGroup(channel).containUser(user)) {
			return getChannelModifyPermission(channel, Permission.PermissionType.Group);
		}

		return getChannelModifyPermission(channel, Permission.PermissionType.All);
	}

	public void onUserRemoved(User user) {
		List<String> devices = new ArrayList<>();
		List<String> channels = new ArrayList<>();
		if(m_deviceOwner.containsValue(user)) {
			Set<Map.Entry<String, User>> deviceSet = m_deviceOwner.entrySet();
			for(Map.Entry<String, User> entry : deviceSet) {
				devices.add(entry.getKey());
			}
		}
		if(m_channelOwner.containsValue(user)) {
			Set<Map.Entry<String, User>> channelSet = m_channelOwner.entrySet();
			for(Map.Entry<String, User> entry : channelSet) {
				channels.add(entry.getKey());
			}
		}
		for(String deviceId : devices)
			m_deviceOwner.remove(deviceId);
		for(String channelId : channels)
			m_channelOwner.remove(channelId);
	}
}
