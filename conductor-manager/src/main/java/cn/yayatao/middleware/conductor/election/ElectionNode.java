package cn.yayatao.middleware.conductor.election;


import com.alipay.sofa.jraft.Lifecycle;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.entity.UserLog;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import com.alipay.sofa.jraft.util.internal.ThrowUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jiachun.fjc
 */
public class ElectionNode implements Lifecycle<ElectionNodeOptions> {

    private static final Logger LOG = LoggerFactory.getLogger(ElectionNode.class);

    private final List<LeaderStateListener> listeners = new CopyOnWriteArrayList<>();
    private RaftGroupService raftGroupService;
    private Node node;
    private ElectionOnlyStateMachine fsm;

    private boolean started;

    @Override
    public boolean init(final ElectionNodeOptions opts) {
        if (this.started) {
            LOG.info("[ElectionNode: {}] already started.", opts.getServerAddress());
            return true;
        }
        // node options
        NodeOptions nodeOpts = opts.getNodeOptions();
        if (nodeOpts == null) {
            nodeOpts = new NodeOptions();
        }
        this.fsm = new ElectionOnlyStateMachine(this.listeners);
        nodeOpts.setFsm(this.fsm);
        final Configuration initialConf = new Configuration();
        if (!initialConf.parse(opts.getInitialServerAddressList())) {
            throw new IllegalArgumentException("Fail to parse initConf: " + opts.getInitialServerAddressList());
        }
        // Set the initial cluster configuration
        nodeOpts.setInitialConf(initialConf);
        final String dataPath = opts.getDataPath();
        try {
            FileUtils.forceMkdir(new File(dataPath));
        } catch (final IOException e) {
            LOG.error("Fail to make dir for dataPath {}.", dataPath);
            return false;
        }
        // Set the data path
        // Log, required
        nodeOpts.setLogUri(Paths.get(dataPath, "log").toString());
        // Metadata, required
        nodeOpts.setRaftMetaUri(Paths.get(dataPath, "meta").toString());
        nodeOpts.setSnapshotUri(Paths.get(dataPath, "snapshot").toString());

        final String groupId = opts.getGroupId();
        final PeerId serverId = new PeerId();
        if (!serverId.parse(opts.getServerAddress())) {
            throw new IllegalArgumentException("Fail to parse serverId: " + opts.getServerAddress());
        }
        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOpts, rpcServer);


        this.node = this.raftGroupService.start();
        if (this.node != null) {
            this.started = true;
        }
        return this.started;
    }

    @Override
    public void shutdown() {
        if (!this.started) {
            return;
        }
        if (this.raftGroupService != null) {
            this.raftGroupService.shutdown();
            try {
                this.raftGroupService.join();
            } catch (final InterruptedException e) {
                ThrowUtil.throwException(e);
            }
        }
        this.started = false;
        LOG.info("[ElectionNode] shutdown successfully: {}.", this);
    }

    public Node getNode() {
        return node;
    }

    public ElectionOnlyStateMachine getFsm() {
        return fsm;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isLeader() {
        return this.fsm.isLeader();
    }

    public void addLeaderStateListener(final LeaderStateListener listener) {
        this.listeners.add(listener);
    }
}