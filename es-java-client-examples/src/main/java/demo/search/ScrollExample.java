package demo.search;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.Date;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * <p>While a search request returns a single “page” of results, the scroll API can be used to retrieve large numbers of results (or even all results) from a single search request, in much the same way as you would use a cursor on a traditional database.</p>
 *
 * <p><strong>Scrolling is not intended for real time user requests</strong>, but rather for processing large amounts of data, e.g. in order to reindex the contents of one index into a new index with a different configuration.</p>
 *
 * <pre>
 *     curl -XPOST '10.10.100.59:9200/twitter/tweet/_search?scroll=1m&pretty' -H 'Content-Type: application/json' -d'
     {
         "size": 100,
         "query": {
             "match" : {
             "title" : "elasticsearch"
             }
         }
     }
     '

 * </pre>
 * @author chaokunyang
 */
public class ScrollExample {

    public static void main(String[] args) {
        ClientExecutor.execute(client -> {
            index(client);
            scroll(client);
        });
    }

    private static void index(Client client) {
        try {
            for(int i = 0; i < 5; i++) {
                client.prepareIndex("test", "test_type", i + "")
                        .setSource(jsonBuilder()
                                .startObject()
                                .field("multi", "test")
                                .field("postDate", new Date())
                                .field("message", "trying out Elasticsearch")
                                .endObject()
                        ).get();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void scroll(Client client) {
        QueryBuilder qb = termQuery("multi", "test");

        SearchResponse scrollResp = client.prepareSearch("test")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100).get(); //max of 100 hits will be returned for each scroll until no hits are returned
        do {
            for (SearchHit hit : scrollResp.getHits().getHits()) {
                //Handle the hit...
                System.out.printf("id: %s, fields: %s", hit.getId(), hit.getSource());
                System.out.println();
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while(scrollResp.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
    }

}
