package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel.ChannelDataType;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel.ChannelMode;

public class DeviceProfile implements IDeviceProfile{
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceProfile.class);

	static public DeviceProfile parseDeviceAdvertisementMessage(byte[] data) {
		return parseDeviceAdvertisementMessage(new JSONObject(new String(data)));
	}
	static public DeviceProfile parseDeviceAdvertisementMessage(JSONObject jsonObject) {
		AccessControlManager accessControlManager = AccessControlManager.getInstance();
		DeviceProfile profile = new DeviceProfile();
		
		String name = jsonObject.getString("Name");
		String description = jsonObject.getString("Description");
		JSONObject deviceMain = jsonObject.getJSONObject("Detail");
		String type = deviceMain.getString("Type");
		String uuid = deviceMain.getString("UUID");
		String perm = deviceMain.getString("Permission");
		String protocol = deviceMain.getString("Protocol");
		
		profile.setJSONRoot(jsonObject);
		profile.setId(AccessControlManager.GetRandomDeviceId());
		profile.setName(name);
		profile.setDescription(description);
		profile.setType(DeviceType.valueOf(type));
		profile.setUUID(UUID.fromString(uuid));
		accessControlManager.registerDevicePermission(profile, perm);
		profile.setDataExchangeProtocol(DataExchangeProtocol.valueOf(protocol));
		
		JSONArray channels = deviceMain.getJSONArray("Channels");
		
		for(int i=0; i<channels.length(); i++) {
			JSONObject obj = channels.getJSONObject(i);
			String url = "";
			if(profile.getDataExchangeProtocol() == DataExchangeProtocol.COAP) {
				obj.getString("URL");
			}
			String topic = obj.getString("Topic");
			String valueType = obj.getString("Type");
			String valuePermission = obj.getString("Permission");
			String valueMode = obj.getString("Mode");
			String valueQos = obj.getString("QOS");
			String valueDescription = obj.getString("Description");
			String id = AccessControlManager.GetRandomChannelId();
			TopicChannel<?> ch = null;
			ChannelDataType cdt = ChannelDataType.valueOf(valueType);
			ChannelMode mode = ChannelMode.valueOf(valueMode);
			TopicChannel.ChannelQoS qos = TopicChannel.ChannelQoS.fromNum(valueQos);
			
			if(cdt == ChannelDataType.Boolean) {
				try {
					Boolean valueBoolDefault = obj.getBoolean("Default");
					ch = new TopicChannel<Boolean>(cdt,
							topic,
							valuePermission,
							valueBoolDefault);
				}
				catch (JSONException e) {
					//e.printStackTrace();
					ch = new TopicChannel<Boolean>(cdt,
							topic,
							valuePermission);
				}
			}
			else if(cdt == ChannelDataType.Integer) {
				try {
					Integer valueIntegerDefault = obj.getInt("Default");
					Integer valueIntegerMin = obj.getInt("Min");
					Integer valueIntegerMax = obj.getInt("Max");
					ch = new TopicChannel<Integer>(cdt,
							topic,
							valuePermission,
							valueIntegerDefault, valueIntegerMin, valueIntegerMax);
				}
				catch (JSONException e) {
					//e.printStackTrace();
					ch = new TopicChannel<Integer>(cdt,
							topic,
							valuePermission);
				}
			}
			else if(cdt == ChannelDataType.Short) {
				try {
					Short valueShortDefault = Short.parseShort(obj.getString("Default"));
					Short valueShortMin = Short.parseShort(obj.getString("Min"));
					Short valueShortMax = Short.parseShort(obj.getString("Max"));
					ch = new TopicChannel<Short>(cdt,
							topic,
							valuePermission,
							valueShortDefault, valueShortMin, valueShortMax);
				}
				catch (JSONException e) {
					//e.printStackTrace();
					ch = new TopicChannel<Short>(cdt,
							topic,
							valuePermission);
				}
			}
			else if(cdt == ChannelDataType.String) {
				try {
					String valueString = obj.getString("Default");
					ch = new TopicChannel<String>(cdt,
							topic,
							valuePermission,
							valueString);
				}
				catch (JSONException e) {
					//e.printStackTrace();
					ch = new TopicChannel<String>(cdt,
							topic,
							valuePermission);
				}
			}
			
			if(ch == null) {
				s_logger.error("Channel read error!");
			}
			else {
				if(profile.getDataExchangeProtocol() == DataExchangeProtocol.COAP) {
					ch.setURL(url);
				}
				ch.setId(id);
				ch.setMode(mode);
				ch.setDescription(valueDescription);
				ch.setQoS(qos);
				ch.setDevice(profile);
				profile.addChannel(ch);
				accessControlManager.registerChannelPermission(ch, valuePermission);
			}
			
		}
		
		return profile;
	}
	
	private String m_id;
	private String m_name;
	private String m_description;
	private DeviceType m_type;
	private UUID m_uuid;
	private Map<String, TopicChannel<?>> m_channleList;
	private JSONObject m_jsonRoot;
	private DataExchangeProtocol m_protocol;
	private JSONObject m_jsonReturn;
	
	public DeviceProfile() {
		m_channleList = new HashMap<>();
	}
	
	private void addChannel (TopicChannel<?> channel) {
		m_channleList.put(channel.getTopic(), channel);
	}
	
	@Override
	public boolean setName(String name) {
		if(name == null)
			return false;
		
		this.m_name = name;
		return true;
	}

	@Override
	public boolean setDataExchangeProtocol(DataExchangeProtocol protocol) {
		this.m_protocol = protocol;
		return true;
	}

	@Override
	public boolean setDataExchangeProtocolVersion(String version) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setCommunicationTechnology(CommunicationTechnology tech) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDescription(String description) {
		if(description == null)
			return false;
		
		this.m_description = description;
		return true;
	}

	@Override
	public boolean setType(DeviceType type) {
		this.m_type = type;
		return true;
	}

	@Override
	public boolean setUUID(UUID uuid) {
		if(uuid == null)
			return false;
		
		this.m_uuid = uuid;
		return true;
	}

	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public DataExchangeProtocol getDataExchangeProtocol() {
		return m_protocol;
	}

	@Override
	public String getDataExchangeProtocolVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CommunicationTechnology getCommunicationTechnology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public DeviceType getType() {
		return m_type;
	}

	@Override
	public UUID getUUID() {
		return m_uuid;
	}

	@Override
	public List<String> getDataTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAlternativeDataTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAlternativeDataTopicName(String oriTopic, String newTopic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, QoSLevel> getDataTopicQoSs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDataTopicQoS(String topic, QoSLevel qos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, SecurityLevel> getDataTopicSecurities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setDataTopicSecurity(String topic, SecurityLevel security) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getControlTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAlternativeControlTopicNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setAlternativeControlTopicName(String oriTopic, String newTopic) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, QoSLevel> getControlTopicQoSs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setControlTopicQoS(String topic, QoSLevel qos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Map<String, SecurityLevel> getControlTopicSecurities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setControlTopicSecurity(String topic, SecurityLevel security) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setJSONRoot(JSONObject root) {
		this.m_jsonRoot = root;
		return true;
	}
	
	@Override
	public JSONObject getJSONRoot() {
		return m_jsonRoot;
	}
	
	@Override
	public boolean setId(String id) {
		if(id == null) {
			return false;
		}
		
		this.m_id = id;
		return true;
	}
	
	@Override
	public String getId() {
		return m_id;
	}
	@Override
	public List<TopicChannel<?>> getChannels() {
		return new ArrayList<>(m_channleList.values());
	}

	@Override
	public boolean initialize() {
		JSONObject object = new JSONObject();
		JSONArray array = new JSONArray();
		for(TopicChannel channel : m_channleList.values()) {
			if(getDataExchangeProtocol() == DataExchangeProtocol.MQTT) {
				channel.setAlternativeTopic(channel.getTopic() + channel.getId());
				channel.connect();
				array.put(channel.getJsonObject());
			}
		}
		object.put("device_id", m_id);
		object.put("channels", array);
		m_jsonReturn = object;
		s_logger.debug("Initialize: " + object.toString());
		return true;
	}

	@Override
	public JSONObject getJSONReturn() {
		return m_jsonReturn;
	}
}
