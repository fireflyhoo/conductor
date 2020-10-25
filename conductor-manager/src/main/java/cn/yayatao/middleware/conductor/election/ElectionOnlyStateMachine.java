package cn.yayatao.middleware.conductor.election;

import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author jiachun.fjc
 */
public class ElectionOnlyStateMachine extends StateMachineAdapter {

    private static final Logger LOG        = LoggerFactory.getLogger(ElectionOnlyStateMachine.class);

    private final AtomicLong leaderTerm = new AtomicLong(-1L);
    private final List<LeaderStateListener> listeners;

    public ElectionOnlyStateMachine(List<LeaderStateListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void onApply(final Iterator it) {
        // election only, do nothing
        while (it.hasNext()) {
            LOG.info("On apply with term: {} and index: {}. ", it.getTerm(), it.getIndex());
            it.next();
        }
    }

    @Override
    public void onLeaderStart(final long term) {
        super.onLeaderStart(term);
        this.leaderTerm.set(term);
        for (final LeaderStateListener listener : this.listeners) { // iterator the snapshot
            listener.onLeaderStart(term);
        }
    }

    @Override
    public void onLeaderStop(final Status status) {
        super.onLeaderStop(status);
        final long oldTerm = leaderTerm.get();
        this.leaderTerm.set(-1L);
        for (final LeaderStateListener listener : this.listeners) { // iterator the snapshot
            listener.onLeaderStop(oldTerm);
        }
    }

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    public void addLeaderStateListener(final LeaderStateListener listener) {
        this.listeners.add(listener);
    }
}