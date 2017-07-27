package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;

import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author chaokunyang
 */
public class BulkExample {
    public static void main(String[] args) {
        ClientExecutor.execute(BulkExample::bulk);
    }

    private static void bulk(Client client) {
        try {
            BulkRequestBuilder bulkRequest = client.prepareBulk();

// either use client#prepare, or use Requests# to directly build index/delete requests
            bulkRequest.add(client.prepareIndex("twitter", "tweet", "3")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
            );

            bulkRequest.add(client.prepareIndex("twitter", "tweet", "4")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "another post")
                            .endObject()
                    )
            );

            BulkResponse bulkResponse = bulkRequest.get();
            if (bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                System.out.println(bulkResponse.buildFailureMessage());
            }else {
                bulkResponse.forEach(r -> System.out.println(r.getResponse()));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
