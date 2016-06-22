package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.*;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;

/**
 * 
 * @author jojllman
 * This is a class for managing device and maintain device profile. Also give device list 
 * to users.
 *
 */

public class DeviceManager {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceManager.class);
	private static DeviceManager instance;
	public static DeviceManager getInstance() { return instance; }
	
	private List<IDeviceProfile> m_deviceProfiles;
	private Map<Integer, IDeviceProfile> m_pendingProfiles;
	private AccessControlManager m_access;
	private Random m_rand;
	
	public DeviceManager() {
		m_deviceProfiles = new ArrayList<>();
		m_pendingProfiles = new HashMap<>();
		m_rand = new Random();
		instance = this;

		s_logger.debug("Device manager started.");
	}
	
	public void setAccessControlManager(AccessControlManager acc) {
		m_access = acc;
	}

	public boolean isDeviceReady(IDeviceProfile profile) { return isDeviceExist(profile); }
	public boolean isDeviceExist(IDeviceProfile profile) { return m_deviceProfiles.contains(profile); }
	public List<IDeviceProfile> getDeviceProfiles() {
		synchronized (m_deviceProfiles) {
			return new ArrayList<IDeviceProfile>(m_deviceProfiles);
		}
	}
	private void insertDeviceProfile(IDeviceProfile profile) {
		m_deviceProfiles.add(profile);
		s_logger.debug("Insert device profile " + profile);
	}
	
	public void onDeviceJoinRequest(DeviceProfile profile, ChannelHandlerContext ctx) {
		//TODO: onDeviceJoin
		int request = m_rand.nextInt();
		m_pendingProfiles.put(request, profile);
		onResponseRequest(true, request, ctx);
	};
	
	public void onDeviceConnected(JSONObject json, ChannelHandlerContext ctx) {
		DeviceProfile profile = DeviceProfile.parseDeviceAdvertisementMessage(json);
		onDeviceJoinRequest(profile, ctx);
	};
	
	public void onDeviceDisconnected(IDeviceProfile device) {
		String id = device.getId();
		
		if(m_deviceProfiles.contains(device)) {
			m_deviceProfiles.remove(device);
		}
		
		Iterator<Entry<Integer, IDeviceProfile>> it = m_pendingProfiles.entrySet().iterator();
		while(it.hasNext()) {
			Entry<Integer, IDeviceProfile> entry = it.next();
			if(entry.getValue().getId().compareTo(id) == 0) {
				m_pendingProfiles.remove(entry.getKey());
				break;
			}
		}

		m_access.unregisterDevicePermission(device);
		List<TopicChannel<?>> channels = device.getChannels();
		for(TopicChannel channel : channels) {
			m_access.unregisterChannelPermission(channel);
			m_access.unsetChannelGroup(channel);
			m_access.unsetChannelOwner(channel);
		}
		m_access.unsetDeviceGroup(device);
		m_access.unsetDeviceOwner(device);

	};
	
	public void onResponseRequest(boolean accept, int requestID, ChannelHandlerContext ctx) {
		final IDeviceProfile profile = m_pendingProfiles.get(Integer.valueOf(requestID));
		
		if(accept)
			insertDeviceProfile(profile);
		
		m_pendingProfiles.remove(requestID);
		profile.initialize();
		try {
			ByteBuf heapBuffer = ctx.alloc().buffer(4
					+ profile.getJSONReturn().toString().getBytes().length
					+ "ACK".getBytes().length);
			heapBuffer.writeBytes("ACK".getBytes());
			heapBuffer.writeInt(profile.getJSONReturn().toString().getBytes().length);
			heapBuffer.writeBytes(profile.getJSONReturn().toString().getBytes());
			ctx.writeAndFlush(heapBuffer).sync();
			s_logger.debug("Acked to device");
//			ctx.channel().closeFuture().addListener(new ChannelFutureListener() {
//				@Override
//				public void operationComplete(ChannelFuture channelFuture) throws Exception {
//					onDeviceDisconnected(profile);
//				}
//			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public TopicChannel findChannelById(String channelId) {
		for(IDeviceProfile device : m_deviceProfiles) {
			List<TopicChannel<?>> channels = device.getChannels();
			for(TopicChannel channel : channels) {
				if (channel.getId().compareTo(channelId) == 0) {
					return channel;
				}
			}
		}
		return null;
	}

	public IDeviceProfile findDeviceById(String deviceId) {
		for(IDeviceProfile device : m_deviceProfiles) {
			if(device.getId().compareTo(deviceId) == 0) {
				return device;
			}
		}
		return null;
	}
}
