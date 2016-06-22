/**
 * 
 */
package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

/**
 * @author jojllman
 * This is a device profile interface class. It's is used to store device's profile
 *
 */
public interface IDeviceProfile {
	static public enum DataExchangeProtocol {
		MQTT,COAP
	}
	static public enum CommunicationTechnology {
		BLE,ZigBee,WiFi,Ethernet
	}
	
	static public enum QoSLevel {
		None,AtleastOnce,ExactlyOnce
	}
	
	static public enum SecurityLevel {
		None,TLS,SSL,DTLS
	}
	
	static public enum DeviceType {
		Acuator, Sensor
	}
	
	public boolean setName(String name);
	public boolean setDataExchangeProtocol(DataExchangeProtocol protocol);
	public boolean setDataExchangeProtocolVersion(String version);
	public boolean setCommunicationTechnology(CommunicationTechnology tech);
	public boolean setDescription(String description);
	public boolean setType(DeviceType type);
	public boolean setUUID(UUID uuid);
	public String getName();
	public DataExchangeProtocol getDataExchangeProtocol();
	public String getDataExchangeProtocolVersion();
	public CommunicationTechnology getCommunicationTechnology();
	public String getDescription();
	public DeviceType getType();
	public UUID getUUID();
	public boolean setJSONRoot(JSONObject root);
	public JSONObject getJSONRoot();
	public boolean setId(String id);
	public String getId();
	
	public List<String> getDataTopicNames();
	public Map<String, String> getAlternativeDataTopicNames();
	public boolean setAlternativeDataTopicName(String oriTopic, String newTopic);
	public Map<String, QoSLevel> getDataTopicQoSs();
	public boolean setDataTopicQoS(String topic, QoSLevel qos);
	public Map<String, SecurityLevel> getDataTopicSecurities();
	public boolean setDataTopicSecurity(String topic, SecurityLevel security);
	
	public List<String> getControlTopicNames();
	public Map<String, String> getAlternativeControlTopicNames();
	public boolean setAlternativeControlTopicName(String oriTopic, String newTopic);
	public Map<String, QoSLevel> getControlTopicQoSs();
	public boolean setControlTopicQoS(String topic, QoSLevel qos);
	public Map<String, SecurityLevel> getControlTopicSecurities();
	public boolean setControlTopicSecurity(String topic, SecurityLevel security);
	
	public List<TopicChannel<?>> getChannels();

	public boolean initialize();
	public JSONObject getJSONReturn();
}
