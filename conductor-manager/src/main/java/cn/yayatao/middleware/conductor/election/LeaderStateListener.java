package cn.yayatao.middleware.conductor.election;

public interface LeaderStateListener {

    /**
     * Called when current node becomes leader
     */
    void onLeaderStart(final long leaderTerm);

    /**
     * Called when current node loses leadership.
     */
    void onLeaderStop(final long leaderTerm);
}