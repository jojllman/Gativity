package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.event;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.access.AccessControlManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.IDeviceProfile;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.TopicChannel;

public class EventManager {
	private static final Logger s_logger = LoggerFactory.getLogger(EventManager.class);
	private static final int s_decendCount = 100;
	private static EventManager instance;
	public static EventManager getInstance() { return instance; }
	
	private DeviceManager deviceManager;
	private Map<String, List<Event>> userEvents;
	private Executor executor;
	private Runnable evaluateRunnable;
	private TimerTask eventCountTask;
	private TimerTask eventExecTask;
	private Timer eventTimer;
	private BlockingQueue<Event> eventQueue;
	
	public EventManager() {
		userEvents = new HashMap<>();
		eventQueue = new LinkedBlockingQueue<>();
		eventTimer = new Timer();
		executor = Executors.newFixedThreadPool(1);

		evaluateRunnable = new Runnable() {
			@Override
			public void run() {
				Iterator<Map.Entry<String, List<Event>>> it = userEvents.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, List<Event>> entry = it.next();
					List<Event> events = entry.getValue();
					for(Event event : events) {
						try {
							if(event.isActive()) {
								if (event.evaluate()) {
									event.execute();
								}
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		eventCountTask = new TimerTask() {
			@Override
			public void run() {
				Iterator<Map.Entry<String, List<Event>>> it = userEvents.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, List<Event>> entry = it.next();
					List<Event> events = entry.getValue();
					for(Event event : events) {
						try {
							if (event.decendTimerCount(s_decendCount) == 0) {
								eventQueue.add(event);
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		eventExecTask = new TimerTask() {
			@Override
			public void run() {
				try {
					if(eventQueue.size() > 0) {
						Event e = eventQueue.take();
						if(e.isActive()) {
							if(e.evaluate()) {
								e.execute();
								if(!e.isRepeat())
									e.disable();
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		eventTimer.scheduleAtFixedRate(eventCountTask, 100, s_decendCount);
		eventTimer.scheduleAtFixedRate(eventExecTask, 100, s_decendCount);

		instance = this;
		s_logger.debug("Event manager started.");
	}
	
	public void setDeviceManager(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
	public Map<String, TopicChannel<?>> getTopicChannels(List<String> channelStrings) {
		Map<String, TopicChannel<?>> map = new HashMap<>();
		List<IDeviceProfile> profiles = deviceManager.getDeviceProfiles();
		boolean escape;
		for(String ch : channelStrings) {
			escape = false;
			for(IDeviceProfile profile : profiles) {
				List<TopicChannel<?>> channels = profile.getChannels();
				for(TopicChannel<?> channel : channels) {
					s_logger.debug("Channel ID: " + ch);
					s_logger.debug("Channel ID: " + channel.getId());
					if(ch.compareTo(channel.getId()) == 0) {
						map.put(ch, channel);
						escape = true;
						break;
					}
				}
				if(escape)
					break;
			}
		}

		if(map.size() == 0)
			return null;

		return map;
	}

	public String addEvent(String userid, Event event) {
		List<Event> events = userEvents.get(userid);
		if(events == null) {
			events = new ArrayList<>();
			userEvents.put(userid, events);
		}

		event.setEventId(AccessControlManager.GetRandomEventId());
		event.active();
		events.add(event);
		return event.getEventId();
	}

	public boolean removeEvent(String userid, Event event) {
		List<Event> events = userEvents.get(userid);
		if(events == null)
			events = new ArrayList<>();

		events.add(event);
		return true;
	}

	public List<Event> getUserEvents(String userid) {
		if(userEvents.get(userid) == null)
			return null;
		return new ArrayList<>(userEvents.get(userid));
	}
	public Event getUserEvent(String userid, String eventId) {
		List<Event> events = userEvents.get(userid);
		for(Event event : events) {
			if(event.getEventId().compareTo(eventId) == 0)
				return event;
		}
		return null;
	}
	public boolean doesUserHasEvent(String userId, String eventId) {
		List<Event> events = userEvents.get(userId);
		if(events == null)
			return false;

		for(Event event : events) {
			if(event.getEventId().compareTo(eventId) == 0)
				return true;
		}

		return false;
	}

	public Map<String, List<Event>> getAllEvents() {
		return new HashMap<>(userEvents);
	}

}
