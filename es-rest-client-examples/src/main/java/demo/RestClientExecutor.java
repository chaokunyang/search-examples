package demo;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * @author chaokunyang
 */
public class RestClientExecutor {

    public static void execute(Consumer<RestClient> restClientConsumer) {
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        Header[] defaultHeaders = {new BasicHeader("Content-Type", "application/json")};

        RestClient restClient = RestClient.builder(
                new HttpHost("10.10.100.59", 9200, "http"),
                new HttpHost("10.10.100.77", 9200, "http"),
                new HttpHost("10.10.100.99", 9200, "http"))
                .setDefaultHeaders(defaultHeaders)
                .setFailureListener(sniffOnFailureListener)
                .build();

        Sniffer sniffer = Sniffer.builder(restClient).build();
        sniffOnFailureListener.setSniffer(sniffer);

        restClientConsumer.accept(restClient);

        try {
            sniffer.close();
            restClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
