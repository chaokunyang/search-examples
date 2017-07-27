package demo.search;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;

/**
 * @author chaokunyang
 */
public class AggregationExample {

    public static void main(String[] args) {
        ClientExecutor.execute(AggregationExample::aggregation);
    }

    private static void aggregation(Client client) {
        SearchResponse sr = client.prepareSearch("get-together").setTypes("event")
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(
                        AggregationBuilders.dateHistogram("agg1")
                                .field("date")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                )
                .get();

        // Get your facet results
        Histogram agg1 = sr.getAggregations().get("agg1");
        System.out.println(agg1.getName() + ", " + agg1.getMetaData());

    }

}
