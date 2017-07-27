package demo.querydsl;

import demo.ClientExecutor;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * @author chaokunyang
 */
public class MatchAllExample {

    public static void main(String[] args) {
        ClientExecutor.execute(MatchAllExample::matchAll);
    }

    private static void matchAll(Client client) {
        QueryBuilder qb = matchAllQuery();
    }

}
