package demo;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Consumer;

/**
 * @author chaokunyang
 */
public class ClientExecutor {
    public static void execute(Consumer<Client> command) {
        // on startup
        Settings settings = Settings.builder()
                .put("cluster.name", "ucloud-search")
                .put("client.transport.sniff", true).build();

        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.10.100.59"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.10.100.77"), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.10.100.99"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        command.accept(client);

        // on shutdown
        client.close();
    }
}
