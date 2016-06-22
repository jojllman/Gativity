package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.discovery;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;

public class DeviceDiscoveryHandler extends ChannelInboundHandlerAdapter {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceDiscoveryHandler.class);
	
	private DeviceManager deviceManager;
	
	public DeviceDiscoveryHandler(DeviceManager manager) {
		deviceManager = manager;
	}

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        
    }		

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    		
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	JSONObject json = (JSONObject) msg;
    	deviceManager.onDeviceConnected(json, ctx);
        s_logger.debug(json.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
