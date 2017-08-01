package com.timeyang.search.jest.example;

import io.searchbox.action.Action;
import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import org.elasticsearch.common.settings.Settings;

import java.io.IOException;

/**
 * @author chaokunyang
 */
public class IndexExample {

    public static void main(String[] args) throws IOException {
        // index1();
        // System.out.println( indexExists("integration_test_person"));
        index2();
    }

    private static void index1() throws IOException {

        // Clients.getInstance().execute(new CreateIndex.Builder("books").build());

        String settings = "{\"settings\" : {\n" +
                "        \"number_of_shards\" : 5,\n" +
                "        \"number_of_replicas\" : 1\n" +
                "    }}";

        // articles exists, post can't be applied in es 5.x. jest need upgrading
        CreateIndex s = new CreateIndex.Builder("articles_1").settings(Settings.builder().loadFromSource(settings).build().getAsMap()).build();

        Clients.getInstance().execute(s);
    }

    // doesn't work in es 5.x. because post index is not support any more
    static boolean indexExists(String index) {
        Action action = new IndicesExists.Builder(index).build();
        try {
            JestResult result = Clients.getInstance().execute(action);
            return result.isSucceeded();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void index2() throws IOException {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("number_of_shards",5);
        settingsBuilder.put("number_of_replicas",1);

        Clients.getInstance().execute(new CreateIndex.Builder("articles_2").settings(settingsBuilder.build().getAsMap()).build());
    }
}
