package demo.admin;

import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
import demo.ClientExecutor;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;

/**
 * @author chaokunyang
 */
public class IndicesAdminExample {

    public static void main(String[] args) {
        ClientExecutor.execute(IndicesAdminExample::indexAdmin);
    }

    private static void indexAdmin(Client client) {

        deleteIfPresent(client, "twitter2", "company");

        IndicesAdminClient indicesAdminClient = client.admin().indices();

        client.admin().indices().prepareCreate("twitter2")
                .setSettings(Settings.builder()
                        .put("index.number_of_shards", 3)
                        .put("index.number_of_replicas", 2)
                )
                .get();

        client.admin().indices().prepareCreate("company")
                .addMapping("tweet", "{\n" +
                        "    \"tweet\": {\n" +
                        "      \"properties\": {\n" +
                        "        \"message\": {\n" +
                        "          \"type\": \"string\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }")
                .get();


        // The PUT mapping API also allows to add a new type to an existing index:
        client.admin().indices().preparePutMapping("twitter2")
                .setType("user")
                .setSource("{\n" +
                        "  \"properties\": {\n" +
                        "    \"name\": {\n" +
                        "      \"type\": \"string\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .get();

        // You can also provide the type in the source document
        client.admin().indices().preparePutMapping("twitter2")
                .setType("user")
                .setSource("{\n" +
                        "    \"user\":{\n" +
                        "        \"properties\": {\n" +
                        "            \"name\": {\n" +
                        "                \"type\": \"string\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "    }\n" +
                        "}")
                .get();

        // You can use the same API to update an existing mapping:
        client.admin().indices().preparePutMapping("twitter2")
                .setType("user")
                .setSource("{\n" +
                        "  \"properties\": {\n" +
                        "    \"user_name\": {\n" +
                        "      \"type\": \"string\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}")
                .get();


        // The refresh API allows to explicitly refresh one or more index:
        client.admin().indices().prepareRefresh().get(); // Refresh all indices
        client.admin().indices()
                .prepareRefresh("twitter2")
                .get();
        client.admin().indices()
                .prepareRefresh("twitter2", "company")
                .get(); // Refresh many indices


        // The get settings API allows to retrieve settings of index/indices:
        GetSettingsResponse response = client.admin().indices()
                .prepareGetSettings("twitter2", "company").get();
        for (ObjectObjectCursor<String, Settings> cursor : response.getIndexToSettings()) {
            String index = cursor.key;
            Settings settings = cursor.value;
            Integer shards = settings.getAsInt("index.number_of_shards", null);
            System.out.printf("index %s number_of_shards: %s", index, shards);
            System.out.println();
            Integer replicas = settings.getAsInt("index.number_of_replicas", null);
            System.out.printf("index %s number_of_replicas: %s", index, replicas);
            System.out.println();
        }

        // You can change index settings by calling:
        client.admin().indices().prepareUpdateSettings("twitter")
                .setSettings(Settings.builder()
                        .put("index.number_of_replicas", 2)
                )
                .get();

    }

    private static void deleteIfPresent(Client client, String ... indices) {
        assert indices != null && indices.length > 0;
        for(String index : indices) {
            // IndicesExistsResponse existsResponse = client.admin().indices().exists(new IndicesExistsRequest(index)).actionGet();
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().prepareExists(index).get();
            if(indicesExistsResponse.isExists()) {
                client.admin().indices().prepareDelete(index).get(); // 必须execute().actionGet()，这样才能保证同步执行，避免索引未删除就执行下面的创建索引代码
            }
        }
    }

}
