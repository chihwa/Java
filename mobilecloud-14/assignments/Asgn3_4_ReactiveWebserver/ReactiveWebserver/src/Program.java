import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Program {

	/**
	 *  Wrapper Facade pattern: ServerBootstrap has it that wraps lower level socket calls
	 *  
	 *  Reactor pattern: After binding of ServerBootstrap, a new thread performs the Reactor role
	 *  that handles demultiplexing of messages. 
	 *  
	 *  Acceptor-Connector pattern's acceptor role (service initialization) is being happening
	 *  in ChannelFactory with a Selector in the parent thread.
	 * 
	 *  Half-Sync/Half-Async pattern: It's being handled by NioServerSocketChannelFactory
	 *  that each socket is handled in separate thread. Internally synchronized request is shared
	 *  between the task and thread pool acceptor.
	 *  
	 *  Tested under Eclipse with netty-3.6.0.Final.jar at
	 *  http://dl.bintray.com/netty/downloads/#netty-3.6.0.Final-dist.tar.bz2
	 */

	public static class EchoServer {

		private ServerBootstrap bootstrap;
		private ChannelFactory factory;

		private InetSocketAddress address;

		public EchoServer(InetSocketAddress addr){
			address = addr;
		}

		public void start() throws IOException{

			factory = new NioServerSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool());

			bootstrap = new ServerBootstrap(factory);

			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() {
					return Channels.pipeline(new EchoServerHandler());
				}
			});

			bootstrap.setOption("child.tcpNoDelay", true);
			bootstrap.setOption("child.keepAlive", true);

			bootstrap.bind(address);

			System.out.println("Server is running at " + address + '\n');
		}

	}

	public static class EchoServerHandler extends SimpleChannelUpstreamHandler {
		@Override
		public void channelConnected(
				ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			System.out.println("connection accepted for " + e.getChannel());
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
			Channel ch = e.getChannel();
			ch.write(e.getMessage());
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
			e.getCause().printStackTrace();
			Channel ch = e.getChannel();
			ch.close();
		}
	}

	public static void main(String[] args) {
		Integer port = 8080;

		try {
			if (args.length != 0) {
				if (args.length == 1) {
					try {
						port = Integer.parseInt(args[0]);

					} catch (NumberFormatException e) {
						throw new Exception(String.format(
								"Invalid argument: %s. Should be integer.",
								args[0]));
					}
				} else {
					throw new Exception("usage: java server port_number");
				}
			}

			EchoServer server = new EchoServer(new InetSocketAddress("localhost", port));
			server.start();

		} catch (IOException e) {
			System.err.println(e);

		} catch (Exception e) {
			System.err.println(e);
		}
	}


}