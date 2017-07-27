package demo.aggregation;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

/**
 * @author chaokunyang
 */
public class StructuringAggregationExample {

    public static void main(String[] args) {
        ClientExecutor.execute(StructuringAggregationExample::structure);
    }

    private static void structure(Client client) {
        SearchResponse sr = client.prepareSearch()
                .addAggregation(
                        AggregationBuilders.terms("top-tags").field("tags.verbatim")
                                .subAggregation(AggregationBuilders.dateHistogram("dates_breakdown")
                                        .field("date")
                                        .dateHistogramInterval(DateHistogramInterval.MONTH)
                                        .subAggregation(AggregationBuilders.avg("to-events").field("event"))
                                )
                )
                .execute().actionGet();

        System.out.println(sr);
    }

}
