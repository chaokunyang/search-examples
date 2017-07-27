package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author chaokunyang
 */
public class BulkProcessorExample {
    public static void main(String[] args) {
        // ClientExecutor.execute(BulkProcessorExample::bulkProcessor);
        ClientExecutor.execute(BulkProcessorExample::bulkProcessorInTest);
    }

    /**
     * If you are running tests with elasticsearch and are using the BulkProcessor to populate your dataset you should better set the number of concurrent requests to 0 so the flush operation of the bulk will be executed in a synchronous manner
     * @param client
     */
    private static void bulkProcessorInTest(Client client) {
        BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {

            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {

            } /* Listener methods */ })
                .setBulkActions(10000)
                .setConcurrentRequests(0)
                .build();

        // Add your requests
        try {
            bulkProcessor.add(
                    new IndexRequest("twitter", "tweet", "6").source(
                            jsonBuilder()
                                    .startObject()
                                    .field("user", "kimchy")
                                    .field("postDate", new Date())
                                    .field("message", "trying out Elasticsearch")
                                    .endObject()
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Flush any remaining requests
        bulkProcessor.flush();

        // Or close the bulkProcessor if you don't need it anymore
        bulkProcessor.close();

        // Refresh your indices
        client.admin().indices().prepareRefresh().get();

        // Now you can start searching!
        System.out.println(client.prepareSearch().get());

    }

    private static void bulkProcessor(Client client) {
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                client,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId,
                                           BulkRequest request) {
                        // ...
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          BulkResponse response) {
                        // ...
                    }

                    @Override
                    public void afterBulk(long executionId,
                                          BulkRequest request,
                                          Throwable failure) {
                        // ...
                    }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))
                .build();

        try {
            bulkProcessor.add(new IndexRequest("twitter", "tweet", "5").source(
                    jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));


        try {
            bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bulkProcessor.close();

    }
}
