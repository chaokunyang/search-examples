package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;

/**
 * @author chaokunyang
 */
public class DeleteByQueryExample {

    public static void main(String[] args) {
        ClientExecutor.execute(DeleteByQueryExample::sync);
    }

    private static void sync(Client client) {
        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("user", "kimchy"))
                        .source("twitter")
                        .get();

        long deleted = response.getDeleted();
        System.out.println(deleted);
    }

    // failed to execute failure callback on [demo.document.DeleteByQueryExample
    private static void async(Client client) {
        DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("user", "kimchy"))
                .source("twitter")
                .execute(new ActionListener<BulkByScrollResponse>() {

                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        System.out.println(response.getDeleted());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
