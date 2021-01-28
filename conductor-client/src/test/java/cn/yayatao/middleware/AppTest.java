package cn.yayatao.middleware;

import cn.yayatao.middleware.conductor.client.ConductorClient;
import cn.yayatao.middleware.conductor.client.config.ClientConfig;
import cn.yayatao.middleware.conductor.client.producer.TaskSender;
import cn.yayatao.middleware.conductor.exception.ConductorException;
import cn.yayatao.middleware.conductor.model.Task;
import com.google.gson.Gson;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {

    static Logger logger = LoggerFactory.getLogger(AppTest.class);
    /**
     * Rigorous Test :-)
     */


    public static void main(String[] args) throws IOException {
        shouldAnswerWithTrue();
    }


    public static void shouldAnswerWithTrue() throws IOException {
        ClientConfig config = new ClientConfig();
        config.setAccessKeyId("accessKey");
        config.setAccessKeySecret("accessKeySecret");
        config.setServerHosts("127.0.0.1:8888");
        config.setClientGroup("clientGroup");
        config.setConnectTimeout(1000);
        config.setSendTimeout(1000);
        config.setRetryTimes(1);
        config.setConsumeEnable(false);
        ConductorClient client = new ConductorClient(config);
        client.start();
        TaskSender sender = client.getSender();
        try {
            sender.send(new Task());
        } catch (ConductorException e) {
            logger.info("xxx");
        }

        System.in.read();
        System.in.read();
        assertTrue(true);
    }
}
