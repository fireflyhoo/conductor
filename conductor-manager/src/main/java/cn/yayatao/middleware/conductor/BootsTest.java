package cn.yayatao.middleware.conductor;

import cn.yayatao.middleware.conductor.election.ElectionNode;
import cn.yayatao.middleware.conductor.election.ElectionNodeOptions;
import cn.yayatao.middleware.conductor.election.LeaderStateListener;
import com.alipay.sofa.jraft.entity.PeerId;

import java.util.concurrent.TimeUnit;

public class BootsTest {


    public static  class CoollerOne{
        public static void main(String[] args) {
            String dataPath = "data/server1";
            String groupId = "conductor-group";
            String serverIdStr = "127.0.0.1:6002";
            String initialConfStr = "127.0.0.1:6001,127.0.0.1:6002,127.0.0.1:6003";

            startElection(dataPath, groupId, serverIdStr, initialConfStr);
        }
    }

    public static  class CoollerTowen{
        public static void main(String[] args) {
            String dataPath = "data/server2";
            String groupId = "conductor-group";
            String serverIdStr = "127.0.0.1:6003";
            String initialConfStr = "127.0.0.1:6001,127.0.0.1:6002,127.0.0.1:6003";

            startElection(dataPath, groupId, serverIdStr, initialConfStr);
        }
    }


    public static void main(String[] args) {

        String dataPath = "data/server";
        String groupId = "conductor-group";
        String serverIdStr = "127.0.0.1:6001";
        String initialConfStr = "127.0.0.1:6001,127.0.0.1:6002,127.0.0.1:6003";

        startElection(dataPath, groupId, serverIdStr, initialConfStr);
    }

    private static void startElection(String dataPath, String groupId, String serverIdStr, String initialConfStr) {
        final ElectionNodeOptions electionOpts = new ElectionNodeOptions();

        electionOpts.setDataPath(dataPath);

        electionOpts.setGroupId(groupId);
        electionOpts.setServerAddress(serverIdStr);
        electionOpts.setInitialServerAddressList(initialConfStr);
        final ElectionNode node = new ElectionNode();

        node.addLeaderStateListener(new LeaderStateListener() {
            @Override
            public void onLeaderStart(long leaderTerm) {
                PeerId serverId = node.getNode().getLeaderId();
                String ip       = serverId.getIp();
                int    port     = serverId.getPort();

                System.out.println("[ElectionBootstrap] Leader's ip is: " + ip + ", port: " + port);
                System.out.println("[ElectionBootstrap] Leader start on term: " + leaderTerm);
            }

            @Override
            public void onLeaderStop(long leaderTerm) {
                System.out.println("[ElectionBootstrap] Leader stop on term: " + leaderTerm);
            }
        });
        node.init(electionOpts);
        while (true){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(node.getNode().getLeaderId());
        }


    }
}
