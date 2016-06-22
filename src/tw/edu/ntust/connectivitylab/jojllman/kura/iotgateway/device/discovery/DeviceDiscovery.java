package tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import tw.edu.ntust.connectivitylab.jojllman.kura.iotgateway.device.DeviceManager;

public class DeviceDiscovery {
	private static final Logger s_logger = LoggerFactory.getLogger(DeviceDiscovery.class);

	private ServerBootstrap bootstrap;
	private ChannelFuture channelFuture;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public DeviceDiscovery() {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new DeviceDiscoveryDecoder(), 
								new DeviceDiscoveryHandler(DeviceManager.getInstance()));
					}
				}).option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	public void startDiscovery() throws InterruptedException {
		int port = 7777;
		channelFuture = bootstrap.bind(port).sync();
			
		s_logger.debug("Discovery server started.");
	}

	public void stopDiscovery() {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
		channelFuture = null;
		
		s_logger.debug("Discovery server stopped.");
	}

}
