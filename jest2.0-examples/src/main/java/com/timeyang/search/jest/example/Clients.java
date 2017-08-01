package com.timeyang.search.jest.example;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

/**
 * @author chaokunyang
 */
public class Clients {

    private static JestClient client;

    static {
        client = buildClient();
    }

    private Clients() {
    }

    private static JestClient buildClient() {
        // Construct a new Jest client according to configuration via factory
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://10.10.100.77:9200")
                .multiThreaded(true)
                //Per default this implementation will create no more than 2 concurrent connections per given route
                .defaultMaxTotalConnectionPerRoute(4)
                // and no more 20 connections in total
                .maxTotalConnection(40)
                .build());
        JestClient client = factory.getObject();

        return client;
    }

    public static JestClient getInstance() {
        return client;
    }
}
