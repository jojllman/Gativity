package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device;

import org.eclipse.paho.client.mqttv3.*;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.Permission;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile.DataExchangeProtocol;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event.EventManager;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class TopicChannel<T> implements Runnable, MqttCallback {
	private static final Logger s_logger = LoggerFactory.getLogger(TopicChannel.class);
	private static final String s_serverURL = "tcp://localhost:1883";

	static public enum ChannelDataType {
		Boolean, Integer, Short, String
	}
	static public enum ChannelMode {
		r,w,o,rw,ow
	}
	static public enum ChannelQoS {
		FireAndForget(0), DeliverAtleastOnce(1),DeliverExactlyOnce(2);
		private final int mask;

	    ChannelQoS(int mask)
	    {
	        this.mask = mask;
	    }

	    public int getMask()
	    {
	        return mask;
	    }
		static public ChannelQoS fromNum(String s) {
			Integer i = Integer.parseInt(s);
			if(i == 0) {
				return FireAndForget;
			}
			if(i == 1) {
				return DeliverAtleastOnce;
			}
			else {
				return DeliverExactlyOnce;
			}
		}
	}
	
	private IDeviceProfile device;
	
	private T obj;
	private T min;
	private T max;
	private String topic;
	private String altTopic;
	private String id;
	private ChannelDataType type;
	private Permission perm;
	private ChannelMode mode;
	private String description;
	private String url;
	private ChannelQoS qos;
	private DataExchangeProtocol protocol;
	private JSONObject json;
	
	private MqttAsyncClient mqttClient;
	private HashMap<IMqttDeliveryToken, T> mqttDeliverQueue;
	private boolean justWrite = false;
	
	public TopicChannel(ChannelDataType type, String topic, String perm) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}

	public TopicChannel(ChannelDataType type, String topic, String perm, T data) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}
	
	public TopicChannel(ChannelDataType type, String topic, String perm, T data, T min, T max) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}
	
	public TopicChannel(ChannelDataType type, String topic, String perm, T min, T max) {
		this.type = type;
		this.topic = topic;
		this.perm = new Permission(perm);
	}
	
	public T getValue() { return obj; }
	public T setValue(T value) {
		if(protocol == DataExchangeProtocol.MQTT) {
			if(mode == ChannelMode.w || mode == ChannelMode.rw) {
				try {
					IMqttDeliveryToken token = mqttClient.publish(altTopic,
							value.toString().getBytes(),
							qos.getMask(),
							true);
					mqttDeliverQueue.put(token, value);
					s_logger.debug("Send value: " + value);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}
	public ChannelDataType getType() { return type; }
	public ChannelDataType setType(ChannelDataType type) { this.type = type; return type; }
	public T getMin() { return min; }
	public T setMin(T value) { min = value; return min; }
	public T getMax() { return max; }
	public T setMax(T value) { max = value; return max; }
	public String getTopic() { return topic; }
	public String setTopic(String topic) { this.topic = topic; return this.topic; }
	public String getPermission() { return this.perm.getPermissionString(); }
	public String setPermission(String perm) {
		this.perm.setPermission(perm);
		return this.perm.getPermissionString();
	}
	public String getAlternativeTopic() { return altTopic; }
	public String setAlternativeTopic(String topic) { altTopic = topic; return altTopic; }
	public String getId() { return id;}
	public String setId(String id) { this.id = id; return this.id; } 
	public ChannelMode getMode() { return mode; }
	public ChannelMode setMode(ChannelMode mode) { this.mode = mode; return this.mode; }
	public String setDescription(String desc) { this.description = desc; return this.description; }
	public String getDescription() { return this.description; }
	public String setURL(String coapURL) { this.url = coapURL; return this.url; }
	public String getURL() { return this.url; }
	public ChannelQoS setQoS(ChannelQoS qos) { this.qos = qos; return this.qos; }
	public ChannelQoS getQoS() { return this.qos; }
	public IDeviceProfile setDevice(IDeviceProfile device) {
		this.device = device;
		protocol = device.getDataExchangeProtocol();
		if(protocol == DataExchangeProtocol.MQTT) {
			mqttDeliverQueue = new HashMap<>();
		}
		return this.device;
	}
	public IDeviceProfile getDevice() { return this.device; }
	
	public boolean connect() {
		if(id == null || topic == null || device == null)
			return false;

		if(protocol == DataExchangeProtocol.MQTT) {
			try {
				DataExchangeProtocol protocol = device.getDataExchangeProtocol();
				if (protocol == DataExchangeProtocol.MQTT) {
					mqttClient = new MqttAsyncClient(s_serverURL, this.id, new MemoryPersistence());
					MqttConnectOptions connOpts = new MqttConnectOptions();
					connOpts.setCleanSession(true);
					IMqttToken token = mqttClient.connect(connOpts);
					token.waitForCompletion(10000);
					mqttClient.setCallback(this);
					if(mode == ChannelMode.r || mode == ChannelMode.rw) {
						mqttClient.subscribe(altTopic, qos.getMask());
					}
				}
			} catch (MqttException e) {
				if (e.getReasonCode() == MqttException.REASON_CODE_CLIENT_TIMEOUT) {
					s_logger.info("Mqtt timeout, Topic:" + topic);
				} else {
					e.printStackTrace();
				}
				return false;
			}
		}
		else if(protocol == DataExchangeProtocol.COAP) {

		}

		prepareJsonObject();
		return true;
	}

	public boolean disconnect() {
		if(protocol == DataExchangeProtocol.MQTT) {
			IMqttToken token;
			try {
				token = mqttClient.disconnect(10000);
				token.waitForCompletion(10000);
				mqttClient = null;
			} catch (MqttException e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	public void run() {
		
	}

	@Override
	public void connectionLost(Throwable throwable) {
		mqttClient = null;
		s_logger.info("Mqtt connection lost, Topic:" + topic + ", Reason:" + throwable.getMessage());
		throwable.printStackTrace();
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		if(topic.compareToIgnoreCase(altTopic) != 0)
			return;

		String msg = new String(mqttMessage.getPayload());
		s_logger.debug("Message arrived: " + msg);
		T value;
		switch(type) {
			case Integer: {
				value = (T) Integer.valueOf(msg);
				break;
			}
			case Boolean: {
				value = (T) Boolean.valueOf(msg);
				break;
			}
			case Short: {
				value = (T) Short.valueOf(msg);
				break;
			}
			case String: {
				value = (T) msg;
				break;
			}
			default:
				s_logger.error("ChannelDataType error!");
				return;
		}

		if(justWrite) {
			if(obj != value) {
				s_logger.error("Write operabtion is interrupted.");
			}
			justWrite = false;
		}

		obj = value;
		s_logger.debug("Value has changed: " + obj.toString());

		//EventManager.getInstance().evaluateAll();
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
		obj = mqttDeliverQueue.get(iMqttDeliveryToken);
		mqttDeliverQueue.remove(iMqttDeliveryToken);
		justWrite = true;
		s_logger.debug("Delivered value: " + obj.toString());

		//EventManager.getInstance().evaluateAll();
	}

	public void prepareJsonObject() {
		JSONObject object = new JSONObject();
//		object.put("url", "tcp://192.168.1.1");
//		object.put("port", "1883");
		object.put("origin_topic", topic);
		object.put("new_topic", altTopic);
		object.put("gateway_read_prefix", "_read");
		object.put("gateway_write_prefix", "_write");
		this.json = object;
	}

	public JSONObject getJsonObject() {
		return this.json;
	}
}
