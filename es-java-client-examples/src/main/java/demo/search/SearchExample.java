package demo.search;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Calendar;
import java.util.Date;

/**
 * @author chaokunyang
 */
public class SearchExample {

    public static void main(String[] args) {
        ClientExecutor.execute(SearchExample::search);
    }

    private static void search(Client client) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);
        SearchResponse response = client.prepareSearch("twitter", "twitter2")
                .setTypes("tweet", "tweet")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("user", "kimchy")) // Query
                .setPostFilter(QueryBuilders.rangeQuery("postDate").from(calendar.getTimeInMillis()).to(new Date().getTime())) // Filter
                .setFrom(0).setSize(60).setExplain(true)
                .get();

        System.out.println(response);
    }

}
