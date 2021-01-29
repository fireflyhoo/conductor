package cn.yayatao.middleware.conductor.client.network.netty;

import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.exception.NetworkException;
import cn.yayatao.middleware.conductor.client.network.AbstractClient;
import cn.yayatao.middleware.conductor.client.network.MessageChannel;
import cn.yayatao.middleware.conductor.client.network.MessageChannelHandler;
import cn.yayatao.middleware.conductor.client.tools.PacketTools;
import cn.yayatao.middleware.conductor.model.URL;
import cn.yayatao.middleware.conductor.packet.base.Ping;
import cn.yayatao.middleware.conductor.protobuf.MessageModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * netty 客户端
 *
 * @author fireflyhoo
 */
public class NettyClient extends AbstractClient implements MessageChannel {
    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * 定时器线程池
     */
    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1,
            new DefaultThreadFactory("conductor-client-heartbeat", true));


    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private final Integer connectTimeout = 3000;
    private final ClientConfig config = new ClientConfig();
    private Bootstrap bootstrap;
    private NioEventLoopGroup worker;
    private Channel channel;
    /**
     * 心跳定时器
     */
    private ScheduledFuture<?> heartbeatTimer;


    @Override
    public long getLastActivityTime() {
        return lastActivityTime;
    }

    @Override
    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    /***
     * 最后活动时间
     */
    private volatile long lastActivityTime;


    public NettyClient(URL url, MessageChannelHandler channelHandler,ClientConfig config) throws NetworkException {
        super(url, channelHandler, config);
    }

    @Override
    protected void initClient() {
        bootstrap = new Bootstrap();
        worker = new NioEventLoopGroup(DEFAULT_IO_THREADS, new DefaultThreadFactory("netty-client-worker", true));
        bootstrap.group(worker);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout);
        final NettyClientHandler clientHandler = new NettyClientHandler(getURL(), this);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline()
                        .addLast(new IdleStateHandler(NetworkSettings.READ_IDLE_TIME, NetworkSettings.WRITE_IDLE_TIME,
                                NetworkSettings.ALL_IDLE_TIME, NetworkSettings.IDLE_TIME_UNIT))
                        .addLast(new ProtobufVarint32FrameDecoder())
                        .addLast(new ProtobufDecoder(MessageModel.MessagePacket.getDefaultInstance()))
                        .addLast(clientHandler)
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        .addLast(new ProtobufEncoder());


            }
        });
    }

    @Override
    protected void doConnect() throws NetworkException {
        long start = System.currentTimeMillis();
        ChannelFuture channelFuture = null;
        int retryTimes = config.getRetryTimes();
        InetSocketAddress brokerAddress = getRemoteAddress();
        do {
            //循环连接测试
            try {
                channelFuture = bootstrap.connect(brokerAddress).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                          LOGGER.info("connect success broker: {}",channelFuture.channel().remoteAddress());
                        }
                    }
                });
                channelFuture.awaitUninterruptibly(3000, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                LOGGER.warn("connect throwable",e);
                // IGNORE
            }
            if (channelFuture != null && channelFuture.isSuccess()) {
                this.channel = channelFuture.channel();
                NettyChannels.getOrAddChannel(this.channel, this.url, this.channelHandler);
                startHeatbeatTimer();
                return;
            }
            retryTimes--;
        } while ((channelFuture == null || !channelFuture.channel().isActive()) && retryTimes > 0);

        if (retryTimes <= 0 && (channelFuture == null || !channelFuture.isSuccess())) {
            throw new NetworkException("client(url: " + getURL() + ") failed to connect to server "
                    + getRemoteAddress() + " client-side timeout "
                    + config.getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client " + getLocalAddress());
        }
    }


    /***
     *开始心跳
     */
    private void startHeatbeatTimer() {
        stopHeartbeatTimer();
        long heartbeat = config.getHeartbeatInterval();
        heartbeatTimer = scheduled.scheduleWithFixedDelay(new HeartBeatTask(),
                heartbeat, heartbeat, TimeUnit.MILLISECONDS);
    }

    /***
     * 停止心跳
     */
    private void stopHeartbeatTimer() {
        //取消系统调度信息
        if (heartbeatTimer != null && !heartbeatTimer.isCancelled()) {
            heartbeatTimer.cancel(true);
        }
    }

    @Override
    protected void doDisconnect() {
        try {
            NettyChannels.removeChannelIfDisconnected(channel);
        } catch (Throwable e) {
            LOGGER.warn(e.getMessage());
        }

    }

    @Override
    protected MessageChannel getChannel() {
        return NettyChannels.getOrAddChannel(channel, url, this);
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress(getURL().getHost(), getURL().getPort());
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        this.close(-1);
    }

    @Override
    public void close(int closeTimeout) {
        NettyChannels.removeChannelIfDisconnected(channel);
        channel.close();
        closed = true;
    }

    @Override
    public void caught(MessageChannel channel, Throwable throwable) {
        LOGGER.warn("异常信息", throwable);
    }

    class HeartBeatTask implements Runnable {
        @Override
        public void run() {
            MessageChannel messageChannel = NettyChannels.getOrAddChannel(channel, url, NettyClient.this);
            try {
                messageChannel.send(PacketTools.build(config.getAccessKeyId(), new Ping()));
                LOGGER.info("ping");
            } catch (NetworkException e) {
                LOGGER.warn("心跳失败", e);
            }
        }
    }
}
