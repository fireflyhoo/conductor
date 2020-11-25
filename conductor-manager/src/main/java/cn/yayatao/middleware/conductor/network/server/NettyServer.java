package cn.yayatao.middleware.conductor.network.server;

import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.network.netty.NetworkSettings;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class NettyServer extends AbstractServer {

    /**
     * 默认IO线程数
     */
    private static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * 连接上的通道
     */
    private Map<String, MessageChannel> channels; // ip:port ,channel

    private ServerBootstrap bootstrap;

    /***
     * 线程id
     */
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    //管道
    private Channel channel;

    public NettyServer(InetSocketAddress bindAddress, MessageChannelHandler channelHandler) {
        super(bindAddress, channelHandler);
    }

    /**
     * 监听本地端口
     */
    @Override
    protected void bind() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("NettyServerBoss", false));
        workerGroup = new NioEventLoopGroup(DEFAULT_IO_THREADS, new DefaultThreadFactory("NettyServerWorker", false));
        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getURL(), this);
        channels = nettyServerHandler.getChannels();
        bootstrap.group(
                bossGroup, workerGroup
        ).channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(NetworkSettings.READ_IDLE_TIME, NetworkSettings.WRITE_IDLE_TIME,
                                NetworkSettings.ALL_IDLE_TIME, NetworkSettings.IDLE_TIME_UNIT))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufDecoder(MessageModel.MessagePacket.getDefaultInstance()))
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufEncoder())
                                .addLast(nettyServerHandler);
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(6666);
        System.out.println("6666");
        channelFuture.syncUninterruptibly();
        channel = channelFuture.channel();
    }

    private URL getURL() {
        return null;
    }
}
